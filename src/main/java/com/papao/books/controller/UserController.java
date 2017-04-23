package com.papao.books.controller;

import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.repository.UserActivityRepository;
import com.papao.books.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController extends AbstractController {

    private final UserRepository repository;
    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserController(MongoTemplate mongoTemplate,
                          UserRepository userRepository,
                          UserActivityRepository userActivityRepository) {
        super(mongoTemplate);
        this.repository = userRepository;
        this.userActivityRepository = userActivityRepository;
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

    public int getRatingMediu(ObjectId bookId) {
        List<UserActivity> usersWithRatings = userActivityRepository.getByBookRatings_BookId(bookId);
        if (usersWithRatings.isEmpty()) {
            return 0;
        }
        int rating = 0;
        for (UserActivity activity : usersWithRatings) {
            rating += activity.getRatingForBook(bookId);
        }
        return rating / usersWithRatings.size();
    }

    public int getUserRating(ObjectId userId, ObjectId bookId) {
        UserActivity activity = userActivityRepository.getByUserId(userId);
        if (activity != null) {
            return activity.getRatingForBook(bookId);
        }
        return 0;
    }
}
