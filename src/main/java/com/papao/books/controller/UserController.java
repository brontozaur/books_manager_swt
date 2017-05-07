package com.papao.books.controller;

import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.repository.UserActivityRepository;
import com.papao.books.repository.UserRepository;
import com.papao.books.view.auth.EncodeLive;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserController {

    private static UserRepository repository;
    private static UserActivityRepository userActivityRepository;

    @Autowired
    public UserController(UserRepository userRepository,
                          UserActivityRepository userActivityRepository) {
        UserController.repository = userRepository;
        UserController.userActivityRepository = userActivityRepository;
    }

    public static User save(User user) {
        return repository.save(user);
    }

    public static User findOne(ObjectId id) {
        if (id == null) {
            return null;
        }
        return repository.findOne(id.toString());
    }

    public static void delete(User user) {
        repository.delete(user);
    }

    public static List<User> findAll() {
        return repository.findAll();
    }

    public static UserActivity saveUserActivity(UserActivity userActivity) {
        return userActivityRepository.save(userActivity);
    }

    public static UserActivity getUserRatingObject(ObjectId userId, ObjectId bookId) {
        return userActivityRepository.getByUserIdAndBookId(userId, bookId);
    }

    public static int getPersonalRating(ObjectId bookId) {
        return getPersonalRating(EncodeLive.getIdUser(), bookId);
    }

    public static int getPersonalRating(ObjectId userId, ObjectId bookId) {
        UserActivity activity = getUserRatingObject(userId, bookId);
        if (activity != null) {
            return activity.getRatingForBook(bookId);
        }
        return 0;
    }

    public static UserActivity saveBookRatingForCurrentUser(ObjectId bookId, int rating) {
        UserActivity userActivity = getUserRatingObject(EncodeLive.getIdUser(), bookId);
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setBookId(bookId);
            userActivity.setUserId(EncodeLive.getIdUser());
        }
        userActivity.getBookRating().setRating(rating);
        return saveUserActivity(userActivity);
    }

    public static List<UserActivity> removeAllUserActivities(ObjectId userId) {
        return userActivityRepository.removeByUserId(userId);
    }
}
