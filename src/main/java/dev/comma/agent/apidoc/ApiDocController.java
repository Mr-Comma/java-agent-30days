package dev.comma.agent.apidoc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDocController {

    private final ApiDocParserService parserService;
    private final ApiDocAnalyzerService analyzerService;
    private final ApiDocDebugSchemaService debugSchemaService;

    public ApiDocController(
            ApiDocParserService parserService,
            ApiDocAnalyzerService analyzerService,
            ApiDocDebugSchemaService debugSchemaService) {
        this.parserService = parserService;
        this.analyzerService = analyzerService;
        this.debugSchemaService = debugSchemaService;
    }

    @PostMapping("/api-docs/parse")
    public ApiDocParseResponse parse(@RequestBody ApiDocParseRequest request) {
        return parserService.parse(request.openApiJson());
    }

    @PostMapping("/api-docs/analyze")
    public ApiDocAnalysisResponse analyze(@RequestBody ApiDocParseRequest request) {
        return analyzerService.analyze(request.openApiJson());
    }

    @GetMapping("/api-docs/debug-schema")
    public ApiDocDebugSchemaResponse debugSchema() {
        return debugSchemaService.schema();
    }
}
