package org.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
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

    @FindBy(xpath = "//div[@aria-label='Incorrect email or password.']")
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }
    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        loginButton.click();
    }
    public String getErrorMsg(){
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    public String clickForgotPassword() {
        forgotPasswordLink.click();
        return newPassTitle.getText();
    }

    public String clickRegisterHere() {
        registerHereLink.click();
        return registerTitle.getText();

    }
}
