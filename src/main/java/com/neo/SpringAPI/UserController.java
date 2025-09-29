package com.neo.SpringAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/users")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @PostMapping(path="/add")
  public @ResponseBody String addNewUser (@RequestParam String name
      , @RequestParam String email, @RequestParam String password, @RequestParam String role) {
    // @ResponseBody means the returned String is the response, not a view name
    // @RequestParam means it is a parameter from the GET or POST request

    User n = new User();
    n.setName(name);
    n.setEmail(email);
    n.setPassword(password);
    n.setRole(role);
    userRepository.save(n);
    return "Saved";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<User> getAllUsers() {
    // This returns a JSON or XML with the users
    return userRepository.findAll();
  }

  @PutMapping(path="/update") 
  public @ResponseBody String updateUser(
      @RequestParam(name = "id") Integer id,
      @RequestParam(name = "name") String name,
      @RequestParam(name = "email") String email,
      @RequestParam(name = "password") String password,
      @RequestParam(name = "role") String role) {

    User n = userRepository.findById(id).orElse(null);
    if (n == null) {
      return "User not found";
    }
    n.setName(name);
    n.setEmail(email);
    n.setPassword(password);
    n.setRole(role);
    userRepository.save(n);
    return "Updated";
  }
}