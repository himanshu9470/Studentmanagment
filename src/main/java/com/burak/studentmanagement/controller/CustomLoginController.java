package com.burak.studentmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Controller
public class CustomLoginController {
    private static final Logger logger = LoggerFactory.getLogger(CustomLoginController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/custom-login")
    public String customLogin(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            HttpServletRequest request,
            Model model) {
        
        try {
            logger.info("Login attempt for username: {} with role: {}", username, role);
            
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(username, password);
            authToken.setDetails(new WebAuthenticationDetails(request));
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            // Check if user has the selected role
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean hasRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
            
            if (!hasRole) {
                logger.warn("User {} doesn't have role {}", username, role);
                model.addAttribute("error", "You don't have permission to access as " + role);
                return "login/login-form";
            }
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            logger.info("Login successful for username: {} with role: {}", username, role);
            
            // Redirect based on role
            switch (role) {
                case "STUDENT":
                    return "redirect:/student";
                case "TEACHER":
                    return "redirect:/teacher";
                default:
                    return "redirect:/";
            }
            
        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for username: {}", username);
            model.addAttribute("error", "Invalid username or password");
            return "login/login-form";
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for username: {}", username, e);
            model.addAttribute("error", "Authentication failed: " + e.getMessage());
            return "login/login-form";
        }
    }
}