/*
 * Copyright (c) 2018 JCPenney Co. All rights reserved.
 */

package com.jcp.omni.snap.expectionhandler;

public class BusinessException extends RuntimeException {
  private final ErrorCode errorCode;

  /**
   * Constructs exception with specified error code and message.
   *
   * @param errorCode code of error
   * @param message   message
   */
  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Constructs exception with specified error code, message, severity and numeric error code.
   *
   * @param errorCode code of error (string)
   * @param message message
   * @param severity error severity SEV1/2/3
   * @param numericErrorCode code of error (number, e.g. 404, 503, etc.)
   */
  protected BusinessException(ErrorCode errorCode, String message, String severity,
                              int numericErrorCode) {
    super("severity=" + severity
        + "numericErrorCode=" + numericErrorCode
        + "errorCode=" + errorCode.getErrorCode()
        + ", httpStatusCode=" + errorCode.getHttpStatus()
        + ", message=[" + message + "]");
    this.errorCode = errorCode;
  }

  /**
   * Constructs exception with specified error code, message, severity, numeric error code and cause.
   *
   * @param errorCode code of error (string)
   * @param message message
   * @param severity error severity SEV1/2/3
   * @param numericErrorCode code of error (number, e.g. 404, 503, etc.)
   * @param cause exception which was wraped by this exception
   */
  protected BusinessException(ErrorCode errorCode, String message, String severity,
                              int numericErrorCode, Throwable cause) {
    super("severity=" + severity
        + "numericErrorCode=" + numericErrorCode
        + "errorCode=" + errorCode.getErrorCode()
        + ", httpStatusCode=" + errorCode.getHttpStatus()
        + ", message=[" + message + "]", cause);
    this.errorCode = errorCode;
  }

  /**
   * Gets error code of exception.
   *
   * @return code of error
   */
  public ErrorCode getErrorCode() {
    return errorCode;
  }

}
