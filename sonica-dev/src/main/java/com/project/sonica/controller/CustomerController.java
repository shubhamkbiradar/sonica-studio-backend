package com.project.sonica.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.CustomerRequest;
import com.project.sonica.dto.CustomerResponse;
import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.entity.Customer;
import com.project.sonica.mapper.CustomerMapper;
import com.project.sonica.responseBuilderUtility.ResponseBuilder;
import com.project.sonica.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
	private final CustomerService customerService;
	private final CustomerMapper customerMapper;
	private final ResponseBuilder responseBuilder;

	public CustomerController(CustomerService customerService, CustomerMapper customerMapper,
			ResponseBuilder responseBuilder) {
		this.customerService = customerService;
		this.customerMapper = customerMapper;
		this.responseBuilder = responseBuilder;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<CustomerResponse>> register(@RequestBody CustomerRequest request) {
		Customer customer = customerMapper.toEntity(request);
		Customer saved = customerService.registerCustomer(customer);
		CustomerResponse response = customerMapper.toResponse(saved);
		return responseBuilder.success(response, "Customer registered successfully");
	}

	@GetMapping("/{email}")
	public ResponseEntity<ApiResponse<CustomerResponse>> getByEmail(@PathVariable String email) {
		return customerService.findByEmail(email).map(customerMapper::toResponse)
				.map(res -> responseBuilder.success(res, "Customer found"))
				.orElse(responseBuilder.error(HttpStatus.NOT_FOUND, "Customer not found"));
	}

	@GetMapping
	public List<CustomerResponse> getAll() {
		return customerService.getAllCustomers().stream().map(customerMapper::toResponse).collect(Collectors.toList());
	}

}

// Old code

//@RestController
//@RequestMapping("/api/customers")
//public class CustomerController {
//    @Autowired
//    private CustomerService customerService;
//    @Autowired
//    private CustomerMapper customerMapper;
//
//    @PostMapping("/register")
//    public ResponseEntity<ApiResponse<CustomerResponse>> register(@RequestBody CustomerRequest request) {
//        Customer customer = customerMapper.toEntity(request);
//        Customer saved = customerService.registerCustomer(customer);
//        CustomerResponse response = customerMapper.toResponse(saved);
//
//        ApiResponse<CustomerResponse> apiResponse =
//                new ApiResponse<>(HttpStatus.OK.value(), "Customer registered successfully", response);
//
//        return ResponseEntity.ok(apiResponse);
//    }
//
//
//    @GetMapping("/{email}")
//    public ResponseEntity<CustomerResponse> getByEmail(@PathVariable String email) {
//        return customerService.findByEmail(email)
//                .map(customerMapper::toResponse)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping
//    public List<CustomerResponse> getAll() {
//        return customerService.getAllCustomers()
//                .stream()
//                .map(customerMapper::toResponse)
//                .collect(Collectors.toList());
//    }
//}
