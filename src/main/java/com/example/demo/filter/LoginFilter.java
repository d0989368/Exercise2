package com.example.demo.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

//@Component
@WebFilter("/success")
public class LoginFilter implements Filter {

    @Autowired
    private UserService userService;

    private static final List<String> ALLOWED_URLS = Arrays.asList(
            "/demo/login",
            "/css/",
            "/js/",
            "/h2-console",
            "/h2-console/*"
    );

    private boolean allowedRequest(String uri) {
        return ALLOWED_URLS.stream().anyMatch(uri::startsWith);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String method = req.getMethod();

        if (!allowedRequest(uri)) {
            User sessionUser = (User) req.getSession().getAttribute("user");

            if (sessionUser == null) {
                if ("/demo/login".equals(uri) && "POST".equalsIgnoreCase(method)) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    BufferedReader reader = req.getReader();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String body = sb.toString();

                    ObjectMapper mapper = new ObjectMapper();
                    User user = mapper.readValue(body, User.class);

                    if (userService.validateUser(user)) {
                        req.getSession().setAttribute("user", user);
                        res.setStatus(HttpServletResponse.SC_OK);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"status\": \"success\"}");
                        return;
                    } else {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"status\": \"error\", \"message\": \"Invalid credentials\"}");
                        return;
                    }
                } else {
                    res.sendRedirect("/demo/error");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }
}
