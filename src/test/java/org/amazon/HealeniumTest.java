package org.amazon;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HealeniumTest {

    private SelfHealingDriver healingDriver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        healingDriver = SelfHealingDriver.create(driver);
        healingDriver.manage().window().maximize();
        healingDriver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
    }

    @Test
    public void testHealeniumFunctionality() throws InterruptedException {
        healingDriver.get("https://rahulshettyacademy.com/client/");
        // Intentionally use wrong locator id to trigger healing
        healingDriver.findElement(By.cssSelector("userEmail")).sendKeys("jivan@gmail.com");
        healingDriver.findElement(By.xpath("userPassword")).sendKeys("jivan123");
        healingDriver.findElement(By.id("login")).click();

        // Add a small wait to see the result (replace with explicit wait in real code)
        Thread.sleep(3000);

        // Add assertion or further checks here to confirm login success
    }

    @AfterClass
    public void tearDown() {
        if (healingDriver != null) {
            healingDriver.quit();
        }
    }
}
