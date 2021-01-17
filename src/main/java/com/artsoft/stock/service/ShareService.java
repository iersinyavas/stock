package com.artsoft.stock.service;

import com.artsoft.stock.dto.Share;

public interface ShareService {

    void updateSharePrice(Share share, Double change);
    void setShareStartPrice(Share share);
    void updateShare(Share share);
}
