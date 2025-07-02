package org.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ProductListingPageObjects {
    WebDriverWait wait;
    private WebDriver driver;

    public ProductListingPageObjects(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    By logoTxt= By.xpath("//p[text()='Automation Practice']");

    public String getLogoTxt(){
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(logoTxt)));
        return driver.findElement(logoTxt).getText();
    }
}
