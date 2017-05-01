package com.papao.books.view.util;

import com.papao.books.model.config.*;
import com.papao.books.repository.SettingsRepository;
import com.papao.books.view.auth.EncodeLive;
import com.papao.books.view.view.SWTeXtension;
import org.eclipse.swt.graphics.Rectangle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SettingsController {

    private static SettingsRepository settingRepository;

    @Autowired
    public SettingsController(SettingsRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public static WindowSetting getWindowSetting(String windowKey) {
        return settingRepository.getWindowSetting(windowKey, EncodeLive.getIdUser());
    }

    public static void saveWindowCoords(Rectangle bounds, String windowKey) {
        WindowSetting setting = getWindowSetting(windowKey);
        if (setting == null) {
            setting = new WindowSetting();
        }
        setting.setX(bounds.x);
        setting.setY(bounds.y);
        setting.setWidth(bounds.width);
        setting.setHeight(bounds.height);
        setting.setWindowKey(windowKey);
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static TableSetting getTableSetting(int nrOfColumns, Class clazz, String tableKey) {
        TableSetting setting = settingRepository.getTableSetting(clazz.getCanonicalName(), tableKey, EncodeLive.getIdUser());
        if (setting == null || setting.getNrOfColumns() == 0) {
            setting = new TableSetting(nrOfColumns, clazz.getCanonicalName(), tableKey);
            if (setting.isValid()) {
                setting = settingRepository.save(setting);
            } else {
                SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
            }
        }
        return setting;
    }

    public static void saveTableConfig(TableSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static GeneralSetting getGeneralSetting(String settingKey) {
        return settingRepository.getGeneralSetting(settingKey, EncodeLive.getIdUser());
    }

    public static void saveGeneralSetting(String key, Object value) {
        GeneralSetting setting = settingRepository.getGeneralSetting(key, EncodeLive.getIdUser());
        if (setting == null) {
            setting = new GeneralSetting();
        }
        setting.setKey(key);
        setting.setValue(value);
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static ExportPdfSetting getExportPdfSetting() {
        ExportPdfSetting setting = settingRepository.getExportPdfSetting(EncodeLive.getIdUser());
        if (setting == null) {
            setting = new ExportPdfSetting();
            if (setting.isValid()) {
                settingRepository.save(setting);
            } else {
                SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
            }
        }
        return setting;
    }

    public static void saveExportPdfSetting(ExportPdfSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static ExportXlsSetting getExportXlsSetting() {
        return settingRepository.getExportXlsSetting(EncodeLive.getIdUser());
    }

    public static void saveExportXlsSetting(ExportXlsSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static ExportHtmlSetting getExportHtmlSetting() {
        return settingRepository.getExportHtmlSetting(EncodeLive.getIdUser());
    }

    public static void saveExportHtmlSetting(ExportHtmlSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static ExportRtfSetting getExportRtfSetting() {
        return settingRepository.getExportRtfSetting(EncodeLive.getIdUser());
    }

    public static void saveExportRtfSetting(ExportRtfSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }

    public static ExportTxtSetting getExportTxtSetting() {
        return settingRepository.getExportTxtSetting(EncodeLive.getIdUser());
    }

    public static void saveExportTxtSetting(ExportTxtSetting setting) {
        if (setting.isValid()) {
            settingRepository.save(setting);
        } else {
            SWTeXtension.displayMessageW("Setare invalida!", setting.toString());
        }
    }
}
