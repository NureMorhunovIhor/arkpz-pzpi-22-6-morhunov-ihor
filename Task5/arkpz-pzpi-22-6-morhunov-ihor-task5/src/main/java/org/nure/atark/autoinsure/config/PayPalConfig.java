package org.nure.atark.autoinsure.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalConfig {


    @Bean
    public APIContext apiContext() {
        return new APIContext(
                "ARNqcWuZ4K44S0bRvpIoLBTwEl8XnqojXA-UCt5_L9g5xZDG7TAiZGwsxA3kekSe7J1WpXWsHnESEeNV",
                "EC3HVnzTy_Xn7UMeqtBF38VuIYsHehZxI7bbr5uFq-GMdSSsY4BSb2mo-G3C4Ys0s9Q4glXYmmWu5T1t",
                "sandbox"
        );
    }

}

