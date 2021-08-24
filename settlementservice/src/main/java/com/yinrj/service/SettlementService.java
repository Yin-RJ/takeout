package com.yinrj.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

/**
 * @author yinrongjie
 * @version 1.0
 * @date 2021/8/24
 * @description
 */
@Service
public class SettlementService {
    private Random random = new Random(12);

    public Integer settlement(Integer accountId, BigDecimal amount) {
        return random.nextInt(10000000);
    }
}
