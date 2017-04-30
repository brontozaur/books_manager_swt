package com.papao.books.model.config;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

public class GeneralSetting extends AbstractSetting {

    private String key;
    private Object value;

    public GeneralSetting(ObjectId idUser, String numeUser) {
        super(idUser, numeUser, SettingType.GENERAL);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(key) && value != null;
    }
}
