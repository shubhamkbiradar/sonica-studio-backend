package com.project.sonica.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Local/dev safety net.
 *
 * If Flyway is disabled, we force Hibernate schema auto-update so a fresh local DB
 * can boot without pre-created tables. This also protects against accidental env var
 * overrides like SPRING_JPA_HIBERNATE_DDL_AUTO=validate.
 *
 * You can override the forced value with: sonica.hibernate.ddl-auto=validate|update|none
 */
@Configuration
public class HibernateSchemaConfig {
	private static final Logger log = LoggerFactory.getLogger(HibernateSchemaConfig.class);

	@Bean
	public HibernatePropertiesCustomizer hibernateSchemaCustomizer(Environment env) {
		return hibernateProperties -> {
			boolean flywayEnabled = env.getProperty("spring.flyway.enabled", Boolean.class, true);

			// Explicit escape hatch (lets CI/prod tighten things without changing code).
			String forced = env.getProperty("sonica.hibernate.ddl-auto");

			if (forced == null && !flywayEnabled) {
				forced = "update";
			}

			if (forced != null) {
				hibernateProperties.put("hibernate.hbm2ddl.auto", forced);
				log.info("Hibernate schema management forced to '{}' (flyway.enabled={})", forced, flywayEnabled);
			}
		};
	}
}
