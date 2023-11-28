package net.mikoto.aozora.forward.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author mikoto
 * &#064;date 2023/11/28
 * Create for aozora
 */
@AllArgsConstructor
public enum AvailableRoute {
    /**
     * Get artwork route ajax/illust/
     */
    Artwork("/ajax/illust/")
    ;

    @Getter
    private String routeName;
}
