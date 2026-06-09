package dev.comma.agent.apidoc;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ApiDocAnalyzerService {

    private final ApiDocParserService parserService;

    public ApiDocAnalyzerService(ApiDocParserService parserService) {
        this.parserService = parserService;
    }

    public ApiDocAnalysisResponse analyze(String openApiJson) {
        ApiDocParseResponse parseResponse = parserService.parse(openApiJson);
        List<ApiEndpointAdvice> advices = parseResponse.endpoints().stream()
                .map(ApiEndpointAdvice::from)
                .toList();
        return new ApiDocAnalysisResponse(parseResponse.endpointCount(), advices);
    }
}
