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

import com.neo.SpringAPI.Entities.Article;
import com.neo.SpringAPI.Repositories.ArticleRepository;

@Controller
@RequestMapping(path="/articles")
public class ArticleController {
  @Autowired
  private ArticleRepository articleRepository;

  @PostMapping(path="/add")
  public @ResponseBody String addNewArticle (
    @RequestParam(name = "content", required = true) String content, 
    @RequestParam(name = "authorId", required = true) String authorId) {

    Article n = new Article();
    n.setDate(java.time.LocalDateTime.now());
    n.setContent(content);
    n.setAuthorId(authorId);
    articleRepository.save(n);
    return "Saved";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<Article> getAllArticles() {
    // This returns a JSON or XML with the articles
    return articleRepository.findAll();
  }

  @GetMapping(path="/get")
  public @ResponseBody Article getArticleById(
    @RequestParam(name = "id", required = true) Integer id) {

    return articleRepository.findById(id).orElse(null);
  }

  @PutMapping(path="/update") 
  public @ResponseBody String updateArticle(
      @RequestParam(name = "id", required = true) Integer id,
      @RequestParam(name = "content", required = true) String content) {

    Article n = articleRepository.findById(id).orElse(null);
    if (n == null) {
      return "Article not found";
    }
    n.setContent(content);
    articleRepository.save(n);
    return "Updated";
  }

  @DeleteMapping(path="/delete")
  public @ResponseBody String deleteArticle(
    @RequestParam(name = "id", required = true) Integer id) {
      
    Article n = articleRepository.findById(id).orElse(null);
    if (n == null) {
      return "Article not found";
    }
    articleRepository.delete(n);
    return "Deleted";
  }
}