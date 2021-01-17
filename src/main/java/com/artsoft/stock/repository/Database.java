package com.artsoft.stock.repository;

import com.artsoft.stock.dto.Customer;
import com.artsoft.stock.dto.Share;
import com.artsoft.stock.dto.ShareOrder;
import com.artsoft.stock.util.GeneralEnumeration.ShareEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    public static Map<ShareEnum, Share> shareMap = new ConcurrentHashMap<>();
    public static BlockingQueue<ShareOrder> shareOrderQueue = new ArrayBlockingQueue<>(100);
    public static Map<String, Customer> customerMap = new HashMap<>();

}
