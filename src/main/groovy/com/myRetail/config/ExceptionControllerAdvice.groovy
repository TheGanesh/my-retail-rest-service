package com.myRetail.config

import com.myRetail.exception.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.client.RestClientException

@CompileStatic
@ControllerAdvice
@Slf4j
class ExceptionControllerAdvice {

    @Autowired
    @Qualifier('messageSource')
    private MessageSource messageSource

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorResponse handleInvalidPriceUpdateException(InvalidPriceUpdateException invalidPriceUpdateException) {
        return handleApplicationException(invalidPriceUpdateException)
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    ErrorResponse handleAuthenticationException(AuthenticationException authenticationException) {
        return handleApplicationException(authenticationException)
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    ErrorResponse handlePreconditionFailedException(PreconditionFailedException preconditionFailedException) {
        return handleApplicationException(preconditionFailedException)
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        return handleApplicationException(resourceNotFoundException)
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    ErrorResponse handleRestClientException(RestClientException restClientException) {
        log.error("action=handleRestClientException", restClientException)
        return new ErrorResponse(
                errorCode: "INVALID_PRODUCT_ID",
                errorMessage: messageSource.getMessage("INVALID_PRODUCT_ID", null, LocaleContextHolder.getLocale())
        )
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ErrorResponse handleRuntimeException(RuntimeException exception) {
        log.error("action=handleRuntimeException", exception)
        return new ErrorResponse(
                errorCode: "INTERNAL_EXCEPTION",
                errorMessage: messageSource.getMessage("INTERNAL_EXCEPTION", null, LocaleContextHolder.getLocale())
        )
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.error("action=handleHttpMessageNotReadableException", exception)
        return new ErrorResponse(
                errorCode: "INVALID_REQUEST",
                errorMessage: messageSource.getMessage("INVALID_REQUEST", null, LocaleContextHolder.getLocale())
        )
    }

    ErrorResponse handleApplicationException(GenericException exception) {
        log.error("action=handle${exception.class.simpleName}", exception)
        return new ErrorResponse(
                errorCode: exception.errorCode,
                errorMessage: messageSource.getMessage(exception.errorCode, exception.params, LocaleContextHolder.getLocale())
        )
    }
}