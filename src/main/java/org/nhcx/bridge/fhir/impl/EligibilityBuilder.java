package org.nhcx.bridge.fhir.impl;

import org.hl7.fhir.r4.model.*;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.fhir.FhirUseCaseBuilder;
import org.springframework.stereotype.Component;

import java.sql.Date;

/**
 * Builds FHIR bundle for Coverage Eligibility use case.
 */
@Component
public class EligibilityBuilder implements FhirUseCaseBuilder {

    @Override
    public boolean supports(String useCase) {
        return "ELIGIBILITY".equalsIgnoreCase(useCase);
    }

    @Override
    public Bundle build(Object dto) {

        if (!(dto instanceof EligibilityDTO )) {
            throw new IllegalArgumentException("Invalid DTO type for Eligibility use case");
        }
        // --------------------
        // Patient Resource
        // --------------------
        Patient patient = new Patient();
        patient.setId(((EligibilityDTO) dto).patientId());
        patient.addName().setText(((EligibilityDTO) dto).patientName());
        patient.setGender(Enumerations.AdministrativeGender.fromCode(((EligibilityDTO) dto).gender()));
        patient.setBirthDate(Date.valueOf(((EligibilityDTO) dto).dateOfBirth()));

        // --------------------
        // Coverage Resource
        // --------------------
        Coverage coverage = new Coverage();
        coverage.setStatus(Coverage.CoverageStatus.ACTIVE);
        coverage.setSubscriberId(((EligibilityDTO) dto).policyNumber());
        coverage.setBeneficiary(new Reference("Patient/" + ((EligibilityDTO) dto).patientId()));
        coverage.addPayor().setDisplay(((EligibilityDTO) dto).payerCode());

        // --------------------
        // Bundle
        // --------------------
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);

        bundle.addEntry()
                .setResource(patient);

        bundle.addEntry()
                .setResource(coverage);

        return bundle;
    }
}