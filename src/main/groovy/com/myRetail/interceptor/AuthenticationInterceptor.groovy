package com.myRetail.interceptor

import com.myRetail.exception.AuthenticationException
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@Component
@CompileStatic
class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    Environment environment

    @Override
    boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String clientId = request.getHeader("X-CLIENT-ID")
        String clientSecret = request.getHeader("X-CLIENT-SECRET")
        String secureClientSecret = environment.getProperty("security.${clientId}.clientSecret")

        if (!clientId || !clientSecret || clientSecret != secureClientSecret) {
            throw new AuthenticationException(clientSecret)
        }

        MDC.put("clientId", clientId)
        MDC.put("requestId", request.getHeader("X_REQUEST_ID") ?: UUID.randomUUID().toString())

        MDC.put("REQUEST_TIMER_START", Long.valueOf(System.currentTimeMillis()).toString())

        return true
    }

    @Override
    void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        response.addHeader(HttpHeaders.CACHE_CONTROL, "no-cache,no-store")
        response.addHeader(HttpHeaders.EXPIRES, '0')

        Long timerStartTime = MDC.get("REQUEST_TIMER_START") as Long
        String stats = ''
        if (timerStartTime) {
            stats = "roundTripMilliSec=${System.currentTimeMillis() - timerStartTime}, path=${request.requestURI}, method=${request.method}, httpStatus=${response.status}"
        }

        log.info(stats)
        MDC.clear()
    }

}
