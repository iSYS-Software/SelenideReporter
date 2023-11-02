package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;
import org.junit.BeforeClass;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Fallbacks(baseUrl = "these", apiUrl1="values", apiUrl2="shouldBe", apiUrl3="overridden")
public class FallbacksTest_SystemProps extends UITest {

    @BeforeClass
    public static void setUpClass() {
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
