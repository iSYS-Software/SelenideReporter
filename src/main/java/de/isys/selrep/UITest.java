package de.isys.selrep;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Test;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.JsonFormatter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.google.gson.TypeAdapter;

import de.isys.selrep.callback.UITestCallback;
import de.isys.selrep.callback.UITestFinalizeCallback;
import de.isys.selrep.typeadapter.FileAdapter;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UITest {

    private static final String REPORTDIR = "REPORT.UITEST.DIR";
    private static final String BROWSER = "UITEST.BROWSER";
    private static final String BROWSERLANG = "UITEST.BROWSER.LANG";
    private static final String BASEURL = "BASE.URL";
    private static final String CHROMEDRIVER = "webdriver.chrome.driver";
    private static final String APPEND = "APPEND.TO.EXISTING.REPORT";

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

        if (System.getProperty(CHROMEDRIVER) == null) {
            System.setProperty(CHROMEDRIVER, "/usr/bin/chromedriver");
        }
        System.out.println("Location of chromedriver: " + System.getProperty(CHROMEDRIVER));
    }

    @BeforeClass
    public static void setUpClass() throws IOException {
        if (System.getProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY) == null) {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "info");
        }
        if (System.getProperty(REPORTDIR) == null) {
            System.setProperty(REPORTDIR, "build/reports/selenide");
        }
        else {
            new File(System.getProperty(REPORTDIR)).mkdirs();
        }
        if (System.getProperty(BROWSER) == null) {
            System.setProperty(BROWSER, "chrome");
        }
        if (System.getProperty(BROWSERLANG) == null) {
            System.setProperty(BROWSERLANG, "en");
        }
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        System.setProperty("chromeoptions.args", "--lang=" + System.getProperty(BROWSERLANG));

        String reportPath = System.getProperty(REPORTDIR) + "/index.html";
        String jsonPath = System.getProperty(REPORTDIR) + "/index.json";
        System.out.println("Writing Report to " + reportPath);

        boolean appendToExistingReport = System.getProperty(APPEND) == null ? true : System.getProperty(APPEND).equalsIgnoreCase("true");

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath)
                .config(ExtentSparkReporterConfig.builder()
                        .offlineMode(true)
                        .build())
                .viewConfigurer()
                .viewOrder()
                .as(new ViewName[] { ViewName.TEST, ViewName.DASHBOARD })
                .apply();
        reports = new ExtentReports();
        reports.setReportUsesManualConfiguration(true);
        if (appendToExistingReport) {
            try {
                JsonFormatter jsonReporter = new JsonFormatter(jsonPath);
                Map<Type, TypeAdapter<?>> typeMappings = new HashMap<>();
                typeMappings.put(File.class, new FileAdapter());
                jsonReporter.addTypeAdapterMapping(typeMappings);
                reports.createDomainFromJsonArchive(jsonPath);
                reports.attachReporter(jsonReporter);
            }
            catch (Throwable t) {
                System.err.println("Could not parse JSON at " + jsonPath + " due to: " + t.toString());
            }
        }
        reports.attachReporter(sparkReporter);
        Configuration.reportsFolder = System.getProperty(REPORTDIR);
        Configuration.browser = System.getProperty(BROWSER);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        try {
            List<Test> testList = reports.getReport().getTestList();
            testList.get(testList.size() - 1).setEndTime(new Date());
            reports.flush();
            System.out.println("Flushed Test Report.");
        }
        catch (Throwable t) {
            System.err.println("Could not flush Report: " + t.toString());
        }
    }

    protected void run(UITestCallback callback) {
        run(Thread.currentThread().getStackTrace()[2].getMethodName(), callback);
    }

    protected void run(String name, UITestCallback callback) {
        run(name, "", callback, null);
    }

    protected void run(String testName, String testDescription, UITestCallback callback) {
        run(testName, testDescription, callback, null);
    }

    protected void run(String testName, 
                       String testDescription, 
                       UITestCallback callback, 
                       UITestFinalizeCallback onFinalize) {
        if (StringUtils.isBlank(testName)) {
            testName = "Default-Test";
        }
        ExtentTest extentTest = reports.createTest(testName, testDescription);
        extentTest.getModel().setStartTime(new Date());
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
                try {
                    report.startSection("Finalizer");
                    onFinalize.execute(result);
                }
                catch (Throwable t) {
                    report.failOnException(t);
                }
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

    /**
     * Note: not all By selectors will work in every browser, but By.id and By.cssSelector should be safe.
     * 
     * @param host an element with a ShadowRoot DOM node
     * @param innerElement selector
     * @return the element selected by selector from within the ShadowRoot DOM node of host
     */
    protected SelenideElement getElementInShadowRootOf(SelenideElement host, By innerElement) {
        return stdMacros.getElementInShadowRootOf(host, innerElement);
    }

    /**
     * @param host an element with a ShadowRoot DOM node
     * @param innerElement selector
     * @return the collection of elements selected by selector from within the ShadowRoot DOM node of host
     */
    protected ElementsCollection getElementsInShadowRootOf(SelenideElement host, By innerElement) {
        return stdMacros.getElementsInShadowRootOf(host, innerElement);
    }

    /**
     * Traverses a ShadowRoot hierarchy according to the supplied selectors, where each selector will
     * select the next ShadowRoot DOM node.
     * 
     * Note: not all By selectors will work in every browser, but By.id and By.cssSelector should be safe.
     * 
     * @param host an element with a ShadowRoot DOM node
     * @param nestedSelectors selectors
     * @return the element selected by the last selector
     */
    protected SelenideElement getElementInNestedShadowRootOf(SelenideElement host, By... nestedSelectors) {
        return stdMacros.getElementInNestedShadowRootOf(host, nestedSelectors);
    }

    /**
     * @param host an element with a ShadowRoot DOM node
     * @param nestedSelectors selectors, at least two are required
     * @return the collection of elements selected by the last selector
     */
    protected ElementsCollection getElementsInNestedShadowRootOf(SelenideElement host, By... nestedSelectors) {
        return stdMacros.getElementsInNestedShadowRootOf(host, nestedSelectors);
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
