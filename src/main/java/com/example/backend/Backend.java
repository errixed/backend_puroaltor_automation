package com.example.backend;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Backend {

    public static void switchToNewWindow(WebDriver driver, String mainWindow) {
        for (String window : driver.getWindowHandles()) {
            if (!window.equals(mainWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }
    }

    public static void switchToMainWindow(WebDriver driver, String mainWindow) {
        driver.switchTo().window(mainWindow);
    }

    public static void login(WebDriver driver) {
        driver.findElement(By.name("ctl00$Login$TxtBoxUserName")).sendKeys("petscriptions");
        driver.findElement(By.name("ctl00$Login$TxtBoxPassword")).sendKeys("YWQXE");
        driver.findElement(By.name("ctl00$Login$BtnLogin")).click();
    }

    public static void main(String[] args) {

        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://eshiponline.purolator.com/ShipOnline/SecurePages/Public/FormsLogin.aspx?ReturnUrl=/ShipOnline/Welcome.aspx&lang=E");
            String mainWindow = driver.getWindowHandle();


            //login
            login(driver);

            //new session
            driver.findElement(By.id("ctl00_CPPC_LinkButtonNewSession")).click();

            //login
            login(driver);

            //create a new shipment
            driver.findElement(By.name("ctl00$CPPC$btnCreateShipment")).click();

            //navigate to address book
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu']/ul/li[2]/a")).click();
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu:submenu:9']/li[1]/a")).click();

            //Switch to the new window
            switchToNewWindow(driver, mainWindow);

            //search a location
            driver.findElement(By.name("ctl00$CPPC$txtFilterString")).sendKeys("ALLANDALE VETERINARY HOSPITAL");
            driver.findElement(By.name("ctl00$CPPC$btnSearch")).click();
            driver.findElement(By.partialLinkText("ALLANDALE VETERINARY HOSPITAL")).click();

            //switch back to original window
            switchToMainWindow(driver, mainWindow);

            //shipping
            driver.findElement(By.name("ctl00$CPPC$btnNext")).click();
            driver.findElement(By.id("idCharac_PCH_HEALTHCARE")).click();
            driver.findElement(By.name("ctl00$CPPC$btnShip")).click();
            //Switch to the new window
            switchToNewWindow(driver, mainWindow);
            driver.findElement(By.name("ctl00$CPPC$ConfirmMessage_OSNR$btnOk")).click();
            //switch back to original window
            switchToMainWindow(driver, mainWindow);

            //create a new shipment again
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu']/ul/li[1]/a")).click();
            driver.findElement(By.xpath("//*[@id='ctl00_AppMenu_ShippingMenu:submenu:2']/li[1]/a")).click();

            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
