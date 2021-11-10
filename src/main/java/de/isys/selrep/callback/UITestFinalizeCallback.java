package de.isys.selrep.callback;

import de.isys.selrep.UITestResult;

@FunctionalInterface
public interface UITestFinalizeCallback {

    public abstract void execute(UITestResult result);

}
