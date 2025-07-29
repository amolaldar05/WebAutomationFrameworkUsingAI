package org.BaseComponent;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.Utils.ConfigReader;
import org.Utils.SeleniumGridManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.safari.*;
import org.testng.annotations.*;

import java.net.URI;
import java.time.Duration;

public class BaseClass_Healenium {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void startSeleniumGrid() {
        System.out.println("🔧 Managing Selenium Grid lifecycle...");
        SeleniumGridManager.startHub();
        SeleniumGridManager.startNode();
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "runMode", "hubUrl"})
    public void setUp(@Optional String browserFromXML,
                      @Optional String runModeFromXML,
                      @Optional String hubUrlFromXML) throws Exception {
        initializeDriver(browserFromXML, runModeFromXML, hubUrlFromXML);
    }

    public void initializeDriver(String browserFromXML, String runModeFromXML, String hubUrlFromXML) throws Exception {
        // Use values from XML params if present, else from properties file
        String browser = (browserFromXML != null && !browserFromXML.isBlank())
                ? browserFromXML.toLowerCase()
                : ConfigReader.getBrowser().toLowerCase();

        String runMode = (runModeFromXML != null && !runModeFromXML.isBlank())
                ? runModeFromXML.toLowerCase()
                : ConfigReader.getRunMode().toLowerCase();

        String hubUrl = (hubUrlFromXML != null && !hubUrlFromXML.isBlank())
                ? hubUrlFromXML
                : ConfigReader.getHubUrl();
        boolean isHeadless = ConfigReader.isHeadless();
        int timeout = ConfigReader.getTimeout();
        String baseUrl = ConfigReader.getBaseUrl();
        boolean isHealingEnabled = ConfigReader.isHealingEnabled(); // Add this method in ConfigReader

        WebDriver baseDriver;

        if (runMode.equals("grid")) {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            switch (browser) {
                case "chrome" -> {
                    ChromeOptions options = new ChromeOptions();
                    if (isHeadless) options.addArguments("--headless=new");
                    options.addArguments("--incognito");
                    capabilities.merge(options);
                    capabilities.setBrowserName("chrome");
                }
                case "firefox" -> {
                    FirefoxOptions options = new FirefoxOptions();
                    if (isHeadless) options.addArguments("--headless");
                    options.addArguments("-private");
                    capabilities.merge(options);
                    capabilities.setBrowserName("firefox");
                }
                case "safari" -> {
                    capabilities.setBrowserName("safari");
                    SafariOptions options = new SafariOptions();
                    capabilities.merge(options);
                }
                case "edge" -> {
                    EdgeOptions options = new EdgeOptions();
                    if (isHeadless) options.addArguments("--headless");
                    options.addArguments("--inprivate");
                    capabilities.merge(options);
                    capabilities.setBrowserName("MicrosoftEdge");
                }
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
            }

            baseDriver = new RemoteWebDriver(new URI(hubUrl).toURL(), capabilities);
        } else {
            baseDriver = startLocalDriver(browser, isHeadless);
        }

        if (isHealingEnabled) {
            System.out.println("🩹 Healenium Healing Driver is ENABLED.");
            SelfHealingDriver healingDriver = SelfHealingDriver.create(baseDriver);

            driver.set(healingDriver);
        } else {
            System.out.println("🧪 Standard WebDriver is being used.");
            driver.set(baseDriver);
        }

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(timeout));
        getDriver().manage().window().maximize();

        if (baseUrl != null && !baseUrl.isEmpty()) {
            getDriver().get(baseUrl);
        }
    }

    private WebDriver startLocalDriver(String browser, boolean isHeadless) {
        return switch (browser) {
            case "chrome" -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (isHeadless) options.addArguments("--headless=new");
                options.addArguments("--incognito");
                yield new ChromeDriver(options);
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadless) options.addArguments("--headless");
                options.addArguments("-private");
                yield new FirefoxDriver(options);
            }
            case "safari" -> {
                if (!isMac()) throw new UnsupportedOperationException("Safari only supported on macOS");
                enableSafariDriver();
                yield new SafariDriver();
            }
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions options = new EdgeOptions();
                if (isHeadless) options.addArguments("--headless");
                options.addArguments("--inprivate");
                yield new EdgeDriver(options);
            }
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    private void enableSafariDriver() {
        try {
            ProcessBuilder pb = new ProcessBuilder("safaridriver", "--enable");
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            System.err.println("SafariDriver enable failed: " + e.getMessage());
        }
    }

    public static WebDriver getDriver() {

        if (driver.get() == null) {
            throw new IllegalStateException("WebDriver not initialized");
        }
        return driver.get();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver.get() != null) {
            getDriver().quit();
            driver.remove();
            System.out.println("✅ WebDriver closed and cleaned up.");
        }
    }

    @AfterSuite(alwaysRun = true)
    public void stopSeleniumGrid() {
        SeleniumGridManager.stopNode();
        SeleniumGridManager.stopHub();
    }
}
