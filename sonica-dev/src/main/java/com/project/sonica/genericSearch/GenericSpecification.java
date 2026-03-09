package com.project.sonica.genericSearch;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class GenericSpecification<T> implements Specification<T> {
	private SearchCriteria criteria;

	public GenericSpecification(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		switch (criteria.getOperation()) {
		case EQUAL:
			return cb.equal(root.get(criteria.getKey()), criteria.getValue());
		case LIKE:
			return cb.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
		case GREATER_THAN:
			return cb.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN:
			return cb.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case BETWEEN:
			if (criteria.getValue() instanceof List<?> values && values.size() == 2) {
				Object start = values.get(0);
				Object end = values.get(1);
				if (start instanceof Comparable && end instanceof Comparable) {
					return cb.between(root.get(criteria.getKey()), (Comparable) start, (Comparable) end);
				}
			}
			break;
		case IN:
			if (criteria.getValue() instanceof List<?> values) {
				CriteriaBuilder.In<Object> inClause = cb.in(root.get(criteria.getKey()));
				for (Object val : values) {
					inClause.value(val);
				}
				return inClause;
			}
			break;
		}
		return null;
	}
}
