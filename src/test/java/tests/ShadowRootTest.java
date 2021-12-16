package tests;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Fallbacks(baseUrl="https://start.vaadin.com/app/")
public class ShadowRootTest extends UITest {

    @Test
    public void shadowRoot() {
        this.run("Shadow Root", "check if shadow root is accessible by helper methods", settings -> {
            open(BASE_URL);
            $("footer button").click();
            $$(".shepherd-header .shepherd-cancel-icon").filterBy(visible).first().click();
            report.info("App Start Screen");
            SelenideElement inner = getElementInShadowRootOf($("#app-preview"), By.cssSelector("#previewLumo"));
            inner.shouldBe(visible);
            ElementsCollection innerColl = getElementsInShadowRootOf($("#app-preview"), By.cssSelector("div"));
            innerColl.shouldHave(size(4));
        });
    }

}
