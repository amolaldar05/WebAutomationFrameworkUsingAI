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

public class ProductListingPageObjects extends HeaderObjects {
    WebDriverWait wait;
    private WebDriver driver;
    int productIndex;

    public ProductListingPageObjects(WebDriver driver) {
        super(driver);
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @FindBy(css = ".card-body h5 b")
    List<WebElement> productNames;

    @FindBy(css = ".btn.w-10.rounded")
    List<WebElement> addToCartButtons;

    @FindBy (css="div[class='ng-tns-c31-1 la-3x la-ball-scale-multiple ng-star-inserted']")
    WebElement spinner;

    @FindBy(css = "div[aria-label='Product Added To Cart']")
    private WebElement productAddedSuccessMsg;

    By cartCountLabel = By.xpath("//button[@class='btn btn-custom']//label");

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



    public String clickAddToCartBtnSpecProduct(String productName) {
        getIndexSpceProduct(productName);
        if (productIndex >= 0 && productIndex < addToCartButtons.size()) {
            addToCartButtons.get(productIndex).click();
            // Wait for spinner hidden or toast visible
//            wait.until(ExpectedConditions.or(
//                    ExpectedConditions.not(ExpectedConditions.visibilityOf(spinner)),
//                    ExpectedConditions.visibilityOf(productAddedSuccessMsg)
//            ));



            // Immediately capture toast before it disappears
            String toastMsg = getProductAddedSuccessMsg();
            return toastMsg;
        } else {
            throw new RuntimeException("Add to Cart button not found for product: " + productName);
        }
    }



/*
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
    }*/

//    public void clickAddToCartBtnSpecProduct(String productName) {
//        wait.until(ExpectedConditions.visibilityOfAllElements(productNames));
//       // List<WebElement> products = driver.findElements(By.cssSelector(".card"));
//
//        boolean productFound = false;
//        int index=0;
//        for (WebElement product : productNames) {
//            //String name = product.findElement(By.cssSelector("h5 b")).getText().trim();
//            if (product.getText().equalsIgnoreCase(productName)) {
//
//                addToCartButtons.get(productIndex).click();
//                productFound = true;
//                break;
//            }
//        }
//
//        if (!productFound) {
//            throw new RuntimeException("Add to Cart button not found for product: " + productName);
//        }
//    }


    public String getProductAddedSuccessMsg() {
        try {
            wait.until(ExpectedConditions.visibilityOf(productAddedSuccessMsg));
            // Wait for the success message to be visible
            return productAddedSuccessMsg.getText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get product added success message: " + e.getMessage(), e);
        }
    }



    public int getCartCount(WebDriver driver) {
        String countText = driver.findElement(cartCountLabel).getText().trim();
        if (countText.isEmpty()) {
            return 0; // Cart is empty
        }
        return Integer.parseInt(countText);
    }

//    public void verifyCartCountIncrementAfterProductAdded(String productName) {
//        int beforeCount = getCartCount(driver);
//        System.out.println("🛒 Cart count before click: " + beforeCount);
//
//        // Click the Add to Cart button
//        clickAddToCartBtnSpecProduct(productName);
//        getProductAddedSuccessMsg();
//
//        // Wait until the cart count increases
//        new WebDriverWait(driver, Duration.ofSeconds(5)).until(d -> {
//            int afterCount = getCartCount(driver);
//            return afterCount > beforeCount;
//        });
//
//        int afterCount = getCartCount(driver);
//        System.out.println("🛒 Cart count after click: " + afterCount);
//
//        if (afterCount == beforeCount + 1) {
//            System.out.println("✅ Cart count incremented successfully.");
//        } else {
//            throw new AssertionError("❌ Cart count did not increment as expected!");
//        }
    }






