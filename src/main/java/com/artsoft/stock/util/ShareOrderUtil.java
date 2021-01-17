package com.artsoft.stock.util;

import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ShareOrderUtil {

    Random random = new Random();

    public ShareOrderStatus getShareOrderStatus(){
        return ShareOrderStatus.values()[random.nextInt(ShareOrderStatus.values().length)];
    }

    public ShareEnum getShare(){
        return ShareEnum.values()[random.nextInt(ShareEnum.values().length)];
    }

    private double buyMax(Map<Double, Integer> buyShareOrderMap, double nowPrice){
        try {
            return buyShareOrderMap.entrySet().stream()
                    .filter(doubleIntegerEntry -> doubleIntegerEntry.getValue() > 0)
                    .map(Map.Entry::getKey).max(Double::compare).get();
        }catch (Exception e){
            return nowPrice;
        }
    }

    private double sellMin(Map<Double, Integer> sellShareOrderMap, double sellPrice){
        try {
            return sellShareOrderMap.entrySet().stream()
                    .filter(doubleIntegerEntry -> doubleIntegerEntry.getValue() > 0)
                    .map(Map.Entry::getKey).min(Double::compare).get();
        }catch (Exception e){
            return sellPrice;
        }
    }

    private int sellTotalLot(Map<Double, Integer> sellShareOrderMap){
        return sellShareOrderMap.entrySet().stream().mapToInt(value -> value.getValue()).sum();
    }

    private int buyTotalLot(Map<Double, Integer> buyShareOrderMap){
        return buyShareOrderMap.entrySet().stream().mapToInt(value -> value.getValue()).sum();
    }

}
