package de.isys.selrep;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;

import lombok.Getter;
import lombok.Setter;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

@Getter
@Setter
public class UITestReport {

    private static int screenshotCounter = 100;

    private final String reportDirPath;
    private final ExtentTest extentTest;

    private ExtentTest extentSection = null;

    public UITestReport(String reportDirPath, ExtentTest extentTest) {
        this.reportDirPath = reportDirPath;
        this.extentTest = extentTest;
    }

    public void startSection(String sectionName) {
        startSection(sectionName, "");
    }

    public void startSection(String sectionName, String sectionDescription) {
        if (StringUtils.isBlank(sectionName)) {
            sectionName = "Default-Section";
        }
        extentSection = extentTest.createNode(sectionName);
        System.out.println("Starting Section '" + sectionName + "'");
    }

    public void info(String msg) {
        info(msg, true);
    }

    public void info(String msg, boolean captureScreen) {
        writeToReport(Status.INFO, msg, captureScreen);
    }

    public void warning(String msg) {
        warning(msg, true);
    }

    public void warning(String msg, boolean captureScreen) {
        writeToReport(Status.WARNING, msg, captureScreen);
    }

    public void skip(String msg) {
        skip(msg, true);
    }

    public void skip(String msg, boolean captureScreen) {
        writeToReport(Status.SKIP, msg, captureScreen);
    }

    public void pass() {
        pass("");
    }

    public void pass(String msg) {
        pass(msg, true);
    }

    public void pass(String msg, boolean captureScreen) {
        writeToReport(Status.PASS, msg, captureScreen);
    }

    public void fail(String msg) {
        fail(msg, true);
    }

    public void fail(String msg, boolean stopTest) {
        failWithMessage(msg == null ? "" : msg, null);
    }

    private void writeToReport(Status status, String msg, boolean captureScreen) {
        if (captureScreen) {
            try {
                TakesScreenshot screenshot = (TakesScreenshot) getWebDriver(); // can throw NPE
                File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
                File destFile = createNonExistingFileForScreenshot();
                FileUtils.copyFile(sourceFile, destFile);
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(destFile.getName()).build();
                log(status, msg, media);
            }
            catch (Exception e) {
                msg = "Could not take Screenshot: " + e.getMessage();
                log(Status.WARNING, msg);
            }
        }
        else {
            log(status, msg);
        }
        System.out.println(msg);
    }

    void failOnException(Throwable e) {
        failWithMessage("Fatal Error", e);
    }

    private void failWithMessage(String msg, Throwable t) {
        String errorMsg = t == null ? msg : msg + " caused by " + t.toString();
        writeToReport(Status.FAIL, errorMsg, true);
        org.junit.Assert.fail();
    }

    private void log(Status status, String msg) {
        log(status, msg, null, null);
    }

    private void log(Status status, String msg, Media media) {
        log(status, msg, null, media);
    }

    private void log(Status status, String details, Throwable t, Media media) {
        if (extentSection != null) {
            // we are logging to a section
            extentSection.log(status, details, t, media);
            if (status == Status.PASS || status == Status.FAIL) extentSection = null; // section ends here
        }
        else if (extentTest != null) {
            // we are logging to the main test
            extentTest.log(status, details, t, media);
        }
        else throw new UITestException("Test Report not initialized");
    }

    private File createNonExistingFileForScreenshot(){
        File file = null;
        while (file == null || file.exists()) {
            String fileNameCounter = String.valueOf(screenshotCounter++);
            String fileName = "screen-" + fileNameCounter + ".png";
            file = new File(reportDirPath, fileName);
        }
        return file;
    }

}
