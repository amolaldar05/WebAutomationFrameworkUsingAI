package org.BaseComponent;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.Utils.ConfigReader;
import org.Utils.SeleniumGridManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;

public class BaseClass2 {

    public static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static boolean gridStarted = false;

    @BeforeSuite(alwaysRun = true)
    public void startGridIfNeeded() {
        if (ConfigReader.getRunMode().equalsIgnoreCase("grid") && !gridStarted) {
            String seleniumJar = ConfigReader.getSeleniumServerJarPath(); // add this in config.properties
            String hubIp = ConfigReader.getHubIp();                 // add in config.properties
            int hubPort = Integer.parseInt(ConfigReader.getHubPort());                // add in config.properties

            SeleniumGridManager.startHub();
            SeleniumGridManager.startNode();
            gridStarted = true;
        }
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "runMode", "hubUrl"}) // runMode: local or grid
    public void setUp(@Optional String browserFromXML,
                      @Optional String runModeFromXML,
                      @Optional String hubUrlFromXML) throws MalformedURLException, URISyntaxException {

        initializeDriver(browserFromXML, runModeFromXML, hubUrlFromXML);
    }

    public void initializeDriver(String browserFromXML, String runModeFromXML, String hubUrlFromXML) throws MalformedURLException, URISyntaxException {
        String browser = (browserFromXML != null && !browserFromXML.trim().isEmpty())
                ? browserFromXML.toLowerCase()
                : ConfigReader.getBrowser().toLowerCase();

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
                System.out.println("✅ Running tests on Selenium Grid: " + hubUrl);

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

                driver.set(new RemoteWebDriver(new URI(hubUrl).toURL(), capabilities));
            } else {
                throw new Exception("Skipping Grid setup. Running locally...");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Grid unavailable. Falling back to local WebDriver.");
            System.err.println("Error: " + e.getMessage());

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

    @AfterSuite(alwaysRun = true)
    public void stopGridIfStarted() {
        if (gridStarted) {
            SeleniumGridManager.stopHub();
            SeleniumGridManager.stopNode();
        }
    }
}
