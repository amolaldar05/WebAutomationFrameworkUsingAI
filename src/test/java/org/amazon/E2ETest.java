package org.amazon;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;
import org.PageObjects.ProductListingPageObjects;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class E2ETest extends BaseClass {
    SoftAssert softAssert = new SoftAssert();
    LoginPageObjects loginPageObjects;
    ProductListingPageObjects productListingPageObjects;
    @Test
    public void e2eUserJourneyTest(){
        loginPageObjects= new LoginPageObjects(getDriver());
        loginPageObjects.enterEmail("Jivan@gmail.com");
        loginPageObjects.enterPassword("Jivan@7799");
        loginPageObjects.clickLogin();
        String successMsg = loginPageObjects.getSuccessMsg();
        softAssert.assertEquals(successMsg, "Login Successfully", "Login was not successful");
        productListingPageObjects = new ProductListingPageObjects(getDriver());
        productListingPageObjects.clickAddToCartBtnSpecProduct("ADIDAS ORIGINAL");
        String productAddedToCartMsg=productListingPageObjects.getProductAddedSuccessMsg();
        softAssert.assertEquals(productAddedToCartMsg, "Product Added Successfully", "Product was not added to cart successfully");
        String cartMsg = productListingPageObjects.getCartSuccessMsg();




    }
}
