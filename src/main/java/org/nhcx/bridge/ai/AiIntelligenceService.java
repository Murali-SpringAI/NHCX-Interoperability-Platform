package org.nhcx.bridge.ai;

import org.nhcx.bridge.canonical.ClaimDTO;
import org.nhcx.bridge.canonical.EligibilityDTO;

/**
 * AI Intelligence Hub
 *
 * Central enrichment service operating on canonical domain models.
 *
 * Architectural Role:
 * - Enhances normalized healthcare data before FHIR transformation
 * - Provides semantic normalization
 * - Enables future ML/LLM integration
 *
 * IMPORTANT:
 * This layer does NOT know about HL7 or FHIR.
 * It only understands canonical domain objects.
 */
public interface AiIntelligenceService {

    /**
     * Enhances any canonical DTO.
     * Different domain types are handled internally.
     */
    Object enhance(Object dto);
}
