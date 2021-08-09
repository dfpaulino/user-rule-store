package org.farmtec.store.subscriber.rule.store.exceptions;

import org.springframework.dao.DataAccessException;

/**
 * Created by dp on 05/08/2021
 */
public class RuleServiceException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMsg;
    private Throwable throwable;

    public RuleServiceException(String errorMsg, ErrorCode errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public RuleServiceException(Throwable e) {
        super(e);
        this.errorMsg = e.getMessage();
        this.throwable = e;
    }

    public RuleServiceException(String message, Throwable err) {
        super(message, err);
        this.errorMsg = message;
        this.throwable = err;
    }

    public ErrorCode getErrorCode() {
        ErrorCode localErrorCode = ErrorCode.GENERIC;
        if (null != this.errorCode) {
            localErrorCode = this.errorCode;
        } else {
            //try to figure out a erorCode based on throwable
            if (throwable instanceof DataAccessException) {
                localErrorCode = ErrorCode.STORAGE_ERROR;
            }
        }

        return localErrorCode;

    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
