package de.isys.selrep.callback;

import de.isys.selrep.UITestSettings;

@FunctionalInterface
public interface UITestCallback {

    public abstract void execute(UITestSettings settings);

}
