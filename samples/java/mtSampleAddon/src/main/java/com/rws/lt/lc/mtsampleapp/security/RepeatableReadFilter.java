package com.rws.lt.lc.mtsampleapp.security;


import com.rws.lt.lc.mtsampleapp.web.ServletFilterOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(ServletFilterOrder.ACCOUNT_ID_FILTER_ORDER)
public class RepeatableReadFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new RepeatableReadHttpServletRequestWrapper(httpServletRequest), httpServletResponse);
    }

}
