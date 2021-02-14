package com.artsoft.stock.service;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.exception.InsufficientBalanceException;
import com.artsoft.stock.util.exception.NotHaveShareException;
import com.artsoft.stock.util.exception.WrongLotInformationException;

import java.util.List;

public interface
ShareOrderService {
    ShareOrder createShareOrder(Customer customer) throws InsufficientBalanceException, WrongLotInformationException, NotHaveShareException;
    void processedShareOrders(Share share, ShareOrder buyShareOrder, ShareOrder sellShareOrder);
    boolean isProcessedImmediately(ShareOrder shareOrder);
}
