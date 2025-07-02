package org.Utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.Getter;

public class ExtentReportUtils {
    private static ExtentReports extent;
    @Getter
    private static ExtentTest test;
    private static ExtentSparkReporter htmlReporter;

    public static void initReports(String reportPath) {
        if (extent == null) {
            htmlReporter = new ExtentSparkReporter(reportPath);
            htmlReporter.config().setTheme(Theme.STANDARD);
            htmlReporter.config().setDocumentTitle("Automation Test Report");
            htmlReporter.config().setReportName("Test Execution Report");
            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);
        }
    }

    public static ExtentTest createTest(String testName, String description) {
        test = extent.createTest(testName, description);
        return test;
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}

