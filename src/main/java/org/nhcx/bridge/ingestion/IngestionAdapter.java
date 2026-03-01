package org.nhcx.bridge.ingestion;


import org.nhcx.bridge.canonical.EligibilityDTO;

/**
 * Generic Ingestion Adapter
 *
 * Converts raw source payloads (HL7, CSV, etc.)
 * into canonical domain models (EligibilityDTO, ClaimDTO, etc.)
 *
 * This abstraction allows the platform to support multiple
 * healthcare workflows without changing core logic.
 */
public interface IngestionAdapter<T> {

    boolean supports(String sourceType);

    T transform(String payload);
}
