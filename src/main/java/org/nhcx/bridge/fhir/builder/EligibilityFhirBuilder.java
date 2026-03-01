package org.nhcx.bridge.fhir.builder;

import org.hl7.fhir.r4.model.*;
import org.nhcx.bridge.canonical.ClaimDTO;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.domain.clinical.ClinicalCondition;
import org.nhcx.bridge.fhir.FhirUseCaseBuilder;
import org.nhcx.bridge.terminology.StandardizedCode;
import org.nhcx.bridge.terminology.TerminologyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EligibilityFhirBuilder implements FhirUseCaseBuilder {

    @Autowired
    private TerminologyService terminologyService;

    @Override
    public boolean supports(String useCase) {
        return "ELIGIBILITY".equalsIgnoreCase(useCase);
    }

    @Override
    public Bundle build(Object dto) {

        if (!(dto instanceof EligibilityDTO )) {
            throw new IllegalArgumentException("Invalid DTO type for Eligibility use case");
        }
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);

        addPatient(bundle, (EligibilityDTO) dto);
        addCoverage(bundle, (EligibilityDTO) dto);
        addConditions(bundle, (EligibilityDTO) dto);

        return bundle;
    }

    private void addPatient(Bundle bundle, EligibilityDTO dto) {

        Patient patient = new Patient();
        patient.setId(dto.patientId());
        patient.addName().setText(dto.patientName());
        patient.setGender(mapGender(dto.gender()));

        bundle.addEntry().setResource(patient);
    }

    private void addCoverage(Bundle bundle, EligibilityDTO dto) {

        if (dto.policyNumber() == null && dto.payerCode() == null) {
            return;
        }

        Coverage coverage = new Coverage();

        coverage.setStatus(Coverage.CoverageStatus.ACTIVE);

        // Policy number
        if (dto.policyNumber() != null) {
            coverage.setSubscriberId(dto.policyNumber());
        }

        // Payer organization
        if (dto.payerCode() != null) {
            Organization payer = new Organization();
            payer.setName(dto.payerCode());

            bundle.addEntry().setResource(payer);

            coverage.addPayor(new Reference(payer));
        }

        bundle.addEntry().setResource(coverage);
    }

    private void addConditions(Bundle bundle, EligibilityDTO dto) {

        if (dto.conditions() == null) return;

        for (ClinicalCondition condition : dto.conditions()) {

            StandardizedCode normalized =
                    terminologyService.normalize(condition.code(), condition.codingSystem());

            if (normalized == null) continue;

            Condition fhirCondition = new Condition();

            fhirCondition.setCode(
                    new CodeableConcept().addCoding(
                            new Coding()
                                    .setSystem(normalized.system())
                                    .setCode(normalized.code())
                                    .setDisplay(normalized.display())
                    )
            );

            bundle.addEntry().setResource(fhirCondition);
        }
    }

    private Enumerations.AdministrativeGender mapGender(String gender) {

        if (gender == null) {
            return Enumerations.AdministrativeGender.UNKNOWN;
        }

        return switch (gender.trim().toLowerCase()) {
            case "m", "male" -> Enumerations.AdministrativeGender.MALE;
            case "f", "female" -> Enumerations.AdministrativeGender.FEMALE;
            case "other", "o" -> Enumerations.AdministrativeGender.OTHER;
            default -> Enumerations.AdministrativeGender.UNKNOWN;
        };
    }
}