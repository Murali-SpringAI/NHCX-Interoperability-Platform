package org.nhcx.bridge.fhir.builder;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Claim;
import org.hl7.fhir.r4.model.Money;
import org.hl7.fhir.r4.model.Reference;
import org.nhcx.bridge.canonical.ClaimDTO;
import org.nhcx.bridge.fhir.FhirUseCaseBuilder;
import org.springframework.stereotype.Component;

/**
 * Claim FHIR Builder
 *
 * Implements FhirUseCaseBuilder<ClaimDTO>.
 *
 * Responsibilities:
 * - Transform canonical ClaimDTO into FHIR-compliant Claim resource
 * - Encapsulate FHIR mapping logic for claim domain
 *
 * Architectural Significance:
 * - Uses Factory Pattern via UseCaseFactory
 * - Keeps FHIR mapping isolated from ingestion and AI layers
 * - Enables additional domains without modifying controller
 */
@Component
public class ClaimFhirBuilder implements FhirUseCaseBuilder {

    @Override
    public boolean supports(String useCase) {
        return "CLAIM".equalsIgnoreCase(useCase);
    }

    @Override
    public Bundle build(Object dto) {

        if (!(dto instanceof ClaimDTO claimDTO)) {
            throw new IllegalArgumentException("Invalid DTO type for CLAIM use case");
        }

        // --- Create FHIR Claim Resource ---
        Claim claim = new Claim();
        claim.setId(((ClaimDTO) dto).claimId());
        claim.setStatus(Claim.ClaimStatus.ACTIVE);

        // --- Reference Patient ---
        claim.setPatient(new Reference("Patient/" + ((ClaimDTO) dto).patientId()));

        // --- Add Diagnosis Coding ---
        claim.addDiagnosis()
                .getDiagnosisCodeableConcept()
                .addCoding()
                .setSystem("http://hl7.org/fhir/sid/icd-10")
                .setCode(((ClaimDTO) dto).diagnosisCode());

        // --- Add Procedure Coding ---
        claim.addProcedure()
                .getProcedureCodeableConcept()
                .addCoding()
                .setSystem("http://www.ama-assn.org/go/cpt")
                .setCode(((ClaimDTO) dto).procedureCode());

        // --- Set Claim Total ---
        claim.setTotal(new Money().setValue(((ClaimDTO) dto).amount()));

        // --- Wrap in FHIR Bundle ---
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.addEntry().setResource(claim);

        return bundle;
    }
}