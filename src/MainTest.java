import io.appium.java_client.AppiumDriver;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;

public class MainTest {
    private AppiumDriver<WebElement> driver;
    private ActionsHelper actionsHelper;
    private final String readingListName = "StarWars";

    @Before
    public void setUp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("automationName", "UiAutomator2");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "8.0");
        capabilities.setCapability("deviceName", "AndroidTestDevice");
        capabilities.setCapability("appPackage", "org.wikipedia");
        capabilities.setCapability("appActivity", ".main.MainActivity");
        capabilities.setCapability("app", "G:\\github\\MobileAppAutomator\\MobileAppAutomator-Ex05\\apks\\org.wikipedia.apk");

        driver = new AppiumDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
        actionsHelper = new ActionsHelper(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testAddAndDeleteArticlesInReadingList() {
        final String resourceIdArticleTitleXpath = "//*[@resource-id='org.wikipedia:id/page_list_item_title']";
        final String firstArticleTitle = "Star Wars";
        final String secondArticleTitle = "Star Wars (film)";
        final String firstArticleTitleXpath = resourceIdArticleTitleXpath + "[@text='" + firstArticleTitle + "']";
        final String secondArticleTitleXpath = resourceIdArticleTitleXpath + "[@text='" + secondArticleTitle + "']";

        // Добавляем первую статью в новый список чтения
        searchArticles();
        openArticle(By.xpath(firstArticleTitleXpath));
        callAddToReadingList();
        addArticleToNewReadingList();
        closeArticle();

        // Добавляем вторую статью в уже созданный список чтения
        searchArticles();
        openArticle(By.xpath(secondArticleTitleXpath));
        callAddToReadingList();
        addArticleToExistingReadingList();
        closeArticle();

        // Удаляем первую статью из списка чтения
        removeArticleFromReadingList(By.xpath(firstArticleTitleXpath + "/.."));

        // Проверяем, что в списке чтения нет первой статьи и есть вторая статья
        Assert.assertFalse("Первая статья все еще присутствует!",
                actionsHelper.isElementPresent(By.xpath(firstArticleTitleXpath)));
        Assert.assertTrue("Вторая статья отсутствует!",
                actionsHelper.isElementPresent(By.xpath(secondArticleTitleXpath)));

        // Открываем вторую статью и проверяем текст заголовка
        openArticle(By.xpath(secondArticleTitleXpath));
        final String viewPageTitleTextId = "org.wikipedia:id/view_page_title_text";
        Assert.assertEquals("Заголовок второй статьи не соответствует ожидаемому!",
                actionsHelper.waitForElementAndGetText(By.id(viewPageTitleTextId)),
                secondArticleTitle);
    }

    private void searchArticles() {
        final String searchBlockXpath = "//*[@resource-id='org.wikipedia:id/search_container']";
        actionsHelper.waitForElementAndClick(By.xpath(searchBlockXpath));

        final String searchText = "star wars";
        final String searchInputXpath = "//*[@resource-id='org.wikipedia:id/search_src_text']";
        actionsHelper.waitForElementAndSendKeys(By.xpath(searchInputXpath), searchText);
    }

    private void openArticle(By by) {
        actionsHelper.waitForElementAndClick(by, actionsHelper.LONG_TIMEOUT_IN_SECONDS);
    }

    private void closeArticle() {
        final String navigateUpButtonXpath = "//android.widget.ImageButton[@content-desc='Navigate up']";
        actionsHelper.waitForElementAndClick(By.xpath(navigateUpButtonXpath));
    }

    private void callAddToReadingList() {
        final String moreOptionsButtonXpath = "//android.widget.ImageView[@content-desc='More options']";
        actionsHelper.waitForElementAndClick(By.xpath(moreOptionsButtonXpath), actionsHelper.LONG_TIMEOUT_IN_SECONDS);
        waitMoreOptionsMenuAllItemsClickable();

        final String addToReadingListMenuItemXpath = "//*[@resource-id='org.wikipedia:id/title'][@text='Add to reading list']";
        actionsHelper.waitForElementAndClick(By.xpath(addToReadingListMenuItemXpath));
    }

    private void waitMoreOptionsMenuAllItemsClickable() {
        final String resourceIdXpath = "//*[@resource-id='org.wikipedia:id/title']";

        final String[] itemsXpath;
        itemsXpath = new String[] {
                "[@text='Font and theme']",
                "[@text='Share link']",
                "[@text='Add to reading list']",
                "[@text='Find in page']",
                "[@text='Similar pages']",
                "[@text='Change language']"
        };

        for (String s : itemsXpath) {
            actionsHelper.waitForElementClickable(By.xpath(resourceIdXpath + s), actionsHelper.LONG_TIMEOUT_IN_SECONDS);
        }
    }

    private void addArticleToNewReadingList() {
        // Если появилась форма с описанием возможностей Reading List - нажимаем кнопку "GOT IT", иначе - кнопку создания нового Reading List
        final String gotItButtonXpath = "//*[@resource-id='org.wikipedia:id/onboarding_button'][@text='GOT IT']";
        final String createNewButtonXpath = "//android.widget.TextView[@text='Create new']";
        if (actionsHelper.isElementPresent(By.xpath(gotItButtonXpath))) {
            actionsHelper.waitForElementAndClick(By.xpath(gotItButtonXpath));
        } else {
            actionsHelper.waitForElementAndClick(By.xpath(createNewButtonXpath));
        }

        final String inputReadingListNameXpath = "//*[@text='Name of this list']" +
                "//*[@resource-id='org.wikipedia:id/text_input']";
        actionsHelper.waitForElementAndSendKeys(By.xpath(inputReadingListNameXpath), readingListName);

        final String okButtonXpath = "//*[@resource-id='android:id/button1'][@text='OK']";
        actionsHelper.waitForElementAndClick(By.xpath(okButtonXpath));
    }

    private void addArticleToExistingReadingList() {
        final String existingReadingListXpath = "//*[@resource-id='org.wikipedia:id/item_title'][@text='" + readingListName + "']";
        actionsHelper.waitForElementAndClick(By.xpath(existingReadingListXpath));
    }

    private void removeArticleFromReadingList(By by) {
        final String myListsButtonXpath = "//android.widget.FrameLayout[@content-desc='My lists']/*[@resource-id='org.wikipedia:id/icon']";
        actionsHelper.waitForElementAndClick(By.xpath(myListsButtonXpath));

        final String readingListXpath = "//*[@resource-id='org.wikipedia:id/item_title'][@text='" + readingListName + "']";
        actionsHelper.waitForElementAndClick(By.xpath(readingListXpath));

        actionsHelper.waitForElementAndSwipeLeft(by, actionsHelper.DEFAULT_TIMEOUT_IN_SECONDS);
    }
}
