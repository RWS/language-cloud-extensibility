package com.rws.lt.lc.mtsampleapp.tracing;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TracingFilterTest {
    @Spy
    private TracingHeadersUtils tracingHeadersUtils;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private FilterChain filterChainMock;

    @Mock(lenient = true)
    private HttpServletRequest httpServletRequestMock;

    @Mock(lenient = true)
    private HttpServletResponse httpServletResponseMock;

    @Spy
    @InjectMocks
    private TracingFilter filter;

    @Test
    public void addNewlyGeneratedTraceId() throws Exception {
        doReturn("GET").when(httpServletRequestMock).getMethod();
        doReturn(new StringBuffer("http://host/abc")).when(httpServletRequestMock).getRequestURL();
        doReturn(null).when(httpServletRequestMock).getQueryString();
        doReturn(null).when(httpServletRequestMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        doReturn(null).when(httpServletResponseMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        doAnswer(invocation -> {

            assertNotNull(TracingLocalContext.getTracingId());
            return null;

        }).when(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);

        filter.init(filterConfig);
        filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);
        verify(httpServletResponseMock).addHeader(eq(TracingHeadersUtils.TRACING_ID_HEADER), notNull(String.class));
        assertNull(TracingLocalContext.getTracingId());
    }

    @Test
    public void addTraceIdFromRequest() throws Exception {
        String traceId = "9d12eae3-ed2d-47c5-8619-c7ee1c4a64cc";

        doReturn("GET").when(httpServletRequestMock).getMethod();
        doReturn(new StringBuffer("http://host/abc")).when(httpServletRequestMock).getRequestURL();
        doReturn(null).when(httpServletRequestMock).getQueryString();
        doReturn(traceId).when(httpServletRequestMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        doReturn(null).when(httpServletResponseMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);

        doAnswer(invocation -> {

            assertEquals(traceId, TracingLocalContext.getTracingId());
            return null;

        }).when(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);

        filter.init(filterConfig);
        filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);
        verify(httpServletResponseMock).addHeader(eq(TracingHeadersUtils.TRACING_ID_HEADER), eq(traceId));
        assertNull(TracingLocalContext.getTracingId());
    }

    @Test
    public void addTraceIdWhenError() throws Exception {
        doReturn("GET").when(httpServletRequestMock).getMethod();
        doReturn(new StringBuffer("http://host/abc")).when(httpServletRequestMock).getRequestURL();
        doReturn(null).when(httpServletRequestMock).getQueryString();
        doReturn(null).when(httpServletRequestMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        doReturn(null).when(httpServletResponseMock).getHeader(TracingHeadersUtils.TRACING_ID_HEADER);
        doAnswer(invocation -> {

            assertNotNull(TracingLocalContext.getTracingId());
            throw new ServletException("boom");

        }).when(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);

        filter.init(filterConfig);
        try {
            filter.doFilter(httpServletRequestMock, httpServletResponseMock, filterChainMock);
            fail("Expected ServletException");
        } catch (ServletException expected) {
        }

        verify(filterChainMock).doFilter(httpServletRequestMock, httpServletResponseMock);
        verify(httpServletResponseMock).addHeader(eq(TracingHeadersUtils.TRACING_ID_HEADER), notNull(String.class));
        assertNull(TracingLocalContext.getTracingId());
    }


    @After
    public void cleanupRequestLocalContext() {
        TracingLocalContext.clean();
    }

}
