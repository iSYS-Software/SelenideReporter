package tests;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ScrollDirection;
import com.codeborne.selenide.ScrollOptions;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;
import de.isys.selrep.UITestException;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.source;

@Fallbacks(baseUrl="https://www.isys.de/", apiUrl1="https://24pullrequests.com/projects.json")
public class ExampleTest extends UITest {

    @Test
    public void homepage() {
        this.run("Homepage", "check if homepage contains all expected elements", settings -> {
            open(BASE_URL);

            acceptCookieBanner();

            report.startSection("Header");
            SelenideElement context = $(".vs-main-navigation").should(appear);
            checkLogo(context, BASE_URL);
            ElementsCollection allVisibleLinks = $(".vs-main").$$("a[href]").filterBy(visible);
            clickRandomSameSiteLinkFrom(allVisibleLinks, BASE_URL);
            checkLogo($(".vs-main-navigation"), BASE_URL);
            report.info("Random same-site Link clicked, Logo still present");
            open(BASE_URL);
            report.pass("Header Section ok", false); // this ends the section

            report.startSection("Content Area");
            settings.setTimeout(8000l); // for UI operations that need more time to complete
            $$(".wp-block-columns").filterBy(visible).shouldHave(sizeGreaterThanOrEqual(1));
            settings.restoreDefaultTimeout();
            report.pass("Content Section ok");
            report.startSection("Footer");
            $(byTagName("footer")).$(withText("iSYS Software GmbH")).scrollTo().should(appear);
            report.pass("Footer Section ok");
        }, result -> {
            report.info("This is called after the test has finished (whether successfully or not) and can be used to clean up", false);
            if (result.getThrowable() != null) {
                report.info("This test has thrown: " + result.getThrowable().getClass().getName(), false);
            }
        });
    }

    private void acceptCookieBanner() {
        SelenideElement acceptCookiesButton = $("#CookieBoxSaveButton").should(appear);
        report.info("Cookie Notice appeared");
        acceptCookiesButton.click();
        $("#CookieBoxSaveButton").shouldNotBe(visible);
        report.info("Cookies accepted");
    }

    @Test
    public void firefoxTest() {
        this.run("Homepage with Firefox", "check Homepage with different Browser", settings -> {
            settings.setBrowser("firefox");
            Selenide.closeWebDriver(); // start with a new webdriver
            open(BASE_URL);
            acceptCookieBanner();
            checkLogo($(".vs-main-navigation"), BASE_URL);
            report.pass("Homepage loads with Firefox");
        });
    }

    @Test
    public void callExternalApi() {
        this.run("External API", "check if external APIs can be configured", settings -> {
            open(API_URL_1);
            if(! source().contains("github_url")) {
                throw new UITestException("API call not successful!");
            }
            report.info("External API called");
        });
    }

    private void checkLogo(SelenideElement context, String baseUrl) {
        SelenideElement logo = context.$$(".vs-logo a").filterBy(visible).shouldHave(size(1)).get(0);
        logo.shouldHave(attribute("href", baseUrl));
        report.info("Logo found on page " + Selenide.title());
    }

    private void clickRandomSameSiteLinkFrom(ElementsCollection allLinks, String baseUrl) {
        SelenideElement randomLink = null;
        while (randomLink == null || !randomLink.attr("href").startsWith(baseUrl)) {
            randomLink = allLinks.get(RandomUtils.nextInt(0, allLinks.size()));
        }
        randomLink.scrollIntoView(false);
        report.info("Random Link: " + randomLink.attr("href"));
        randomLink.click();
    }

}
