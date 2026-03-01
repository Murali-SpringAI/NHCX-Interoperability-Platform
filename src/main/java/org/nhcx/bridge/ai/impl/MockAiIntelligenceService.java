package org.nhcx.bridge.ai.impl;

import org.nhcx.bridge.ai.AiIntelligenceService;
import org.nhcx.bridge.canonical.ClaimDTO;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.domain.clinical.ClinicalCondition;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Mock AI Intelligence Service
 *
 * This simulates what an LLM/ML system would do:
 * - semantic normalization
 * - enrichment
 * - data quality correction
 *
 * Later this can call:
 * OpenAI / Bedrock / on-prem ML / terminology server
 */
@Service
public class MockAiIntelligenceService implements AiIntelligenceService {

    @Override
    public Object enhance(Object dto) {

        System.out.println("AI ENGINE: Processing object type -> " + dto.getClass().getSimpleName());

        if (dto instanceof EligibilityDTO eligibility) {
            return enhanceEligibility(eligibility);
        }

        if (dto instanceof ClaimDTO claim) {
            return enhanceClaim(claim);
        }

        // unknown domain — pass through safely
        return dto;
    }

    /**
     * Simulates clinical semantic normalization.
     */
    private EligibilityDTO enhanceEligibility(EligibilityDTO dto) {

        System.out.println("AI ENGINE: Normalizing patient demographics and clinical conditions");

        String normalizedGender = normalizeGender(dto.gender());

        List<ClinicalCondition> enrichedConditions =
                dto.conditions().stream()
                        .map(this::normalizeCondition)
                        .toList();

        return new EligibilityDTO(
                dto.patientId(),
                dto.patientName(),
                dto.dateOfBirth(),
                normalizedGender,
                dto.payerCode(),
                dto.policyNumber(),
                enrichedConditions
        );
    }

    /**
     * Simulates claim intelligence.
     * Example: fraud risk scoring or anomaly detection.
     */
    private ClaimDTO enhanceClaim(ClaimDTO dto) {

        System.out.println("AI ENGINE: Running claim anomaly detection");
        // mock logic: flag suspicious high value claims
        if (dto.amount() > 10000) {
            System.out.println("AI ALERT: High value claim detected → " + dto.claimId());
        }

        return dto;
    }

    /**
     * AI normalization example
     */
    private String normalizeGender(String gender) {
        if (gender == null) return "unknown";

        return switch (gender.toLowerCase()) {
            case "m", "male" -> "male";
            case "f", "female" -> "female";
            default -> "unknown";
        };
    }

    /**
     * Simulates free-text → normalized condition
     */
    private ClinicalCondition normalizeCondition(ClinicalCondition condition) {

        System.out.println("AI ENGINE: Normalizing condition -> " + condition.code());

        if (condition.code() == null) return condition;

        // pretend AI inferred better terminology
        return new ClinicalCondition(
                condition.code().toUpperCase(),
                condition.display(),
                "AI_NORMALIZED"
        );
    }
}