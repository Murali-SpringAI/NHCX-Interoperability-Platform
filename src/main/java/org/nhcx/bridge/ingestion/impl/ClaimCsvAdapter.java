package org.nhcx.bridge.ingestion.impl;

import org.nhcx.bridge.canonical.ClaimDTO;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.ingestion.IngestionAdapter;
import org.springframework.stereotype.Component;

/**
 * Claim CSV Adapter
 *
 * Implements the Strategy Pattern via IngestionAdapter.
 *
 * Responsibilities:
 * - Parse raw CSV payload
 * - Convert to canonical ClaimDTO
 *
 * Architectural Significance:
 * - No changes required in AdapterFactory
 * - Plug-and-play extension
 * - Validates open/closed principle
 */
@Component
public class ClaimCsvAdapter implements IngestionAdapter<ClaimDTO> {

    @Override
    public boolean supports(String sourceType) {
        // Enables dynamic resolution through AdapterFactory
        return "CLAIM_CSV".equalsIgnoreCase(sourceType);
    }

    @Override
    public ClaimDTO transform(String payload) {

        // Example CSV format:
        // claimId,patientId,providerId,payerCode,serviceDate,diagnosisCode,procedureCode,amount
        String[] parts = payload.split(",");

        return new ClaimDTO(
                parts[0].trim(),                // claimId
                parts[1].trim(),                // patientId
                parts[2].trim(),                // providerId
                parts[3].trim(),                // payerCode
                parts[4].trim(),                // serviceDate
                parts[5].trim(),                // diagnosisCode
                parts[6].trim(),                // procedureCode
                Double.valueOf(parts[7].trim()) // amount
        );
    }
}