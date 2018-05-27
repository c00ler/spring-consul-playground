package com.github.avenderov.configuration;

import org.togglz.core.Feature;

public interface FeatureNameStrategy {

    String asProperty(Feature feature);

}
