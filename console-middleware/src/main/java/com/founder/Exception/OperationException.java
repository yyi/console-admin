package com.founder.Exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class OperationException extends RuntimeException implements  ErrorMessage{


    @Setter
    @Getter
    private ErrorMessage errorMessage;

    @Setter
    @Getter
    private Object attachInfo;

    public OperationException(ErrorMessage errorMessage, Throwable cause, Object attachInfo) {
        super(errorMessage.getErrorMessage(),cause);
        this.errorMessage = errorMessage;
        this.attachInfo = attachInfo;
    }

    public OperationException(ErrorMessage errorMessage){
        this(errorMessage, null, null);
    }

    public OperationException(ErrorMessage errorMessage,Throwable cause){
        this(errorMessage, cause, null);
    }

    public OperationException(ErrorMessage errorMessage,Object attachInfo){
        this(errorMessage, null, attachInfo);
    }


    @Override
    public String getErrorCode() {
        return errorMessage.getErrorCode();
    }

    @Override
    public String getErrorMessage(){
        return errorMessage.getErrorMessage();
    }
}
