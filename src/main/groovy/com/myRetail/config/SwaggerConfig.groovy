package com.myRetail.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

import static com.google.common.collect.Lists.newArrayList

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    public Docket swggerDocket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.myRetail"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .globalResponseMessage(RequestMethod.POST,
                newArrayList(new ResponseMessageBuilder()
                        .code(500)
                        .message("General 400/500 exceptions")
                        .responseModel(new ModelRef("Error Contract"))
                        .build()))
                .globalOperationParameters(
                newArrayList(
                        new ParameterBuilder()
                                .name("id")
                                .defaultValue("13860428")
                                .modelRef(new ModelRef("string"))
                                .parameterType("path")
                                .required(true)
                                .build(),
                        new ParameterBuilder()
                                .name("X-CLIENT-ID")
                                .defaultValue("XXXX")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .build(),
                        new ParameterBuilder()
                                .name("X-CLIENT-SECRET")
                                .defaultValue("9x44bun4fc9b96ugswhhwfe4p6mb3qe6")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(true)
                                .build()

                ))

    }

    private ApiInfo apiInfo() {
        new ApiInfo("MyRetail Product Information API", "Provides details about product information including its current retails price [Source Code](https://github.com/TheGanesh/myretail-rest-service)", "", "", new Contact("", "", "kandisa.ganesh@gmail.com"), "", "")
    }
}