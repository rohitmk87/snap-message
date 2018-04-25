/*
 * Copyright (c) 2018 JCPenney Co. All rights reserved.
 */

package com.jcp.omni.snap.expectionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcp.omni.snap.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class BusinessExceptionMapper {

  private static final Logger logger = LoggerFactory.getLogger(BusinessExceptionMapper.class);
  private static ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Catches custom exception & returns error DTO to upstream system
   *
   * @param clientException code of error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<List<ErrorDto>> handleClientException(Exception clientException) {
    logger.debug("Caught ClientException in advice controller.. ");
    List<ErrorDto> errorDtoList = new ArrayList<>();
    ErrorDto errorDto = new ErrorDto();
    errorDto.setErrorCode("GENERIC_ERROR");
    errorDto.setErrorMessage("Generic Exception occurred.");
    errorDtoList.add(errorDto);
    logger.info("ControllerAdvice-ClientException occurred in service call - " + clientException);
    return new ResponseEntity<List<ErrorDto>>(errorDtoList, HttpStatus.NOT_FOUND);
  }


}
