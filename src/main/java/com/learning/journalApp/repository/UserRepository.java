package com.learning.journalApp.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.learning.journalApp.entity.User;

public interface UserRepository extends MongoRepository<User, ObjectId> {

    User findByUserName(String userName);

}
