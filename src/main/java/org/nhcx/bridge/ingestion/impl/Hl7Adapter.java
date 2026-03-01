package org.nhcx.bridge.ingestion.impl;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;

import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.util.Terser;

import ca.uhn.hl7v2.validation.ValidationContext;
import ca.uhn.hl7v2.validation.impl.ValidationContextFactory;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.domain.clinical.ClinicalCondition;
import org.nhcx.bridge.ingestion.IngestionAdapter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * HL7 v2 Adapter using Terser.
 * Extracts Patient + Insurance details (IN1 segment)
 * Version-agnostic implementation.
 */
@Component
public class Hl7Adapter implements IngestionAdapter<EligibilityDTO> {

    private static final DateTimeFormatter HL7_DATE =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public boolean supports(String sourceType) {
        return "HL7".equalsIgnoreCase(sourceType);
    }

    @Override
    public EligibilityDTO transform(String payload) {


        try {

            payload = payload.replaceAll("\\r?\\n", "\r");

            HapiContext context = new DefaultHapiContext();
            // Set validation to nothing
            context.setValidationContext((ValidationContext) ValidationContextFactory.noValidation());

            Parser parser = context.getGenericParser();
            Message message = parser.parse(payload);

            System.out.println("print Structure: " + message.printStructure());

            Terser terser = new Terser(message);

            String patientId = terser.get("/PID-3-1");
            String patientName = terser.get("/PID-5-1") + " " + terser.get("/PID-5-2");
            String dob = terser.get("/PID-7");
            String gender = terser.get("/PID-8");

            String payerCode = terser.get("/.IN1-4");
            String policyNumber = terser.get("/.IN1-36");

            List<ClinicalCondition> conditions = extractConditions(terser);

            return new EligibilityDTO(
                    patientId,
                    patientName,
                    dob,
                    gender,
                    payerCode,
                    policyNumber,
                    conditions
            );

        } catch (Exception e) {
            throw new RuntimeException("HL7 Parsing Failed", e);
        }
    }

    private List<ClinicalCondition> extractConditions(Terser terser) {

        List<ClinicalCondition> conditions = new ArrayList<>();

        int insuranceRep = 0;

        while (true) {
            try {

                String diagnosis = terser.get("/INSURANCE(" + insuranceRep + ")/DG1(0)-3");

                if (diagnosis == null || diagnosis.isBlank()) {
                    break;
                }

                String[] parts = diagnosis.split("\\^");

                String code = parts.length > 0 ? parts[0] : null;
                String display = parts.length > 1 ? parts[1] : null;

                conditions.add(new ClinicalCondition(
                        code,
                        display,
                        "HL7"
                ));

                insuranceRep++;

            } catch (Exception e) {
                break;
            }
        }

        return conditions;
    }
}