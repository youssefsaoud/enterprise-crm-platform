package com.example.crm.invitations;

import com.example.crm.common.web.ApiError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class InvitationExceptionHandler {
    @ExceptionHandler(InvitationException.class)
    ResponseEntity<ApiError> handle(InvitationException error) {
        return ResponseEntity.status(error.status)
            .body(ApiError.of(error.status, "Invitation request failed", error.getMessage()));
    }
}
