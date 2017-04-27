package com.papao.books.controller;

import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.repository.UserActivityRepository;
import com.papao.books.repository.UserRepository;
import com.papao.books.view.auth.EncodeLive;
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

    public UserActivity saveUserActivity(UserActivity userActivity) {
        return userActivityRepository.save(userActivity);
    }

    public UserActivity getUserRatingObject(ObjectId userId, ObjectId bookId) {
        return userActivityRepository.getByUserIdAndBookId(userId, bookId);
    }

    public int getPersonalRating(ObjectId bookId) {
        return getPersonalRating(EncodeLive.getIdUser(), bookId);
    }

    public int getPersonalRating(ObjectId userId, ObjectId bookId) {
        UserActivity activity = getUserRatingObject(userId, bookId);
        if (activity != null) {
            return activity.getRatingForBook(bookId);
        }
        return 0;
    }

    public UserActivity saveBookRatingForCurrentUser(ObjectId bookId, int rating) {
        UserActivity userActivity = getUserRatingObject(EncodeLive.getIdUser(), bookId);
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setBookId(bookId);
            userActivity.setUserId(EncodeLive.getIdUser());
        }
        userActivity.getBookRating().setRating(rating);
        return saveUserActivity(userActivity);
    }
}
