package com.project.sonica.sortAndFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.project.sonica.genericSearch.SearchOperation;
import com.project.sonica.genericSearch.SpecificationBuilder;

@Component
public class FilterParser {

	public <T> Specification<T> parseFilters(Map<String, String> filters, boolean useOr) {
		SpecificationBuilder<T> builder = new SpecificationBuilder<>();

		for (Map.Entry<String, String> entry : filters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			// Handle IN queries (comma-separated values)
			if (value.contains(",")) {
				List<String> values = Arrays.stream(value.split(",")).map(String::trim).toList();
				builder.with(key, values, SearchOperation.IN);
			}
			// Handle ranges
			else if (key.equalsIgnoreCase("startDate") && filters.containsKey("endDate")) {
				LocalDate start = LocalDate.parse(filters.get("startDate"));
				LocalDate end = LocalDate.parse(filters.get("endDate"));
				builder.with("eventDate", Arrays.asList(start, end), SearchOperation.BETWEEN);
			} else if (key.startsWith("min") || key.startsWith("max")) {
				String field = key.replace("min", "").replace("max", "");
				field = Character.toLowerCase(field.charAt(0)) + field.substring(1);

				String minKey = "min" + field.substring(0, 1).toUpperCase() + field.substring(1);
				String maxKey = "max" + field.substring(0, 1).toUpperCase() + field.substring(1);

				if (filters.containsKey(minKey) && filters.containsKey(maxKey)) {
					builder.with(field, Arrays.asList(filters.get(minKey), filters.get(maxKey)),
							SearchOperation.BETWEEN);
				}
			}
			// Default: equality or LIKE
			else {
				if (value.matches("\\d+")) {
					builder.with(key, Integer.parseInt(value), SearchOperation.EQUAL);
				} else if (value.matches("\\d+(\\.\\d+)?")) {
					builder.with(key, new BigDecimal(value), SearchOperation.EQUAL);
				} else {
					builder.with(key, value, SearchOperation.LIKE);
				}
			}
		}

		return useOr ? builder.build() : builder.build();
	}
}
