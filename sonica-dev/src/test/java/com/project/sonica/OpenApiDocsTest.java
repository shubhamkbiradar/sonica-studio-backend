package com.project.sonica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class OpenApiDocsTest {
	@Autowired
	private WebApplicationContext wac;

	@Test
	void apiDocsShouldLoad() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		MvcResult result = mockMvc.perform(get("/v3/api-docs")).andReturn();
		int status = result.getResponse().getStatus();
		String body = result.getResponse().getContentAsString();

		if (result.getResolvedException() != null) {
			result.getResolvedException().printStackTrace();
		}

		assertThat(status).withFailMessage("Expected 200 but got %s. Body:\n%s", status, body).isEqualTo(200);
	}
}
