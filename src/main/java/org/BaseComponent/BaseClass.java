package org.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.Utils.ConfigReader;
import org.Utils.SeleniumGridManager;
import org.openqa.selenium.SessionNotCreatedException;
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
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseClass {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeSuite(alwaysRun = true)
    public void startSeleniumGrid() {
        System.out.println("🔧 Managing Selenium Grid lifecycle...");
        SeleniumGridManager.startHub();
        SeleniumGridManager.startNode();
    }
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
            System.out.println("🌐 Browser set from TestNG XML: " + browser);
        } else {
            browser = ConfigReader.getBrowser().toLowerCase();
            System.out.println("🌐 Browser set from config.properties: " + browser);
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

                    case "safari":
                        SafariOptions safariOptions = new SafariOptions();
                        System.out.println("⚠️ Safari does not support headless mode. Running in normal mode.");
                        capabilities.setBrowserName("safari");
                        capabilities.merge(safariOptions);
                        System.out.println("⚠️ Ensure macOS Grid node has SafariDriver enabled!");
                        break;

                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        if (isHeadless) edgeOptions.addArguments("--headless");
                        edgeOptions.addArguments("--inprivate");
                        capabilities.merge(edgeOptions);
                        capabilities.setBrowserName("MicrosoftEdge");
                        break;

                    default:
                        throw new IllegalArgumentException("❌ Unsupported browser for Grid: " + browser);
                }

                try {
                    driver.set(new RemoteWebDriver(new URI(hubUrl).toURL(), capabilities));
                    System.out.println("✅ Connected to Selenium Grid and started browser: " + browser);
                } catch (Exception gridException) {
                    System.err.println("⚠️ Grid issue detected (" + gridException.getClass().getSimpleName() + "): " + gridException.getMessage());
                    System.err.println("➡️ Falling back to local WebDriver for browser: " + browser);
                    startLocalDriver(browser, isHeadless);
                }

            } else {
                System.out.println("⚡ Running tests locally...");
                startLocalDriver(browser, isHeadless);
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

    private void startLocalDriver(String browser, boolean isHeadless) {
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (isHeadless) chromeOptions.addArguments("--headless=new");
                chromeOptions.addArguments("--incognito");
                driver.set(new ChromeDriver(chromeOptions));
                System.out.println("✅ Started Chrome locally.");
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) firefoxOptions.addArguments("--headless");
                firefoxOptions.addArguments("-private");
                driver.set(new FirefoxDriver(firefoxOptions));
                System.out.println("✅ Started Firefox locally.");
                break;

            case "safari":
                if (isMac()) {
                    enableSafariDriver(); // ✅ Auto-enable SafariDriver on macOS
                    SafariOptions safariOptions = new SafariOptions();
                    System.out.println("⚠️ Safari does not support headless mode. Running in normal mode.");
                    driver.set(new SafariDriver(safariOptions));
                    System.out.println("✅ Started Safari locally.");
                } else {
                    throw new UnsupportedOperationException("❌ Safari is only supported on macOS!");
                }
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (isHeadless) edgeOptions.addArguments("--headless");
                edgeOptions.addArguments("--inprivate");
                driver.set(new EdgeDriver(edgeOptions));
                System.out.println("✅ Started Edge locally.");
                break;

            default:
                throw new IllegalArgumentException("❌ Unsupported browser for local execution: " + browser);
        }
    }

    private boolean isMac() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("mac");
    }

    private void enableSafariDriver() {
        try {
            System.out.println("🔄 Checking and enabling SafariDriver...");
            ProcessBuilder pb = new ProcessBuilder("safaridriver", "--enable");
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("SafariDriver: " + line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ SafariDriver enabled successfully.");
            } else {
                System.out.println("⚠️ SafariDriver enable command returned exit code: " + exitCode);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Unable to auto-enable SafariDriver. Run 'safaridriver --enable' manually.");
        }
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("❌ WebDriver not initialized for this thread!");
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
        System.out.println("🧹 Cleaning up Selenium Grid...");
        SeleniumGridManager.stopNode();
        SeleniumGridManager.stopHub();
    }
}
