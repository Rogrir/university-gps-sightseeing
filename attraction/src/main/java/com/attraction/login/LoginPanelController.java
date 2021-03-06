package com.attraction.login;

import com.attraction.security.CustomUserDetails;
import com.attraction.security.jwt.JwtResponse;
import com.attraction.security.jwt.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@AllArgsConstructor
public class LoginPanelController {

  private AuthenticationManager authenticationManager;

  JwtUtils jwtUtils;

  @GetMapping("/login")
  public String loadPanel() {
    return "login";
  }

  @GetMapping("/password-reset")
  public String loadResetPanel() {
    return "password_reset";
  }

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@Valid LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.add("Set-Cookie", "access_token=" + jwt);
    JwtResponse jwtResponse = new JwtResponse(
            jwt,
            userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            userDetails.getAuthority().getAuthority());
    return ResponseEntity.status(HttpStatus.OK).headers(responseHeader).body(jwtResponse);
  }
}
