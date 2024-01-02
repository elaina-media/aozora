package net.mikoto.aozora.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mikoto
 * &#064;date 2024/1/2
 * Create for aozora
 */

@Data
@Component
@ConfigurationProperties(prefix = "pixiv-client")
public class DynamicConfig {
    String[] cookies = new String[] {
            "100916322_qUikNqRqd3tyQaljYQ0IKK6aVpcIu96w",
            "100916389_Ou7APQ00NuPiJTgCjCmMHpIHEGhiNVEn",
            "100916420_Xu2XlbVG7ZwtLIZl7h5hY24wZItIoQw7",
            "100916458_qIAe5aSLIhztoiwbSFCxHhinsf2Lkz4i",
            "100916473_lmyfaP73DQtY4cWG0K91601xI2i13BGo",
            "100916501_z7xLABvGX2LBGUA2gaOFsKCd2KYwXill",
            "100916521_669daslJ21TVM2biH9tCNHqgJw0mxLXk"
    };
    String cpsVersion = "a48f2f681629909b885608393916b81989accf5b";
}
