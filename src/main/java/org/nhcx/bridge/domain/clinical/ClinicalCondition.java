package org.nhcx.bridge.domain.clinical;

/**
 * Canonical internal representation of a clinical condition.
 * Independent of source system (HL7, CSV, etc.).
 */
public record ClinicalCondition(String code,
                                String display,
                                String codingSystem) {
}
