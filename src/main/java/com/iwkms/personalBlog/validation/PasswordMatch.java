package com.iwkms.personalBlog.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
    String message() default "Пароли не совпадают";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String password();
    String confirmPassword();
} 