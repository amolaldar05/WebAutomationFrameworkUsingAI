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
    int productIndex;

    public ProductListingPageObjects(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    By logoTxt = By.xpath("//p[text()='Automation Practice']");

    @FindBy(css = ".btn.btn-custom")
    List<WebElement> headerMenus;

    @FindBy(css = "div[aria-label='Logout Successfully']")
    private WebElement logoutSuccessMsg;

    @FindBy(css = ".card-body h5 b")
    List<WebElement> productNames;

    @FindBy(css = ".btn.w-10.rounded")
    List<WebElement> addToCartButtons;

    @FindBy (css="div[class='ng-tns-c31-1 la-3x la-ball-scale-multiple ng-star-inserted']")
    WebElement spinner;

    @FindBy(css = "div[aria-label='Product Added To Cart']")
    private WebElement productAddedSuccessMsg;




    public String getLogoTxt() {
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

                        () -> {
                            throw new RuntimeException("Menu not found: " + menuName);
                        }
                );
        wait.until(ExpectedConditions.visibilityOfAllElements(logoutSuccessMsg));
        return logoutSuccessMsg.getText();
    }

    public void getIndexSpceProduct(String productName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
        productNames.stream()
                .filter(name -> name.getText().equalsIgnoreCase(productName))
                .findFirst()
                .ifPresent(name -> {
                    productIndex = productNames.indexOf(name);
                    System.out.println("Matching product index: " + productIndex);
                });

    }

    public void clickAddToCartBtnSpecProduct(String productName) {
        wait.until(ExpectedConditions.visibilityOfAllElements(addToCartButtons));
        addToCartButtons.stream()
                .filter(button -> productNames.get(addToCartButtons.indexOf(button)).getText().equalsIgnoreCase(productName))
                .findFirst()
                .ifPresentOrElse(
                        WebElement::click,
                        () -> {
                            throw new RuntimeException("Add to Cart button not found for product: " + productName);
                        });
    }

    public String getProductAddedSuccessMsg() {
        try {
            wait.until(ExpectedConditions.invisibilityOf(spinner));
            wait.until(ExpectedConditions.visibilityOf(productAddedSuccessMsg));
            return productAddedSuccessMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get product added success message: " + e.getMessage(), e);
        }
    }

    public String getCartSuccessMsg() {
        return "";
    }
}



