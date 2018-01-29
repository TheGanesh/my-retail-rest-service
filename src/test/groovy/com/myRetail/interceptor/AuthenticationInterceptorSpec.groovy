package com.myRetail.interceptor

import com.myRetail.exception.AuthenticationException
import org.springframework.core.env.Environment
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationInterceptorSpec extends Specification {

    AuthenticationInterceptor authenticationInterceptor

    Environment environment

    def setup() {

        environment = Mock(Environment)

        authenticationInterceptor = new AuthenticationInterceptor(
                environment: environment
        )
    }

    @Unroll
    def "authentication success scenarios"() {

        setup:

        HttpServletResponse response = Mock(HttpServletResponse)
        Object handler = Mock()

        HttpServletRequest request = Mock(HttpServletRequest)
        request.getHeader("X-CLIENT-ID") >> clientId
        request.getHeader("X-CLIENT-SECRET") >> clientSecret
        environment.getProperty("security.${clientId}.clientSecret") >> secureClientSecret

        when:

        Boolean result = authenticationInterceptor.preHandle(request, response, handler)

        then:

        result == expected

        where:

        clientId | clientSecret | secureClientSecret | expected
        "X"      | "XX"         | "XX"               | true
        "Y"      | "YY"         | "YY"               | true

    }

    @Unroll
    def "authentication failure scenarios"() {

        setup:

        HttpServletResponse response = Mock(HttpServletResponse)
        Object handler = Mock()

        HttpServletRequest request = Mock(HttpServletRequest)
        request.getHeader("X-CLIENT-ID") >> clientId
        request.getHeader("X-CLIENT-SECRET") >> clientSecret
        environment.getProperty("security.${clientId}.clientSecret") >> secureClientSecret

        when:

        authenticationInterceptor.preHandle(request, response, handler)

        then:

        AuthenticationException authenticationException = thrown()
        authenticationException.errorCode == "INVALID_CREDENTIALS"

        where:

        clientId | clientSecret | secureClientSecret | expected
        null     | null         | null               | false
        null     | "XX"         | "XX"               | false
        "X"      | null         | "XX"               | false
        "X"      | "XX"         | null               | false
        "X"      | "XX"         | "YY"               | false
        null     | "YY"         | "YY"               | false

    }


}
