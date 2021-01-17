package com.artsoft.stock.service;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.util.exception.InsufficientBalanceException;
import com.artsoft.stock.util.exception.NotHaveShareException;
import com.artsoft.stock.util.exception.WrongLotInformationException;

import java.util.List;

public interface
ShareOrderService {
    ShareOrder createShareOrder(Customer customer) throws InsufficientBalanceException, WrongLotInformationException, NotHaveShareException;
    void decomposeShareOrder(ShareOrder shareOrder);
    void processedShareOrders(ShareOrder buyShareOrderList, ShareOrder sellShareOrderList);
    void deletedShareOrders(Share share, List<ShareOrder> buyLevelBuyShareOrderList, List<ShareOrder> buyLevelSellShareOrderList, List<ShareOrder> sellLevelBuyShareOrderList, List<ShareOrder> sellLevelSellShareOrderList);
    void immediatelyShareOrder(ShareOrder shareOrder);
}
