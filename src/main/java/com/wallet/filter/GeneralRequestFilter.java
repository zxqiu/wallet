package com.wallet.filter;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.wallet.utils.misc.XSSFilter;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by zxqiu on 4/24/17.
 */
public class GeneralRequestFilter implements ContainerRequestFilter {
	private static final Logger logger_ = LoggerFactory.getLogger(GeneralRequestFilter.class);
	private static XSSFilter xssFilter = new XSSFilter();

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		ContainerRequest request = (ContainerRequest) containerRequestContext;
		if (containerRequestContext.hasEntity()) {
			request.bufferEntity();
			String in = request.readEntity(String.class);
			logger_.info("Incoming data in request :" + in);
			String out = xssFilter.stripXSS(in);
			logger_.info("Stripped data in request :" + out);

			request.setEntityStream(new ByteArrayInputStream(out.getBytes()));
		}
	}
}
