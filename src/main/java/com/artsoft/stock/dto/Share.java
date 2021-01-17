package com.artsoft.stock.dto;

import com.artsoft.stock.model.Depth;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Share {

    private ShareEnum shareCode;
    private Double startPrice;
    private Double buyPrice;
    private Double sellPrice;
    private Depth depth = new Depth();

    public Share(ShareEnum shareCode, Double startPrice, Double buyPrice, Double sellPrice) {
        this.shareCode = shareCode;
        this.startPrice = startPrice;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public Share(ShareEnum shareCode, Double startPrice) {
        this.shareCode = shareCode;
        this.startPrice = startPrice;
    }
}
