package org.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.Utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseClass3 {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "runMode", "hubUrl"}) // runMode: local or grid
    public void setUp(@Optional String browserFromXML,
                      @Optional String runModeFromXML,
                      @Optional String hubUrlFromXML) throws MalformedURLException, URISyntaxException {

        initializeDriver(browserFromXML, runModeFromXML, hubUrlFromXML);
    }

    public void initializeDriver(String browserFromXML, String runModeFromXML, String hubUrlFromXML) throws MalformedURLException, URISyntaxException {
        String browser;

        // Browser selection
        if (browserFromXML != null && !browserFromXML.trim().isEmpty()) {
            browser = browserFromXML.toLowerCase();
            System.out.println("Browser set from TestNG XML: " + browser);
        } else {
            browser = ConfigReader.getBrowser().toLowerCase();
            System.out.println("Browser set from config.properties: " + browser);
        }

        String runMode = (runModeFromXML != null && !runModeFromXML.trim().isEmpty())
                ? runModeFromXML.toLowerCase()
                : ConfigReader.getRunMode(); // "local" or "grid"

        String hubUrl = (hubUrlFromXML != null && !hubUrlFromXML.trim().isEmpty())
                ? hubUrlFromXML
                : ConfigReader.getHubUrl();

        boolean isHeadless = ConfigReader.isHeadless();
        int timeout = ConfigReader.getTimeout();
        String baseUrl = ConfigReader.getBaseUrl();

        try {
            if (runMode.equals("grid")) {
                // Run in Selenium Grid
                System.out.println("✅ Attempting to run tests on Selenium Grid: " + hubUrl);

                DesiredCapabilities capabilities = new DesiredCapabilities();

                switch (browser) {
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        if (isHeadless) chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--incognito");
                        capabilities.merge(chromeOptions);
                        capabilities.setBrowserName("chrome");
                        break;

                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        if (isHeadless) firefoxOptions.addArguments("--headless");
                        firefoxOptions.addArguments("-private");
                        capabilities.merge(firefoxOptions);
                        capabilities.setBrowserName("firefox");
                        break;

                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        if (isHeadless) edgeOptions.addArguments("--headless");
                        edgeOptions.addArguments("--inprivate");
                        capabilities.merge(edgeOptions);
                        capabilities.setBrowserName("MicrosoftEdge");
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported browser for Grid: " + browser);
                }

                // Try connecting to Grid Hub
                driver.set(new RemoteWebDriver(new URI(hubUrl).toURL(), capabilities));
            } else {
                throw new Exception("Skipping Grid setup. Running locally instead.");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not connect to Grid Hub. Falling back to local WebDriver...");
            System.err.println("Error: " + e.getMessage());

            // Fallback to local execution
            switch (browser) {
                case "chrome":
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    if (isHeadless) chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--incognito");
                    driver.set(new org.openqa.selenium.chrome.ChromeDriver(chromeOptions));
                    break;

                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    if (isHeadless) firefoxOptions.addArguments("--headless");
                    firefoxOptions.addArguments("-private");
                    driver.set(new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions));
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    if (isHeadless) edgeOptions.addArguments("--headless");
                    edgeOptions.addArguments("--inprivate");
                    driver.set(new org.openqa.selenium.edge.EdgeDriver(edgeOptions));
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported browser: " + browser);
            }
        }

        // Common settings
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        getDriver().manage().window().maximize();

        if (baseUrl != null && !baseUrl.isEmpty()) {
            getDriver().get(baseUrl);
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("WebDriver not initialized for this thread!");
        }
        return driver.get();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}
