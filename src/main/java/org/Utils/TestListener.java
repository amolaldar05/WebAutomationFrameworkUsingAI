package org.Utils;

import org.BaseComponent.BaseClass;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

    public class TestListener extends TestListenerAdapter {
        @Override
        public void onTestFailure(ITestResult result) {
            String testName = result.getName();
            ScreenshotUtils.takeScreenshot(testName);
            WebDriver driver = BaseClass.getDriver();
            if (driver != null) {
                driver.quit();
                BaseClass.driver.remove();
            }
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            WebDriver driver = BaseClass.getDriver();
            if (driver != null) {
                driver.quit();
                BaseClass.driver.remove();
            }
        }
    }
