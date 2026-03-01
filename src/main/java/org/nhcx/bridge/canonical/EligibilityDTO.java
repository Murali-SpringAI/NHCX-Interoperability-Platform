package org.nhcx.bridge.canonical;

import org.nhcx.bridge.domain.clinical.ClinicalCondition;

import java.time.LocalDate;
import java.util.List;

/**
 * Canonical model for Coverage Eligibility use case.
 * Acts as intermediate abstraction between legacy input and FHIR output.
 */
public record EligibilityDTO(
        String patientId,
        String patientName,
        String dateOfBirth,
        String gender,
        String payerCode,
        String policyNumber,
        List<ClinicalCondition> conditions

) {
}