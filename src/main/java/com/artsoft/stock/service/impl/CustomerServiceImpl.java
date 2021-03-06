package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Portfolio;
import com.artsoft.stock.service.CustomerService;
import com.artsoft.stock.util.GeneralEnumeration;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Customer createCustomer(String customerName){
        synchronized (this) {
            Map<GeneralEnumeration.ShareEnum, Integer> haveShare = new HashMap<>();
            haveShare.put(GeneralEnumeration.ShareEnum.ALPHA, 1000);
        /*haveShare.put(ShareEnum.BETA, 1000);
        haveShare.put(ShareEnum.GAMMA, 100);
        haveShare.put(ShareEnum.DDD, 100);
        haveShare.put(ShareEnum.EEE, 100);*/
            Portfolio portfolio = new Portfolio(10000.0, haveShare);
            log.info("{} tarafından {} müşterisi oluşturuldu...", Thread.currentThread().getName(), customerName);
            return new Customer(customerName, portfolio);
        }
    }

    @Override
    public void payEmployeeSalary(Customer customer) {

    }

    @Override
    public void updatePortfolio(Customer customer){

    }
}
