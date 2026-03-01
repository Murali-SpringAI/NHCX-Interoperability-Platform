package org.nhcx.bridge.fhir;

import org.hl7.fhir.r4.model.Bundle;

/**
 * Generic FHIR use case builder.
 * Each implementation handles a specific use case:
 *  - ELIGIBILITY
 *  - CLAIM
 *  - COMMUNICATION
 *
 * @param <T> Canonical DTO type
 */
public interface FhirUseCaseBuilder<T> {

    /**
     * Determines if this builder supports the requested use case.
     */
    boolean supports(String useCase);

    /**
     * Builds a FHIR Bundle from canonical DTO.
     */
    Bundle build(Object dto);
}