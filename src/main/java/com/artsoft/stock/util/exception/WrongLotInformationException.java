package com.artsoft.stock.util.exception;

public class WrongLotInformationException extends Exception {

    @Override
    public String getMessage() {
        return "1 den az lot girilemez.";
    }
}
