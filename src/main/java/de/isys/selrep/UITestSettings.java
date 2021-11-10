package de.isys.selrep;

import com.codeborne.selenide.Configuration;
import static de.isys.selrep.UITest.BROWSER;

public class UITestSettings {

    private final String baseUrl;

    public UITestSettings(String baseUrl) {
        this.baseUrl = baseUrl;
        restoreDefaultTimeout();
        restoreDefaultBrowserSize();
        Configuration.headless = true;
        Configuration.savePageSource = false;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public long getTimeout() {
        return Configuration.timeout;
    }

    public void setTimeout(long timeout) {
        Configuration.timeout = timeout;
    }

    public final void restoreDefaultTimeout() {
        Configuration.timeout = 4000;
    }

    public String getBrowserSize() {
        return Configuration.browserSize;
    }

    public void setBrowserSize(String size) {
        Configuration.browserSize = size;
    }

    public final void restoreDefaultBrowserSize() {
        Configuration.browserSize = "1920x1080";
    }

    public String getBrowser() {
        return Configuration.browser;
    }

    public void setBrowser(String browser) {
        Configuration.browser = browser;
    }

    public final void restoreDefaultBrowser() {
        Configuration.browser = System.getProperty(BROWSER);
    }

    public boolean getHeadless() {
        return Configuration.headless;
    }

    public void setHeadless(boolean headless) {
        Configuration.headless = headless;
    }

    public boolean getSavePageSource() {
        return Configuration.savePageSource;
    }

    public void setSavePageSource(boolean savePageSource) {
        Configuration.savePageSource = savePageSource;
    }

}
