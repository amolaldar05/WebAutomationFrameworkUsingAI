package org.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPageObjects {

    private final WebDriver driver;

    public LoginPageObjects(WebDriver driver) {
        this.driver=driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "userEmail")
    private WebElement emailInput;

    @FindBy(id = "userPassword")
    private WebElement passwordInput;

    @FindBy(id = "login")
    private WebElement loginButton;

    @FindBy(css = ".forgot-password-link")
    private WebElement forgotPasswordLink;

    @FindBy(css = ".login-wrapper-footer-text .text-reset")
    private WebElement registerHereLink;

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

    public void clickForgotPassword() {
        forgotPasswordLink.click();
        driver.navigate().back();
    }

    public void clickRegisterHere() {
        registerHereLink.click();
    }
}

