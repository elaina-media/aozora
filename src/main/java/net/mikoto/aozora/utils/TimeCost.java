package net.mikoto.aozora.utils;

import java.util.Date;

/**
 * @author mikoto
 * &#064;date 2024/2/2
 * Create for aozora
 */
public interface TimeCost {
    Date startDate = new Date();
    default double getTimeCost() {
        run();
        return (double) (new Date().getTime() - startDate.getTime()) / 1000.00;
    }
    void run();
}
