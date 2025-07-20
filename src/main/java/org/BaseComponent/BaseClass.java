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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseClass {

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
            // Check if running in Docker container
            boolean isDocker = System.getenv("DOCKER") != null;

            if (runMode.equals("grid")) {
                System.out.println("✅ Attempting to run tests on Selenium Grid: " + hubUrl);

                DesiredCapabilities capabilities = new DesiredCapabilities();

                switch (browser) {
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        if (isHeadless) chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--no-sandbox");
                        chromeOptions.addArguments("--disable-dev-shm-usage");
                        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                        break;

                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        if (isHeadless) firefoxOptions.addArguments("--headless");
                        capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                        break;

                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        if (isHeadless) edgeOptions.addArguments("--headless");
                        capabilities.setCapability(EdgeOptions.CAPABILITY, edgeOptions);
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported browser for Grid: " + browser);
                }

                driver.set(new RemoteWebDriver(new URI(hubUrl).toURL(), capabilities));

            } else {
                System.out.println("⚡ Running tests locally...");
                switch (browser) {
                    case "chrome":
                        if (!isDocker) {
                            WebDriverManager.chromedriver().setup();
                        }
                        ChromeOptions chromeOptions = new ChromeOptions();
                        if (isHeadless) chromeOptions.addArguments("--headless=new");
                        driver.set(new ChromeDriver(chromeOptions));
                        break;

                    case "firefox":
                        if (!isDocker) {
                            WebDriverManager.firefoxdriver().setup();
                        }
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        if (isHeadless) firefoxOptions.addArguments("--headless");
                        driver.set(new FirefoxDriver(firefoxOptions));
                        break;

                    case "edge":
                        if (!isDocker) {
                            WebDriverManager.edgedriver().setup();
                        }
                        EdgeOptions edgeOptions = new EdgeOptions();
                        if (isHeadless) edgeOptions.addArguments("--headless");
                        driver.set(new EdgeDriver(edgeOptions));
                        break;

                    case "safari":
                        if (isMac()) {
                            SafariOptions safariOptions = new SafariOptions();
                            driver.set(new SafariDriver(safariOptions));
                        } else {
                            throw new UnsupportedOperationException("❌ Safari is only supported on macOS!");
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("Unsupported browser: " + browser);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to initialize WebDriver: " + e.getMessage(), e);
        }

        // Common settings
        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        getDriver().manage().window().maximize();

        if (baseUrl != null && !baseUrl.isEmpty()) {
            getDriver().get(baseUrl);
        }
    }

    private boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
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
