package org.uario.seaworkengine.web.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.uario.seaworkengine.web.login.LoginSuccessHandlerImpl;

public class PreAuthFilter extends GenericFilterBean {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {

			final HttpServletRequest httpServletRequest = (HttpServletRequest) request;

			final String url = httpServletRequest.getRequestURL().toString();

			if (StringUtils.contains(url, "mobile")) {

				final HttpSession session = httpServletRequest.getSession();
				session.setAttribute(LoginSuccessHandlerImpl.MOBILE_TAG, LoginSuccessHandlerImpl.MOBILE_TAG);

			}

		}

		chain.doFilter(request, response);

	}

}
