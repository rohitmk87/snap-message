package com.jcp.omni.snap.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for error.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDto {
  private String errorCode;
  private String errorMessage;
}
