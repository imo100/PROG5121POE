package prog5121part1;

import java.util.Scanner;

/**
 * Main class that runs the registration and login application.
 */
public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("======================");
        System.out.println("REGISTRATION SYSTEM");
        System.out.println("======================");

        // Get registration information
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        System.out.print("Enter your username: ");
        String username = input.nextLine();

        System.out.print("Enter your password: ");
        String password = input.nextLine();

        System.out.print("Enter your South African cellphone number: ");
        String cellPhoneNumber = input.nextLine();

        // Create Login object
        Login user = new Login(
                firstName,
                lastName,
                username,
                password,
                cellPhoneNumber
        );

        // Display registration results
        System.out.println();
        System.out.println(" REGISTRATION ");

        if (user.checkUserName()) {
            System.out.println("Username successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        if (user.checkPasswordComplexity()) {
            System.out.println("Password successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        }

        if (user.checkCellPhoneNumber()) {
            System.out.println("Cell phone number successfully added.");
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not contain international code.");
        }

        // Only allow login if all registration details are valid
        if (user.checkUserName()
                && user.checkPasswordComplexity()
                && user.checkCellPhoneNumber()) {

            System.out.println();
            System.out.println("Registration successful.");
            System.out.println();

            // Login section
            System.out.println(" LOGIN ");

            System.out.print("Enter your username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter your password: ");
            String loginPassword = input.nextLine();

            user.loginUser(loginUsername, loginPassword);

            System.out.println(user.returnLoginStatus());

        } else {

            System.out.println();
            System.out.println("Registration unsuccessful. Please correct your details.");
        }

        input.close();
    }
}