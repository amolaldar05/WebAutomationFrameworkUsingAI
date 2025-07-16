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

public class HeaderObjects {
    private final WebDriver driver;
    private final WebDriverWait wait;
    // This class can be used to define common header elements or methods
    // that can be inherited by other page objects like ProductListingPageObjects.
    // Currently, it is empty but can be extended in the future as needed.
    By logoTxt = By.xpath("//p[text()='Automation Practice']");

    @FindBy(css = ".btn.btn-custom")
    List<WebElement> headerMenus;

    @FindBy(css = "div[aria-label='Logout Successfully']")
    private WebElement logoutSuccessMsg;

    public HeaderObjects(WebDriver driver){
       this. driver = driver;
       PageFactory.initElements(driver, this); // Uncomment if using PageFactory
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public String getLogoTxt() {
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(logoTxt)));
        return driver.findElement(logoTxt).getText();
    }

    public void clickHeaderMenu(String menuName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(headerMenus));
        if (headerMenus.isEmpty()) {
            throw new RuntimeException("Header menus are not loaded or empty.");
        }
        headerMenus.stream()
                .filter(menu -> menu.getText().trim().equalsIgnoreCase(menuName.trim()))
                .findFirst()
                .ifPresentOrElse(
                        WebElement::click,

                        () -> {
                            throw new RuntimeException("Menu not found: " + menuName);
                        }
                );

    }

    public String getLogoutSuccessMsg() {
        wait.until(ExpectedConditions.visibilityOf(logoutSuccessMsg));
        return logoutSuccessMsg.getText();
    }
}
