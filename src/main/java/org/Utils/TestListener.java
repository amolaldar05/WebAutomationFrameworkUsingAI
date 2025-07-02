package org.Utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        // Code to execute when a test starts
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Code to execute when a test passes
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String screenshotPath = org.Utils.ScreenshotUtils.takeScreenshot(methodName + "_FAILED_" + System.currentTimeMillis());
        System.out.println("Screenshot taken for failed test: " + screenshotPath);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Code to execute when a test is skipped
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not used
    }

    @Override
    public void onStart(ITestContext context) {
        // Code to execute before any test starts
    }

    @Override
    public void onFinish(ITestContext context) {
        // Code to execute after all tests finish
    }
}
