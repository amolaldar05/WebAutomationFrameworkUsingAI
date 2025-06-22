package org.amazon;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;
import org.Utils.JsonUtils;
import org.Utils.ThrottleManager;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTests extends BaseClass {
    private LoginPageObjects loginPage;
    JsonUtils jsonUtils;



    @BeforeMethod
    public void setUpPage() throws IOException {
        loginPage = new LoginPageObjects(getDriver());
         jsonUtils = new JsonUtils("src/main/java/org/resources/testdata.json");
    }

    @Test(priority = 1, retryAnalyzer = org.Utils.RetryAnalyzer.class)
    public void testValidLogin() throws IOException {
        ThrottleManager.waitIfNeeded();

        JsonNode firstUser = jsonUtils.getRootNode().get("users").get(0);
        String username = firstUser.get("username").asText();
        String password = firstUser.get("password").asText();
        loginPage.enterEmail(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/app"), "Login failed for valid credentials!");
    }

    @Test(priority = 2)
    public void testInvalidLogin() {
        ThrottleManager.waitIfNeeded();

        JsonNode firstUser = jsonUtils.getRootNode().get("users").get(1);
        String username = firstUser.get("username").asText();
        String password = firstUser.get("password").asText();
        loginPage.enterEmail(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();
        // Example: check for error message or URL remains the same
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/login"), "Invalid login did not behave as expected!");
    }

    @Test(priority = 3)
    public void testForgotPassword() {
        ThrottleManager.waitIfNeeded();

        loginPage.clickForgotPassword();
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/password-new"), "Forgot password link did not navigate correctly!");
    }

    @Test(priority = 4, groups="registration")
    public void testRegisterHereLink() {
        ThrottleManager.waitIfNeeded();

        loginPage.clickRegisterHere();
        // Adjust the URL or element check as per your app's behavior
        Assert.assertTrue(getDriver().getCurrentUrl().contains("/register"), "Register link did not navigate correctly!");
    }
}