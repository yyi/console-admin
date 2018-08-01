package com.founder.exception.dailyadmin;

import com.founder.Exception.ErrorMessage;

/**
 *
 */
public enum EquipmentError implements ErrorMessage {

    EquipmentRecordNotExists("700001", "未查到设备管理填报记录。" );



    private String errorMessage;

    private String errorCode;


    EquipmentError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
