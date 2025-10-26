package com.todo.webapp.security;

import com.todo.webapp.entity.User;
import com.todo.webapp.interceptor.JwtInterceptor;

public class AuthenticatedUserContext {

    /**
     * Returns the currently authenticated user stored in ThreadLocal.
     * Can be used in services, controllers, etc.
     */
    public static User getCurrentUser() {
        return JwtInterceptor.getAuthenticatedUser();
    }



    /**
     * Clears the thread-local user (optional — normally handled by interceptor).
     */
    public static void clear() {
        JwtInterceptor.clearAuthenticatedUser();
    }
}
