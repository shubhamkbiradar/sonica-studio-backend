package com.project.sonica.aop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.sonica.aop.annotations.RequireRole;
import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.entity.Customer;
import com.project.sonica.repos.CustomerRepository;

@SpringBootTest
@Import(AopSecurityAndTransactionTest.TestBeans.class)
class AopSecurityAndTransactionTest {
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private TestAopService testAopService;

	@BeforeEach
	void clearDb() {
		customerRepository.deleteAll();
	}

	@Test
	void transaction_rollsBackOnCheckedException_whenConfigured() {
		assertThrows(BoomCheckedException.class, () -> testAopService.createCustomerThenThrowRollback("a@a.com"));
		assertThat(customerRepository.count()).isZero();
	}

	@Test
	void transaction_commitsOnCheckedException_byDefault() {
		assertThrows(BoomCheckedException.class, () -> testAopService.createCustomerThenThrowNoRollback("b@b.com"));
		assertThat(customerRepository.count()).isEqualTo(1);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void security_allowsRole() {
		assertThat(testAopService.adminOnly()).isEqualTo("ok");
	}

	@Test
	@WithMockUser(roles = "CUSTOMER")
	void security_deniesRole() {
		assertThrows(AccessDeniedException.class, () -> testAopService.adminOnly());
	}

	static class BoomCheckedException extends Exception {
		private static final long serialVersionUID = 1L;

		BoomCheckedException(String message) {
			super(message);
		}
	}

	static class TestAopService {
		private final CustomerRepository customerRepository;

		TestAopService(CustomerRepository customerRepository) {
			this.customerRepository = customerRepository;
		}

		@SonicaTx(rollbackOn = { Exception.class })
		void createCustomerThenThrowRollback(String email) throws BoomCheckedException {
			customerRepository.save(newCustomer(email));
			throw new BoomCheckedException("boom");
		}

		@SonicaTx
		void createCustomerThenThrowNoRollback(String email) throws BoomCheckedException {
			customerRepository.save(newCustomer(email));
			throw new BoomCheckedException("boom");
		}

		@RequireRole("ADMIN")
		String adminOnly() {
			return "ok";
		}

		private static Customer newCustomer(String email) {
			Customer c = new Customer();
			c.setEmail(email);
			c.setName("Test");
			return c;
		}
	}

	static class TestBeans {
		@Bean
		TestAopService testAopService(CustomerRepository customerRepository) {
			return new TestAopService(customerRepository);
		}
	}
}
