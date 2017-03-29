package com.papao.books.util;

import com.papao.books.ui.AppImages;

import java.util.Map;
import java.util.TreeMap;

public final class AtomUtils {

    public final static int MODUL_ABSTRACT_APP = 100;
    /**
     * MODUL_ABSTRACT_APP se va folosi in principiu pt setari la nivel de app si user care nu tin de nici un modul
     **/

    public final static int MODUL_CONFIG = 101;
    public final static int MODUL_CONTABILITATE = 102;
    public final static int MODUL_GESTIUNE = 103;
    public final static int MODUL_VALUTE = 104;
    public final static int MODUL_ASOCIATIE = 105;

    public final static String MODUL_ABSTRACT_APP_STR = "Aplicatie";

    public final static String MODUL_CONFIG_STR = "Administrare";
    public final static String MODUL_CONFIG_DESC = AtomUtils.MODUL_CONFIG_STR + " - setare parametri aplicatie";

    public final static String MODUL_CONTABILITATE_STR = "Contabilitate";
    public final static String MODUL_CONTABILITATE_DESC = AtomUtils.MODUL_CONTABILITATE_STR + " - inregistrari financiare";

    public final static String MODUL_GESTIUNE_STR = "Gestiune";
    public final static String MODUL_GESTIUNE_DESC = AtomUtils.MODUL_GESTIUNE_STR + " - articole si stocuri";

    public final static String MODUL_VALUTE_STR = "Info valutar";
    public final static String MODUL_VALUTE_DESC = AtomUtils.MODUL_VALUTE_STR + " - valute si banci";

    public final static String MODUL_ASOCIATIE_STR = "Asociatie";
    public final static String MODUL_ASOCIATIE_DESC = AtomUtils.MODUL_ASOCIATIE_STR + " - evidente asociatii de locatari";

    public static Map<Integer, String> mapModules = new TreeMap<Integer, String>();
    private static Map<Integer, String> mapModulesDesc = new TreeMap<Integer, String>();

    public static final String FIELD_MODULE_PREFIX = "MODUL_";
    public static final String FIELD_MODULE_SUFIX = "_STR";
    public static final String FIELD_MODULE_DESC_SUFIX = "_DESC";

    public static final String FIELD_TABS_PREFIX = "TAB_";
    public static final String FIELD_TABS_SUFIX = "_STR";

    public static boolean IS_MODUL_ADMIN_AVAILABLE;
    public static boolean IS_MODUL_CONTABILITATE_AVAILABLE;
    public static boolean IS_MODUL_GESTIUNE_AVAILABLE;
    public static boolean IS_MODUL_VALUTE_AVAILABLE;
    public static boolean IS_MODUL_ASOCIATIE_AVAILABLE;

    public static final String MODUL_CONTABILITATE_CLASS = "com.encode.borg.module.conta.ContaAtom";
    public static final String MODUL_GESTIUNE_CLASS = "com.encode.borg.module.gestiune.GestiuneAtom";
    public static final String MODUL_VALUTE_CLASS = "com.encode.borg.module.valuta.ValuteAtom";
    public static final String MODUL_ADMIN_CLASS = "com.encode.borg.module.config.AdminAtom";
    public static final String MODUL_ASOCIATIE_CLASS = "com.encode.borg.module.as.AsociatieAtom";

    static {
        Constants.linkTypes(AtomUtils.class, AtomUtils.FIELD_MODULE_PREFIX, AtomUtils.FIELD_MODULE_SUFIX, AtomUtils.mapModules);
        Constants.linkTypes(AtomUtils.class, AtomUtils.FIELD_MODULE_PREFIX, AtomUtils.FIELD_MODULE_DESC_SUFIX, AtomUtils.mapModulesDesc);
        try {
            Class.forName(AtomUtils.MODUL_ADMIN_CLASS);
            AtomUtils.IS_MODUL_ADMIN_AVAILABLE = true;
        } catch (Exception exc) {
            AtomUtils.IS_MODUL_ADMIN_AVAILABLE = false;
        }
        try {
            Class.forName(AtomUtils.MODUL_GESTIUNE_CLASS);
            AtomUtils.IS_MODUL_GESTIUNE_AVAILABLE = true;
        } catch (Exception exc) {
            AtomUtils.IS_MODUL_GESTIUNE_AVAILABLE = false;
        }
        try {
            Class.forName(AtomUtils.MODUL_CONTABILITATE_CLASS);
            AtomUtils.IS_MODUL_CONTABILITATE_AVAILABLE = true;
        } catch (Exception exc) {
            AtomUtils.IS_MODUL_CONTABILITATE_AVAILABLE = false;
        }
        try {
            Class.forName(AtomUtils.MODUL_VALUTE_CLASS);
            AtomUtils.IS_MODUL_VALUTE_AVAILABLE = true;
        } catch (Exception exc) {
            AtomUtils.IS_MODUL_VALUTE_AVAILABLE = false;
        }
        try {
            Class.forName(AtomUtils.MODUL_ASOCIATIE_CLASS);
            AtomUtils.IS_MODUL_ASOCIATIE_AVAILABLE = true;
        } catch (Exception exc) {
            AtomUtils.IS_MODUL_ASOCIATIE_AVAILABLE = false;
        }
    }

    private AtomUtils() {
    }

    public static String getImageNameForModul(final int idModul) {
        switch (idModul) {
        case MODUL_ABSTRACT_APP: {
            return AppImages.IMG_HOME;
        }
        case MODUL_CONFIG: {
            return AppImages.IMG_CONFIG;
        }
        case MODUL_CONTABILITATE: {
            return AppImages.IMG_CONTA;
        }
        case MODUL_GESTIUNE: {
            return AppImages.IMG_GESTIUNE;
        }
        case MODUL_VALUTE: {
            return AppImages.IMG_VALUTA;
        }
        case MODUL_ASOCIATIE: {
            return AppImages.IMG_PARTENERI;
        }
        default:
            return AppImages.IMG_BORG_MAIN;
        }
    }

    public static boolean isModuleAvailable(final int idModul) {
      return true;
    }

    public static String getImageNameForModul(final long idModul) {
        return AtomUtils.getImageNameForModul((int) idModul);
    }

    public static String getNumeModul(final int idModul) {
        String nume = AtomUtils.mapModules.get(idModul);
        if (nume == null) {
            nume = "";
        }
        return nume;
    }

    public static String getNumeModul(final long idModul) {
        return AtomUtils.getNumeModul((int) idModul);
    }

    public static String getModuleDescription(final int idModul) {
        String descriere = AtomUtils.mapModulesDesc.get(idModul);
        if (descriere == null) {
            descriere = "";
        }
        return descriere;
    }
}
