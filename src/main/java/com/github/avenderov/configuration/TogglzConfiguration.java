package com.github.avenderov.configuration;

import com.github.avenderov.togglz.Features;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.togglz.core.Feature;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.spi.FeatureProvider;

@Configuration
@Slf4j
public class TogglzConfiguration {

    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(Features.class);
    }

    @Bean
    public FeatureNameStrategy featureNameStrategy() {
        return f -> String.format("feature.%s", CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, f.name()));
    }

    @Bean
    public StateRepository stateRepository(final Environment environment, final FeatureNameStrategy namingStrategy) {
        return new StateRepository() {

            @Override
            public FeatureState getFeatureState(final Feature feature) {
                final String property = namingStrategy.asProperty(feature);
                log.debug("Searching for the value of the boolean property: {}", property);

                final Boolean value = environment.getProperty(property, Boolean.class);
                if (value == null || !value) {
                    return new FeatureState(feature, false);
                }

                return new FeatureState(feature, true);
            }

            @Override
            public void setFeatureState(final FeatureState featureState) {
                log.warn("Setting '{}' feature state would not have any effect. State must be set in Consul",
                        featureState.getFeature().name());
            }

        };
    }

}
