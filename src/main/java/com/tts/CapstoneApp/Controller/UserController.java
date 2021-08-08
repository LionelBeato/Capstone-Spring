package com.tts.CapstoneApp.Controller;


import com.tts.CapstoneApp.Model.User;
import com.tts.CapstoneApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;


@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

    @GetMapping("/get")
    public User getUser(String id) {
        return userService.findById(id);
    }

//    @PostMapping("/post")
//    public User addNewUser(@RequestBody User user) {
//        System.out.println(user);
//
//        return userService.saveUser(user);
//    }

    @PostMapping("/post/google")
    public User addNewUserGoogle(@AuthenticationPrincipal OAuth2User principal, @RequestBody User user) {
        String username = principal.getAttribute("given_name");
        String id = principal.getAttribute("sub");
        String email = principal.getAttribute("email");

        User foundUser = userService.findById(id);

        foundUser.setUsername(username);
        foundUser.setEmail(email);


        System.out.println("PostUser: " + user);

        return userService.saveUser(foundUser);
    }

    @GetMapping("/login")
    public User getLoggedInUser(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println("this is my principal: " + principal);
        String id = principal.getAttribute("sub");
        return userService.findById(id);
    }

//    @GetMapping("/jwt")
//    public User getuserThroughJwt(@AuthenticationPrincipal Jwt jwt) {
//        System.out.println("this is my principal: " + jwt);
//        String id = jwt.getId();
//
//        return userService.findById(id);
//    }

    @GetMapping("/oidc-principal")
    public OidcIdToken getOidcUserPrincipal(@AuthenticationPrincipal OidcUser principal) {
        return principal.getIdToken();
    }

    @PostMapping("/favorites")
    public User updateMovies(@AuthenticationPrincipal OAuth2User principal, @RequestBody User user) {
        String id = principal.getAttribute("sub");
        User foundUser = userService.findById(id);
        foundUser.setFavoriteMovies(user.getFavoriteMovies());
        return userService.saveUser(foundUser);
    }

    @Value("/")
    private String contextPath;

    @GetMapping("*")
    public ModelAndView defaultPage(Map<String, Object> model) {
        model.put("contextPath", contextPath);
        return new ModelAndView("index", model);
    }

}
