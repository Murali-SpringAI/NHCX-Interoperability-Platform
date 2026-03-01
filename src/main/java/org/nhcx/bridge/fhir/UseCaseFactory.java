package org.nhcx.bridge.fhir;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory to select appropriate FHIR builder based on use case.
 * Uses Spring auto-wired list of all implementations.
 */
@Component
public class UseCaseFactory {

    private final List<FhirUseCaseBuilder> builders;

    public UseCaseFactory(List<FhirUseCaseBuilder> builders) {
        this.builders = builders;
    }

    public FhirUseCaseBuilder getBuilder(String useCase) {
        return builders.stream()
                .filter(b -> b.supports(useCase))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unsupported use case: " + useCase));
    }
}