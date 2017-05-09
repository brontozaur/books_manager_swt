package com.papao.books.config;

public enum BooleanSetting {

    SYSTEM_TRAY_MESSAGES(true),
    APP_USE_SYSTEM_TRAY(true),
    SEARCH_HIGHLIGHT_USES_BOLD(true),
    SEARCH_HIGHLIGHT_USES_COLOR(true),
    WINDOWS_REENTER_DATA(true),
    WINDOWS_ASK_ON_CLOSE(true),
    TABLES_USE_CONFIG(true),
    WINDOWS_USE_COORDS(true),
    SHOW_RICH_WINDOWS(true),
    REPORT_SHOW_OPTIONS(true),
    LEFT_TREE_SHOW_ALL(true),
    LEFT_TREE_SHOW_NUMBERS(true),
    LEFT_TREE_SHOW_RECENT(true),
    AUTOPOPULATE_TABS(true),
    PERSPECTIVE_SHOW_GALLERY(false);

    private boolean defaultValue;

    BooleanSetting(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }
}
