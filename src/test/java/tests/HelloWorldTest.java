package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

@Fallbacks(baseUrl="https://en.wikipedia.org/wiki/Hello%20World")
public class HelloWorldTest extends UITest {

    @Test
    public void helloWorld() {
        this.run("Hello World UI-Test", settings -> {
            open(BASE_URL);
            report.info("Opened " + BASE_URL, false);
            $(".mw-logo-icon").should(appear);
            report.info("Wikipedia Logo found");
            // ... more tests
            report.pass("All good with " + BASE_URL, false);
        });
    }

}
