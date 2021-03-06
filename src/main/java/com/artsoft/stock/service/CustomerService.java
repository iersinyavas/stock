package com.artsoft.stock.service;

import com.artsoft.stock.dto.Customer;

public interface CustomerService {

    Customer createCustomer(String customerName);
    void payEmployeeSalary(Customer customer);
    void updatePortfolio(Customer customer);
}
