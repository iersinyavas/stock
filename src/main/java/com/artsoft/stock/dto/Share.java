package com.artsoft.stock.dto;

import com.artsoft.stock.model.Depth;
import com.artsoft.stock.repository.Database;
import com.artsoft.stock.util.GeneralEnumeration.ShareSessionType;
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
    private ShareSessionType shareSessionType;
    private Depth depth = new Depth();
    private Double max;
    private Double min;

    public Share(ShareEnum shareCode, Double startPrice, Double buyPrice, Double sellPrice, ShareSessionType shareSessionType) {
        this.shareCode = shareCode;
        this.startPrice = startPrice;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.shareSessionType = shareSessionType;
    }

    public Share(ShareEnum shareCode, Double startPrice, ShareSessionType shareSessionType) {
        this.shareCode = shareCode;
        this.startPrice = startPrice;
        this.shareSessionType = shareSessionType;
    }
}
