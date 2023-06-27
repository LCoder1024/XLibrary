package org.devio.xlibrary.http;

import java.io.IOException;

public class ApiException extends IOException {
    private String code;

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
