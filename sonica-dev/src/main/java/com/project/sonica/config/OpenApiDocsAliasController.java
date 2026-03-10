package com.project.sonica.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Some tools/environments are configured to fetch OpenAPI JSON from `/v3/docs`,
 * while springdoc's default is `/v3/api-docs`.
 *
 * Forwarding keeps the response shape identical (no client-visible redirects).
 */
@Controller
public class OpenApiDocsAliasController {
	@GetMapping({ "/v3/docs", "/v3/docs/" })
	public String forwardDocsRoot() {
		return "forward:/v3/api-docs";
	}

	@GetMapping("/v3/docs/swagger-config")
	public String forwardSwaggerConfig() {
		return "forward:/v3/api-docs/swagger-config";
	}

	// GroupedOpenApi uses `/v3/api-docs/{group}`; keep parity for `/v3/docs/{group}`.
	@GetMapping("/v3/docs/{group}")
	public String forwardDocsGroup(@PathVariable("group") String group) {
		return "forward:/v3/api-docs/" + group;
	}
}

