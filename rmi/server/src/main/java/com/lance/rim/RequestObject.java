package com.lance.rim;

import java.io.Serializable;
import java.util.Arrays;

public class RequestObject implements Serializable {

    private static final long serialVersionUID = -3773367994815078072L;
    private String className;
    private String MethodName;
    private Object[] args;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RequestObject{" +
                "className='" + className + '\'' +
                ", MethodName='" + MethodName + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
