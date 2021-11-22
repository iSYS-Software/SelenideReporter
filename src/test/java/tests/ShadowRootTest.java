package tests;

import com.codeborne.selenide.SelenideElement;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.Test;

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
            SelenideElement shadowRoot = getShadowRootFor($("#app-preview"));
            report.info(shadowRoot.innerText(), false);
            shadowRoot.$("#previewLumo").shouldBe(visible);
        });
    }

}
