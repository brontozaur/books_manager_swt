package com.papao.books.controller;

import com.papao.books.model.User;
import com.papao.books.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController extends AbstractController {

    private final UserRepository repository;

    @Autowired
    public UserController(MongoTemplate mongoTemplate,
                          UserRepository userRepository) {
        super(mongoTemplate);
        this.repository = userRepository;
    }

    public User save(User user) {
        return repository.save(user);
    }

    public User findOne(ObjectId id) {
        if (id == null) {
            return null;
        }
        return repository.findOne(id.toString());
    }

    public void delete(User user) {
        this.repository.delete(user);
    }

    public List<User> findAll() {
        return repository.findAll();
    }
}
