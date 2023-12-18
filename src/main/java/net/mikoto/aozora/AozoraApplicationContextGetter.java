package net.mikoto.aozora;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2023/11/17
 * Create for aozora
 */
@Getter
@Component
public class AozoraApplicationContextGetter implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取bean
     *
     * @param clz service对应的类
     * @return Object bean的实例对象
     */
    public <T> @NotNull T getBean(Class<T> clz) throws BeansException {
        return applicationContext.getBean(clz);
    }
}
