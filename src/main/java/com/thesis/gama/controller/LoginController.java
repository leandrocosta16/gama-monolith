package com.thesis.gama.controller;

import com.thesis.gama.security.JwtTokenUtil;
import com.thesis.gama.security.JwtRequest;
import com.thesis.gama.security.JwtResponse;
import com.thesis.gama.security.JwtUserDetailsService;
import com.thesis.gama.model.User;
import com.thesis.gama.exceptions.NoUserWithEmailException;
import com.thesis.gama.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final JwtUserDetailsService userDetailsService;

    private final UserService userService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Operation(summary = "Authenticates and generates a token")
    @PostMapping
    public HttpEntity<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        User userInfo = this.userService.findByEmail(authenticationRequest.getEmail()).get();
        final String token = jwtTokenUtil.generateToken(userDetails, userInfo.getId(), userInfo.getAccount().getRole().name());
        System.out.println("--------------");
        System.out.println(jwtTokenUtil.getUserIdFromToken(token));
        return ResponseEntity.ok(new JwtResponse(token, userInfo.getAccount().getEmail()));
    }
    private void authenticate(String username, String password){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
