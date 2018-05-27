package com.github.avenderov.togglz;

import org.togglz.core.Feature;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

    RETURN_NO_CONTENT;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }

}
