package tests;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Fallbacks(baseUrl = "https://www.isys.de", apiUrl1 = "https://api1", apiUrl2 = "https://api2", apiUrl3 = "https://api3")
public class FallbacksTest_AnnotatedValues extends UITest {

    @Test
    public void checkValues() {
        assertEquals("https://www.isys.de", BASE_URL);
        assertEquals("https://api1", API_URL_1);
        assertEquals("https://api2", API_URL_2);
        assertEquals("https://api3", API_URL_3);
    }

}
