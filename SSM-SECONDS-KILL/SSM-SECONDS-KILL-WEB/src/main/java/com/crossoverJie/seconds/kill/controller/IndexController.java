package com.crossoverJie.seconds.kill.controller;

import com.crossoverJie.seconds.kill.api.OrderService;
import com.crossoverJie.seconds.kill.api.StockService;
import com.crossoverjie.distributed.annotation.ControllerLimit;
import com.crossoverjie.distributed.annotation.SpringControllerLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 15/04/2018 21:58
 * @since JDK 1.8
 */
@Controller
@RequestMapping(value = "/")
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private OrderService orderService;


    //初始化库存
    @RequestMapping("/init/{num}")
    @ResponseBody
    public String initSocker(@PathVariable Integer num) {

        stockService.initStock(num);

        logger.info("init socker");
        return "OK";
    }



    @RequestMapping("/health")
    @ResponseBody
    public String health() {
        logger.info("heath");
        return "OK";
    }

    @RequestMapping("/getStockNum")
    @ResponseBody
    public String getStockNum() {
        int currentCount = 0;
        try {
            currentCount = stockService.getCurrentCount();
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        logger.info("currentCount={}", currentCount);
        return String.valueOf(currentCount);
    }


    @RequestMapping("/createWrongOrder/{sid}")
    @ResponseBody
    public String createWrongOrder(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createWrongOrder(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }


    /**
     * 乐观锁更新库存
     * 1、单机、单库,单应用10秒内1千并发无压力，事务100%通过
     * 2、单机、单库,单应用5秒内1万并发有压力，事务100%错误,消费者应用服务器未死?
     * 3、单机、单库,单应用1秒内1万并发无压力
     * @param sid
     * @return
     */
    @RequestMapping("/createOptimisticOrder/{sid}")
    @ResponseBody
    public String createOptimisticOrder(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }

    /**
     * 乐观锁更新库存 限流
     * @param sid
     * @return
     */
    @SpringControllerLimit(errorCode = 200)
    @RequestMapping("/createOptimisticLimitOrder/{sid}")
    @ResponseBody
    public String createOptimisticLimitOrder(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createOptimisticOrder(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }

    /**
     * 核心业务已完善
     *
     * 乐观锁更新库存 限流 库存改为查询 Redis 提高性能
     * @param sid
     * @return
     */
    @SpringControllerLimit(errorCode = 200,errorMsg = "request has limited")
    @RequestMapping("/createOptimisticLimitOrderByRedis/{sid}")
    @ResponseBody
    public String createOptimisticLimitOrderByRedis(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            id = orderService.createOptimisticOrderUseRedis(sid);
        } catch (Exception e) {
           // if(id==0) logger.error("无法生成订单！");
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }

    /**
     * 乐观锁更新库存 限流 库存改为查询 Redis 提高性能
     * 异步创建订单 Kafka
     * @param sid
     * @return
     */
    @SpringControllerLimit
    @RequestMapping("/createOptimisticLimitOrderByRedisAndKafka/{sid}")
    @ResponseBody
    public String createOptimisticLimitOrderByRedisAndKafka(@PathVariable int sid) {
        logger.info("sid=[{}]", sid);
        int id = 0;
        try {
            orderService.createOptimisticOrderUseRedisAndKafka(sid);
        } catch (Exception e) {
            logger.error("Exception",e);
        }
        return String.valueOf(id);
    }


}
