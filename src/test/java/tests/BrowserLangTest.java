package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Fallbacks(baseUrl="https://www.wikipedia.org", browserLang="de")
public class BrowserLangTest extends UITest {

    @Test
    public void browserLang_de() {
        this.run("Browser German Language UI-Test", settings -> {
            open(BASE_URL);
            report.info("Opened " + BASE_URL);

            $(".central-textlogo-wrapper strong").should(appear).shouldHave(text("Die freie Enzyklopädie"));

            report.pass("All good with " + BASE_URL, false);
        });
    }

}
