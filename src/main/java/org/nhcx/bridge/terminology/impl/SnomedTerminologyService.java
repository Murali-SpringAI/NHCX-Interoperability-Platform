package org.nhcx.bridge.terminology.impl;

import org.nhcx.bridge.terminology.StandardizedCode;
import org.nhcx.bridge.terminology.TerminologyService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * SNOMED CT normalization implementation.
 * In production, this should call an external terminology server.
 */
@Service
public class SnomedTerminologyService implements TerminologyService {

    private static final Map<String, StandardizedCode> SNOMED_MAP = Map.of(
            "DIABETES", new StandardizedCode(
                    "http://snomed.info/sct",
                    "44054006",
                    "Type 2 diabetes mellitus"
            ),
            "HYPERTENSION", new StandardizedCode(
                    "http://snomed.info/sct",
                    "38341003",
                    "Hypertensive disorder"
            )
    );

    @Override
    public StandardizedCode normalize(String code, String sourceSystem) {

        if (code == null) {
            return null;
        }


        return SNOMED_MAP.getOrDefault(
                code.toUpperCase(),
                new StandardizedCode(
                        "http://snomed.info/sct",
                        code,
                        code
                )
        );
    }
}