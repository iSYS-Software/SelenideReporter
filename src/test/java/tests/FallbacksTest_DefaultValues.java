package tests;

import de.isys.selrep.UITest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FallbacksTest_DefaultValues extends UITest {

    @Test
    public void checkValues() {
        assertEquals("http://localhost:8080", BASE_URL);
        assertEquals(null, API_URL_1);
        assertEquals(null, API_URL_2);
        assertEquals(null, API_URL_3);
    }

}
