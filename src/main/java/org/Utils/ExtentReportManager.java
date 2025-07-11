package org.Utils;




import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {
    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            // If I want the report to be overwritten every time, then use below code
            String reportPath = System.getProperty("user.dir") + "/reports/extent-report.html";

//            If I want the report should not be overwritten, then use below code
//            String timestamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//            String reportPath = System.getProperty("user.dir") + "/reports/extent-report-" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setReportName("Automation Test Report");
            sparkReporter.config().setDocumentTitle("Test Execution Report");
            sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Tester", ConfigReader.getTesterName());
        }
        return extent;
    }

}

