package de.isys.selrep;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Log;
import com.aventstack.extentreports.model.Media;
import com.aventstack.extentreports.model.Test;
import com.codeborne.selenide.Configuration;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;

import lombok.Getter;
import lombok.Setter;

import static com.codeborne.selenide.Selenide.screenshot;

@Getter
@Setter
public class UITestReport {

    private int startingScreenshotCounter = 100;
    private boolean compressScreenshots = true;
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
                String screenshotFileName = screenshot(generateUniqueBaseName());
                if (compressScreenshots) {
                    screenshotFileName = compress(screenshotFileName);
                }
                Media media = MediaEntityBuilder.createScreenCaptureFromPath(screenshotFileName).build();
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

    private String compress(String screenshotFileName) {
        try {
            File compressed = ScreenshotCompressor.compress(toFile(screenshotFileName));
            return StringUtils.substringBeforeLast(screenshotFileName, "/") + "/" + compressed.getName();
        }
        catch (Throwable t) {
            // the uncompressed screenshot is still usable
            System.err.println("Could not compress Screenshot " + screenshotFileName + ": " + t.toString());
            return screenshotFileName;
        }
    }

    /**
     * Removes all screenshots of this test from the report and deletes their files.
     */
    public void discardScreenshots() {
        discardScreenshots(extentTest.getModel());
    }

    private void discardScreenshots(Test test) {
        for (Log log : test.getLogs()) {
            if (log.hasMedia()) {
                try {
                    Files.deleteIfExists(toFile(log.getMedia().getPath()).toPath());
                }
                catch (Throwable t) {
                    System.err.println("Could not delete Screenshot " + log.getMedia().getPath() + ": " + t.toString());
                }
                log.setMedia(null);
            }
        }
        test.getChildren().forEach(this::discardScreenshots);
    }

    private static File toFile(String path) {
        // Selenide returns screenshot locations as file URLs
        return path.startsWith("file:") ? new File(URI.create(path)) : new File(path);
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
