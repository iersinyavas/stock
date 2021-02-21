package com.artsoft.stock.service;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.util.GeneralEnumeration;

public interface ShareService {

    void updateSharePrice(Share share, Double change);
    //void setRandomShareStartPrice(Share share);
    void setOpeningPrice(Share share, Double startPrice);
    void shareSessionTypeChange(Share share, GeneralEnumeration.ShareSessionType shareSessionType);
    void setShareBuyOrSellPrice(Share share);
}
