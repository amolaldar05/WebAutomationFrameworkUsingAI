package org.amazon;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;
import org.PageObjects.ProductListingPageObjects;
import org.Utils.ConfigReader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class E2ETest extends BaseClass {
    SoftAssert softAssert = new SoftAssert();
    LoginPageObjects loginPageObjects;
    ProductListingPageObjects productListingPageObjects;
    String[] products={"ZARA COAT 3","ADIDAS ORIGINAL","IPHONE 13 PRO"};


    @BeforeMethod
    public void setup() {
        loginPageObjects = new LoginPageObjects(getDriver());
        productListingPageObjects = new ProductListingPageObjects(getDriver());
    }

    @Test(dataProvider="getLoginData",timeOut = 10000)
    public void e2eUserJourneyTest(String userEmail,String password){

        loginPageObjects.enterEmail(userEmail);
        loginPageObjects.enterPassword(password);
        loginPageObjects.clickLogin();
        String successMsg = loginPageObjects.getSuccessMsg();
        softAssert.assertEquals(successMsg, "Login Successfully", "Login was not successful");

//        productListingPageObjects.verifyCartCountIncrementAfterProductAdded("ADIDAS ORIGINAL");
        for(String product : products) {
            productListingPageObjects.clickAddToCartBtnSpecProduct(product);
            String productAddedToCartMsg = productListingPageObjects.getProductAddedSuccessMsg();
            softAssert.assertEquals(productAddedToCartMsg, "Product Added Successfully", "Product was not added to cart successfully: " + product);
        }
        productListingPageObjects.clickAddToCartBtnSpecProduct("ADIDAS ORIGINAL");
        String productAddedToCartMsg=productListingPageObjects.getProductAddedSuccessMsg();
        softAssert.assertEquals(productAddedToCartMsg, "Product Added Successfully", "Product was not added to cart successfully");
        productListingPageObjects.clickHeaderMenu("Cart");
        softAssert.assertAll();

    }


    @DataProvider(name = "getLoginData")
    public Object[][] getData() {
        return new Object[][]{{ConfigReader.getLoginEmail(), ConfigReader.getLoginPass()}};
    }

}
