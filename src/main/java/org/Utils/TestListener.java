package org.Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.BaseComponent.BaseClass;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ExtentReportManager.getInstance();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        ExtentTest extentTest = extent.createTest(methodName);
        test.set(extentTest);
        test.get().info("🚀 Test started: " + methodName);
        System.out.println("🚀 Test started: " + methodName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().log(Status.PASS, "✅ Test Passed");
        System.out.println("✅ Test passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        try {
            String screenshotPath = ScreenshotUtils.takeScreenshot(BaseClass.getDriver(),
                    methodName + "_FAILED_" + System.currentTimeMillis()
            );
            test.get()
                    .fail("❌ Test Failed: " + result.getThrowable())
                    .addScreenCaptureFromPath(screenshotPath);
            System.out.println("📸 Screenshot taken for failed test: " + screenshotPath);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to capture screenshot: " + e.getMessage());
            test.get().fail("❌ Test Failed but screenshot not captured: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get().log(Status.SKIP, "⚠️ Test Skipped: " + result.getThrowable());
        System.out.println("⚠️ Test skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        System.out.println("📄 Report generated at: " + System.getProperty("user.dir") + "/reports/extent-report.html");
    }
}
