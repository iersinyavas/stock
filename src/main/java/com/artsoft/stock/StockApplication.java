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
import com.artsoft.stock.util.GeneralEnumeration.ShareSessionType;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderOperationStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import com.artsoft.stock.util.MathOperation;
import com.artsoft.stock.util.exception.InsufficientBalanceException;
import com.artsoft.stock.util.exception.NotHaveShareException;
import com.artsoft.stock.util.exception.WrongLotInformationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@Slf4j
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
    public void init() {
        Database.shareMap.put(ShareEnum.ALPHA, new Share(ShareEnum.ALPHA, 1.0, 1.0, 1.01, ShareSessionType.OPENING));
        /*Database.shareMap.put(ShareEnum.BETA, new Share(ShareEnum.BETA, 1.0));
        Database.shareMap.put(ShareEnum.GAMMA, new Share(ShareEnum.GAMMA, 1.0, 1.0, 1.01));*/

        Database.shareMap.get(ShareEnum.ALPHA).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.ALPHA));
        /*Database.shareMap.get(ShareEnum.BETA).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.BETA));
        Database.shareMap.get(ShareEnum.GAMMA).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.GAMMA));*/
        //shareService.setRandomShareStartPrice(Database.shareMap.get(ShareEnum.ALPHA));

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
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
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
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
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
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
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
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
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
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread f = new Thread(() -> {
            Customer customer = createCustomer("F");
            Database.customerMap.put("F", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread g = new Thread(() -> {
            Customer customer = createCustomer("G");
            Database.customerMap.put("G", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread h = new Thread(() -> {
            Customer customer = createCustomer("H");
            Database.customerMap.put("H", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread i = new Thread(() -> {
            Customer customer = createCustomer("I");
            Database.customerMap.put("I", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread j = new Thread(() -> {
            Customer customer = createCustomer("J");
            Database.customerMap.put("J", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread k = new Thread(() -> {
            Customer customer = createCustomer("K");
            Database.customerMap.put("K", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread l = new Thread(() -> {
            Customer customer = createCustomer("L");
            Database.customerMap.put("L", customer);
            while (true) {
                try {
                    Thread.sleep(2000 + random.nextInt(3000));
                    ShareOrder shareOrder = shareOrderService.createShareOrder(customer);
                    shareOrder.setId(idPlus());
                    Share share = Database.shareMap.get(shareOrder.getShareEnum());

                    /*if (shareOrderService.isProcessedImmediately(shareOrder)){
                        if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)){
                            shareOrder.setPrice(share.getSellPrice());
                        }else {
                            shareOrder.setPrice(share.getBuyPrice());
                        }
                    }*/
                    if (share.getShareSessionType().equals(ShareSessionType.OPENING)){
                        Database.shareOrderQueue.put(shareOrder);
                        continue;
                    }

                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                } catch (InterruptedException ex) {

                } catch (WrongLotInformationException ex) {

                } catch (InsufficientBalanceException ex) {

                } catch (NotHaveShareException ex) {

                }
            }
        });

        Thread processedBuyLevelShareOrders = new Thread(() -> {
            Share share = Database.shareMap.get(ShareEnum.ALPHA);
            Level buyLevel;
            Level sellLevel;
            while (true) {
                try {
                    Thread.sleep(500);
                    buyLevel = share.getDepth().getLevelMap().get(share.getBuyPrice());
                    sellLevel = share.getDepth().getLevelMap().get(share.getSellPrice());
                    int buy = 0;

                    if (!buyLevel.getBuyShareOrderQueue().isEmpty() && !buyLevel.getSellShareOrderQueue().isEmpty()) {
                        ShareOrder buyShareOrder = buyLevel.getBuyShareOrderQueue().peek();
                        ShareOrder sellShareOrder = buyLevel.getSellShareOrderQueue().peek();

                        if (buyLevel.getBuyLotQuantity() >= sellShareOrder.getLot()) {
                            tradeService.processedShareOrder(share, buyShareOrder, sellShareOrder);

                            if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.RECEIVED)) {
                                //buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
                                buyLevel.getBuyShareOrderQueue().take();
                                log.info("{} idli share order silinmiş olmalı", buyShareOrder.getId());
                            }
                            if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.RECEIVED)) {
                                //sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
                                buyLevel.getSellShareOrderQueue().take();
                                log.info("{} idli share order silinmiş olmalı", sellShareOrder.getId());
                            }

                            buyLevel.setBuyLotQuantity(buyLevel.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            buyLevel.setBuyShareOrderQuantity(buyLevel.getBuyShareOrderQueue().size());

                            buyLevel.setSellLotQuantity(buyLevel.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            buyLevel.setSellShareOrderQuantity(buyLevel.getSellShareOrderQueue().size());
                            buy++;

                        } else {
                            //buyLevel.getBuyShareOrderQueue().put(buyShareOrder);
                            //buyLevel.getSellShareOrderQueue().put(sellShareOrder);

                            buyLevel.setBuyLotQuantity(buyLevel.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            buyLevel.setBuyShareOrderQuantity(buyLevel.getBuyShareOrderQueue().size());

                            buyLevel.setSellLotQuantity(buyLevel.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            buyLevel.setSellShareOrderQuantity(buyLevel.getSellShareOrderQueue().size());
                            shareService.updateSharePrice(share, -0.01);
                            log.info("Fiyat azaldı...");
                        }
                    }else {
                        if (buyLevel.getBuyShareOrderQueue().isEmpty() && !sellLevel.getSellShareOrderQueue().isEmpty()) {
                            shareService.updateSharePrice(share, -0.01);
                            log.info("Fiyat azaldı...");
                        }
                    }

                    log.info("ALIŞ tarafı ---> {} hissesi ALIŞ : {} SATIŞ : {} Alış sayısı : {} Lot sayısı : {}", share.getShareCode().getCode(), share.getBuyPrice(), share.getSellPrice(), buy, buyLevel.getBuyLotQuantity());
                } catch (InterruptedException ex) {

                } catch (NullPointerException npe) {

                }
            }
        });

        Thread processedSellLevelShareOrders = new Thread(() -> {
            Share share = Database.shareMap.get(ShareEnum.ALPHA);
            Level sellLevel;
            Level buyLevel;
            while (true) {
                try {
                    Thread.sleep(500);
                    buyLevel = share.getDepth().getLevelMap().get(share.getBuyPrice());
                    sellLevel = share.getDepth().getLevelMap().get(share.getSellPrice());
                    int sell = 0;

                    if (!sellLevel.getBuyShareOrderQueue().isEmpty() && !sellLevel.getSellShareOrderQueue().isEmpty()) {
                        ShareOrder buyShareOrder = sellLevel.getBuyShareOrderQueue().peek();
                        ShareOrder sellShareOrder = sellLevel.getSellShareOrderQueue().peek();

                        if (sellLevel.getSellLotQuantity() >= buyShareOrder.getLot()) {
                            tradeService.processedShareOrder(share, buyShareOrder, sellShareOrder);

                            if (buyShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                                //buyShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
                                sellLevel.getBuyShareOrderQueue().take();
                                log.info("{} idli share order silinmiş olmalı", buyShareOrder.getId());
                            }
                            if (sellShareOrder.getShareOrderOperationStatus().equals(ShareOrderOperationStatus.REMOVE)) {
                                //sellShareOrder.setShareOrderOperationStatus(ShareOrderOperationStatus.RECEIVED);
                                sellLevel.getSellShareOrderQueue().take();
                                log.info("{} idli share order silinmiş olmalı", sellShareOrder.getId());
                            }

                            sellLevel.setBuyLotQuantity(sellLevel.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            sellLevel.setBuyShareOrderQuantity(sellLevel.getBuyShareOrderQueue().size());

                            sellLevel.setSellLotQuantity(sellLevel.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            sellLevel.setSellShareOrderQuantity(sellLevel.getSellShareOrderQueue().size());
                            sell++;

                        } else {
                            //sellLevel.getBuyShareOrderQueue().put(buyShareOrder);
                            //sellLevel.getSellShareOrderQueue().put(sellShareOrder);

                            sellLevel.setBuyLotQuantity(sellLevel.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            sellLevel.setBuyShareOrderQuantity(sellLevel.getBuyShareOrderQueue().size());

                            sellLevel.setSellLotQuantity(sellLevel.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                            sellLevel.setSellShareOrderQuantity(sellLevel.getSellShareOrderQueue().size());
                            shareService.updateSharePrice(share, 0.01);
                            log.info("Fiyat arttı...");
                        }
                    }else {
                        if (!buyLevel.getBuyShareOrderQueue().isEmpty() && sellLevel.getSellShareOrderQueue().isEmpty()) {
                            shareService.updateSharePrice(share, 0.01);
                            log.info("Fiyat arttı...");
                        }
                    }

                    log.info("SATIŞ tarafı ---> {} hissesi ALIŞ : {} SATIŞ : {} Satış sayısı : {} Lot sayısı : {}", share.getShareCode().getCode(), share.getBuyPrice(), share.getSellPrice(), sell, sellLevel.getSellLotQuantity());
                } catch (InterruptedException ex) {

                } catch (NullPointerException npe) {

                }
            }
        });

        Thread openingPrice = new Thread(() -> {
            Share share = Database.shareMap.get(ShareEnum.ALPHA);
            shareService.setOpeningPrice(share, share.getStartPrice());
            Database.shareMap.get(ShareEnum.ALPHA).getDepth().getLevelMap(Database.shareMap.get(ShareEnum.ALPHA));
            ShareOrder shareOrder = null;
            try {
                while (!Database.shareOrderQueue.isEmpty()) {
                    shareOrder = Database.shareOrderQueue.take();
                    if (shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY)) {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getBuyShareOrderQueue().put(shareOrder);
                        level.setBuyLotQuantity(level.getBuyShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setBuyShareOrderQuantity(level.getBuyShareOrderQueue().size());
                    } else {
                        Level level = share.getDepth().getLevelMap().get(shareOrder.getPrice());
                        level.getSellShareOrderQueue().put(shareOrder);
                        level.setSellLotQuantity(level.getSellShareOrderQueue().stream().mapToInt(ShareOrder::getLot).sum());
                        level.setSellShareOrderQuantity(level.getSellShareOrderQueue().size());
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });

        a.start();
        b.start();
        c.start();
        d.start();
        e.start();
        f.start();
        g.start();
        h.start();
        i.start();
        j.start();
        k.start();
        l.start();
        Thread.sleep(5000);
        openingPrice.start();
        openingPrice.join();
        //Thread.sleep(20000);
        //shareService.shareSessionTypeChange(Database.shareMap.get(ShareEnum.ALPHA), ShareSessionType.NORMAL);
        processedBuyLevelShareOrders.start();
        processedSellLevelShareOrders.start();
    }

    private synchronized int idPlus() {
        return ++id;
    }

    private Customer createCustomer(String customerName) {
        Map<ShareEnum, Integer> haveShare = new HashMap<>();
        haveShare.put(ShareEnum.ALPHA, 1000);
        /*haveShare.put(ShareEnum.BETA, 1000);
        haveShare.put(ShareEnum.GAMMA, 100);
        haveShare.put(ShareEnum.DDD, 100);
        haveShare.put(ShareEnum.EEE, 100);*/
        Portfolio portfolio = new Portfolio(10000.0, haveShare);
        return new Customer(customerName, portfolio);
    }

}
