package org.nhcx.bridge.ingestion.impl;

import com.opencsv.CSVReader;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.domain.clinical.ClinicalCondition;
import org.nhcx.bridge.ingestion.IngestionAdapter;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvAdapter implements IngestionAdapter<EligibilityDTO> {

    @Override
    public boolean supports(String sourceType) {
        return "CSV".equalsIgnoreCase(sourceType);
    }

    @Override
    public EligibilityDTO transform(String payload) {

        try (CSVReader reader = new CSVReader(new StringReader(payload))) {

            List<String[]> rows = reader.readAll();

            if (rows.size() < 2) {
                throw new IllegalArgumentException("CSV contains no data rows");
            }

            List<ClinicalCondition> conditions = new ArrayList<>();

            String patientId = null;
            String patientName = null;
            String dateOfBirth = null;
            String gender = null;
            String payerCode = null;
            String policyNumber = null;

            // Skip header row (index 0)
            for (int i = 1; i < rows.size(); i++) {

                String[] row = rows.get(i);

                if (row.length < 8) {
                    throw new IllegalArgumentException("Invalid CSV row at line " + (i + 1));
                }

                patientId = row[0];
                patientName = row[1];
                dateOfBirth = row[2];
                gender = normalizeGender(row[3]);
                payerCode = row[4];
                policyNumber = row[5];

                String conditionCode = row[6];
                String conditionDisplay = row[7];

                if (conditionCode != null && !conditionCode.isBlank()) {
                    conditions.add(new ClinicalCondition(
                            conditionCode,
                            conditionDisplay,
                            "CSV"
                    ));
                }
            }

            return new EligibilityDTO(
                    patientId,
                    patientName,
                    dateOfBirth,
                    gender,
                    payerCode,
                    policyNumber,
                    conditions
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV payload", e);
        }
    }

    private String normalizeGender(String gender) {

        if (gender == null) return null;

        return switch (gender.trim().toUpperCase()) {
            case "M" -> "male";
            case "F" -> "female";
            case "O" -> "other";
            default -> "unknown";
        };
    }
}