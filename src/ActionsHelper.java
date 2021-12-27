import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ActionsHelper {
    public final long DEFAULT_TIMEOUT_IN_SECONDS = 5;
    public final long LONG_TIMEOUT_IN_SECONDS = 10;
    private final  AppiumDriver<WebElement> driver;

    ActionsHelper(AppiumDriver<WebElement> driver) {
        this.driver = driver;
    }

    public boolean isElementPresent(By by) {
        return isElementPresent(by, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public boolean isElementPresent(By by, long timeoutInSeconds) {
        try {
            waitForElementPresent(by, timeoutInSeconds);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String waitForElementAndGetText(By by) {
        return waitForElementAndGetText(by, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public String waitForElementAndGetText(By by, long timeoutInSeconds) {
        WebElement element = waitForElementPresent(by, timeoutInSeconds);
        return element.getText();
    }

    @SuppressWarnings("UnusedReturnValue")
    public WebElement waitForElementAndClick(By by) {
        return waitForElementAndClick(by, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    public WebElement waitForElementAndClick(By by, long timeoutInSeconds) {
        WebElement element = waitForElementPresent(by, timeoutInSeconds);
        element.click();

        return element;
    }

    @SuppressWarnings("UnusedReturnValue")
    public WebElement waitForElementAndSendKeys(By by, String charSequences) {
        return waitForElementAndSendKeys(by, charSequences, DEFAULT_TIMEOUT_IN_SECONDS);
    }

    @SuppressWarnings("UnusedReturnValue")
    public WebElement waitForElementAndSendKeys(By by, String charSequences, long timeoutInSeconds) {
        WebElement element = waitForElementPresent(by, timeoutInSeconds);
        element.clear();
        element.sendKeys(charSequences);

        return element;
    }

    public void waitForElementAndSwipeLeft(By by, long timeoutInSeconds) {
        final long swipeDurationInMillis = 1000;

        WebElement element = waitForElementPresent(by, timeoutInSeconds);

        Point elementLocation = element.getLocation();
        int elementYCenter = element.getLocation().y + (element.getSize().height / 2);

        new TouchAction<>(driver)
                .press(PointOption.point(elementLocation.x + element.getSize().width, elementYCenter))
                .waitAction(WaitOptions.waitOptions(Duration.ofMillis(swipeDurationInMillis)))
                .moveTo(PointOption.point(elementLocation.x, elementYCenter))
                .release()
                .perform();
    }

    public WebElement waitForElementPresent(By by, long timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.withMessage("Не найден элемент: " + by.toString());

        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    @SuppressWarnings("UnusedReturnValue")
    public WebElement waitForElementClickable(By by, long timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.withMessage("Не найден кликабельный элемент:" + by.toString());

        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }
}
