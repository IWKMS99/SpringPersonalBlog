package com.iwkms.personalBlog.config;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public final class AppConstants {

    public static final class Content {
        public static final int POST_TITLE_MAX_LENGTH = 200;
        public static final int POST_CONTENT_MAX_LENGTH = 20_000;
        
        private Content() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Roles {
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String ROLE_ADMIN = ROLE_PREFIX + "ADMIN";
        public static final String ROLE_USER = ROLE_PREFIX + "USER";

        private Roles() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Views {
        // User
        public static final String LOGIN_VIEW = "login";
        public static final String LOGOUT_VIEW = "logout";
        public static final String REGISTRATION_VIEW = "registration";

        //Post
        public static final String REDIRECT_HOME = "redirect:/";
        public static final String REDIRECT_POST_DETAIL = "redirect:/post/";
        public static final String VIEW_POSTS = "posts";
        public static final String VIEW_POST_DETAILS = "postDetails";
        public static final String VIEW_POST_FORM = "postForm";
        public static final String VIEW_POST_EDIT_FORM = "postEditForm";


        private Views() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Urls {
        public static final String[] PUBLIC_URLS = {"/", "/post/*", "/user/login", "/user/register"};
        public static final String[] PUBLIC_RESOURCE_URLS = {"/css/**", "/js/**"};
        public static final String[] AUTHENTICATED_URLS = {"/post/new"};
        public static final String[] USER_CONTENT_URLS = {"/post/{id}/edit", "/post/{id}/delete"};

        public static final String LOGIN_PAGE_URL = "/user/login";
        public static final String DEFAULT_SUCCESS_URL = "/";
        public static final String LOGOUT_URL = "/user/logout";
        public static final String LOGOUT_SUCCESS_URL = "/";

        private Urls() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Messages {
        // Post
        public static final String MSG_POST_NOT_FOUND = "Post not found";
        public static final String MSG_POST_CREATED = "Post successfully created";
        public static final String MSG_POST_UPDATED = "Post successfully updated";
        public static final String MSG_POST_DELETED = "Post successfully deleted";
        public static final String MSG_USER_NOT_FOUND = "User not found";
        public static final String MSG_UNAUTHORIZED = "You don't have permission to perform this action";

        // User
        public static final String REGISTRATION_SUCCESS_KEY = "registrationSuccess";
        public static final String REGISTRATION_ERROR_KEY = "registrationError";
        public static final String SUCCESS_MESSAGE = "Регистрация прошла успешно! Теперь вы можете войти.";
        public static final String GENERIC_ERROR_MESSAGE = "Произошла ошибка при регистрации.";

        private Messages() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Attributes {
        // Post
        public static final String ATTR_ERROR_MESSAGE = "errorMessage";
        public static final String ATTR_SUCCESS_MESSAGE = "successMessage";
        public static final String ATTR_POST = "post";
        public static final String ATTR_POSTS = "posts";

        // User
        public static final String USER_DTO_ATTRIBUTE = "userDto";

        private Attributes() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }


    public static final class Validation {
        public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]+$";
        
        private Validation() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    public static final class Security {
        public static final PolicyFactory SAFE_HTML_POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        
        private Security() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }
    }

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}