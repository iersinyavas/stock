package com.artsoft.stock.model;

import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Level {

    private ShareEnum shareCode;
    private Double price;
    private List<ShareOrder> shareOrderList = new CopyOnWriteArrayList<>();
    private Integer buyLotQuantity = 0;
    private Integer sellLotQuantity = 0;
    private Integer buyShareOrderQuantity = 0;
    private Integer sellShareOrderQuantity = 0;

    public Level(Double price) {
        this.price = price;
    }

}
