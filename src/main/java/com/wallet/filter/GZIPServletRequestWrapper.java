package com.wallet.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class GZIPServletRequestWrapper extends HttpServletRequestWrapper {
    public GZIPServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new GZIPServletInputStream(super.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new GZIPServletInputStream(super.getInputStream())));
    }

    private class GZIPServletInputStream extends ServletInputStream {
        private InputStream input;

        public GZIPServletInputStream(InputStream input) throws IOException {
            this.input = new GZIPInputStream(input);
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return input.available() == 0;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
