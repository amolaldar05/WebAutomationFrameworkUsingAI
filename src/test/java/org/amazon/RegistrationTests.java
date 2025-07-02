package org.amazon;

import org.PageObjects.LoginPageObjects;
import org.PageObjects.RegistrationPageObjects;
import org.BaseComponent.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.Utils.MySQLUtils;
import org.testng.asserts.SoftAssert;
import org.TestData.RegistrationTestData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationTests extends BaseClass {

    @Test(groups = "regression")
    public void testValidRegistration() throws SQLException {
        SoftAssert softAssert = new SoftAssert();
        // Use test data from the dedicated package
        RegistrationTestData testData = RegistrationTestData.generateRandom();

        String accountCreatedSucMsg = getUserRegiData(testData);
        softAssert.assertEquals(accountCreatedSucMsg, "Account Created Successfully");

        // Only insert into DB if registration was successful
        if ("Account Created Successfully".equals(accountCreatedSucMsg)) {
            org.Utils.MySQLUtils.createUsersTableAndInsertSample(
                testData.firstName, testData.lastName, testData.email, testData.phone, testData.occupation, testData.gender, testData.password, true
            );
        }
        softAssert.assertAll();
    }

    private static String getUserRegiData(RegistrationTestData testData) {
        LoginPageObjects loginPageObjects = new LoginPageObjects(getDriver());
        RegistrationPageObjects registrationPage = new RegistrationPageObjects(getDriver());
        loginPageObjects.clickRegisterHere();
        registrationPage.enterFirstName(testData.firstName);
        registrationPage.enterLastName(testData.lastName);
        registrationPage.enterEmail(testData.email);
        registrationPage.enterPhone(testData.phone);
        registrationPage.selectOccupation(testData.occupation);
        registrationPage.selectGender(testData.gender);
        registrationPage.enterPassword(testData.password);
        registrationPage.enterConfirmPassword(testData.password);
        registrationPage.checkAgeCheckbox();
        String accountCreatedSucMsg = registrationPage.clickRegister();
        return accountCreatedSucMsg;
    }

}
