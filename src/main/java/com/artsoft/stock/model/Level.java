package com.artsoft.stock.model;

import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.util.GeneralEnumeration.ShareOrderStatus;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import lombok.*;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Level {

    private ShareEnum shareCode;
    private ShareOrderStatus levelShareOrderStatus;
    private Double price;
    private BlockingQueue<ShareOrder> buyShareOrderQueue = new LinkedBlockingQueue();
    private BlockingQueue<ShareOrder> sellShareOrderQueue = new LinkedBlockingQueue();
    private BlockingQueue<ShareOrder> remainingShareOrderQueue = new LinkedBlockingQueue();
    private Integer buyLotQuantity = 0;
    private Integer sellLotQuantity = 0;
    private Integer buyShareOrderQuantity = 0;
    private Integer sellShareOrderQuantity = 0;

    public Level(Double price) {
        this.price = price;
    }

}
