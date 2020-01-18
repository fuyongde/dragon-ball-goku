package com.goku.order.rest;

import com.goku.order.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author fuyongde
 * @date 2020/1/18 15:36
 */
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderRestController {

    private static Random random = new Random();

    @PostMapping
    public Map<String, Object> save(@RequestBody Order order) {
        long id = random.nextLong();
        order.setId(id);
        Date now = new Date();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        log.info("request body : {}", order);
        Map<String, Object> map = new HashMap<>(16);
        map.put("order", order);
        return map;
    }
}
