package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TradeService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private ShareOrderService shareOrderService;
    @Autowired
    private ShareService shareService;

    @Override
    public void matchShareOrder(Share share) {
        synchronized (this) {
            Level buyLevel = share.getDepth().getLevelMap().get(share.getBuyPrice());
            Level sellLevel = share.getDepth().getLevelMap().get(share.getSellPrice());

            if (buyLevel.getPrice() != null && sellLevel.getPrice() != null) {
                while (true) {

                    Map<ShareOrderStatus, List<ShareOrder>> buyLevelGroupedShareOrderByShareOrderStatus = buyLevel.getShareOrderList().stream()
                            .filter(shareOrder -> !shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderOperationStatus.REMOVE))
                            .collect(Collectors.groupingBy(ShareOrder::getShareOrderStatus));

                    Map<ShareOrderStatus, List<ShareOrder>> sellLevelGroupedShareOrderByShareOrderStatus = sellLevel.getShareOrderList().stream()
                            .filter(shareOrder -> !shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderOperationStatus.REMOVE))
                            .collect(Collectors.groupingBy(ShareOrder::getShareOrderStatus));

                    List<ShareOrder> buyLevelBuyShareOrderList = buyLevelGroupedShareOrderByShareOrderStatus.get(ShareOrderStatus.BUY);
                    List<ShareOrder> buyLevelSellShareOrderList = buyLevelGroupedShareOrderByShareOrderStatus.get(ShareOrderStatus.SELL);

                    List<ShareOrder> sellLevelBuyShareOrderList = sellLevelGroupedShareOrderByShareOrderStatus.get(ShareOrderStatus.BUY);
                    List<ShareOrder> sellLevelSellShareOrderList = sellLevelGroupedShareOrderByShareOrderStatus.get(ShareOrderStatus.SELL);
                    try {
                        if (!buyLevelBuyShareOrderList.isEmpty() || !sellLevelSellShareOrderList.isEmpty()) {
                            if (buyLevelBuyShareOrderList != null) {
                                ShareOrder buySellMax = buyLevelSellShareOrderList.stream().max(Comparator.comparing(ShareOrder::getPrice)).get();
                                shareOrderService.processedShareOrders(buySellMax, buyLevelBuyShareOrderList.get(0));
                            }else {
                                shareService.updateSharePrice(Database.shareMap.get(buyLevel.getShareCode()), -0.01);
                                buyLevelSellShareOrderList.forEach(shareOrder -> shareOrderService.decomposeShareOrder(shareOrder));
                                buyLevelGroupedShareOrderByShareOrderStatus.remove(ShareOrderStatus.SELL);
                            }
                            if (sellLevelSellShareOrderList != null) {
                                ShareOrder sellBuyMin = sellLevelBuyShareOrderList.stream().min(Comparator.comparing(ShareOrder::getPrice)).get();
                                shareOrderService.processedShareOrders(sellBuyMin, sellLevelSellShareOrderList.get(0));
                            }else {
                                shareService.updateSharePrice(Database.shareMap.get(buyLevel.getShareCode()), 0.01);
                                sellLevelBuyShareOrderList.forEach(shareOrder -> shareOrderService.decomposeShareOrder(shareOrder));
                                sellLevelGroupedShareOrderByShareOrderStatus.remove(ShareOrderStatus.BUY);
                                break;
                            }
                            shareOrderService.deletedShareOrders(share, buyLevelBuyShareOrderList, buyLevelSellShareOrderList, sellLevelBuyShareOrderList, sellLevelSellShareOrderList);
                            shareService.updateShare(share);
                        }
                    } catch (NullPointerException npe) {
                        /*if (buyLevelBuyShareOrderList == null) {
                            shareService.updateSharePrice(Database.shareMap.get(buyLevel.getShareCode()), -0.01);
                        } else {
                            shareService.updateSharePrice(Database.shareMap.get(buyLevel.getShareCode()), 0.01);
                        }*/
                        break;
                    }
                }
            }
        }
    }
}
