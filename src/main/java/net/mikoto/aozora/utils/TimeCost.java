package net.mikoto.aozora.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

/**
 * @author mikoto
 * &#064;date 2024/2/2
 * Create for aozora
 */
public interface TimeCost {
    default double getTimeCost() {
        Date startDate = new Date();
        run();
        return (double) (new Date().getTime() - startDate.getTime()) / 1000.00;
    }
    void run();
}
