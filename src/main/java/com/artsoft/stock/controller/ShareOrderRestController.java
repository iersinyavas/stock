package com.artsoft.stock.controller;

import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.service.impl.ShareOrderServiceImpl;
import com.artsoft.stock.util.GeneralEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping("/stock/api/shareOrder")
public class ShareOrderRestController {

    @Autowired
    private ShareOrderServiceImpl shareOrderService;

}
