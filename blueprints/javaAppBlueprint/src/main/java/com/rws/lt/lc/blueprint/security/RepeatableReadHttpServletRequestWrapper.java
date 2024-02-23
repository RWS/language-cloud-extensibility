package com.rws.lt.lc.blueprint.security;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Class that wraps HttpServletRequest for reading input stream more then once. It's needed to read request body in
 * exception handler for logging.
 */
@Slf4j
public class RepeatableReadHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final String CONVERTING_ERROR = "Error converting input stream into string";

    private byte[] bodyAsBytes = new byte[]{};

    public RepeatableReadHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            String charsetName = getCharacterEncoding() == null ? StandardCharsets.UTF_8.name() : getCharacterEncoding();
            String body = IOUtils.toString(request.getInputStream(), charsetName);
            bodyAsBytes = body.getBytes(charsetName);
        } catch (IOException e) {
            LOGGER.error(CONVERTING_ERROR, e);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyAsBytes);
        return new ServletInputStream() {
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new RuntimeException("Not implemented");
            }

            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }
}
