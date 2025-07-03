package org.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPageObjects {

    private final WebDriver driver;
    private WebDriverWait wait;


    @FindBy(id = "userEmail")
    private WebElement emailInput;

    @FindBy(id = "userPassword")
    private WebElement passwordInput;

    @FindBy(id = "login")
    private WebElement loginButton;

    @FindBy(css = "div[aria-label='Login Successfully']")
    private WebElement loginSuccessMsg;

    @FindBy(css = "div[aria-label='Incorrect email or password.']")
    private WebElement errorMessage;


    @FindBy(css = ".forgot-password-link")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//h3[text()='Enter New Password']")
    private WebElement newPassTitle;


    @FindBy(css = ".login-wrapper-footer-text .text-reset")
    private WebElement registerHereLink;

    @FindBy(css = "h1.login-title")
    private WebElement registerTitle;


    public LoginPageObjects(WebDriver driver) {
        this.driver=driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }
    public void enterEmail(String email) {
        try {
            emailInput.clear();
            emailInput.sendKeys(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter email: " + e.getMessage(), e);
        }
    }

    public void enterPassword(String password) {
        try {
            passwordInput.clear();
            passwordInput.sendKeys(password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter password: " + e.getMessage(), e);
        }
    }

    public void clickLogin() {
        try {
            loginButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click login button: " + e.getMessage(), e);
        }
    }
    public String getSuccessMsg(){
        try {
            wait.until(ExpectedConditions.visibilityOf(loginSuccessMsg));
//            wait.until(ExpectedConditions.invisibilityOf(loginSuccessMsg));
            return loginSuccessMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get success message: " + e.getMessage(), e);
        }
    }
    public String getErrorMsg(){
        try {
//            wait.until(ExpectedConditions.visibilityOf(errorMessage));
           wait.until(ExpectedConditions.invisibilityOf(errorMessage));
            return errorMessage.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get error message: " + e.getMessage(), e);
        }
    }

    public String clickForgotPassword() {
        try {
            forgotPasswordLink.click();
            return newPassTitle.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click forgot password link: " + e.getMessage(), e);
        }
    }

    public String clickRegisterHere() {
        try {
            registerHereLink.click();
            return registerTitle.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click register here link: " + e.getMessage(), e);
        }
    }
}
