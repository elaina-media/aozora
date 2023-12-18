package net.mikoto.aozora.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author mikoto
 * &#064;date 2023/11/18
 * Create for aozora
 */
@Configuration
@ConfigurationProperties(prefix = "aozora")
@Data
public class AozoraConfig {
    private String[] phpSessionId = new String[]{
            "85113018_zHi9tjL6yOcIQmDwBnqlXJwQMHYdCALj",
            "51815650_Ycp1iu1tn4J5qMlPTOBF7INx5QuISVEK",
            "61694469_9qCrZCnT6WeMvW2udRpezvIdIuYvRrJc",
            "100636494_1eRopdDuB6RpgFLdL8YqpMpYWfNYd1bZ",
            "100636547_kysxKqHDnEJFnSE8cqoHZsYAMcKbXpzl",
            "100638472_41bVRhseWeAR7EIW8KJjAqmh2HUfyN21",
            "100638532_EIHgXokUxXKS06W9w1GdsRMvZEObHE39",
            "100638622_6X9gqpXWN1deciQhCmRfxcqCULsPysSM",
            "100916322_qUikNqRqd3tyQaljYQ0IKK6aVpcIu96w",
            "100916389_Ou7APQ00NuPiJTgCjCmMHpIHEGhiNVEn",
            "100916420_Xu2XlbVG7ZwtLIZl7h5hY24wZItIoQw7",
            "100916458_qIAe5aSLIhztoiwbSFCxHhinsf2Lkz4i",
            "100916473_lmyfaP73DQtY4cWG0K91601xI2i13BGo",
            "100916501_z7xLABvGX2LBGUA2gaOFsKCd2KYwXill",
            "100916521_669daslJ21TVM2biH9tCNHqgJw0mxLXk"
    };
}
