package com.bobo.spi;

/**
 * @author bobo
 * @Description:
 * @date 2018-12-03 11:41
 */
public class GoodPrinter implements Printer {
    @Override
    public void print() {
        System.out.println("你是个好人");
    }
}
