package com.qing.fan.common.enumeration;

/**
 * @author bluefish 2019-09-28
 * @version 1.0.0
 */
public enum YesNoEnum {
    YES(1, "是"),
    NO(0, "否");


    private Integer type;

    private String description;

    YesNoEnum(Integer type, String description) {
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
