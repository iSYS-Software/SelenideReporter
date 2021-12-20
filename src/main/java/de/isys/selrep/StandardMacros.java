package de.isys.selrep;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import lombok.Data;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.source;

public class StandardMacros { 

    void selectRandomOption(SelenideElement select, int offset) {
        int optionCount = select.findAll(byTagName("option")).size() - offset;
        select.find(byTagName("option"), RandomUtils.nextInt(offset, optionCount)).click();
    }

    void waitMillis(long millis) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < millis) {
            // waste some time
            getWebDriver().getPageSource().replace('e', 'E');
        }
    }

    Dimension getBrowserWindowSize() {
        return getWebDriver().manage().window().getSize();
    }

    void resizeBrowserWindow(Dimension dimension) {
        resizeBrowserWindow(dimension.width, dimension.height);
    }

    void resizeBrowserWindow(int width, int height) {
        Dimension d = new Dimension(width, height);
        getWebDriver().manage().window().setSize(d);
    }

    SelenideElement getElementInShadowRootOf(SelenideElement host, By innerElement) {
        return enterShadowRoot(host, innerElement, true).getResultElement();
    }

    ElementsCollection getElementsInShadowRootOf(SelenideElement host, By innerElement) {
        return enterShadowRoot(host, innerElement, false).getResultElements();
    }

    SelenideElement getElementInNestedShadowRootOf(SelenideElement host, By... nestedSelectors) {
        for (By selector : nestedSelectors) {
            host = getElementInShadowRootOf(host, selector);
        }
        return host;
    }

    ElementsCollection getElementsInNestedShadowRootOf(SelenideElement host, By... nestedSelectors) {
        if (nestedSelectors.length < 2) throw new IllegalArgumentException("At least two Selectors needed!");
        for (int i = 0; i < nestedSelectors.length - 1; i++) {
            host = getElementInShadowRootOf(host, nestedSelectors[i]);
        }
        return getElementsInShadowRootOf(host, nestedSelectors[nestedSelectors.length - 1]);
    }

    @Data
    private class ShadowRootResult {
        private SelenideElement resultElement;
        private ElementsCollection resultElements;
    }

    private ShadowRootResult enterShadowRoot(SelenideElement host, By innerElement, boolean firstResultOnly) {
        ShadowRootResult result = new ShadowRootResult();
        WebDriver webDriver = getWebDriver();
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        Object shadowRoot = null;
        try {
            shadowRoot = jse.executeScript("return arguments[0].shadowRoot", host);
        }
        catch (Throwable t) {
            throw new UITestException("Host: " + host + ", By: " + innerElement, t);
        }
        if (shadowRoot == null) throw new UITestException("No Shadow Root for " + host);
        if (shadowRoot instanceof WebElement) {
            // ChromeDriver 95 and Selenium 4
            if (firstResultOnly) result.setResultElement($(((WebElement) shadowRoot).findElement(innerElement)));
            else result.setResultElements($$(((WebElement) shadowRoot).findElements(innerElement)));
        }
        else if (shadowRoot instanceof SearchContext) {
            // ChromeDriver 96+ and Selenium 4
            if (firstResultOnly) result.setResultElement($(((SearchContext) shadowRoot).findElement(innerElement)));
            else result.setResultElements($$(((SearchContext) shadowRoot).findElements(innerElement)));
        }
        else if (shadowRoot instanceof Map)  {
            // ChromeDriver 96+ and Selenium 3.141.59
            // Based on https://github.com/SeleniumHQ/selenium/issues/10050#issuecomment-974231601
            Map<String, Object> shadowRootMap = (Map<String, Object>) shadowRoot;
            String shadowRootKey = (String) shadowRootMap.keySet().toArray()[0];
            RemoteWebElement remoteWebElement = new RemoteWebElement();
            remoteWebElement.setParent((RemoteWebDriver) webDriver);
            remoteWebElement.setId(shadowRootKey);
            if (firstResultOnly) result.setResultElement($(remoteWebElement.findElement(innerElement)));
            else result.setResultElements($$(remoteWebElement.findElements(innerElement)));
        }
        else {
            throw new UITestException("Unexpected return type for shadowRoot: " + shadowRoot.getClass().getName());
        }
        return result;
    }

    String checkRestMail(String mailUser, String searchPhrase) {
        String restMailUrl = "https://www.restmail.net/mail/" + mailUser;
        open(restMailUrl);

        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 10000) {
            // wait up to 10 seconds for mail to be delivered
            Selenide.refresh();
            if ($(withText(searchPhrase)).exists()) {
                return restMailUrl;
            }
        }
        throw new UITestException("Mail with '" + searchPhrase + "' not retrievable after " + ((System.currentTimeMillis() - start) / 1000) + " Seconds.");
    }

    void clickLinkInRestMail(String mailUser, String searchPhrase, String beforeLink, String afterLink) {
        long start = System.currentTimeMillis();
        String restMailUrl = checkRestMail(mailUser, searchPhrase);
        System.out.println("Mail arrived after " + (System.currentTimeMillis() - start) + "ms. Inbox: " + restMailUrl);
        String link = StringUtils.substringBefore(StringUtils.substringAfterLast(source(), beforeLink), afterLink);
        System.out.println("Link extracted: " + link);
        open(link);
    }

    void deleteRestMail(String mailUser) {
        try {
            String restMailUrl = "https://www.restmail.net/mail/" + mailUser;
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(restMailUrl))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> resp = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new UITestException("Cannot delete Mail to " + mailUser + ": " + resp.body() + "/" + resp.statusCode());
            }
            open(restMailUrl);
            System.out.println("Mail to " + mailUser + " deleted.");
        }
        catch (IOException | InterruptedException ex) {
            throw new UITestException(ex);
        }
    }

}
