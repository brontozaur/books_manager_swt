package com.papao.books.controller;

import com.papao.books.model.User;
import com.papao.books.model.UserActivity;
import com.papao.books.repository.UserActivityRepository;
import com.papao.books.repository.UserRepository;
import com.papao.books.ui.auth.EncodeLive;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
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

    public static User getById(ObjectId id) {
        if (id == null) {
            return null;
        }
        Optional<User> optional = repository.findById(id.toString());
        return optional.orElse(null);
    }

    public static void delete(User user) {
        repository.delete(user);
    }

    public static void deleteAllBookUserActivity(ObjectId bookId) {
        List<UserActivity> userActivities = userActivityRepository.getByBookId(bookId);
        userActivityRepository.deleteAll(userActivities);
    }

    public static List<User> findAll() {
        return repository.findAll();
    }

    public static UserActivity saveUserActivity(UserActivity userActivity) {
        return userActivityRepository.save(userActivity);
    }

    public static UserActivity getUserActivity(ObjectId userId, ObjectId bookId) {
        return userActivityRepository.getByUserIdAndBookId(userId, bookId);
    }

    public static int getPersonalRating(ObjectId bookId) {
        return getPersonalRating(EncodeLive.getIdUser(), bookId);
    }

    public static int getPersonalRating(ObjectId userId, ObjectId bookId) {
        UserActivity activity = getUserActivity(userId, bookId);
        if (activity != null) {
            return activity.getRating();
        }
        return 0;
    }

    public static void saveBookRatingForCurrentUser(ObjectId bookId, int rating) {
        UserActivity userActivity = getUserActivity(EncodeLive.getIdUser(), bookId);
        if (userActivity == null) {
            userActivity = new UserActivity();
            userActivity.setBookId(bookId);
            userActivity.setUserId(EncodeLive.getIdUser());
        }
        userActivity.setRating(rating);
        if (userActivity.isChanged()) {
            saveUserActivity(userActivity);
        } else {
            logger.error("User activity not saved because it was not changed!");
        }
    }

    public static void removeUserActivities(ObjectId userId) {
        userActivityRepository.removeByUserId(userId);
    }

    public static List<ObjectId> getBookIdsWithSpecifiedRatingForCurrentUser(int rating) {
        List<ObjectId> ids = new ArrayList<>();
        List<UserActivity> userActivities;
        if (rating == -1) {
            userActivities = userActivityRepository.findAll();
        } else {
            userActivities = userActivityRepository.getByRatingAndUserId(rating, EncodeLive.getIdUser());
        }
        for (UserActivity userActivity : userActivities) {
            ids.add(userActivity.getBookId());
        }
        return ids;
    }

    public static List<ObjectId> getBookIdsWithSpecifiedTranslationRatingForCurrentUser(int rating) {
        List<ObjectId> ids = new ArrayList<>();
        List<UserActivity> userActivities;
        if (rating == -1) {
            userActivities = userActivityRepository.findAll();
        } else {
            userActivities = userActivityRepository.getByTranslationRatingAndUserId(rating, EncodeLive.getIdUser());
        }
        for (UserActivity userActivity : userActivities) {
            ids.add(userActivity.getBookId());
        }
        return ids;
    }

    public static List<ObjectId> getBookIdsForUser(ObjectId userId) {
        List<ObjectId> ids = new ArrayList<>();
        List<UserActivity> userActivities;
        if (userId == null) {
            userActivities = userActivityRepository.findAll();
        } else {
            userActivities = userActivityRepository.getByUserId(userId);
        }
        for (UserActivity userActivity : userActivities) {
            ids.add(userActivity.getBookId());
        }
        return ids;
    }

    public static List<ObjectId> getReadedBookIdsForUser(ObjectId userId) {
        List<ObjectId> ids = new ArrayList<>();
        List<UserActivity> userActivities = userActivityRepository.getByUserIdAndCarteCititaCititaIs(userId, true);
        for (UserActivity userActivity : userActivities) {
            ids.add(userActivity.getBookId());
        }
        return ids;
    }

    public static List<UserActivity> getReadedBookForUser(ObjectId userId) {
        List<ObjectId> ids = new ArrayList<>();
        return userActivityRepository.getByUserIdAndCarteCititaCititaIs(userId, true);
    }



    public static List<ObjectId> getBookIdsWithReview(ObjectId userId) {
        List<ObjectId> ids = new ArrayList<>();
        List<UserActivity> userActivities = userActivityRepository.getByUserIdAndReviewIsGreaterThan(userId, "");
        for (UserActivity userActivity : userActivities) {
            ids.add(userActivity.getBookId());
        }
        return ids;
    }

    public static List<UserActivity> getAllCartiCitite() {
        return userActivityRepository.getByCarteCitita_CititaIs(true);
    }
}
