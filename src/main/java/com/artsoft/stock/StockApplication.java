package com.artsoft.stock;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Portfolio;
import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareOrderService;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.service.TradeService;
import com.artsoft.stock.service.impl.ShareOrderServiceImpl;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import com.artsoft.stock.util.exception.InsufficientBalanceException;
import com.artsoft.stock.util.exception.NotHaveShareException;
import com.artsoft.stock.util.exception.WrongLotInformationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class StockApplication implements CommandLineRunner {

    @Autowired
    private ShareOrderService shareOrderService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private ShareService shareService;

    Random random = new Random();

    private static int id = 0;

    @PostConstruct
    public void init(){
        Database.shareMap.put(ShareEnum.AAA, new Share(ShareEnum.AAA, 1.0));
        Database.shareMap.put(ShareEnum.BBB, new Share(ShareEnum.BBB, 1.0));
        /*Database.shareMap.put(ShareEnum.CCC, new Share(ShareEnum.CCC, 1.0, 1.0, 1.01));
        Database.shareMap.put(ShareEnum.DDD, new Share(ShareEnum.DDD, 1.0, 1.0, 1.01));
        Database.shareMap.put(ShareEnum.EEE, new Share(ShareEnum.EEE, 1.0, 1.0, 1.01));*/

        Database.shareMap.get(ShareEnum.AAA).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.AAA));
        Database.shareMap.get(ShareEnum.BBB).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.BBB));
        /*Database.shareMap.get(ShareEnum.CCC).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.CCC));
        Database.shareMap.get(ShareEnum.DDD).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.DDD));
        Database.shareMap.get(ShareEnum.EEE).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.EEE));*/

    }

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Thread a = new Thread(() -> {
            Customer customer = createCustomer("A");
            Database.customerMap.put("A", customer);
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                            || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                        shareOrderService.immediatelyShareOrder(shareOrder);
                    }else {
                        //put(shareOrder);
                        shareOrderService.decomposeShareOrder(shareOrder);
                    }
                    //System.out.println(shareOrder);
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }

            }
        });

        Thread b = new Thread(() -> {
            Customer customer = createCustomer("B");
            Database.customerMap.put("B", customer);
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                            || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                        shareOrderService.immediatelyShareOrder(shareOrder);
                    }else {
                        //put(shareOrder);
                        shareOrderService.decomposeShareOrder(shareOrder);
                    }
                    //System.out.println(shareOrder);
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });
        Thread c = new Thread(() -> {
            Customer customer = createCustomer("C");
            Database.customerMap.put("C", customer);

            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                            || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                        shareOrderService.immediatelyShareOrder(shareOrder);
                    }else {
                        //put(shareOrder);
                        shareOrderService.decomposeShareOrder(shareOrder);
                    }
                    //System.out.println(shareOrder);
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });
        Thread d = new Thread(() -> {
            Customer customer = createCustomer("D");
            Database.customerMap.put("D", customer);
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                            || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                        shareOrderService.immediatelyShareOrder(shareOrder);
                    }else {
                        //put(shareOrder);
                        shareOrderService.decomposeShareOrder(shareOrder);
                    }
                    //System.out.println(shareOrder);
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });
        Thread e = new Thread(() -> {
            Customer customer = createCustomer("E");
            Database.customerMap.put("E", customer);
            while (true) {
                try {
                    Thread.sleep(random.nextInt(10000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    if ((shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY) && shareOrder.getPrice() > Database.shareMap.get(shareOrder.getShareEnum()).getBuyPrice())
                            || (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL) && shareOrder.getPrice() < Database.shareMap.get(shareOrder.getShareEnum()).getSellPrice())){
                        shareOrderService.immediatelyShareOrder(shareOrder);
                    }else {
                        //put(shareOrder);
                        shareOrderService.decomposeShareOrder(shareOrder);
                    }
                    //System.out.println(shareOrder);
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread decomposeShareOrder = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            while (true){
                try {
                    Thread.sleep(100);
                    ShareOrder shareOrder = Database.shareOrderQueue.take();
                    shareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
                    shareOrderService.decomposeShareOrder(shareOrder);
                } catch (InterruptedException ex) {

                }
            }
        });

        Thread matchShareOrderA = new Thread(() -> {
            Share share = Database.shareMap.get(ShareEnum.AAA);
            shareService.setShareStartPrice(share);
            while (true) {
                try {
                    Thread.sleep(1000);
                    tradeService.matchShareOrder(share);
                    System.out.println("A Hissesi ===> Alış : " + share.getBuyPrice() + " --- " + "Satış : " + share.getSellPrice());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        });

       Thread matchShareOrderB = new Thread(() -> {
           Share share = Database.shareMap.get(ShareEnum.BBB);
           shareService.setShareStartPrice(share);
            while(true) {
                try {
                    Thread.sleep(1000);
                    tradeService.matchShareOrder(share);
                    System.out.println("B Hissesi ===> Alış : " + share.getBuyPrice() + " --- " + "Satış : " + share.getSellPrice());
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        });

        /*Thread matchShareOrderC = new Thread(() -> {

            while(true) {
                try {
                    Thread.sleep(1000);
                    shareOrderService.matchShareOrderThread(MarketUtil.cBuyShareOrderMap, MarketUtil.cSellShareOrderMap);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        });

        Thread matchShareOrderD = new Thread(() -> {

            while(true) {
                try {
                    Thread.sleep(1000);
                    shareOrderService.matchShareOrderThread(MarketUtil.dBuyShareOrderMap, MarketUtil.dSellShareOrderMap);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        });

        Thread matchShareOrderE = new Thread(() -> {

            while(true) {
                try {
                    Thread.sleep(1000);
                    shareOrderService.matchShareOrderThread(MarketUtil.eBuyShareOrderMap, MarketUtil.eSellShareOrderMap);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

        });
*/
        a.start();
        b.start();
        c.start();
        d.start();
        e.start();
        //decomposeShareOrder.start();
        matchShareOrderA.start();
        matchShareOrderB.start();
        /*matchShareOrderC.start();
        matchShareOrderD.start();
        matchShareOrderE.start();*/
    }

    private void put(ShareOrder shareOrder){
        try {
            Database.shareOrderQueue.put(shareOrder);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private synchronized int idPlus(){
        return ++id;
    }

    private Customer createCustomer(String customerName){
        Map<ShareEnum, Integer> haveShare = new HashMap<>();
        haveShare.put(ShareEnum.AAA, 1000);
        haveShare.put(ShareEnum.BBB, 1000);
        /*haveShare.put(ShareEnum.CCC, 100);
        haveShare.put(ShareEnum.DDD, 100);
        haveShare.put(ShareEnum.EEE, 100);*/
        Portfolio portfolio = new Portfolio(10000.0, haveShare);
        return new Customer(customerName, portfolio);
    }

}
