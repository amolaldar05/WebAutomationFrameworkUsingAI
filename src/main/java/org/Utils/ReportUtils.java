package org.Utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportUtils implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        Reporter.log("Test Started: " + result.getName(), true);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Reporter.log("Test Passed: " + result.getName(), true);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Reporter.log("Test Failed: " + result.getName(), true);
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshot(result.getName());
            Reporter.log("<a href='" + screenshotPath + "'>Screenshot</a>", true);
        } catch (Exception e) {
            Reporter.log("Failed to capture screenshot: " + e.getMessage(), true);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Reporter.log("Test Skipped: " + result.getName(), true);
    }

    @Override
    public void onStart(ITestContext context) {
        Reporter.log("Test Suite Started: " + context.getName(), true);
    }

    @Override
    public void onFinish(ITestContext context) {
        Reporter.log("Test Suite Finished: " + context.getName(), true);
    }
}
