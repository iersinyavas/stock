package com.artsoft.stock.config;

import com.artsoft.stock.util.CustomerUtil;
import com.artsoft.stock.util.ShareOrderUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfig {

    @Bean
    public ShareOrderUtil shareUtil(){
        return new ShareOrderUtil();
    }

    @Bean
    public CustomerUtil customerUtil(){
        return new CustomerUtil();
    }

}
