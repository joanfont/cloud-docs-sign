package com.joanfont.clouddocssign.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/images/**",
                "/css/**",
                "/js/**",
                "/fonts/**",
                "/vendor/**"
        ).addResourceLocations(
                "classpath:/META-INF/resources/webjars/",
                "classpath:/static/images/",
                "classpath:/static/css/",
                "classpath:/static/js/",
                "classpath:/static/fonts/",
                "classpath:/static/vendor/"
        );
    }

}
