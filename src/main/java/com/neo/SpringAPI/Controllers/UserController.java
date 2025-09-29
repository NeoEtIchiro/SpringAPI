package com.neo.SpringAPI.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.neo.SpringAPI.Entities.User;
import com.neo.SpringAPI.Repositories.UserRepository;

@Controller
@RequestMapping(path="/users")
public class UserController {
  @Autowired
  private UserRepository userRepository;

  @PostMapping(path="/add")
  public @ResponseBody String addNewUser (
    @RequestParam(name = "name", required = true) String name, 
    @RequestParam(name = "email", required = true) String email, 
    @RequestParam(name = "password", required = true) String password, 
    @RequestParam(name = "role", required = true) String role) {

    if (!role.equalsIgnoreCase("moderator") && !role.equalsIgnoreCase("publisher")) {
      return "Error: role must be 'moderator' or 'publisher'";
    }

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

  @GetMapping(path="/get")
  public @ResponseBody User getUserById(
    @RequestParam(name = "id", required = true) Integer id) {

    return userRepository.findById(id).orElse(null);
  }

  @PutMapping(path="/update") 
  public @ResponseBody String updateUser(
      @RequestParam(name = "id", required = true) Integer id,
      @RequestParam(name = "name", required = true) String name,
      @RequestParam(name = "email", required = true) String email,
      @RequestParam(name = "password", required = true) String password,
      @RequestParam(name = "role", required = true) String role) {

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

  @DeleteMapping(path="/delete")
  public @ResponseBody String deleteUser(
    @RequestParam(name = "id", required = true) Integer id) {
      
    User n = userRepository.findById(id).orElse(null);
    if (n == null) {
      return "User not found";
    }
    userRepository.delete(n);
    return "Deleted";
  }
}