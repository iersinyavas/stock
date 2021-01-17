package com.artsoft.stock.util;

import com.artsoft.stock.repository.Database;
import org.springframework.stereotype.Component;

@Component
public class GeneralEnumeration {

    protected GeneralEnumeration(){};

    public enum ShareEnum{
        AAA, BBB/*, CCC, DDD, EEE*/;

        String code;
        private double price;

        private ShareEnum(){
            this.code = this.name();
        }

        public String getCode(){
            return this.code;
        }

        public double getPrice(){
            return MathOperation.choosePrice(Database.shareMap.get(ShareEnum.values()[this.ordinal()]).getStartPrice());
        }
    }

    public enum ShareOrderStatus {

        BUY, SELL;

    }

    public enum ShareOrderOperationStatus{
        SENT, RECEIVED, PROCESSING, REMOVE;
    }

    public enum BuyOrSell{
        BUY, SELL;
    }

}
