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

    @Override
    @Transactional(readOnly = true)
    public void decomposeShareOrder(ShareOrder shareOrder) {
        synchronized (this){
            Share share = Database.shareMap.get(shareOrder.getShareEnum());

            Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
            level.setShareCode(share.getShareCode());

            if (shareOrder.getShareOrderOperationStatus().equals(ShareOrderStatus.BUY)){
                level.setBuyLotQuantity(shareOrder.getLot() + level.getBuyLotQuantity());
                level.setBuyShareOrderQuantity(level.getBuyShareOrderQuantity() + 1);
            }else {
                level.setSellLotQuantity(shareOrder.getLot() + level.getSellLotQuantity());
                level.setSellShareOrderQuantity(level.getSellShareOrderQuantity() + 1);
            }
            level.getShareOrderList().add(shareOrder);
        }
    }

    @Transactional(readOnly = true)
    public void immediatelyShareOrder(ShareOrder shareOrder) {
        synchronized (this){
            Share share = Database.shareMap.get(shareOrder.getShareEnum());

            if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice()){
                Level level = share.getDepth().getLevelMap().get(share.getSellPrice());
                share.getDepth().getLevelMap().get(shareOrder.getPrice());
                level.setShareCode(share.getShareCode());

                level.setBuyLotQuantity(shareOrder.getLot() + level.getBuyLotQuantity());
                level.setBuyShareOrderQuantity(level.getBuyShareOrderQuantity() + 1);

                level.getShareOrderList().add(shareOrder);
            }

            if(shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice()){
                Level level = share.getDepth().getLevelMap().get(share.getBuyPrice());
                share.getDepth().getLevelMap().get(shareOrder.getPrice());
                level.setShareCode(share.getShareCode());

                level.setSellLotQuantity(shareOrder.getLot() + level.getSellLotQuantity());
                level.setSellShareOrderQuantity(level.getSellShareOrderQuantity() + 1);

                level.getShareOrderList().add(shareOrder);
            }
        }
    }

    private void shareOrderLotUpdate(ShareOrder bigLotShareOrder, ShareOrder smallLotShareOrder) {
        bigLotShareOrder.setLot(bigLotShareOrder.getLot() - smallLotShareOrder.getLot());
        smallLotShareOrder.setLot(0);
    }

    @Override
    @Transactional(readOnly = true)
    public void processedShareOrders(ShareOrder buyShareOrder, ShareOrder sellShareOrder) {
        buyShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.PROCESSING);
        sellShareOrder.setShareOrderOperationStatus(GeneralEnumeration.ShareOrderOperationStatus.PROCESSING);

        if (buyShareOrder.getLot() > sellShareOrder.getLot()) {
            Double sellAmount = MathOperation.arrangeDouble((double) sellShareOrder.getLot() * sellShareOrder.getPrice());
            Double sellBalance = sellShareOrder.getCustomer().getPortfolio().getBalance();
            sellShareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(sellBalance + sellAmount));

            buyShareOrder.getCustomer().getPortfolio().getHaveShare().put(buyShareOrder.getShareEnum(), buyShareOrder.getCustomer().getPortfolio().getHaveShare().get(buyShareOrder.getShareEnum()) + sellShareOrder.getLot());

            shareOrderLotUpdate(buyShareOrder, sellShareOrder);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);

        } else if (buyShareOrder.getLot() < sellShareOrder.getLot()) {
            Double sellAmount = MathOperation.arrangeDouble((double) buyShareOrder.getLot() * sellShareOrder.getPrice());
            Double sellBalance = sellShareOrder.getCustomer().getPortfolio().getBalance();
            sellShareOrder.getCustomer().getPortfolio().setBalance(MathOperation.arrangeDouble(sellBalance + sellAmount));

            buyShareOrder.getCustomer().getPortfolio().getHaveShare().put(buyShareOrder.getShareEnum(), buyShareOrder.getCustomer().getPortfolio().getHaveShare().get(buyShareOrder.getShareEnum()) + buyShareOrder.getLot());

            shareOrderLotUpdate(sellShareOrder, buyShareOrder);

            buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.REMOVE);
            sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);

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

    @Override
    public void deletedShareOrders(Share share, List<ShareOrder> buyLevelBuyShareOrderList, List<ShareOrder> buyLevelSellShareOrderList, List<ShareOrder> sellLevelBuyShareOrderList, List<ShareOrder> sellLevelSellShareOrderList) {
        synchronized (this){
            Predicate<ShareOrder> deletedShareOrder = shareOrder -> shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE);

            List<ShareOrder> buyLevelBuyShareOrder = buyLevelBuyShareOrderList.stream().filter(shareOrder -> shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)).collect(Collectors.toList());
            List<ShareOrder> buyLevelSellShareOrder = buyLevelSellShareOrderList.stream().filter(shareOrder -> shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)).collect(Collectors.toList());
            List<ShareOrder> sellLevelBuyShareOrder = sellLevelBuyShareOrderList.stream().filter(shareOrder -> shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)).collect(Collectors.toList());
            List<ShareOrder> sellLevelSellShareOrder = sellLevelSellShareOrderList.stream().filter(shareOrder -> shareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)).collect(Collectors.toList());

            if(!buyLevelBuyShareOrder.isEmpty()){
                share.getDepth().getLevelMap().get(buyLevelBuyShareOrder.get(0).getPrice()).getShareOrderList().removeIf(deletedShareOrder);
            }
            if (!buyLevelSellShareOrder.isEmpty()){
                share.getDepth().getLevelMap().get(buyLevelSellShareOrder.get(0).getPrice()).getShareOrderList().removeIf(deletedShareOrder);
            }
            if (!sellLevelBuyShareOrder.isEmpty()){
                share.getDepth().getLevelMap().get(sellLevelBuyShareOrder.get(0).getPrice()).getShareOrderList().removeIf(deletedShareOrder);
            }
            if (!sellLevelSellShareOrder.isEmpty()){
                share.getDepth().getLevelMap().get(sellLevelSellShareOrder.get(0).getPrice()).getShareOrderList().removeIf(deletedShareOrder);
            }
        }
    }
}