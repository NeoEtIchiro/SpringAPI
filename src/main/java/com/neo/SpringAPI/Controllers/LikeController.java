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

import com.neo.SpringAPI.Entities.Like;
import com.neo.SpringAPI.Repositories.LikeRepository;
import com.neo.SpringAPI.Repositories.UserRepository;
import com.neo.SpringAPI.Repositories.ArticleRepository;

@Controller
@RequestMapping(path="/likes")
public class LikeController {
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ArticleRepository articleRepository;

  @PostMapping(path="/add")
  public @ResponseBody String addNewLike (
    @RequestParam(name = "authorId", required = true) Integer authorId, 
    @RequestParam(name = "articleId", required = true) Integer articleId, 
    @RequestParam(name = "value", required = true) Integer value) {

    if(userRepository.findById(authorId).orElse(null) == null) {
      return "Error: authorId does not correspond to a user";
    }
    if(articleRepository.findById(articleId).orElse(null) == null) {
      return "Error: articleId does not correspond to an article";
    }

    if (value != -1 && value != 0 && value != 1) {
      return "Error: value must be -1, 0, or 1";
    }

    Like n = new Like();
    n.setArticleId(articleId);
    n.setAuthorId(authorId);
    n.setValue(value);
    likeRepository.save(n);
    return "Saved";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<Like> getAllLikes() {
    // This returns a JSON or XML with the likes
    return likeRepository.findAll();
  }

  @GetMapping(path="/get")
  public @ResponseBody Like getLikeById(
    @RequestParam(name = "id", required = true) Integer id) {

    return likeRepository.findById(id).orElse(null);
  }

  @PutMapping(path="/update") 
  public @ResponseBody String updateLike(
      @RequestParam(name = "id", required = true) Integer id,
      @RequestParam(name = "value", required = true) Integer value) {

    Like n = likeRepository.findById(id).orElse(null);
    if (n == null) {
      return "Like not found";
    }
    if (value != -1 && value != 0 && value != 1) {
      return "Error: value must be -1, 0, or 1";
    }
    n.setValue(value);
    likeRepository.save(n);
    return "Updated";
  }

  @DeleteMapping(path="/delete")
  public @ResponseBody String deleteLike(
    @RequestParam(name = "id", required = true) Integer id) {
      
    Like n = likeRepository.findById(id).orElse(null);
    if (n == null) {
      return "Like not found";
    }
    likeRepository.delete(n);
    return "Deleted";
  }
}