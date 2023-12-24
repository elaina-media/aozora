package net.mikoto.aozora.model.multipleTask;

/**
 * @author mikoto
 * &#064;date 2023/12/3
 * Create for aozora
 */
public interface MultipleTask {
    SingleTask popTask();
    int getLoopCount();
    long getTaskId();
}
