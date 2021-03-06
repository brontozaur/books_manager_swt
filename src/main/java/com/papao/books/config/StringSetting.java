package com.papao.books.config;

import com.papao.books.controller.SettingsController;

public enum StringSetting {

    APP_TIME_FORMAT(SettingsController.DEFAULT_TIME_FORMAT),
    APP_DATE_FORMAT(SettingsController.DEFAULT_DATE_FORMAT);

    private String defaultValue;

    StringSetting(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
