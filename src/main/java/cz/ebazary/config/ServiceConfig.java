package cz.ebazary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.gcwilliams.jodatime.thymeleaf.JodaTimeDialect;

import javax.validation.Validator;

@Configuration
public class ServiceConfig {

    @Bean
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public JodaTimeDialect jodaTimeDialect() {
        return new JodaTimeDialect();
    }

}
