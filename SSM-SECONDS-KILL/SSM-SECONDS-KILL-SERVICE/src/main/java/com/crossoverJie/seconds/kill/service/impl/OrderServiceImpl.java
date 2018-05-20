package com.crossoverJie.seconds.kill.service.impl;

import com.crossoverJie.seconds.kill.api.constant.RedisKeysConstant;
import com.crossoverJie.seconds.kill.dao.StockOrderMapper;
import com.crossoverJie.seconds.kill.pojo.Stock;
import com.crossoverJie.seconds.kill.pojo.StockOrder;
import com.crossoverJie.seconds.kill.service.OrderService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 01/05/2018 14:10
 * @since JDK 1.8
 */
@Transactional(rollbackFor = Exception.class)
@Service(value = "DBOrderService")
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);


    @Resource(name = "DBStockService")
    private com.crossoverJie.seconds.kill.service.StockService stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //@Autowired
    //private JedisCluster jedisCluster;


    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    @Override
    public int createWrongOrder(int sid) throws Exception {

        //校验库存
        Stock stock = checkStock(sid);

        //扣库存
        saleStock(stock);

        //创建订单
        int id = createOrder(stock);

        return id;
    }

    @Override
    public int createOptimisticOrder(int sid) throws Exception {

        //校验库存
        Stock stock = checkStock(sid);

        //乐观锁更新库存
        saleStockOptimistic(stock);

        //创建订单
        int id = createOrder(stock);

        logger.info("订单:" + id);

        return id;
    }

    @Override
    public int createOptimisticOrderUseRedis(int sid) throws Exception {
        //检验库存，从 Redis 获取
        Stock stock = checkStockByRedis(sid);

        //乐观锁更新库存 以及更新 Redis
        saleStockOptimisticByRedis(stock);

        //创建订单
        int id = createOrder(stock);
        return id;
    }

    @Override
    public void createOptimisticOrderUseRedisAndKafka(int sid) throws Exception {

        //检验库存，从 Redis 获取
        Stock stock = checkStockByRedis(sid);

        //利用 Kafka 创建订单
        kafkaProducer.send(new ProducerRecord(kafkaTopic, stock));
        logger.info("=====================================send Kafka success");

    }

    /**
     * 既然要比对销售和库存，那么减库存，并存放在redis,暂时不减db库存
     * @param sid
     * @return
     * @throws Exception
     */
    private Stock checkStockByRedis(int sid) throws Exception {

      /*  Integer count = Integer.parseInt(jedisCluster.get(RedisKeysConstant.STOCK_COUNT + sid));
         Integer sale = Integer.parseInt(jedisCluster.get(RedisKeysConstant.STOCK_SALE + sid));*/

        Stock stock = new Stock();
        String cstring = redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_COUNT + sid);
        String sstring = redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_SALE + sid);
        String newVersionValue = redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_VERSION + sid);

        if (cstring == null && sstring == null && newVersionValue == null) {//redis无任何缓存自己进库update,消费一次
            Integer icount = stockService.getStockById(sid).getCount();//库存总数
            stock.setId(sid);
            stock.setCount(icount); //库存数
            stock.setSale(0);   //销售数
            stock.setVersion(0); //更新库存表
            redisTemplate.opsForValue().set(RedisKeysConstant.STOCK_COUNT + sid,String.valueOf(icount));
            logger.info("================初始redis=====================版本:1   剩余库存数:"+(icount-1)+"  销售数：1");
            return stock;
        }

        Integer count=0,sale=0;
        if(cstring != null && sstring != null){
          sale = Integer.parseInt(sstring);
          count = Integer.parseInt(cstring);//总库-销售 todo  ?
          if (count.equals(sale)) throw new RuntimeException("库存不足 Redis currentCount=" + (count-sale));
         }else {
            throw new RuntimeException("库存 =" + cstring +" 销售=" +sstring);
        }

        // Integer version = Integer.parseInt(jedisCluster.get(RedisKeysConstant.STOCK_VERSION + sid));

        Integer version = Integer.parseInt(newVersionValue);
        stock.setId(sid);
        stock.setCount(count);
        stock.setSale(sale);
        stock.setVersion(version);
        logger.info("================redis=====================版本:" + version);
        return stock;
    }

    /**
     * 乐观锁更新数据库 还要更新 Redis
     *
     * @param stock
     */
    private void saleStockOptimisticByRedis(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock); //根据版本号和库存id更新
        if (count == 0) {
            throw new RuntimeException("并发更新库存失败");
        }
        //自增
        // jedisCluster.incrBy(RedisKeysConstant.STOCK_SALE + stock.getId(),1);
        // jedisCluster.incrBy(RedisKeysConstant.STOCK_VERSION + stock.getId(),1);

       // redisTemplate.opsForValue().set(RedisKeysConstant.STOCK_COUNT + stock.getId(),String.valueOf(count));//TODO 总库存后续比对减掉 ?
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_SALE + stock.getId(), 1);
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_VERSION + stock.getId(), 1);
    }

    private Stock checkStock(int sid) {
        Stock stock = stockService.getStockById(sid);
        if (stock != null && stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足");
        }
        return stock;
    }

    private void saleStockOptimistic(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock);
        if (count == 0) {
            throw new RuntimeException("并发更新库存失败");
        }
    }


    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        int id = orderMapper.insertSelective(order);
        return id;
    }

    private int saleStock(Stock stock) {
        stock.setSale(stock.getSale() + 1);
        return stockService.updateStockById(stock);
    }
}
