package com.crossoverJie.seconds.kill.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.crossoverJie.seconds.kill.api.StockService;
import com.crossoverJie.seconds.kill.api.constant.RedisKeysConstant;
import com.crossoverJie.seconds.kill.pojo.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheKey;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 15/04/2018 23:50
 * @since JDK 1.8
 */
@Service
public class StockServiceImpl implements StockService {


    private Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Resource(name = "DBStockService")
    private com.crossoverJie.seconds.kill.service.StockService stockService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //@Autowired
    //private JedisCluster jedisCluster;


    @Override
    public Integer initStock(Integer n)   {
       for (int i=0; i<n;i++){
           Stock stock=new Stock();
           stock.setId(i);
           stock.setName("商品"+i);
           stock.setCount(0+i);
           stock.setVersion(0);
           stock.setSale(1);
           stockService.initStock(stock);
       }
        return 0;
    }

    @Override
    public Integer getCurrentCount() {
        String remoteAddressString = RpcContext.getContext().getRemoteHostName();
        logger.info("request ={}", remoteAddressString);

        Integer count = getStockCount();

        return count;
    }

    /**
     * 再 Redis 中设置库存
     *
     * @return
     */
    private Integer getStockCount() {


        //String count = jedisCluster.get(RedisKeysConstant.STOCK_COUNT + 1);

        String count = redisTemplate.opsForValue().get(RedisKeysConstant.STOCK_COUNT + 1);
        if (count == null) {
            Stock stock = stockService.getStockById(1);
            count = stock.getCount().toString() ;
            /*jedisCluster.set(RedisKeysConstant.STOCK_COUNT + 1, stock.getCount().toString());
            jedisCluster.set(RedisKeysConstant.STOCK_SALE + 1, stock.getSale().toString());
            jedisCluster.set(RedisKeysConstant.STOCK_VERSION + 1, stock.getVersion().toString());*/
            redisTemplate.opsForValue().set(RedisKeysConstant.STOCK_COUNT + 1, stock.getCount().toString());
            redisTemplate.opsForValue().set(RedisKeysConstant.STOCK_SALE + 1, stock.getSale().toString());
            redisTemplate.opsForValue().set(RedisKeysConstant.STOCK_VERSION + 1, stock.getVersion().toString());
        }

        return Integer.parseInt(count);
    }
}
