package org.nhcx.bridge.api;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Bundle;
import org.nhcx.bridge.ai.AiIntelligenceService;
import org.nhcx.bridge.canonical.EligibilityDTO;
import org.nhcx.bridge.fhir.FhirUseCaseBuilder;
import org.nhcx.bridge.fhir.UseCaseFactory;
import org.nhcx.bridge.ingestion.AdapterFactory;
import org.nhcx.bridge.ingestion.IngestionAdapter;
import org.springframework.web.bind.annotation.*;


/**
 * Transform Controller
 *
 * Orchestrates the interoperability pipeline:
 *
 * 1. Ingestion (Strategy Pattern)
 * 2. Canonical DTO normalization
 * 3. AI Enhancement Layer
 * 4. FHIR Use Case Builder (Factory Pattern)
 *
 * This layered approach ensures:
 * - Separation of concerns
 * - Extensibility
 * - Pluggable intelligence
 */
@RestController
@RequestMapping("/transform")
public class TransformController {

    private final AdapterFactory adapterFactory;
    private final UseCaseFactory useCaseFactory;
    private final AiIntelligenceService aiIntelligenceService;

    /**
     * Constructor Injection ensures:
     * - Loose coupling
     * - Testability
     * - Swappable AI implementations
     */
    public TransformController(AdapterFactory adapterFactory,
                               UseCaseFactory useCaseFactory,
                               AiIntelligenceService aiIntelligenceService) {
        this.adapterFactory = adapterFactory;
        this.useCaseFactory = useCaseFactory;
        this.aiIntelligenceService = aiIntelligenceService;
    }

    @PostMapping
    public String transform(@RequestParam String sourceType,
                            @RequestParam String useCase,
                            @RequestBody String payload) {

        // --- STEP 1: Ingest raw payload into canonical model ---
        IngestionAdapter<?> adapter =
                adapterFactory.getAdapter(sourceType);

        Object dto = adapter.transform(payload);

        // --- STEP 2: AI Enhancement Layer ---
        // This validates that our canonical model can be enriched
        // independently of ingestion and FHIR transformation logic.

        dto = aiIntelligenceService.enhance(dto);


        // --- STEP 3: Transform canonical DTO into FHIR bundle ---
        FhirUseCaseBuilder<EligibilityDTO> builder =
                useCaseFactory.getBuilder(useCase);

        Bundle bundle = builder.build(dto);

        // --- STEP 4: Serialize to FHIR JSON ---
        return FhirContext.forR4()
                .newJsonParser()
                .setPrettyPrint(true)
                .encodeResourceToString(bundle);
    }
}