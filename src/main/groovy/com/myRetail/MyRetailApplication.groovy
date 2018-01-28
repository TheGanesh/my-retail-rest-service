package com.myRetail

import com.myRetail.config.RootConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import([RootConfig])
@EnableCaching
public class MyRetailApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MyRetailApplication.class, args)
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyRetailApplication.class)
    }

}