package org.Utils;

import org.BaseComponent.BaseClass;
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


    public static String takeScreenshot(String fileName) {
        WebDriver driver = BaseClass.getDriver(); // Thread-safe
        if (driver == null) {
            throw new RuntimeException("❌ WebDriver instance is null. Cannot capture screenshot.");
        }
        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String relativePath = "screenshots/" + fileName + "_" + timestamp + ".png";
            File destFile = new File(System.getProperty("user.dir"), relativePath);
            destFile.getParentFile().mkdirs();
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to save screenshot: " + e.getMessage(), e);
        }
    }


}
