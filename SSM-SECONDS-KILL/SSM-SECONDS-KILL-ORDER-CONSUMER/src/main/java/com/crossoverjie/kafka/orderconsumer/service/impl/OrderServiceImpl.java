package com.crossoverjie.kafka.orderconsumer.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.kafka.orderconsumer.constant.RedisKeysConstant;
import com.crossoverjie.kafka.orderconsumer.mapper.StockOrderMapper;
import com.crossoverjie.kafka.orderconsumer.pojo.Stock;
import com.crossoverjie.kafka.orderconsumer.pojo.StockOrder;
import com.crossoverjie.kafka.orderconsumer.service.OrderService;
import com.crossoverjie.kafka.orderconsumer.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private StockService stockService;

    @Autowired
    private StockOrderMapper orderMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;



    @Override
    public void createOptimisticOrderUseRedisAndKafka(Stock stock) throws Exception {
        //乐观锁更新库存 以及更新 Redis
        saleStockOptimisticByRedis(stock);
        //创建订单
        createOrder(stock);
    }


    /**
     * 乐观锁更新数据库 还要更新 Redis
     * @param stock
     */
    private void saleStockOptimisticByRedis(Stock stock) {
        int count = stockService.updateStockByOptimistic(stock); //TODO 事务没起效 ?
        if (count == 0){
            throw new RuntimeException("并发更新库存失败") ;
        }
        //自增
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_SALE + stock.getId(),1) ;
        redisTemplate.opsForValue().increment(RedisKeysConstant.STOCK_VERSION + stock.getId(),1) ;
    }

    private int createOrder(Stock stock) {
        StockOrder order = new StockOrder();
        order.setSid(stock.getId());
        order.setName(stock.getName());
        SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //order.setCreateTime(df.parse(df.format(new Date())));
        int id=0;
          try{
             id= orderMapper.insertSelective(order);
            // id= orderMapper.insertStock(order);
         }catch(Exception e){
              logger.error("=================order:"+JSON.toJSONString(order)+"=================SQL ERR:"+e.getMessage());
              throw  new RuntimeException(e.getMessage());
          }finally {
              order=null;
          }
        return id;
    }

}
