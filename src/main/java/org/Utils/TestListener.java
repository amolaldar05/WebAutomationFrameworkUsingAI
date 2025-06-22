package org.Utils;

import org.BaseComponent.BaseClass;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

    public class TestListener extends TestListenerAdapter {
        @Override
        public void onTestFailure(ITestResult result) {
            Object testClass = result.getInstance();
            WebDriver driver = BaseClass.getDriver();
            String testName = result.getName();
            ScreenshotUtils.takeScreenshot(testName);
        }
    }

