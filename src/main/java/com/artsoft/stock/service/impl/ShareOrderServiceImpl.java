package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Depth;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import com.artsoft.stock.util.MathOperation;
import com.artsoft.stock.util.ShareOrderUtil;
import com.artsoft.stock.util.exception.InsufficientBalanceException;
import com.artsoft.stock.util.exception.NotHaveShareException;
import com.artsoft.stock.util.exception.WrongLotInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ShareOrderServiceImpl implements ShareOrderService {

    @Autowired
    private ShareOrderUtil shareUtil;

    Random random = new Random();

    @Override
    @Transactional(readOnly = true)
    public synchronized ShareOrder createShareOrder(Customer customer) throws InsufficientBalanceException, WrongLotInformationException, NotHaveShareException {
        ShareOrder shareOrder = new ShareOrder();
        shareOrder.setCustomer(customer);
        ShareEnum shareEnum = shareUtil.getShare(); // random
        ShareOrderStatus shareOrderStatus = shareUtil.getShareOrderStatus(); // random
        shareOrder.setShareOrderStatus(shareOrderStatus);
        shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.SENT);
        shareOrder.setPrice(shareEnum.getPrice());

        if (shareOrder.getShareOrderStatus().equals(ShareOrderStatus.BUY) && shareOrder.getPrice() < Database.shareMap.get(ShareEnum.ALPHA).getMin()) {
            shareOrder.setPrice(Database.shareMap.get(ShareEnum.ALPHA).getMin());
        }else{

        }

        Integer lot = 0;
        try {
            shareOrder.setShareEnum(shareEnum);
            lot = shareOrder.getCustomer().getPortfolio().getHaveShare().get(shareEnum);
        }catch (Exception e){
            e.getMessage();
        }

        if (lot >= 1 && shareOrderStatus == ShareOrderStatus.SELL) {
            if (lot >= 10) {
                shareOrder.setLot(random.nextInt(10) + 1);
            }else {
                shareOrder.setLot(random.nextInt(lot) + 1);
            }
        } else if (shareOrderStatus == ShareOrderStatus.BUY) {
            Double balance = shareOrder.getCustomer().getPortfolio().getBalance();
            int selectedLot = (int) (balance / Database.shareMap.get(shareEnum).getBuyPrice());
            if (selectedLot != 0) {
                if (selectedLot >= 10){
                    shareOrder.setLot(random.nextInt(10) + 1);
                }else {
                    shareOrder.setLot(random.nextInt(selectedLot) + 1);
                }
            }else {
                throw new InsufficientBalanceException();
            }
        } else {
            throw new WrongLotInformationException();
        }

        Integer haveLot = shareOrder.getCustomer().getPortfolio().getHaveShare().get(shareEnum);

        if (shareOrderStatus == ShareOrderStatus.SELL && lotValidation(haveLot, lot)) {
            shareOrder.getCustomer().getPortfolio().getHaveShare().put(shareOrder.getShareEnum(), lot - shareOrder.getLot());
        }

        if (shareOrderStatus == ShareOrderStatus.BUY) {
            Double balance = shareOrder.getCustomer().getPortfolio().getBalance();
            Double totalCost = MathOperation.arrangeDouble((double) shareOrder.getLot() * shareOrder.getPrice());
            if (balance >= totalCost) {
                balance -= totalCost;
                shareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(balance));
            } else {
                throw new InsufficientBalanceException();
            }
        }

        shareOrder.setCustomer(customer);
        return shareOrder;
    }

    private boolean lotValidation(Integer haveLot, Integer lot) throws NotHaveShareException{
        if (haveLot < lot){
            throw new NotHaveShareException();
        }
        return true;
    }

    private void shareOrderLotUpdate(ShareOrder bigLotShareOrder, ShareOrder smallLotShareOrder) {
        bigLotShareOrder.setLot(bigLotShareOrder.getLot() - smallLotShareOrder.getLot());
        smallLotShareOrder.setLot(0);
    }

    @Override
    public boolean isProcessedImmediately(ShareOrder shareOrder){
        synchronized (this){
            if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() >= Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                    || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() <= Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void processedShareOrders(Share share, ShareOrder buyShareOrder, ShareOrder sellShareOrder) {
        buyShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.PROCESSING);
        sellShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.PROCESSING);

        if (buyShareOrder.getLot() > sellShareOrder.getLot()) {
            Double sellAmount = MathOperation.arrangeDouble((double) sellShareOrder.getLot() * sellShareOrder.getPrice());
            Double sellBalance = sellShareOrder.getCustomer().getPortfolio().getBalance();
            sellShareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(sellBalance + sellAmount));

            buyShareOrder.getCustomer().getPortfolio().getHaveShare().put(buyShareOrder.getShareEnum(), buyShareOrder.getCustomer().getPortfolio().getHaveShare().get(buyShareOrder.getShareEnum()) + sellShareOrder.getLot());

            shareOrderLotUpdate(buyShareOrder, sellShareOrder);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

        } else if (buyShareOrder.getLot() < sellShareOrder.getLot()) {
            Double sellAmount = MathOperation.arrangeDouble((double) buyShareOrder.getLot() * sellShareOrder.getPrice());
            Double sellBalance = sellShareOrder.getCustomer().getPortfolio().getBalance();
            sellShareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(sellBalance + sellAmount));

            buyShareOrder.getCustomer().getPortfolio().getHaveShare().put(buyShareOrder.getShareEnum(), buyShareOrder.getCustomer().getPortfolio().getHaveShare().get(buyShareOrder.getShareEnum()) + buyShareOrder.getLot());

            shareOrderLotUpdate(sellShareOrder, buyShareOrder);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMAINING);

        } else {
            Double sellAmount = MathOperation.arrangeDouble((double) sellShareOrder.getLot() * sellShareOrder.getPrice());
            Double sellBalance = sellShareOrder.getCustomer().getPortfolio().getBalance();
            sellShareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(sellBalance + sellAmount));

            buyShareOrder.getCustomer().getPortfolio().getHaveShare().put(buyShareOrder.getShareEnum(), buyShareOrder.getCustomer().getPortfolio().getHaveShare().get(buyShareOrder.getShareEnum()) + buyShareOrder.getLot());

            shareOrderLotUpdate(buyShareOrder, sellShareOrder);

            buyShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.REMOVE);

        }
    }

}