package com.ecommerce.server.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// this is one of the secutirty filter
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    // doFiliter is a link in the securty filter chain before get to the controller,
    // it is going to check if there is a token in the header

    @Autowired
    private Jwtgenerator jwtgenerator;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //first, get jwt from the request
        String token = getJWTFromRequest(request);

        // use substring to validate the token
        if(StringUtils.hasText(token) && jwtgenerator.validateToken(token)){
            // get the username from the token
            String username = jwtgenerator.getUsernameFromJWT(token);
            // load the user that is associate with the token
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);


            System.out.println("show the user au"+userDetails.getAuthorities());
            //then we authenticate the user
            // we pass userdetail and pass authories, no need the credital,
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,
                    userDetails.getAuthorities() // if password match, will set the auth to true
                    ); // we need to add the auth in it sothat admin and user can also be detected
            System.out.println(authenticationToken.isAuthenticated());
            System.out.println(authenticationToken.getDetails());

            // then we going to set the detail
           // authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

           // It seems this line is to load the Session info for current request for the server side. However, as we all know, this project relies on token based authentication,
            // and hence loading the session in the server is no use any more (I guess loading the session here is essential if we use the traditional session_id authentication).
 //System.out.println(authenticationToken.getDetails());
//            WebAuthenticationDetailsSource has a single responsibility to convert an instance of HttpServletRequest class into an instance of the WebAuthenticationDetails class.
//            You can think of it as a simple converter.
//
//            HttpServletRequest object which represents the parsed raw HTTP data and is a standard Java class is the input. And the WebAuthenticationDetails is an internal Spring class.
//
//            Therefore, you can think of it as a bridge between servlet classes and Spring classes.
//
//          The HttpServletRequest is an ancient class. Goes all the way back to Java 6


            // use context holder and  log the user in and set the auth token
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println(
                    SecurityContextHolder.getContext().getAuthentication()
            );
        }
    filterChain.doFilter(request,response); // pass to the next filter
    }

    private String getJWTFromRequest(HttpServletRequest request){
            String bearerToken = request.getHeader("Authorization");
            if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
                // use sutringutil to find the token
                return bearerToken.substring(7, bearerToken.length());
            }
            return null;
    }



}
