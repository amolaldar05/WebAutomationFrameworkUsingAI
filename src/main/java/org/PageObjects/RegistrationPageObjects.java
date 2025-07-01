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
        firstNameInput.sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        lastNameInput.sendKeys(lastName);
    }

    public void enterEmail(String email) {
        emailInput.sendKeys(email);
    }

    public void enterPhone(String phone) {
        phoneInput.sendKeys(phone);
    }

    public void selectOccupation(String occupationVisibleText) {
        new Select(occupationSelect).selectByVisibleText(occupationVisibleText);
    }

    public void selectGender(String gender) {
        if (gender.equalsIgnoreCase("Male")) {
            genderMaleRadio.click();
        } else if (gender.equalsIgnoreCase("Female")) {
            genderFemaleRadio.click();
        }
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(confirmPassword);
    }

    public void checkAgeCheckbox() {
        if (!ageCheckbox.isSelected()) {
            ageCheckbox.click();
        }
    }

    public String clickRegister() {

        registerButton.click();
        wait.until(ExpectedConditions.visibilityOf(accountCreatedText));
        return accountCreatedText.getText();

    }
}
