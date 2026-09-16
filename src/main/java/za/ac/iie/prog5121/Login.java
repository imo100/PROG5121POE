package prog5121part1;
public class Login {

    // User registration details
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhoneNumber;

    // Stores whether the login was successful
    private boolean loginSuccessful;

    // Constructor
    public Login(String firstName, String lastName, String username,
                 String password, String cellPhoneNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.loginSuccessful = false;
    }

    // Checks that the username contains an underscore
    // and is no more than five characters long.
    public boolean checkUserName() {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

   /* Validates that the password is at least 8 characters long and contains an 
     uppercase letter, a number, and a special character.*/
    public boolean checkPasswordComplexity() {

        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecialCharacter = false;

        for (int i = 0; i < password.length(); i++) {

            char character = password.charAt(i);

            if (Character.isUpperCase(character)) {
                hasCapital = true;
            }

            if (Character.isDigit(character)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(character)) {
                hasSpecialCharacter = true;
            }
        }

        return hasCapital && hasNumber && hasSpecialCharacter;
    }

    /*
     Validates South African international phone number format format (+27 followed by 9 digits)
      Source:
      Stack Overflow, "Validate South Africa Cell Phone Number"
      https://stackoverflow.com/questions/4058001/
     */
    public boolean checkCellPhoneNumber() {

        if (cellPhoneNumber == null) {
            return false;
        }

        String phoneRegex = "^\\+27[0-9]{9}$";

        return cellPhoneNumber.matches(phoneRegex);
    }

    // Registers the user and returns the required message.
    public String registerUser() {

        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        return "User registered successfully.";
    }

    // Checks whether the entered login details match
    // the registered username and password.
    public boolean loginUser(String enteredUsername, String enteredPassword) {

        loginSuccessful = username.equals(enteredUsername)
                && password.equals(enteredPassword);

        return loginSuccessful;
    }

    // Returns the appropriate login status message.
    public String returnLoginStatus() {

        if (loginSuccessful) {
            return "Welcome " + firstName + " " + lastName
                    + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}