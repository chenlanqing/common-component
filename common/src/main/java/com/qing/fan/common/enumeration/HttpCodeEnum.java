package com.qing.fan.common.enumeration;

/**
 * @author bluefish 2019-10-01
 * @version 1.0.0
 */
public enum  HttpCodeEnum {

    CODE_200(200, "成功"),
    CODE_400(400, "参数异常"),
    CODE_500(500, "系统异常，稍后重试");


    private Integer type;

    private String description;

    HttpCodeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
