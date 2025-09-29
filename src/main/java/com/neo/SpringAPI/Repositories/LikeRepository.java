package com.neo.SpringAPI.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.neo.SpringAPI.Entities.Like;

// This will be AUTO IMPLEMENTED by Spring into a Bean called likeRepository
// CRUD refers Create, Read, Update, Delete

public interface LikeRepository extends CrudRepository<Like, Integer> {

}