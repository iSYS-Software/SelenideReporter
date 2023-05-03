package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.Test;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.assertEquals;

@Fallbacks(baseUrl="https://www.wikipedia.org/")
public class ThisTestIsSupposedToFail extends UITest {

    @Test
    public void failingTest() {
        this.run("Failing Test", "this test will throw an exception during execution", settings -> {
            open(BASE_URL);
            report.info("Homepage called");
            throw new RuntimeException("This Exception is thrown on purpose");
        }, result -> {
            report.info("This is called after the test has finished (whether successfully or not) and can be used to clean up", false);
            if (result.getThrowable() != null) {
                assertEquals("java.lang.RuntimeException", result.getThrowable().getClass().getName());
                report.info("This test has thrown: " + result.getThrowable().getClass().getName(), false);
            }
        });
    }

}
