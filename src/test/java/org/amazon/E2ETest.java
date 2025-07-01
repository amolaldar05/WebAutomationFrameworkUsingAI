package org.amazon;

import org.BaseComponent.BaseClass;
import org.PageObjects.LoginPageObjects;

public class E2ETest extends BaseClass {

    public void e2eUserJourneyTest(){
        LoginPageObjects loginPageObjects= new LoginPageObjects(getDriver());
        loginPageObjects.enterEmail("amol@123.com");
        loginPageObjects.enterPassword("AMol@5463");
        loginPageObjects.clickLogin();

    }
}
