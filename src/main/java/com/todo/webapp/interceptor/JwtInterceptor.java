package com.todo.webapp.interceptor;

import com.auth0.jwk.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.todo.webapp.entity.User;
import com.todo.webapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final JwkProvider jwkProvider;
    private final String jwksUrl;
    private final String cognitoIssuer;
    private final String clientId;

    private static final ThreadLocal<User> authenticatedUser = new ThreadLocal<>();

    public JwtInterceptor(
            UserRepository userRepository,
            @Value("${aws.cognito.jwks-url}") String jwksUrl,
            @Value("${aws.cognito.issuer}") String issuer,
            @Value("${aws.cognito.client-id}") String clientId) {
        this.userRepository = userRepository;
        this.jwksUrl = jwksUrl;
        this.jwkProvider = new JwkProviderBuilder(jwksUrl)
                .cached(10, 24, TimeUnit.HOURS)
                .build();
        this.cognitoIssuer = issuer;
        this.clientId = clientId;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            String token = authHeader.substring(7);
            DecodedJWT unverifiedJwt = JWT.decode(token);

            // Get JWK and verify signature
            Jwk jwk;
            try {
                jwk = jwkProvider.get(unverifiedJwt.getKeyId());
            } catch (SigningKeyNotFoundException e) {
                try {
                    JwkProvider urlProvider = new UrlJwkProvider(new URL(jwksUrl));
                    jwk = urlProvider.get(unverifiedJwt.getKeyId());
                } catch (Exception ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Key not available");
                    return false;
                }
            }
            RSAPublicKey publicKey = (RSAPublicKey) jwk.getPublicKey();
            DecodedJWT verifiedJwt = JWT.require(Algorithm.RSA256(publicKey, null))
                    .withIssuer(cognitoIssuer)
                    .withClaim("token_use", "access")
                    .build()
                    .verify(token);


            // Extract claims
            String email = (verifiedJwt.getClaim("email").asString());


            if (email == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // Split name into first and last (fallbacks if not available)
//            String firstname = name != null ? name.split(" ")[0] : "Unknown";
//            String lastname = (name != null && name.split(" ").length > 1) ? name.split(" ")[1] : "";

            // Insert if not exist
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user = existingUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setFirstname("firstname");
                newUser.setLastname("lastname");
                return userRepository.save(newUser);
            });

            authenticatedUser.set(user);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        authenticatedUser.remove();
    }

    public static User getAuthenticatedUser() {
        return authenticatedUser.get();
    }

    public static void clearAuthenticatedUser() {
        authenticatedUser.remove();
    }
}
