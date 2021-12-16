package tests;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;

import de.isys.selrep.Fallbacks;
import de.isys.selrep.UITest;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.Assert.*;

@Fallbacks(baseUrl = "https://w3.org")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReportAppenderTest extends UITest {

    private static final String REPORTS_PATH = "build/reports/selenide/reportAppender";
    String textWithScreenshot = "Message with Screenshot";
    String textWithoutScreenshot = "Message without Screenshot";

    @BeforeClass
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
            report.pass(textWithoutScreenshot, false);
        });
    }

    @Test
    public void reportAppenderSecondRun() throws IOException {
        UITest.setUpClass();
        this.run("Report Appender second Run", settings -> {
            open(BASE_URL);
            report.info(textWithScreenshot);
            report.pass(textWithoutScreenshot, false);
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
