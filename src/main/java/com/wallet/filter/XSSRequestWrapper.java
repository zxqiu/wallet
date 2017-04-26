package com.wallet.filter;

import com.wallet.utils.misc.XSSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger logger_ = LoggerFactory.getLogger(XSSRequestWrapper.class);
    private XSSFilter xssFilter = null;

    public XSSRequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
        xssFilter = new XSSFilter();
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);

        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = xssFilter.stripXSS(values[i]);
        }

        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        logger_.info("parameter : " + parameter);
        logger_.info("value : " + value);
        return xssFilter.stripXSS(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        logger_.info("name : " + name);
        logger_.info("value : " + value);
        return xssFilter.stripXSS(value);
    }
}
