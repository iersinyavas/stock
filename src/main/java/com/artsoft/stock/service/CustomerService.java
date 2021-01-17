package com.artsoft.stock.service;

import com.artsoft.stock.dto.Customer;

public interface CustomerService {

    void payEmployeeSalary(Customer customer);
    void updatePortfolio(Customer customer);
}
