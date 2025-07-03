package org.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductListingPageObjects {
    WebDriverWait wait;
    private WebDriver driver;

    public ProductListingPageObjects(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    By logoTxt= By.xpath("//p[text()='Automation Practice']");

    @FindBy (css = ".btn.btn-custom")
    List<WebElement> headerMenus;

    @FindBy(css = "div[aria-label='Logout Successfully']")
    private WebElement logoutSuccessMsg;

    public String getLogoTxt(){
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(logoTxt)));
        return driver.findElement(logoTxt).getText();
    }

    public String clickHeaderMenu(String menuName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(headerMenus));
        headerMenus.stream()
                .filter(menu -> menu.getText().equalsIgnoreCase(menuName))
                .findFirst()
                .ifPresentOrElse(
                        WebElement::click,

                        () -> { throw new RuntimeException("Menu not found: " + menuName); }
                );
        wait.until(ExpectedConditions.visibilityOfAllElements(logoutSuccessMsg));
        return logoutSuccessMsg.getText();
    }
}
