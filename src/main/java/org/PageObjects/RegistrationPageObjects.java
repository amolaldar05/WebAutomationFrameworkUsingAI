package org.PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegistrationPageObjects {
    private WebDriver driver;
    WebDriverWait wait;

    @FindBy(id = "firstName")
    private WebElement firstNameInput;

    @FindBy(id = "lastName")
    private WebElement lastNameInput;

    @FindBy(id = "userEmail")
    private WebElement emailInput;

    @FindBy(id = "userMobile")
    private WebElement phoneInput;

    @FindBy(xpath = "//select[@formcontrolname='occupation']")
    private WebElement occupationSelect;

    @FindBy(xpath = "//input[@type='radio' and @value='Male']")
    private WebElement genderMaleRadio;

    @FindBy(xpath = "//input[@type='radio' and @value='Female']")
    private WebElement genderFemaleRadio;

    @FindBy(id = "userPassword")
    private WebElement passwordInput;

    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordInput;

    @FindBy(xpath = "//input[@type='checkbox' and @formcontrolname='required']")
    private WebElement ageCheckbox;

    @FindBy(css = "input[value='Register']")
    private WebElement registerButton;

    @FindBy(xpath = "//h1[text()='Account Created Successfully']")
    private WebElement accountCreatedText;

    @FindBy(xpath = "//button[text()='login']")
    private WebElement loginBtn;


    public RegistrationPageObjects(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void enterFirstName(String firstName) {
        try {
            firstNameInput.sendKeys(firstName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter first name: " + e.getMessage(), e);
        }
    }

    public void enterLastName(String lastName) {
        try {
            lastNameInput.sendKeys(lastName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter last name: " + e.getMessage(), e);
        }
    }

    public void enterEmail(String email) {
        try {
            emailInput.sendKeys(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter email: " + e.getMessage(), e);
        }
    }

    public void enterPhone(String phone) {
        try {
            phoneInput.sendKeys(phone);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter phone: " + e.getMessage(), e);
        }
    }

    public void selectOccupation(String occupationVisibleText) {
        try {
            new Select(occupationSelect).selectByVisibleText(occupationVisibleText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to select occupation: " + e.getMessage(), e);
        }
    }

    public void selectGender(String gender) {
        try {
            if (gender.equalsIgnoreCase("Male")) {
                genderMaleRadio.click();
            } else if (gender.equalsIgnoreCase("Female")) {
                genderFemaleRadio.click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to select gender: " + e.getMessage(), e);
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

    public void enterConfirmPassword(String confirmPassword) {
        try {
            confirmPasswordInput.clear();
            confirmPasswordInput.sendKeys(confirmPassword);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter confirm password: " + e.getMessage(), e);
        }
    }

    public void checkAgeCheckbox() {
        try {
            if (!ageCheckbox.isSelected()) {
                ageCheckbox.click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to check age checkbox: " + e.getMessage(), e);
        }
    }

    public String clickRegister() {
        try {
            registerButton.click();
            wait.until(ExpectedConditions.visibilityOf(accountCreatedText));
            return accountCreatedText.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to register: " + e.getMessage(), e);
        }
    }
}
