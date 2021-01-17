package com.artsoft.stock.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Random;

@Data
@AllArgsConstructor
public class MathOperation {

    private static Random random = new Random();

    public static int doubleToIntegerConvert(double value){
        return (int)(value*100);
    }

    public static double integerToDoubleConvert(int value){
        return (double)value/100;
    }

    public static Double max(double price){
        double value = price + integerToDoubleConvert(doubleToIntegerConvert(price) / 10);
        return parseDouble(decimalFormat().format(value));
    }

    public static Double min(double price){
        double value = price - integerToDoubleConvert(doubleToIntegerConvert(price) / 10);
        return parseDouble(decimalFormat().format(value));
    }

    public synchronized static Double choosePrice(double price){
        double value = integerToDoubleConvert(random.nextInt(doubleToIntegerConvert(max(price) - min(price)) + 1)) + min(price);
        return parseDouble(decimalFormat().format(value));
    }

    public static Double arrangeDouble(double value){
        return parseDouble(decimalFormat().format(value));
    }

    private synchronized static Double parseDouble(String format){
        return Double.parseDouble(format(format));
    }

    private static String format(String format){
        return format.replace(",", ".");
    }

    private static DecimalFormat decimalFormat(){
        DecimalFormat decimalFormat = new DecimalFormat("####.##");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(6);
        return decimalFormat;
    }

}
