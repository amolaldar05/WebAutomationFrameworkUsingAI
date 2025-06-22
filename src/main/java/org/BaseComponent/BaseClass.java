package org.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseClass {
    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private Properties prop;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws FileNotFoundException {
        initializeDriver();
    }

    public void initializeDriver() throws FileNotFoundException {
        prop = new Properties();
        FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/main/java/org/resources/config.properties");
        try {
            prop.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String browser = prop.getProperty("browser");
        String url = prop.getProperty("url");
        boolean isHeadless = Boolean.parseBoolean(prop.getProperty("headless", "false"));

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--incognito");
                if (isHeadless) chromeOptions.addArguments("--headless=new");
                driver.set(new ChromeDriver(chromeOptions));
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("-private");
                if (isHeadless) firefoxOptions.addArguments("--headless");
                driver.set(new FirefoxDriver(firefoxOptions));
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--inprivate");
                if (isHeadless) edgeOptions.addArguments("--headless");
                driver.set(new EdgeDriver(edgeOptions));
                break;
            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);

        }
        getDriver().manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
        getDriver().manage().window().maximize();

        if (url != null && !url.isEmpty()) {
            getDriver().get(url);
        }
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}