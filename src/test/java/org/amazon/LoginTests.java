package org.amazon;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;
import org.PageObjects.ProductListingPageObjects;
import org.Utils.JsonUtils;
import org.Utils.MySQLUtils;
import org.Utils.ThrottleManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

//@Listeners(org.Utils.RetryListener.class)
public class LoginTests extends BaseClass {
	private LoginPageObjects loginPage;
	JsonUtils jsonUtils;
	SoftAssert softAssert = new SoftAssert();

	@BeforeMethod
	public void setUpPage() throws IOException {
		loginPage = new LoginPageObjects(getDriver());
		jsonUtils = new JsonUtils("src/main/java/org/resources/testdata.json");
	}

	@Test(groups = { "regression" })
	public void validLoginTest() throws IOException, InterruptedException, SQLException {
		ThrottleManager.waitIfNeeded();
		ProductListingPageObjects productListingPageObjects = new ProductListingPageObjects(getDriver());
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
		String loginSucMsg = loginPage.getSuccessMsg();
		System.out.println("loginSucMsg:====" + loginSucMsg);
		String logoTxt = productListingPageObjects.getLogoTxt();
		softAssert.assertEquals(loginSucMsg,"Login Successfully");
		softAssert.assertEquals(logoTxt, "Automation Practice");
		softAssert.assertAll();
	}

	@Test(priority = 1, groups = { "regression" })
	public void invalidLoginTest() {
		ThrottleManager.waitIfNeeded();

		JsonNode firstUser = jsonUtils.getRootNode().get("users").get(1);
		String username = firstUser.get("username").asText();
		String password = firstUser.get("password").asText();
		loginPage.enterEmail(username);
		loginPage.enterPassword(password);
		loginPage.clickLogin();
		String errorMsg = loginPage.getErrorMsg();
		softAssert.assertEquals(errorMsg, "Incorrect email or password.");
		softAssert.assertAll();
	}

	@Test(priority = 2, groups = {"smoke"})
	public void forgotPasswordLinkTest() throws InterruptedException {
		ThrottleManager.waitIfNeeded();
		String title = loginPage.clickForgotPassword();
		softAssert.assertEquals(title, "Enter New Password");
		softAssert.assertAll();
		getDriver().navigate().back();
		// Assert.assertTrue(getDriver().getCurrentUrl().contains("/password-new"),
		// "Forgot password link did not navigate correctly!");
	}

	@Test(priority = 3, groups = {"smoke"})
	public void registerHereLinkTest() throws InterruptedException {
		ThrottleManager.waitIfNeeded();
		String registerTitle = loginPage.clickRegisterHere();
		softAssert.assertEquals(registerTitle, "Register");
		softAssert.assertAll();
		getDriver().navigate().back();

	}
}