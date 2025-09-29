package com.neo.SpringAPI.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.neo.SpringAPI.Entities.Article;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface ArticleRepository extends CrudRepository<Article, Integer> {

}