package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.MathOperation;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {
    @Override
    public void updateSharePrice(Share share, Double change) {
        synchronized (this){
            share.setBuyPrice(MathOperation.arrangeDouble(share.getBuyPrice() + change));
            share.setSellPrice(MathOperation.arrangeDouble(share.getSellPrice() + change));
        }
    }

    @Override
    public void setShareStartPrice(Share share){
        synchronized (this){
            int max = (int)(MathOperation.arrangeDouble(MathOperation.max(share.getStartPrice())) * 100) + 1;
            int min = (int)(MathOperation.arrangeDouble(MathOperation.min(share.getStartPrice())) * 100);
            share.setBuyPrice(MathOperation.arrangeDouble((double) (new Random().nextInt(max - min) + min)/100));
            share.setSellPrice(MathOperation.arrangeDouble(share.getBuyPrice() + 0.01));
        }
    }

    @Override
    public void updateShare(Share share) {
        Level level = share.getDepth().getLevelMap().get(share.getBuyPrice());
        level.setBuyLotQuantity(
                level.getShareOrderList().stream()
                        .filter(shareOrder -> shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY))
                        .mapToInt(shareOrder -> shareOrder.getLot()).sum());

        level.setSellLotQuantity(
                level.getShareOrderList().stream()
                        .filter(shareOrder -> shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL))
                        .mapToInt(shareOrder -> shareOrder.getLot()).sum());

        level.setBuyShareOrderQuantity((int)
                level.getShareOrderList().stream()
                        .filter(shareOrder -> shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.BUY))
                        .count());

        level.setSellShareOrderQuantity((int)
                level.getShareOrderList().stream()
                        .filter(shareOrder -> shareOrder.getShareOrderStatus().equals(GeneralEnumeration.ShareOrderStatus.SELL))
                        .count());
    }

}
