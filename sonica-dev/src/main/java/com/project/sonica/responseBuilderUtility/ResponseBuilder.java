package com.project.sonica.responseBuilderUtility;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.pagedResponse.PagedResponse;

@Component("apiResponseBuilder")
public class ResponseBuilder {

    public <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                message,
                data
        );
        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                message,
                data
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String message) {
        ApiResponse<T> response = new ApiResponse<>(
                status.value(),
                message,
                null
        );
        return ResponseEntity.status(status).body(response);
    }

    public <T> ResponseEntity<PagedResponse<T>> paged(Page<T> pageData, String message) {
        PagedResponse<T> response = new PagedResponse<>(
                HttpStatus.OK.value(),
                message,
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
        return ResponseEntity.ok(response);
    }
}
