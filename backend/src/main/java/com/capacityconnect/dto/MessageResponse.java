package com.capacityconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic simple message response, used for success/error confirmations
 * across many endpoints (e.g. "Registration successful, pending admin approval").
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private String message;

    private boolean success;
}