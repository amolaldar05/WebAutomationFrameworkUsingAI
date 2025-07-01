package org.TestData;

public class RegistrationTestData {
    public String firstName;
    public String lastName;
    public String email;
    public String phone;
    public String occupation;
    public String gender;
    public String password;

    public RegistrationTestData(String firstName, String lastName, String email, String phone, String occupation, String gender, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.occupation = occupation;
        this.gender = gender;
        this.password = password;
    }

    public static RegistrationTestData generateRandom() {
        String firstName = "John" + java.util.UUID.randomUUID().toString().substring(0, 5);
        String lastName = "Doe" + java.util.UUID.randomUUID().toString().substring(0, 5);
        String email = "user" + java.util.UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String phone = String.valueOf(System.currentTimeMillis()).substring(3, 13);
        String occupation = "Doctor";
        String gender = "Male";
        String password = "Password@123";
        return new RegistrationTestData(firstName, lastName, email, phone, occupation, gender, password);
    }
}

