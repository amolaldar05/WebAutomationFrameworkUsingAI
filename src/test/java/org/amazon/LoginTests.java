package org.amazon;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;
import org.Utils.JsonUtils;
import org.Utils.ThrottleManager;
import org.Utils.MySQLUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class LoginTests extends BaseClass {
    private LoginPageObjects loginPage;
    JsonUtils jsonUtils;
    SoftAssert softAssert= new SoftAssert();



    @BeforeMethod
    public void setUpPage() throws IOException {
        loginPage = new LoginPageObjects(getDriver());
         jsonUtils = new JsonUtils("src/main/java/org/resources/testdata.json");
    }

    @Test(priority = 4,retryAnalyzer = org.Utils.RetryAnalyzer.class)
    public void testValidLogin() throws IOException, InterruptedException, SQLException {
        ThrottleManager.waitIfNeeded();

        // Fetch valid credentials from database
        String query = "SELECT username, password FROM valid_credentials ORDER BY id DESC LIMIT 1";
        String username = null;
        String password = null;
        try (Connection conn = MySQLUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
            }
        }
        loginPage.enterEmail(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        Thread.sleep(1000);
        softAssert.assertEquals(getDriver().getCurrentUrl(),"https://rahulshettyacademy.com/client/dashboard/dash");
        softAssert.assertAll();
    }

    @Test(priority = 1)
    public void testInvalidLogin() {
        ThrottleManager.waitIfNeeded();

        JsonNode firstUser = jsonUtils.getRootNode().get("users").get(1);
        String username = firstUser.get("username").asText();
        String password = firstUser.get("password").asText();
        loginPage.enterEmail(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        String errorMsg=loginPage.getErrorMsg();
        System.out.println(errorMsg);
        // Example: check for error message or URL remains the same
        softAssert.assertEquals(errorMsg, "Incorrect email or password.");
        softAssert.assertAll();
    }

    @Test(priority = 2)
    public void testForgotPassword() throws InterruptedException {
        ThrottleManager.waitIfNeeded();
        String title=loginPage.clickForgotPassword();
        softAssert.assertEquals(title, "Enter New Password");
        softAssert.assertAll();
        getDriver().navigate().back();
       // Assert.assertTrue(getDriver().getCurrentUrl().contains("/password-new"), "Forgot password link did not navigate correctly!");
    }

    @Test(priority = 3)
    public void testRegisterHereLink() throws InterruptedException {
        ThrottleManager.waitIfNeeded();
        String registerTitle=loginPage.clickRegisterHere();
        softAssert.assertEquals(registerTitle, "Register");
        softAssert.assertAll();
        getDriver().navigate().back();
        // Adjust the URL or element check as per your app's behavior
       // Assert.assertTrue(getDriver().getCurrentUrl().contains("/register"), "Register link did not navigate correctly!");
    }
}