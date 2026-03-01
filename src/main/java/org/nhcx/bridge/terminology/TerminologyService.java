package org.nhcx.bridge.terminology;

/**
 * Generic terminology normalization contract.
 * Converts incoming source codes into standardized codes.
 */
public interface TerminologyService {

    StandardizedCode normalize(String code, String sourceSystem);
}