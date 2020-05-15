package com.eot;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterMethod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public abstract class BaseTest {
    protected static BatchInfo batch;
    protected Eyes eyes;
    protected AppiumDriver driver;

    protected void checkResults(Eyes eyes) {
        Boolean throwtTestCompleteException = false;
        TestResults result = eyes.close(throwtTestCompleteException);
        System.out.println("Visual Testing results - " + result);
        String url = result.getUrl();
        if (result.isNew()) {
            System.out.println("New Baseline Created: URL=" + url);
        } else if (result.isPassed()) {
            System.out.println("All steps passed:     URL=" + url);
        } else {
            System.out.println("Test Failed:          URL=" + url);
        }
    }

    protected void sleep(int duractionInSec) {
        try {
            System.out.println(String.format("Sleep for %d sec", duractionInSec));
            Thread.sleep(duractionInSec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createAppiumDriver(String appPackage, String appActivity, String apkPath) {
        String appiumPort = "4723";
        Integer systemPort = 8201;
        String deviceId = "";
        String deviceName = "Android";
        String APPIUM_SERVER_URL = "http://localhost:port/wd/hub";

        System.out.println(String.format("Create AppiumDriver for - %s:%s, appiumPort - %s", deviceId, systemPort, appiumPort));

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");

//            capabilities.setCapability("app", apkPath);
            capabilities.setCapability("appPackage", appPackage);
            capabilities.setCapability("appActivity", appActivity);
            capabilities.setCapability("noSign", true);
            capabilities.setCapability("noReset", false);

            capabilities.setAcceptInsecureCerts(true);
            driver = new AppiumDriver<>(new URL(APPIUM_SERVER_URL.replace("port", appiumPort)), capabilities);
            System.out.println(String.format("Created AppiumDriver for - %s:%s, appiumPort - %s", deviceId, systemPort, appiumPort));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in creating Appium Driver");
        }
    }

    protected void configureEyes(String testName, BatchInfo batch, String appName) {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        eyes.setMatchLevel(MatchLevel.STRICT);
        eyes.setBatch(batch);
        eyes.setLogHandler(new StdoutLogHandler(false));

        eyes.setForceFullPageScreenshot(false);
        // Start the test.
        eyes.open(driver, appName, testName);
    }

    protected WebElement getButtonByText(String buttonString) {
        String className = "android.widget.Button";
        return findFlutterElementbyClassName(buttonString, className);
    }

    private WebElement findFlutterElementbyClassName(String buttonString, String className) {
        List<WebElement> buttons = driver.findElements(By.className(className));
        return buttons.stream().filter(button -> button.getText().equalsIgnoreCase(buttonString)).findFirst().orElse(null);
    }

    protected WebElement getViewByText(String viewString) {
        String className = "android.view.View";
        return findFlutterElementbyClassName(viewString, className);
    }

    @AfterMethod
    private void afterTest() {
        try {
            checkResults(eyes);
        } finally {
            // Close the browser.
            driver.quit();

            // If the test was aborted before eyes.close was called, ends the test as aborted.
            eyes.abortIfNotClosed();
        }
    }
}
