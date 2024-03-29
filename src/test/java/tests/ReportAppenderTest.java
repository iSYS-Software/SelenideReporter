package tests;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Fallbacks(baseUrl = "https://w3.org")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ReportAppenderTest extends UITest {

    private static final String REPORTS_PATH = "build/reports/selenide/reportAppender";
    String textWithScreenshot = "Message with Screenshot";
    String textWithoutScreenshot = "Message without Screenshot";

    @BeforeAll
    public static void setUpClass() throws IOException {
        System.setProperty("REPORT.UITEST.DIR", REPORTS_PATH);
        deleteDirectoryStream(Paths.get(REPORTS_PATH));
        System.out.println("Cleaned Report Directory");
    }

    @Test
    public void reportAppenderFirstRun() throws IOException {
        UITest.setUpClass();
        this.run("Report Appender first Run", settings -> {
            open(BASE_URL);
            report.info(textWithScreenshot);
            report.info(textWithoutScreenshot, false);
        });
    }

    @Test
    public void reportAppenderSecondRun() throws IOException {
        UITest.setUpClass();
        this.run("Report Appender second Run", settings -> {
            open(BASE_URL);
            report.info(textWithScreenshot);
            report.info(textWithoutScreenshot, false);
        });
        List<com.aventstack.extentreports.model.Test> testList = report.getExtentTest().getExtent().getReport().getTestList();
        for (com.aventstack.extentreports.model.Test test : testList) {
            for (Log logEntry : test.getLogs()) {
                assertEquals(Status.INFO, logEntry.getStatus());
                String details = logEntry.getDetails();
                if (logEntry.getMedia() != null) assertEquals(details, textWithScreenshot);
                else assertEquals(details, textWithoutScreenshot);
            }
        }
    }

    private static void deleteDirectoryStream(Path path) throws IOException {
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

}
