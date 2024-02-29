package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Fallbacks(baseUrl = "these", apiUrl1="values", apiUrl2="shouldBe", apiUrl3="overridden")
public class Z_runMeLast_FallbacksTest_SystemProps extends UITest {

    @BeforeAll
    public static void setUpClass() {
        // this can confuse other tests, so run them before this one
        System.setProperty("BASE.URL", "https://www.isys.de");
        System.setProperty("API.URL.1", "https://api1");
        System.setProperty("API.URL.2", "https://api2");
        System.setProperty("API.URL.3", "https://api3");
    }

    @Test
    public void checkValues() {
        assertEquals("https://www.isys.de", BASE_URL);
        assertEquals("https://api1", API_URL_1);
        assertEquals("https://api2", API_URL_2);
        assertEquals("https://api3", API_URL_3);
    }

}
