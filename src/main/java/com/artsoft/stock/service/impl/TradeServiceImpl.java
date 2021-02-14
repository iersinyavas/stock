package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TradeService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TradeServiceImpl implements TradeService {

    @Autowired
    private ShareOrderService shareOrderService;
    @Autowired
    private ShareService shareService;
    @Autowired
    private TradeService tradeService;

    private boolean checkLevelShareOrderStatus(Level level, ShareOrder shareOrder) {
        if (level.getLevelShareOrderStatus().equals(ShareOrderStatus.BUY)) {
            if (level.getBuyLotQuantity() >= shareOrder.getLot()) {
                return true;
            } else return false;
        } else {
            if (level.getSellLotQuantity() <= shareOrder.getLot()) {
                return true;
            } else return false;
        }
    }

    private boolean checkLevelLotQuantity(Level level, ShareOrder shareOrder) {
        if (level.getLevelShareOrderStatus().equals(ShareOrderStatus.BUY)) {
            if (level.getBuyLotQuantity() >= shareOrder.getLot()) {
                return true;
            } else return false;
        } else {
            if (level.getSellLotQuantity() >= shareOrder.getLot()) {
                return true;
            } else return false;
        }
    }

    private Double levelChange(Level level) {
        if (level.getLevelShareOrderStatus().equals(ShareOrderStatus.BUY)) {
            return -0.01;
        } else return 0.01;
    }


    @Override
    public void processedShareOrder(Share share, ShareOrder buyShareOrder, ShareOrder sellShareOrder) {
        shareOrderService.processedShareOrders(share, buyShareOrder, sellShareOrder);
    }
}
