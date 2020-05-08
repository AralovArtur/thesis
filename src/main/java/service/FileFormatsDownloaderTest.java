package service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Program for downloading file formats
 *
 * @author Artur Aralov
 */
public class FileFormatsDownloaderTest {
    private WebDriver driver;
    private StringBuffer errors;

    @Before
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        Dimension dimension = new Dimension(1920,1080);
        //Resize current window to the set dimension
        driver.manage().window().setSize(dimension);
        errors = new StringBuffer();
    }

    public void downloadFileFormats()   {
        try {
            driver.get("https://www.tartu.ee/et/dokumendid");

            // Display all documents of a specific type (type and period should be changed manually)
            WebElement frame = driver.findElement(By.className("doc-iframe"));
            driver.switchTo().frame(frame);
            driver.findElement(By.name("WebView")).click();
            new Select(driver.findElement(By.name("WebView"))).selectByVisibleText("Väljaminevad kirjad");
            driver.findElement(By.name("WebView")).click();
            driver.findElement(By.name("jpp")).click();
            new Select(driver.findElement(By.name("jpp"))).selectByVisibleText("Avalikud");
            driver.findElement(By.name("jpp")).click();
            driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Märksõna(d):'])[1]/following::div[4]")).click();
            driver.findElement(By.id("edit-date-start")).click();
            driver.findElement(By.id("edit-date-start")).clear();
            driver.findElement(By.id("edit-date-start")).sendKeys("01.01.2019");
            driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Märksõna(d):'])[1]/following::div[4]")).click();
            driver.findElement(By.id("edit-date-end")).click();
            driver.findElement(By.id("edit-date-end")).clear();
            driver.findElement(By.id("edit-date-end")).sendKeys("31.12.2019");
            driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Juurdepääs:'])[1]/following::button[1]")).click();

            String oldTab = driver.getWindowHandle();
            // Get all links to documents
            while (true) {
                List<WebElement> linksToDocuments = driver.findElement(By.className("documents")).findElement(By.tagName("table")).findElements(By.tagName("a"));
                for (WebElement linkToDocument: linksToDocuments) {
                    linkToDocument.click();
                    // Switch to another tab
                    ArrayList<String> allTabs = new ArrayList<String>(driver.getWindowHandles());
                    allTabs.remove(oldTab);
                    driver.switchTo().window(allTabs.get(0));
                    try {
                        List<WebElement> files = driver.findElement(By.className("files")).findElements(By.tagName("a"));
                        for (WebElement file: files) {
                            if (file.getText().contains(".bdoc") || file.getText().contains(".ddoc") || file.getText().contains(".asice")) {
                                file.click();
                            }
                        }
                    } catch (Exception exception) {
                        // If there are no files related to this document, then just change focus back to old tab (do nothing)
                    }
                    // Change focus back to old tab
                    driver.close();
                    driver.switchTo().window(oldTab);
                    driver.switchTo().frame(frame);
                }
                // Go to next page, if exists
                if (driver.findElements(By.className("next")).size() > 0) {
                    driver.findElement(By.className("next")).click();
                    Thread.sleep(5000);
                }
                else {
                    break;
                }
            }
        } catch (Exception exception) {
            errors.append(exception);
        }
    }

    @Test
    public void testDownloadFileFormats() {
        downloadFileFormats();
        assertEquals(errors.toString(), "");
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}