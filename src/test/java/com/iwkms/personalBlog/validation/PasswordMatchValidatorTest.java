package com.iwkms.personalBlog.validation;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordMatchValidatorTest {

    @Mock
    private ConstraintValidatorContext context;
    
    @Mock
    private ConstraintViolationBuilder builder;
    
    @Mock
    private NodeBuilderCustomizableContext nodeBuilder;
    
    private PasswordMatchValidator validator;
    private PasswordMatch passwordMatch;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchValidator();
        
        passwordMatch = mock(PasswordMatch.class);
        when(passwordMatch.password()).thenReturn("password");
        when(passwordMatch.confirmPassword()).thenReturn("confirmPassword");
        when(passwordMatch.message()).thenReturn("Пароли не совпадают");
        
        validator.initialize(passwordMatch);
        
        lenient().when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        lenient().when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        lenient().when(nodeBuilder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_matchingPasswords_shouldReturnTrue() {
        TestObject testObject = new TestObject("password123", "password123");
        
        boolean result = validator.isValid(testObject, context);
        
        assertThat(result).isTrue();
        verify(context, never()).disableDefaultConstraintViolation();
    }

    @Test
    void isValid_mismatchedPasswords_shouldReturnFalse() {
        TestObject testObject = new TestObject("password123", "different");
        
        boolean result = validator.isValid(testObject, context);
        
        assertThat(result).isFalse();
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Пароли не совпадают");
        verify(builder).addPropertyNode("confirmPassword");
        verify(nodeBuilder).addConstraintViolation();
    }

    @Test
    void isValid_nullPassword_shouldReturnFalse() {
        TestObject testObject = new TestObject(null, "password123");
        
        boolean result = validator.isValid(testObject, context);
        
        assertThat(result).isFalse();
    }

    private static class TestObject {
        private final String password;
        private final String confirmPassword;

        public TestObject(String password, String confirmPassword) {
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String getPassword() {
            return password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }
    }   
} 