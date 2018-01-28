package com.myRetail.config

import com.myRetail.interceptor.AuthenticationInterceptor
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMapping
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMappingCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@CompileStatic
@Configuration
@Import(value = [SwaggerConfig])
class RootConfig extends WebMvcConfigurerAdapter {

    @Autowired
    AuthenticationInterceptor authenticationInterceptor

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/")
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
    }


    @Override
    void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns('/**')
                .excludePathPatterns('/health','/error','/swagger-resources/**','/v2/api-docs/**')
    }

    @Bean
    ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource()
        source.addBasenames('message/messages',
                'com.bestbuy.commerce.checkout.validator.messages',
                'com.bestbuy.commerce.checkout.validator.UserMessages')
        return source
    }
    @Bean
    EndpointHandlerMappingCustomizer mappingCustomizer() {
        return new EndpointHandlerMappingCustomizer() {
            @Override
            void customize(EndpointHandlerMapping mapping) {
                mapping.setInterceptors(authenticationInterceptor)
            }
        }
    }


}
