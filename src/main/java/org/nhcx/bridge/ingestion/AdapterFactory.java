package org.nhcx.bridge.ingestion;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdapterFactory {

    private final List<IngestionAdapter<?>> adapters;

    public AdapterFactory(List<IngestionAdapter<?>> adapters) {
        this.adapters = adapters;
    }

    public IngestionAdapter<?> getAdapter(String sourceType) {
        return adapters.stream()
                .filter(a -> a.supports(sourceType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unsupported source type: " + sourceType));
    }
}