package com.bobo.spi;

/**
 * @author bobo
 * @Description:
 * @date 2018-12-03 11:42
 */
public class BadPrinter implements Printer {

    @Override
    public void print() {
        System.out.println("发现了一个坏人!");
    }
}
