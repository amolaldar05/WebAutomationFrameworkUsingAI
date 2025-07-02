package org.Utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.BaseComponent.BaseClass.driver;

public class ScreenshotUtils {

    public static String takeScreenshot(String fileName) {

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File("screenshots" + File.separator + fileName + ".png");
        destFile.getParentFile().mkdirs();
        try {
            Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destFile.getAbsolutePath();
    }


}
