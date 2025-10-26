package com.todo.webapp.security;

import com.todo.webapp.entity.User;
import com.todo.webapp.interceptor.JwtInterceptor;

public class AuthenticatedUserContext {

    public static User getCurrentUser() {
        return JwtInterceptor.getAuthenticatedUser();
    }

    public static void clear() {
        JwtInterceptor.clearAuthenticatedUser();
    }
}
