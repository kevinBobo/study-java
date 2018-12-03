package com.bobo.spi;

import java.util.ServiceLoader;

/**
 * @author bobo
 * @Description:
 * @date 2018-12-03 11:46
 */
public class MainApp {

    public static void main(String[] args) {
        ServiceLoader<Printer> loader = ServiceLoader.load(Printer.class);
        for (Printer printer : loader) {
            printer.print();
        }
    }
}
