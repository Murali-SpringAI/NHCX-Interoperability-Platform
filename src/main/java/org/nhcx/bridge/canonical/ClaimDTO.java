package org.nhcx.bridge.canonical;

/**
 * Canonical Claim Domain Model
 *
 * This represents the normalized, internal platform model for claims.
 *
 * Architectural Purpose:
 * - Acts as a domain abstraction between ingestion and FHIR transformation
 * - Shields downstream layers from raw CSV/HL7/API payload formats
 * - Enables AI enrichment before FHIR mapping
 *
 * By introducing ClaimDTO alongside EligibilityDTO,
 * we validate that our platform supports multiple healthcare domains
 * using the same architectural foundation.
 */
public record ClaimDTO(
        String claimId,
        String patientId,
        String providerId,
        String payerCode,
        String serviceDate,
        String diagnosisCode,
        String procedureCode,
        Double amount
) {
}