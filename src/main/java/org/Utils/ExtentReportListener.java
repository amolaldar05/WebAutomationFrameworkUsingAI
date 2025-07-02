package org.Utils;

import com.aventstack.extentreports.ExtentTest;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        ExtentReportUtils.initReports("./ExtentReport.html");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentReportUtils.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        result.setAttribute("extentTest", test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = (ExtentTest) result.getAttribute("extentTest");
        if (test != null) {
            test.pass("Test Passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = (ExtentTest) result.getAttribute("extentTest");
        if (test != null) {
            test.fail(result.getThrowable());
            try {
                String screenshotPath = ScreenshotUtils.takeScreenshot(result.getName());
                test.addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                test.warning("Screenshot capture failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = (ExtentTest) result.getAttribute("extentTest");
        if (test != null) {
            test.skip("Test Skipped");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportUtils.flushReports();
    }
}

