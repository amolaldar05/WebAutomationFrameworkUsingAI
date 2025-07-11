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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

public class BaseClass {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        initializeDriver();
    }

    public void initializeDriver() {
        String browser = ConfigReader.getBrowser().toLowerCase();
        String url = ConfigReader.getBaseUrl();
        boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));
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
                throw new IllegalArgumentException("❌ Browser not supported: " + browser);
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
        return driver.get();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}
