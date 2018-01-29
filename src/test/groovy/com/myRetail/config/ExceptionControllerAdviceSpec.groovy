package com.myRetail.config

import com.myRetail.exception.*
import org.springframework.context.MessageSource
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindingResult
import org.springframework.web.client.RestClientException
import spock.lang.Specification
import spock.lang.Unroll

class ExceptionControllerAdviceSpec extends Specification {

    ExceptionControllerAdvice exceptionControllerAdvice
    MessageSource messageSource

    def setup() {

        messageSource = Mock(MessageSource)

        exceptionControllerAdvice = new ExceptionControllerAdvice(
                messageSource: messageSource
        )
    }

    @Unroll
    def "handleInvalidPriceUpdateException"() {

        setup:

        BindingResult bindingResult = Mock(BindingResult)

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleInvalidPriceUpdateException(new InvalidPriceUpdateException(bindingResult))

        then:

        errorResponse.errorCode == "INVALID_PRICE_UPDATE"
    }

    @Unroll
    def "handleAuthenticationException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleAuthenticationException(new AuthenticationException("XX"))

        then:

        errorResponse.errorCode == "INVALID_CREDENTIALS"
    }

    @Unroll
    def "handlePreconditionFailedException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handlePreconditionFailedException(new PreconditionFailedException(1L))

        then:

        errorResponse.errorCode == "PRODUCT_ID_MISMATCH"
    }

    @Unroll
    def "handleResourceNotFoundException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleResourceNotFoundException(new ResourceNotFoundException("1"))

        then:

        errorResponse.errorCode == "PRODUCT_NOT_FOUND"
    }

    @Unroll
    def "handleRestClientException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleRestClientException(new RestClientException("error"))

        then:

        errorResponse.errorCode == "INVALID_PRODUCT_ID"
    }

    @Unroll
    def "handleRuntimeException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleRuntimeException(new RuntimeException("error"))

        then:

        errorResponse.errorCode == "INTERNAL_EXCEPTION"
    }

    @Unroll
    def "handleHttpMessageNotReadableException"() {

        when:

        ErrorResponse errorResponse = exceptionControllerAdvice.handleHttpMessageNotReadableException(new HttpMessageNotReadableException("error"))

        then:

        errorResponse.errorCode == "INVALID_REQUEST"
    }

}
