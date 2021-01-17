package com.artsoft.stock.util.exception;

public class InsufficientBalanceException extends Exception {

    @Override
    public String getMessage() {
        return "Bakiyeniz Yetersiz";
    }
}
