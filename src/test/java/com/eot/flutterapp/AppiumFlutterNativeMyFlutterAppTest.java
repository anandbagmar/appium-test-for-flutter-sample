package com.eot.flutterapp;

import com.applitools.eyes.BatchInfo;
import com.eot.BaseTest;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

class AppiumFlutterNativeMyFlutterAppTest extends BaseTest {
    private final String batchName = "appium_flutter-test";
    private final String appName = "flutter-app";
    private final String appPackage = "com.eot.flutterapp";
    private final String appActivity = "com.eot.flutterapp.MainActivity";
    private final String apkPath = "resources/MyFlutterApp.apk";

    @BeforeMethod
    public void beforeTest(Method method) {
        createAppiumDriver(appPackage, appActivity, apkPath);
        configureEyes(method.getName(), new BatchInfo(batchName), appName);
    }

    @Test
    public void incrementCounter() {
        eyes.checkWindow("Hello!");
        String buttonString = "Increment";
        String viewString = "You have pushed the button this many times:";
        WebElement statusMessage = getViewByText(viewString);
        System.out.println(String.format("Found status message - '%s'", statusMessage.getText()));
        WebElement incrementButton = getButtonByText(buttonString);
        IntStream.range(0, 5).forEach(i -> incrementButton(incrementButton, i));
    }

    private void incrementButton(WebElement button, int i) {
        button.click();
        eyes.checkWindow("Increment counter - " + i);
        String expectedCounterValue = String.valueOf(i + 1);
        WebElement statusMessage = getViewByText(expectedCounterValue);
        System.out.println("Increment counter is: " + expectedCounterValue);
        Assert.assertEquals(statusMessage.getText(), expectedCounterValue, "Increment counter is incorrect");
    }

}