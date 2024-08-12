package com.example.demo.filter;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class LoginFilter implements Filter {

	@Autowired
	private UserService userService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		String method = req.getMethod();
		String contextPath = req.getContextPath();

		boolean isStaticResource = uri.startsWith(contextPath + "/static/") || uri.endsWith(".css")
				|| uri.endsWith(".js") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".gif");
		
		if (isStaticResource) {
			chain.doFilter(request, response);
			return;
		}

		if ((contextPath + "/login").equals(uri)) {
			if ("POST".equalsIgnoreCase(method)) {
				handleLogin(req, res);
			} else {
				chain.doFilter(request, response);
			}
		} else {
			if (userNotLoggedIn(req)) {
				res.sendRedirect(contextPath + "/login");
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	private boolean userNotLoggedIn(HttpServletRequest req) {
		return req.getSession().getAttribute("user") == null;
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
		User user = parseRequestBody(req);
		if (user != null && userService.validateUser(user)) {
			req.getSession().setAttribute("user", user);
			writeResponse(res, HttpServletResponse.SC_OK, "{\"status\": \"success\"}");
		} else {
			writeResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
					"{\"status\": \"error\", \"message\": \"Invalid credentials\"}");
		}
	}

	private User parseRequestBody(HttpServletRequest req) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		BufferedReader reader = req.getReader();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return new ObjectMapper().readValue(sb.toString(), User.class);
	}

	private void writeResponse(HttpServletResponse res, int status, String body) throws IOException {
		res.setStatus(status);
		res.setContentType("application/json");
		res.getWriter().write(body);
	}
}