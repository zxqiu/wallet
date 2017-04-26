package com.wallet.filter;

import com.wallet.utils.misc.XSSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zxqiu on 4/24/17.
 */
public class GeneralRequestFilter implements Filter {
	private static final Logger logger_ = LoggerFactory.getLogger(GeneralRequestFilter.class);
	private XSSFilter xssFilter = null;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		xssFilter = new XSSFilter();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		logger_.info("request filter enforced");

		HttpServletRequest httpRequest  = (HttpServletRequest)  servletRequest;
		Map<String, String[]> parameterMap = httpRequest.getParameterMap();
		logger_.info(httpRequest.getRequestURI());
		logger_.info(httpRequest.getRequestURL().toString());
		logger_.info(httpRequest.getPathTranslated());
		logger_.info(httpRequest.getPathInfo());

		for (Map.Entry entry : parameterMap.entrySet()) {
			String vals[] = (String[]) entry.getValue();
			String stripped_vals[] = new String[vals.length];
			for (int i = 0; i < vals.length; i++) {
				stripped_vals[i] = xssFilter.stripXSS(vals[i]);
				logger_.info("before : " + vals[i]);
				logger_.info("after : " + stripped_vals[i]);
			}
			entry.setValue(stripped_vals);
		}

		filterChain.doFilter(new XSSRequestWrapper(httpRequest), servletResponse);
	}

	@Override
	public void destroy() {
	}
}
