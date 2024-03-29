package de.isys.selrep;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Media;
import com.codeborne.selenide.Configuration;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

import static com.codeborne.selenide.Selenide.screenshot;

@Getter
@Setter
public class UITestReport {

    private int startingScreenshotCounter = 100;
    private final String testShortCode;

    private final String reportDirPath;
    private final ExtentTest extentTest;

    private ExtentTest extentSection = null;

    public UITestReport(String reportDirPath, ExtentTest extentTest) {
        this.reportDirPath = reportDirPath;
        this.extentTest = extentTest;
        this.testShortCode = StringUtils.abbreviate(
                extentTest.getModel().getName()
                        .replaceAll("\\P{Print}", "").replaceAll("\\s+","")
                        .replaceAll("[\\\\/:*?\"<>|]", "_"),
                "", 16).toLowerCase();
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
        writeToReport(Status.INFO, msg, null, captureScreen);
    }

    public void warning(String msg) {
        warning(msg, true);
    }

    public void warning(String msg, boolean captureScreen) {
        writeToReport(Status.WARNING, msg, null, captureScreen);
    }

    public void skip(String msg) {
        skip(msg, true);
    }

    public void skip(String msg, boolean captureScreen) {
        writeToReport(Status.SKIP, msg, null, captureScreen);
    }

    public void pass() {
        pass("");
    }

    public void pass(String msg) {
        pass(msg, true);
    }

    public void pass(String msg, boolean captureScreen) {
        writeToReport(Status.PASS, msg, null, captureScreen);
    }

    public void fail(String msg) {
        fail(msg, null);
    }

    public void fail(String msg, Throwable t) {
        failWithMessage(msg == null ? "" : msg, t);
    }

    private void writeToReport(Status status, String msg, Throwable t, boolean captureScreen) {
        if (captureScreen) {
            try {
                String pngFileName = screenshot(generateUniqueBaseName());
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(pngFileName).build();
                String reportsFolder = StringUtils.appendIfMissing(Configuration.reportsFolder, "/");
                String resolvedPath = StringUtils.substringAfter(media.getPath(), reportsFolder);
                // make path relative to support self-contained report
                media.setResolvedPath(resolvedPath);
                log(status, msg, t, media);
            }
            catch (Throwable t2) {
                log(Status.WARNING, "Could not take Screenshot: " + t2.toString());
                log(status, msg, t);
            }
        }
        else {
            log(status, msg);
        }
        System.out.println(msg);
    }

    private String generateUniqueBaseName() {
        // not as good as a UUID, but should be good enough for even the largest of test suites
        return testShortCode + "-" + String.valueOf(startingScreenshotCounter++) + "-" + RandomStringUtils.randomAlphanumeric(4);
    }

    void failOnException(Throwable e) {
        failWithMessage("Fatal Error", e);
    }

    private void failWithMessage(String msg, Throwable t) {
        String errorMsg = t == null ? msg : msg + " caused by " + t.toString();
        writeToReport(Status.FAIL, errorMsg, t, true);
        org.junit.jupiter.api.Assertions.fail();
    }

    private void log(Status status, String msg) {
        log(status, msg, null, null);
    }

    private void log(Status status, String msg, Throwable t) {
        log(status, msg, t, null);
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

}
