package com.mycompany.prog5121poe;
import org.junit.Test;
import static org.junit.Assert.*;

public class LoginTest {

    // Test username correctly formatted
    @Test
    public void testCheckUserNameCorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkUserName());
    }

    // Test username incorrectly formatted
    @Test
    public void testCheckUserNameIncorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyle!!!!!!!",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(user.checkUserName());
    }

    // Test password correctly formatted
    @Test
    public void testCheckPasswordCorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkPasswordComplexity());
        assertEquals(
                "Password successfully captured.",
                user.checkPasswordComplexity()
                        ? "Password successfully captured."
                        : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character."
        );
    }

    // Test password incorrectly formatted
    @Test
    public void testCheckPasswordIncorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "password",
                "+27838968976"
        );

        assertFalse(user.checkPasswordComplexity());

        assertEquals(
                "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                user.checkPasswordComplexity()
                        ? "Password successfully captured."
                        : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character."
        );
    }

    // Test cellphone correctly formatted
    @Test
    public void testCheckCellPhoneCorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(user.checkCellPhoneNumber());

        assertEquals(
                "Cell number successfully captured.",
                user.checkCellPhoneNumber()
                        ? "Cell number successfully captured."
                        : "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again."
        );
    }

    // Test cellphone incorrectly formatted
    @Test
    public void testCheckCellPhoneIncorrect() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "08966553"
        );

        assertFalse(user.checkCellPhoneNumber());

        assertEquals(
                "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                user.checkCellPhoneNumber()
                        ? "Cell number successfully captured."
                        : "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again."
        );
    }

    // Test successful login
    @Test
    public void testLoginSuccessful() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertTrue(
                user.loginUser("kyl_1", "Ch&&sec@ke99!")
        );
    }

    // Test failed login
    @Test
    public void testLoginFailed() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        assertFalse(
                user.loginUser("wrong", "wrongpassword")
        );
    }

    // Test successful login message
    @Test
    public void testSuccessfulLoginMessage() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        user.loginUser("kyl_1", "Ch&&sec@ke99!");

        assertEquals(
                "Welcome John Smith it is great to see you again.",
                user.returnLoginStatus()
        );
    }

    // Test failed login message
    @Test
    public void testFailedLoginMessage() {

        Login user = new Login(
                "John",
                "Smith",
                "kyl_1",
                "Ch&&sec@ke99!",
                "+27838968976"
        );

        user.loginUser("wrong", "wrongpassword");

        assertEquals(
                "Username or password incorrect, please try again.",
                user.returnLoginStatus()
        );
    }
}