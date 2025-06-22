package org.amazon;

import org.PageObjects.RegistrationPageObjects;
import org.BaseComponent.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;

public class RegistrationTests extends BaseClass {

    @Test (dependsOnGroups = "registration")
    public void testValidRegistration() {
        WebDriver driver = getDriver();
        RegistrationPageObjects registrationPage = new RegistrationPageObjects(driver);
        registrationPage.enterFirstName("John");
        registrationPage.enterLastName("Doe");
        registrationPage.enterEmail("john.doe@example.com");
        registrationPage.enterPhone("1234567890");
        registrationPage.selectOccupation("Doctor");
        registrationPage.selectGender("Male");
        registrationPage.enterPassword("Password123!");
        registrationPage.enterConfirmPassword("Password123!");
        registrationPage.checkAgeCheckbox();
        registrationPage.clickRegister();
        // Add assertion for successful registration, e.g., check for a success message or redirect
        // Assert.assertTrue(driver.getPageSource().contains("Registration Successful"));
    }

    @Test
    public void testRegistrationWithMissingFields() {
        WebDriver driver = getDriver();
        RegistrationPageObjects registrationPage = new RegistrationPageObjects(driver);
        registrationPage.enterFirstName("");
        registrationPage.enterLastName("");
        registrationPage.enterEmail("");
        registrationPage.enterPhone("");
        registrationPage.selectOccupation("Doctor");
        registrationPage.selectGender("Male");
        registrationPage.enterPassword("");
        registrationPage.enterConfirmPassword("");
        registrationPage.checkAgeCheckbox();
        registrationPage.clickRegister();
        // Add assertion for error message, e.g., check for validation errors
        // Assert.assertTrue(driver.getPageSource().contains("This field is required"));
    }
}
