package de.isys.selrep;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;

import de.isys.selrep.callback.UITestCallback;
import de.isys.selrep.callback.UITestFinalizeCallback;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Dimension;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.openqa.selenium.By;

public abstract class UITest {

    private static final String REPORTDIR = "REPORT.UITEST.DIR";
    private static final String BROWSER = "UITEST.BROWSER";
    private static final String BASEURL = "BASE.URL";

    private static ExtentReports reports;
    private final StandardMacros stdMacros = new StandardMacros();

    protected UITestReport report = null;
    protected final String BASE_URL;

    public UITest() {
        String baseUrl = System.getProperty(BASEURL);
        if (baseUrl == null) {
            // not configured, so let's check annotation
            baseUrl = getBaseUrlAnnotation();
            if (baseUrl == null) {
                // not annotated either, set default
                baseUrl = "http://localhost:8080";
            }
        }
        this.BASE_URL = baseUrl;
        System.out.println("BASE_URL: " + BASE_URL);
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        if (System.getProperty(REPORTDIR) == null) {
            System.setProperty(REPORTDIR, "build/reports/selenide");
        }
        else {
            new File(System.getProperty(REPORTDIR)).mkdirs();
        }
        if (System.getProperty(BROWSER) == null) {
            System.setProperty(BROWSER, "chrome");
        }

        String reportPath = System.getProperty(REPORTDIR) + "/index.html";
        String jsonPath = System.getProperty(REPORTDIR) + "/index.json";
        System.out.println("Writing Report to " + reportPath);

        reports = new ExtentReports();
        JsonFormatter jsonReporter = new JsonFormatter(jsonPath);
        reports.createDomainFromJsonArchive(jsonPath);
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportPath)
                .config(ExtentSparkReporterConfig.builder()
                        .offlineMode(true)
                        .build())
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] { ViewName.TEST, ViewName.DASHBOARD })
                .apply();
        reports.attachReporter(jsonReporter, htmlReporter);
        Configuration.reportsFolder = System.getProperty(REPORTDIR);
        Configuration.browser = System.getProperty(BROWSER);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        reports.flush();
    }

    protected void run(UITestCallback callback) {
        run(Thread.currentThread().getStackTrace()[2].getMethodName(), callback);
    }

    protected void run(String name, UITestCallback callback) {
        run(name, "", callback, null);
    }

    protected void run(String testName, String testDescription, UITestCallback callback) {
        run(testName, testDescription, callback, null);
    };

    protected void run(String testName, 
                       String testDescription, 
                       UITestCallback callback, 
                       UITestFinalizeCallback onFinalize) {
        if (StringUtils.isBlank(testName)) {
            testName = "Default-Test";
        }
        ExtentTest extentTest = reports.createTest(testName, testDescription);
        this.report = new UITestReport(System.getProperty(REPORTDIR), extentTest);
        UITestSettings settings = new UITestSettings(BROWSER);
        UITestResult result = new UITestResult(settings);
        try {
            callback.execute(settings);
        }
        catch (Throwable t) {
            result.setThrowable(t);
            report.failOnException(t);
        }
        finally {
            if (onFinalize != null) {
                report.startSection("Finalizer");
                onFinalize.execute(result);
            }
        }
    }

    private String getBaseUrlAnnotation() throws UITestException, SecurityException {
        Annotation fallbacks = this.getClass().getAnnotation(Fallbacks.class);
        if (fallbacks != null) {
            Class<? extends Annotation> annotationType = fallbacks.annotationType();
            for (Method method : annotationType.getDeclaredMethods()) {
                try {
                    if (method.getName().equals("baseUrl")) {
                        return "" + method.invoke(fallbacks, (Object[])null);
                    }
                }
                catch (Exception ex) {
                    throw new UITestException("Misconfigured Annotation method: " + method.getName(), ex);
                }
            }
        }
        System.out.println("No Annotation @Fallbacks found");
        return null;
    }

    protected void selectRandomOption(SelenideElement select, int offset) {
        stdMacros.selectRandomOption(select, offset);
    }

    protected void waitMillis(long millis) {
        stdMacros.waitMillis(millis);
    }

    protected Dimension getBrowserWindowSize() {
        return stdMacros.getBrowserWindowSize();
    }

    protected void resizeBrowserWindow(Dimension dimension) {
        stdMacros.resizeBrowserWindow(dimension);
    }

    protected void resizeBrowserWindow(int width, int height) {
        stdMacros.resizeBrowserWindow(width, height);
    }

    protected SelenideElement getShadowRootFor(SelenideElement host) {
        return stdMacros.getShadowRootFor(host);
    }

    protected SelenideElement getElementInShadowRootOf(SelenideElement host, By innerElement) {
        return stdMacros.getElementInShadowRootOf(host, innerElement);
    }

    protected SelenideElement getNestedShadowRoot(SelenideElement host, By... nestedSelectors) {
        return stdMacros.getNestedShadowRoot(host, nestedSelectors);
    }

    /**
     * @param mailUser name of mailbox to use
     * @param searchPhrase a string in the mail body that identifies the correct mail
     * @return the URL that has been accessed
     */
    protected String checkRestMail(String mailUser, String searchPhrase) {
        return stdMacros.checkRestMail(mailUser, searchPhrase);
    }

    /**
     * @param mailUser name of mailbox to use
     * @param searchPhrase a string in the mail body that identifies the correct mail
     * @param beforeLink link url starts after this string
     * @param afterLink link url ends before this string
     */
    protected void clickLinkInRestMail(String mailUser, String searchPhrase, String beforeLink, String afterLink) {
        stdMacros.clickLinkInRestMail(mailUser, searchPhrase, beforeLink, afterLink);
    }

    /**
     * @param mailUser name of mailbox to use
     */
    protected void deleteRestMail(String mailUser) {
        stdMacros.deleteRestMail(mailUser);
    }

}
