package com.example.backend;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // allow React dev server
public class ShipController {

    @PostMapping("/login")
    public String loginOnly() {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://eshiponline.purolator.com/ShipOnline/SecurePages/Public/FormsLogin.aspx?ReturnUrl=/ShipOnline/Welcome.aspx&lang=E");
            Backend.login(driver);
            driver.findElement(By.id("ctl00_CPPC_LinkButtonNewSession")).click();
            Backend.login(driver);

            return "Logged in!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Login failed: " + e.getMessage();
        } finally {
            driver.quit();
        }
    }

    @PostMapping("/ship")
    public String shipLocations(@RequestBody List<String> locations) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://eshiponline.purolator.com/ShipOnline/SecurePages/Public/FormsLogin.aspx?ReturnUrl=/ShipOnline/Welcome.aspx&lang=E");
            String mainWindow = driver.getWindowHandle();

            // Login and navigation
            Backend.login(driver);
            driver.findElement(By.id("ctl00_CPPC_LinkButtonNewSession")).click();
            Backend.login(driver);
            driver.findElement(By.name("ctl00$CPPC$btnCreateShipment")).click();
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu']/ul/li[2]/a")).click();
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu:submenu:9']/li[1]/a")).click();

            for (int i = 0; i < locations.size(); i++) {
                String location = locations.get(i);

                // Open address book
                driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu']/ul/li[2]/a")).click();
                driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu:submenu:9']/li[1]/a")).click();

                // Switch to new window for address book
                Backend.switchToNewWindow(driver, mainWindow);

                // Search and select location
                driver.findElement(By.name("ctl00$CPPC$txtFilterString")).clear();
                driver.findElement(By.name("ctl00$CPPC$txtFilterString")).sendKeys(location);
                driver.findElement(By.name("ctl00$CPPC$btnSearch")).click();
                driver.findElement(By.partialLinkText(location)).click();

                // Switch back to main window and proceed to shipping
                Backend.switchToMainWindow(driver, mainWindow);
                Thread.sleep(1000); // optional, can help with timing issues

                driver.findElement(By.name("ctl00$CPPC$btnNext")).click();
                driver.findElement(By.id("idCharac_PCH_HEALTHCARE")).click();
                driver.findElement(By.name("ctl00$CPPC$btnShip")).click();

                // Confirm shipment in new window
                Backend.switchToNewWindow(driver, mainWindow);
                driver.findElement(By.name("ctl00$CPPC$ConfirmMessage_OSNR$btnOk")).click();
                Backend.switchToMainWindow(driver, mainWindow);

                // Start a new shipment again for next location
                driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu']/ul/li[1]/a")).click();
                driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu:submenu:2']/li[1]/a")).click();

                Thread.sleep(2000); // give time to load before next iteration
            }


            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            driver.quit();
        }
    }
}
