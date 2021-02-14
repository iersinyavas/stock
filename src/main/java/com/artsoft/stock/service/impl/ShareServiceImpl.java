package com.artsoft.stock.service.impl;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.service.ShareService;
import com.artsoft.stock.util.GeneralEnumeration;
import com.artsoft.stock.util.MathOperation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class ShareServiceImpl implements ShareService {

    private static int max;
    private static int min;

    @Override
    public void updateSharePrice(Share share, Double change) {
        synchronized (this){
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
    public void setShareStartPrice(Share share){
        synchronized (this){
            int max = (int)(MathOperation.arrangeDouble(MathOperation.max(share.getStartPrice())) * 100) + 1;
            int min = (int)(MathOperation.arrangeDouble(MathOperation.min(share.getStartPrice())) * 100);
            share.setBuyPrice(MathOperation.arrangeDouble((double) (new Random().nextInt(max - min) + min)/100));
            share.setSellPrice(MathOperation.arrangeDouble(share.getBuyPrice() + 0.01));
        }
    }

}
