package com.petshop.web.config;

import com.petshop.web.security.AdminSessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminClienteRedirectInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!AdminSessionHelper.isAdminLogado()) {
            return true;
        }
        if (!isAreaCliente(request)) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/admin");
        return false;
    }

    private boolean isAreaCliente(HttpServletRequest request) {
        String path = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && path.startsWith(context)) {
            path = path.substring(context.length());
        }
        if (path.isEmpty()) {
            path = "/";
        }
        return path.equals("/")
                || path.equals("/inicio")
                || path.startsWith("/pets")
                || path.startsWith("/agendamentos")
                || path.equals("/login")
                || path.equals("/registro");
    }
}
