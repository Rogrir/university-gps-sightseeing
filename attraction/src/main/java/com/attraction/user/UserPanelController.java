package com.attraction.user;

import java.util.List;

import javax.validation.Valid;

import com.attraction.security.MessageResponse;
import com.attraction.security.jwt.JwtUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Slf4j
@AllArgsConstructor
public class UserPanelController {

  private UserService userService;
  private JwtUtils jwtUtils;
  private PasswordEncoder encoder;

  @GetMapping("/user")
  @PreAuthorize("hasAnyAuthority('USER', 'GUIDE', 'ADMIN')")
  public String loadPanel(@RequestHeader String cookie, Model model) {
    User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(cookie.substring(cookie.indexOf('=') + 1)));
    model.addAttribute("user", user);
    return "user";
  }

  @GetMapping("/users")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public @ResponseBody List<User> getUsers() {
    return userService.getAllUsers();
  }

  @PostMapping("/user/update")
  @PreAuthorize("hasAnyAuthority('USER', 'GUIDE', 'ADMIN')")
  public @ResponseBody ResponseEntity<MessageResponse> changeUserAttributes(@RequestHeader String Cookie,
      @Valid UserRequest userRequest, BindingResult bindingResult) {
    validateInput(bindingResult);
    User user = userService.findByUsername(jwtUtils.getUserNameFromJwtToken(Cookie.substring(Cookie.indexOf('=') + 1)));
    if (user == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("User doesn't exist"));
    }
    String password = userRequest.getPassword() != null && !userRequest.getPassword().isBlank()
        ? userRequest.getPassword()
        : user.getPassword();
    String email = userRequest.getEmail() != null && !userRequest.getEmail().isBlank() ? userRequest.getEmail()
        : user.getEmail();
    String username = userRequest.getUsername() != null && !userRequest.getUsername().isBlank()
        ? userRequest.getUsername()
        : user.getUsername();
    String avatar = userRequest.getAvatar() != null && !userRequest.getAvatar().isBlank()
        ? userRequest.getAvatar()
        : user.getAvatar();
    userService.save(new User(user.getId(), username, encoder.encode(password), email,
        avatar, user.getPoints(), user.getPreferredCity(), user.getPreferredMonument(), user.getRole()));
    return ResponseEntity.ok(new MessageResponse("Changed user profile"));
  }

  @PostMapping("/user/points")
  @PreAuthorize("hasAnyAuthority('ADMIN')")
  public @ResponseBody ResponseEntity<MessageResponse> changeUserAttributes(Integer userId, Integer addPoints) {
    User user = userService.findById(userId);
    if (user == null) {
      return ResponseEntity.badRequest().body(new MessageResponse("User doesn't exist"));
    }
    userService.save(new User(userId, user.getUsername(), user.getPassword(), user.getEmail(),
        user.getAvatar(), user.getPoints() + addPoints, user.getPreferredCity(), user.getPreferredMonument(),
        user.getRole()));
    return ResponseEntity.ok(new MessageResponse("Added points"));
  }

  private void validateInput(BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug(bindingResult.getAllErrors().toString());
      throw new ValidationException(bindingResult.getAllErrors().toString());
    }
  }
}
