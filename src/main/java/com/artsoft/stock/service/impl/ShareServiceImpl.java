package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.GeneralConstant;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareSessionType;
import com.artsoft.stock.util.GeneralEnumeration.DirectionFlag;
import com.artsoft.stock.util.GeneralEnumeration.ShareSessionType;
import com.artsoft.stock.util.MathOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
@Slf4j
public class ShareServiceImpl implements ShareService {

    private static int max;
    private static int min;

    @Override
    public void updateSharePrice(Share share, Double change) {
        synchronized (this) {
            /*Map<Double, Level> levelMap = share.getDepth().getLevelMap();
            while (levelMap.get(share.getBuyPrice() + change).getBuyShareOrderQueue().isEmpty())*/

            if (MathOperation.arrangeDouble(MathOperation.arrangeDouble(share.getBuyPrice() + change)) >= MathOperation.min(share.getStartPrice())
                    && MathOperation.arrangeDouble(MathOperation.arrangeDouble(share.getSellPrice() + change)) <= MathOperation.max(share.getStartPrice())) {

                share.setBuyPrice(MathOperation.arrangeDouble(share.getBuyPrice() + change));
                share.setSellPrice(MathOperation.arrangeDouble(share.getSellPrice() + change));
            }
        }
    }

    @Override
    public void shareSessionTypeChange(Share share, ShareSessionType shareSessionType) {
        share.setShareSessionType(shareSessionType);
    }

    /*@Override
    public void setRandomShareStartPrice(Share share){
        synchronized (this){
            int max = (int)(MathOperation.arrangeDouble(MathOperation.max(share.getStartPrice())) * 100) + 1;
            int min = (int)(MathOperation.arrangeDouble(MathOperation.min(share.getStartPrice())) * 100);
            share.setBuyPrice(MathOperation.arrangeDouble((double) (new Random().nextInt(max - min) + min)/100));
            share.setSellPrice(MathOperation.arrangeDouble(share.getBuyPrice() + 0.01));
        }
    }*/

    @Override
    public void setShareBuyAndSellPrice(Share share, Double change) {
        if (MathOperation.arrangeDouble(MathOperation.arrangeDouble(share.getBuyPrice() + change)) >= MathOperation.min(share.getStartPrice())
                && MathOperation.arrangeDouble(MathOperation.arrangeDouble(share.getSellPrice() + change)) <= MathOperation.max(share.getStartPrice())) {

            share.setBuyPrice(MathOperation.arrangeDouble(share.getBuyPrice() + change));
            share.setSellPrice(MathOperation.arrangeDouble(share.getSellPrice() + change));
        }
    }

    private Double startSharePrice(Share share, Double startPrice, DirectionFlag directionFlag) {
        synchronized (this) {
            Double number = MathOperation.arrangeDouble(startPrice);
            if (directionFlag.equals(DirectionFlag.DOWN)) {
                if (number <= share.getMin()) {
                    share.setBuyPrice(MathOperation.arrangeDouble(startPrice));
                    share.setSellPrice(MathOperation.arrangeDouble(share.getBuyPrice() + 0.01));
                } else {
                    share.setBuyPrice(MathOperation.arrangeDouble(startPrice - 0.01));
                    share.setSellPrice(MathOperation.arrangeDouble(share.getBuyPrice() + 0.01));
                }
                return share.getBuyPrice();
            } else {
                if (number >= share.getMax()) {
                    share.setSellPrice(MathOperation.arrangeDouble(startPrice));
                    share.setBuyPrice(MathOperation.arrangeDouble(share.getSellPrice() - 0.01));
                } else {
                    share.setSellPrice(MathOperation.arrangeDouble(startPrice + 0.01));
                    share.setBuyPrice(MathOperation.arrangeDouble(share.getSellPrice() - 0.01));
                }
                return share.getSellPrice();
            }
        }
    }

    @Override
    public void setOpeningPrice(Share share, Double afterStartPrice) {

        while (share.getShareSessionType().equals(ShareSessionType.OPENING) && Database.shareOrderQueue.size() <= GeneralConstant.QUEUE_SIZE) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Double singlePrice = afterStartPrice;
            Map<ShareOrderStatus, List<ShareOrder>> collect = Database.shareOrderQueue.stream().collect(Collectors.groupingBy(ShareOrder::getShareOrderStatus));

            int receivableLot = collect.get(ShareOrderStatus.BUY).stream()
                    .filter(shareOrder -> shareOrder.getPrice() >= singlePrice)
                    .mapToInt(ShareOrder::getLot)
                    .sum();

            int salableLot = collect.get(ShareOrderStatus.SELL).stream()
                    .filter(shareOrder -> shareOrder.getPrice() <= singlePrice)
                    .mapToInt(ShareOrder::getLot)
                    .sum();

            if (receivableLot > salableLot) {
                //share.setStartPrice(singlePrice);
                afterStartPrice = startSharePrice(share, afterStartPrice, DirectionFlag.UP);
                log.info("Alınabilir lot sayısı : {} ----- Satılabilir lot sayısı : {}, Fiyat : {}", receivableLot, salableLot, afterStartPrice);
                continue;
            } else if (receivableLot < salableLot) {
                //share.setStartPrice(singlePrice);
                afterStartPrice = startSharePrice(share, afterStartPrice, DirectionFlag.DOWN);
                log.info("Alınabilir lot sayısı : {} ----- Satılabilir lot sayısı : {}, Fiyat : {}", receivableLot, salableLot, afterStartPrice);
                continue;
            }
            log.info("Alınabilir lot sayısı : {} ----- Satılabilir lot sayısı : {}, Fiyat : {}", receivableLot, salableLot, afterStartPrice);
        }
        share.setShareSessionType(ShareSessionType.NORMAL);
        log.info("Alış fiyatı : {} Satış fiyatı : {}", share.getBuyPrice(), share.getSellPrice());

    }

}
