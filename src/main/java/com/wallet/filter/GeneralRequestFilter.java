package com.wallet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;

/**
 * Created by zxqiu on 4/24/17.
 */
public class GeneralRequestFilter implements Filter {
	private static final Logger logger_ = LoggerFactory.getLogger(GeneralRequestFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		logger_.info("request filter enforced");

		HttpServletRequest  httpRequest  = (HttpServletRequest)  servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		filterChain.doFilter(new XSSRequestWrapper(httpRequest), servletResponse);
	}

	@Override
	public void destroy() {
	}
}
