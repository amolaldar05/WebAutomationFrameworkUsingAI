package org.Utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.BaseComponent.BaseClass.driver;

public class ScreenshotUtils {


    public static String takeScreenshot(WebDriver driver, String fileName) {
        if (driver == null) {
            throw new RuntimeException("❌ WebDriver instance is null. Cannot capture screenshot.");
        }
        try {
            // Take screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            // Define path
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String relativePath = "screenshots/" + fileName + "_" + timestamp + ".png";
            File destFile = new File(System.getProperty("user.dir"), relativePath);

            // Create parent folders
            destFile.getParentFile().mkdirs();

            // Save screenshot
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());
            return relativePath; // return relative path for report
        } catch (Exception e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

}
