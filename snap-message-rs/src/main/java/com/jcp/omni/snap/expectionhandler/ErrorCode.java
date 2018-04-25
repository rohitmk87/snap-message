/*
 * Copyright (c) 2016 JCPenney Co. All rights reserved.
 */

package com.jcp.omni.snap.expectionhandler;

/**
 * Coode of error.
 *
 * @author isedlovsky
 * @since 3/16/16
 */
public interface ErrorCode {
  /**
   * Returns error key.
   *
   * @return key
   */
  String getErrorKey();

  /**
   * Return error code.
   *
   * @return code
   */
  String getErrorCode();

  /**
   * Return Http-status.
   *
   * @return status
   */
  int getHttpStatus();

  /**
   * Returns error message.
   *
   * @return message
   */
  String getErrorMessage();
}
