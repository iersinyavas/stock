package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.service.MarketService;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketServiceImpl implements MarketService {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private ShareOrderService shareOrderService;

}
