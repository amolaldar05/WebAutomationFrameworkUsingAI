package org.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.Utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseClass {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"}) // Reads browser parameter from TestNG XML
    public void setUp(@Optional String browserFromXML) {
        initializeDriver(browserFromXML);
    }

    public void initializeDriver(String browserFromXML) {
        String browser;

        // 1. Prefer browser from TestNG XML
        if (browserFromXML != null && !browserFromXML.trim().isEmpty()) {
            browser = browserFromXML.toLowerCase();
            System.out.println("Browser set from TestNG XML parameter: " + browser);
        } else {
            // 2. Fallback to config.properties
            browser = ConfigReader.getBrowser().toLowerCase();
            System.out.println("Browser set from config.properties: " + browser);
        }

        String url = ConfigReader.getBaseUrl();
        boolean isHeadless = ConfigReader.isHeadless();

        int timeout = ConfigReader.getTimeout(); // read timeout in seconds

        switch (browser) {
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
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        // Common driver settings
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        getDriver().manage().window().maximize();

        // Navigate to the base URL
        if (url != null && !url.isEmpty()) {
            getDriver().get(url);
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("WebDriver not initialized for this thread!");
        }
        return driver.get();
    }

    @AfterMethod( alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}
