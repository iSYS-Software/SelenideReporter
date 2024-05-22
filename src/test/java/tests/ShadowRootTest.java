package tests;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.jupiter.api.Test;
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
            report.info("App Splash Screen");
            $("#close-splash").click();
            $("#close-splash").should(disappear);
            report.info("Splash Screen closed");
            
            $("#start-project").click();
            report.info("App Start Screen");
            SelenideElement inner = getElementInShadowRootOf($("#overlay"), By.cssSelector("#resizerContainer"));
            inner.shouldBe(visible);
            ElementsCollection innerColl = getElementsInShadowRootOf($("#overlay"), By.cssSelector("div"));
            innerColl.shouldHave(size(13));
            report.pass("Shadow Root is accessible");
        });
    }

}
