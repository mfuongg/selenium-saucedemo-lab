
package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SauceDemoTest {

    private WebDriver driver;
    private WebDriverWait wait;

    private final String baseUrl = "https://www.saucedemo.com/";
    private final String username = "standard_user";
    private final String password = "secret_sauce";

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(baseUrl);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String user, String pass) {
        driver.findElement(By.id("user-name")).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(pass);
        driver.findElement(By.id("login-button")).click();
    }

    // TC01: Login with valid credentials
    @Test
    void TC01_loginWithValidCredentials() {
        login(username, password);

        WebElement title = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.className("title")
            )
        );

        assertEquals("Products", title.getText());
        assertTrue(driver.getCurrentUrl().contains("inventory.html"));
    }

    // TC02: Login with invalid password
    @Test
    void TC02_loginWithInvalidPassword() {
        login(username, "wrong_password");

        WebElement error = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-test='error']")
            )
        );

        assertTrue(error.getText().contains(
            "Username and password do not match"
        ));
        assertTrue(driver.getCurrentUrl().contains("saucedemo.com"));
    }

    // TC03: Add a product to the cart
    @Test
    void TC03_addProductToCart() {
        login(username, password);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("add-to-cart-sauce-labs-backpack")
        )).click();

        WebElement cartBadge = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.className("shopping_cart_badge")
            )
        );

        assertEquals("1", cartBadge.getText());
    }

    // TC04: Verify the product in the cart
    @Test
    void TC04_verifyProductInCart() {
        login(username, password);

        wait.until(ExpectedConditions.elementToBeClickable(
            By.id("add-to-cart-sauce-labs-backpack")
        )).click();

        driver.findElement(By.className("shopping_cart_link")).click();

        WebElement product = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.className("inventory_item_name")
            )
        );

        assertEquals("Sauce Labs Backpack", product.getText());
        assertTrue(driver.getCurrentUrl().contains("cart.html"));
    }

    // TC05: Logout successfully
    @Test
    void TC05_logoutSuccessfully() {
        login(username, password);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("inventory_list")
        ));

        driver.findElement(By.id("react-burger-menu-btn")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
            By.id("logout_sidebar_link")
        )).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("login-button")
        ));

        assertTrue(driver.getCurrentUrl().contains("saucedemo.com"));
        assertTrue(driver.findElement(By.id("login-button")).isDisplayed());
    }
}
