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
		boolean isGzipped = httpRequest.getHeader(HttpHeaders.CONTENT_ENCODING) != null
				&& httpRequest.getHeader(HttpHeaders.CONTENT_ENCODING).contains("gzip");

		if (httpRequest.getHeader(HttpHeaders.CONTENT_ENCODING) != null) {
			logger_.info("request gzipped : " + httpRequest.getHeader(HttpHeaders.CONTENT_ENCODING));
		}
		if (isGzipped) {
			httpRequest = new GZIPServletRequestWrapper(httpRequest);
		}

		if (acceptsGZipEncoding(httpRequest)) {
			httpResponse.addHeader("Content-Encoding", "gzip");
			GZipServletResponseWrapper gzipResponse =
					new GZipServletResponseWrapper(httpResponse);
			filterChain.doFilter(new XSSRequestWrapper(httpRequest), gzipResponse);
			gzipResponse.close();
		} else {
			filterChain.doFilter(new XSSRequestWrapper(httpRequest), servletResponse);
		}
	}

	@Override
	public void destroy() {
	}

	private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) {
		String acceptEncoding =
				httpRequest.getHeader("Accept-Encoding");

		return acceptEncoding != null &&
				acceptEncoding.indexOf("gzip") != -1;
	}
}
