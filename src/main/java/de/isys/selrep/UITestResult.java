package de.isys.selrep;

import lombok.Data;

@Data
public class UITestResult {

    private Throwable throwable;

    private final UITestSettings settings;

    public UITestResult(UITestSettings settings) {
        this.settings = settings;
    }

}
