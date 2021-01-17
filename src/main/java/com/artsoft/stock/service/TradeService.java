package com.artsoft.stock.service;

import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.model.Level;

import java.util.List;
import java.util.Map;

public interface TradeService {

    void matchShareOrder(Share share);
}
