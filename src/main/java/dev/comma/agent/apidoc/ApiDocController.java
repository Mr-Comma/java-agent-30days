package dev.comma.agent.apidoc;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDocController {

    private final ApiDocParserService parserService;

    public ApiDocController(ApiDocParserService parserService) {
        this.parserService = parserService;
    }

    @PostMapping("/api-docs/parse")
    public ApiDocParseResponse parse(@RequestBody ApiDocParseRequest request) {
        return parserService.parse(request.openApiJson());
    }
}
