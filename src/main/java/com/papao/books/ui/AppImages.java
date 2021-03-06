package com.papao.books.ui;

import com.github.haixing_hu.swt.starrating.StarRating;
import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class AppImages {

    private static Logger logger = Logger.getLogger(AppImages.class);

    /**
     * imagini statice default, pe diferite rezolutii, daca o imagine solicitata nu este disponibila
     */
    public final static Image IMAGE_NOT_FOUND_16X16 = AppImages.getImageNotFound(16, 16);
    public final static Image IMAGE_NOT_FOUND_24X24 = AppImages.getImageNotFound(24, 24);
    public final static Image IMAGE_NOT_FOUND_32X32 = AppImages.getImageNotFound(32, 32);
    public final static Image IMAGE_BLANK_128x128 = AppImages.getBlankImage(128, 128);
    public final static Image IMAGE_BLANK_64x64 = AppImages.getBlankImage(64, 64);
    public final static Image IMAGE_BLANK_32x32 = AppImages.getBlankImage(32, 32);
    public final static Image IMAGE_BLANK_16x16 = AppImages.getBlankImage(16, 16);
    private static Image IMAGE_USER_UNKNOWN_128_128;

    private static final String STAR_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAABwElEQVR4Ab2WNbDVQBSGg0uLu+y5wUoqKrxHW0pK3B0q3N11N7hLg9e49A0VLrsXD/lmeK7RnVk95/+/3Mh5z0vafpyRoXSv6FY2spJeONga9YJeKPTHOX+w1Sqksy7wNpeWVYBZF3mbn1WAWRcC/R4M9iugFZ2zIt7mJbXBnOUOtlqe1AZzlu9tPqMEUH2dWG7gciCLGgITi2mmprmgtL2s1V5r5JAzcjzqgTPqfLS/arXcclruOK0eRvv3DYGJkUMuGrR44IUn3jBgwfTCq8M7RgKDuIgOC6ZX0aKDuc6o33kB8YbRwKfij7Va3mUNxRPvxp/5+SH9rJHHWUHxwtNrTgvPjOjgjJxIC8UDLy9uc1pmReJfCYC/0KYrGro0Pi4YTRa1eVRcMJrUYBeodXHBaDL5FycuGE0qqDOqT4qC0Sf5rw3U9IZLn+yiNxRHm+IXy6V6TD8440+syGHNWe08tImg4ZlhbSPx11oF/mF9t5AzYrXAX/GIDaa2VgP+sUatCc9MbdVQPjFyyK3Q4ZGkam36f+Vv4nyX5KJBi0eCz0he88c8vOp3jqtFgxaPeMLzQ3q4oDTTS9nwwKu+2D9N9rM09Bxb/wAAAABJRU5ErkJggg==";

    /**
     * aceasta este calea unde se afla resursele (imaginile)
     */
    public static final String IMAGES_ROOT = "/com/encode/borg/images/";

    /**
     * s-au definit 5 skin-uri ale aplicatiei. In fiecare skin va exista o structura identica de
     * foldere si fisiere, pentru a putea apela 5 fisiere diferite, folosind o singura variabila.
     */
    public static final String SKIN_01 = "skin01/";
    public static final String SKIN_02 = "skin02/";
    public static final String SKIN_03 = "skin03/";
    public static final String SKIN_04 = "skin04/";
    public static final String SKIN_05 = "skin05/";

    /**
     * numele foldereleor din fiecare skin, pe rezolutii plus diverse (misc) si valute.
     */
    private static final String RES_16X16_STR = "16x16/";
    private static final String RES_24X24_STR = "24x24/";
    private static final String RES_32X32_STR = "32x32/";
    private static final String RES_MISC_STR = "misc/";

    /**
     * constante pentru specificarea dimensiunii dorite. Valori speciale 0 si 1 pentru \diverse si
     * \valuta
     */
    public static final int SIZE_MISC = 0;
    public static final int SIZE_16 = 16;
    public static final int SIZE_24 = 24;
    public static final int SIZE_32 = 32;

    /**
     * HashMap-uri statice cu imagini.
     */
    private final static Map<String, Image> HASH_IMAGES_16 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_IMAGES_24 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_IMAGES_32 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_IMAGES_MISC = new HashMap<String, Image>();

    private final static Map<String, Image> HASH_FOCUS_IMAGES_16 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_FOCUS_IMAGES_24 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_FOCUS_IMAGES_32 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_FOCUS_IMAGES_MISC = new HashMap<String, Image>();

    private final static Map<String, Image> HASH_GRAY_IMAGES_16 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_GRAY_IMAGES_24 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_GRAY_IMAGES_32 = new HashMap<String, Image>();
    private final static Map<String, Image> HASH_GRAY_IMAGES_MISC = new HashMap<String, Image>();

    private final static Map<String, Image> FILE_TYPE_IMAGES = new HashMap<String, Image>();

    private final static Map<Integer, Image> HASH_GRAY_STARS = new HashMap<>();
    private final static Map<Integer, Image> HASH_MARKED_STARS = new HashMap<>();
    private final static Map<Integer, Image> HASH_STARS = new HashMap<>();

    /**
     * declararea unui nume unic de variabila, care se va regasi in fiecare folder SKIN, la aceeasi
     * locatie.
     */

    public final static String IMG_ARROW_UP = "arrowUp.png";
    public final static String IMG_ARROW_DOWN = "arrowDown.png";
    public final static String IMG_ARROW_LEFT = "arrowLeft.png";
    public final static String IMG_ARROW_RIGHT = "arrowRight.png";
    public final static String IMG_ADOBE = "adobe.png";
    public final static String IMG_CANCEL = "cancel.png";
    public final static String IMG_COLLAPSE = "collapse.png";
    public final static String IMG_CONFIG = "config.png";
    public final static String IMG_CONTA = "conta.png";
    public final static String IMG_EXPAND = "expand.png";
    public final static String IMG_EXPORT = "export.png";
    public final static String IMG_FILTRU = "filtru.png";
    public final static String IMG_GESTIUNE = "gestiune.png";
    public final static String IMG_HELP = "help.png";
    public final static String IMG_HOME = "home.png";
    public final static String IMG_IMPORT = "import.png";
    public final static String IMG_INFO = "info.png";
    public final static String IMG_MODIFICARE = "modificare.png";
    public final static String IMG_OK = "ok.png";
    public final static String IMG_PLUS = "plus.png";
    public final static String IMG_PRINT = "print.png";
    public final static String IMG_REFRESH = "refresh.png";
    public final static String IMG_SECURITY = "security.png";
    public final static String IMG_STOP = "stop.png";
    public final static String IMG_VALUTA = "valuta.png";
    public final static String IMG_EXCEL = "xcel.png";
    public final static String IMG_SEARCH = "search.png";
    public final static String IMG_DESELECT = "deselect.png";
    public final static String IMG_DESELECT_ALL = "deselectAll.png";
    public final static String IMG_SELECT = "select.png";
    public final static String IMG_SELECT_ALL = "selectAll.png";
    public final static String IMG_WARNING = "warning.png";
    public final static String IMG_COPY = "copy.png";
    public final static String IMG_PASTE = "paste.png";
    public final static String IMG_DETAILS = "detalii.png";
    public final static String IMG_USER = "user.png";
    public final static String IMG_UPDATE = "update.png";
    public final static String IMG_COUNTRY = "country.png";
    public final static String IMG_BROWSER = "browser.png";
    public final static String IMG_BANCA = "banca.png";
    public final static String IMG_BORG_MAIN = "borg.png";
    public final static String IMG_BOOKS_MAIN = "Books-2-icon.png";
    public final static String IMG_SHOW = "show.png";
    public final static String IMG_HIDE = "hide.png";
    public final static String IMG_DETAILS_NEW = "details.png";
    public final static String IMG_TIP = "tip.png";
    public final static String IMG_CALENDAR = "calendar.png";
    public final static String IMG_RESTORE = "restore.png";
    public final static String IMG_CURS_VALUTAR = "cursValutar.png";
    public final static String IMG_APP_EVENT = "event.png";
    public final static String IMG_MYSQL_STATUS = "mysqlStatus.png";
    public final static String IMG_LISTA = "lista.png";
    public final static String IMG_MODUL = "module.png";
    public final static String IMG_MOD_VIZUALIZARE = "modVizualizare.png";
    public final static String IMG_WORD = "msword.png";
    public final static String IMG_WORD2 = "msword2.png";
    public final static String IMG_TRUCK_YELLOW = "truck_yellow.png";
    public final static String IMG_PARTENERI = "parteneri.png";
    public final static String IMG_FULL_STAR = "full-star.png";

    public final static String IMG_ARROW_DOWN_OPAL = "arrow_down.png";
    public final static String IMG_ARROW_UP_OPAL = "arrow_up.png";
    public final static String IMG_ARROW_LEFT_OPAL = "arrow_left.png";
    public final static String IMG_ARROW_RIGHT_OPAL = "arrow_right.png";
    public final static String IMG_CLOSE_NEW = "closeNew.png";
    public final static String IMG_DOUBLE_DOWN = "double_down.png";
    public final static String IMG_DOUBLE_LEFT = "double_left.png";
    public final static String IMG_DOUBLE_RIGHT = "double_right.png";
    public final static String IMG_DOUBLE_UP = "double_up.png";
    public final static String IMG_FEWER_DETAILS = "fewerDetails.png";
    public final static String IMG_MORE_DETAILS = "moreDetails.png";

    public final static String STAR_GRAY_16 = "star16.png";
    public final static String STAR_GRAY_32 = "star32.png";
    public final static String STAR_GRAY_FOCUS_16 = "star_focus16.png";
    public final static String STAR_GRAY_FOCUS_32 = "star_focus32.png";
    public final static String STAR_MARK_16 = "star_mark16.png";
    public final static String STAR_MARK_32 = "star_mark32.png";
    public final static String STAR_MARK_FOCUS_16 = "star_mark_focus16.png";
    public final static String STAR_MARK_FOCUS_32 = "star_mark_focus32.png";


    /**
     * declaram acum icoane suplimentare, diverse (misc)
     */

    public final static String IMG_MISC_BACKGROUND_LOGIN = "backgroundLogin.jpg";
    public final static String IMG_MISC_BARA = "bara.png";
    public final static String IMG_MISC_COMPANY_LOGO = "CompanyLogo.png";
    public final static String IMG_MISC_COMPANY_LOGO2 = "CompanyLogo2.png";
    public final static String IMG_MISC_COMPANY_LOGO3 = "CompanyLogo3.png";
    public final static String IMG_MISC_SIGLA = "sigla.jpg";
    public final static String IMG_MISC_HYPERCUBE_ICO = "Hypercube.ico";
    public final static String IMG_MISC_HYPERCUBE_JPG = "Hypercube.jpg";
    public final static String IMG_MISC_LOGIN_OLD = "loginOld.png";
    public final static String IMG_MISC_LOGIN_OLD2 = "loginOld2.png";
    public final static String IMG_MISC_ENTERPRISE = "enterprise.png";

    public final static String IMG_MISC_SIMPLE_MAXIMIZE = "SimpleMaximize.png";
    public final static String IMG_MISC_SIMPLE_MINIMIZE = "SimpleMinimize.png";
    public final static String IMG_MISC_SIMPLE_X = "SimpleX.png";
    public final static String IMG_MISC_SIMPLE_X_RED = "SimpleXRed.PNG";
    public final static String IMG_MISC_SIMPLE_MENU = "SimpleMenu.png";
    public final static String IMG_MISC_SIMPLE_BACK = "SimpleBack.png";
    public final static String IMG_MISC_SIMPLE_NEXT = "SimpleNext.png";
    public final static String IMG_MISC_USER_UNKNOWN = "userNecunoscut.png";

    public final static String IMG_MISC_PERSPECTIVE = "perspective.png";

    public final static String IMG_MISC_HTML_BACKGROUND = "htmlBackGround.jpg";
    public final static String IMG_MISC_HTML_FRUNZE5 = "backgroundFrunze5.PNG";

    public final static String IMG_MISC_CHECKED = "checked.png";
    public final static String IMG_MISC_UNCHECKED = "unchecked.png";

    private final static String NOT_FOUND = " not found!";
    private final static String STR_RESOURCE = "Resource ";

    private static String currentSkin = AppImages.SKIN_01;

    private static ImageRegistry imageRegistry;

    private AppImages() {
    }

    /**
     * Clasa singletone pentru incarcarea imaginilor din arhiva jar/zip cu imagini. Valoarea
     * parametrului <b>CURRENT_SKIN</b> va fi citita dintr-o clasa, din Properties, etc, fiind
     * initializata din fisierul XML cu setari ale aplicatiei.
     *
     * @param SKIN una din constantele String definite in clasa
     */

    public static void setSkin(final String SKIN) {
        AppImages.currentSkin = SKIN;
    }

    /**
     * @param skin_name one of the values SKIN_01, SKIN_02, SKIN_03, SKIN_04, SKIN_05
     * @param lastDir   one of the values RES_16x16_STR, RES_24x24_STR, RES_32x32_STR, RES_VALUTE_STR,
     *                  RES_MISC_STR
     * @return the complete path of a picture belonging to the desired skin, and having the
     * indicated res. Ex : <i>/com/encode/borg/images/skin01/16x16/</i> for
     * <strong>skin_name</strong> =
     * SKIN_01 and <strong>lastDir</strong> = RES_16x16_STR
     */
    private static String composeImgPath(final String skin_name, final String lastDir) {
        return AppImages.IMAGES_ROOT + skin_name + lastDir;
    }

    public static Image getImage(final Image img, final int width, final int height) {
        return AppImages.resize(img, width, height);
    }

    public static Image getGrayImage(final Image img, final int width, final int height) {
        return new Image(Display.getDefault(), AppImages.resize(img, width, height), SWT.IMAGE_GRAY);
    }

    public static Image getGrayImage(final Image img) {
        return new Image(Display.getDefault(), img, SWT.IMAGE_GRAY);
    }

    /**
     * @param image  the original image
     * @param width  desired with
     * @param height desired height
     * @return new image with desired (width, height) WARNING. A new Image is constructed EVERY time
     * this method is called. Dispose method cannot be done here since we actually need the
     * new Image, so it must be done by the caller.
     */
    public static Image resize(final Image image, final int width, final int height) {
        Image scaled;
        GC gc;
        try {
            if (width <= 0) {
                throw new IllegalArgumentException("Width must be a positive integer " + width);
            }
            if (height <= 0) {
                throw new IllegalArgumentException("Height must be a positive integer " + height);
            }
            scaled = new Image(Display.getDefault(), width, height);
            scaled.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            gc = new GC(scaled);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);
            gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
            gc.dispose();
        } catch (Exception exc) {
            scaled = AppImages.IMAGE_NOT_FOUND_16X16;
            logger.error(exc.getMessage(), exc);
        }
        return scaled;
    }

    private static Image loadImage(final String fileName, final int size) {
        Image img = null;
        ClassLoader classLoader;
        InputStream is;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
            is = classLoader.getResourceAsStream(fileName.substring(1));
            if (is == null) {
                /*
                 * the old way didn't have leading slash, so if we can't find the image stream,
                 * let's see if the old way works.
                 */
                is = classLoader.getResourceAsStream(fileName);
                if (is == null) {
                    logger.info(AppImages.STR_RESOURCE + fileName + AppImages.NOT_FOUND);
                    switch (size) {
                        case 16:
                            img = AppImages.IMAGE_NOT_FOUND_16X16;
                            break;
                        case 24:
                            img = AppImages.IMAGE_NOT_FOUND_24X24;
                            break;
                        case 32:
                            img = AppImages.IMAGE_NOT_FOUND_32X32;
                            break;
                        case SIZE_MISC:
                            img = AppImages.IMAGE_NOT_FOUND_16X16;
                            break;
                        default:
                            img = AppImages.IMAGE_NOT_FOUND_16X16;
                    }
                }
            }

            if (img == null) {
                img = new Image(Display.getDefault(), is);
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException exc) {
                    logger.error(exc.getMessage(), exc);
                }
            }
        } catch (Exception exc) {
            logger.info(AppImages.STR_RESOURCE + fileName + AppImages.NOT_FOUND);
            img = AppImages.IMAGE_NOT_FOUND_16X16;
            logger.error(exc.getMessage(), exc);
        }

        return img;
    }

    public static Image getImageMiscByName(final String NUME_IMG_MISC) {
        Image result;
        result = AppImages.HASH_IMAGES_MISC.get(NUME_IMG_MISC);
        if ((result == null) || result.isDisposed()) {
            result = AppImages.loadImage(AppImages.composeImgPath(AppImages.currentSkin, AppImages.RES_MISC_STR) + NUME_IMG_MISC, AppImages.SIZE_MISC);
            AppImages.HASH_IMAGES_MISC.put(NUME_IMG_MISC, result);
        }
        if ((result == null) || result.isDisposed()) {
            logger.warn(AppImages.STR_RESOURCE + NUME_IMG_MISC + AppImages.NOT_FOUND);
            result = AppImages.IMAGE_NOT_FOUND_16X16;
            AppImages.HASH_IMAGES_MISC.put(NUME_IMG_MISC, result);
        }
        return result;
    }

    public static Image getImageMiscFocusByName(final String NUME_IMG_MISC) {
        Image img = AppImages.HASH_FOCUS_IMAGES_MISC.get(NUME_IMG_MISC);
        if (img != null) {
            return img;
        }
        img = AppImages.getImageFocus(AppImages.getImageMiscByName(NUME_IMG_MISC));
        AppImages.HASH_FOCUS_IMAGES_MISC.put(NUME_IMG_MISC, img);
        return img;
    }

    public static Image getGrayImageMiscByName(final String NUME_IMG_MISC) {
        Image result;
        result = AppImages.HASH_GRAY_IMAGES_MISC.get(NUME_IMG_MISC);
        if ((result == null) || result.isDisposed()) {
            result = new Image(Display.getDefault(), AppImages.getImageMiscByName(NUME_IMG_MISC), SWT.IMAGE_GRAY);
            AppImages.HASH_GRAY_IMAGES_MISC.put(NUME_IMG_MISC, result);
        }
        return result;
    }

    public static Image getImage16(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_IMAGES_16.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = AppImages.loadImage(AppImages.composeImgPath(AppImages.currentSkin, AppImages.RES_16X16_STR) + IMAGE_NAME, AppImages.SIZE_16);
            AppImages.HASH_IMAGES_16.put(IMAGE_NAME, result);
        }
        if ((result == null) || result.isDisposed()) {
            logger.warn(AppImages.STR_RESOURCE + IMAGE_NAME + AppImages.NOT_FOUND);
            result = AppImages.IMAGE_NOT_FOUND_16X16;
            AppImages.HASH_IMAGES_16.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImage16Focus(final String IMAGE_NAME) {
        Image img = AppImages.HASH_FOCUS_IMAGES_16.get(IMAGE_NAME);
        if (img != null) {
            return img;
        }
        img = AppImages.getImageFocus(AppImages.getImage16(IMAGE_NAME));
        AppImages.HASH_FOCUS_IMAGES_16.put(IMAGE_NAME, img);
        return img;
    }

    public static Image getGrayImage16(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_GRAY_IMAGES_16.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = new Image(Display.getDefault(), AppImages.getImage16(IMAGE_NAME), SWT.IMAGE_GRAY);
            AppImages.HASH_GRAY_IMAGES_16.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImage24(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_IMAGES_24.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = AppImages.loadImage(AppImages.composeImgPath(AppImages.currentSkin, AppImages.RES_24X24_STR) + IMAGE_NAME, AppImages.SIZE_24);
            AppImages.HASH_IMAGES_24.put(IMAGE_NAME, result);
        }
        if ((result == null) || result.isDisposed()) {
            logger.warn(AppImages.STR_RESOURCE + IMAGE_NAME + AppImages.NOT_FOUND);
            result = AppImages.IMAGE_NOT_FOUND_24X24;
            AppImages.HASH_IMAGES_24.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImage24Focus(final String IMAGE_NAME) {
        Image img = AppImages.HASH_FOCUS_IMAGES_24.get(IMAGE_NAME);
        if (img != null) {
            return img;
        }
        img = AppImages.getImageFocus(AppImages.getImage24(IMAGE_NAME));
        AppImages.HASH_FOCUS_IMAGES_24.put(IMAGE_NAME, img);
        return img;
    }

    public static Image getGrayImage24(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_GRAY_IMAGES_24.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = new Image(Display.getDefault(), AppImages.getImage24(IMAGE_NAME), SWT.IMAGE_GRAY);
            AppImages.HASH_GRAY_IMAGES_24.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImage32(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_IMAGES_32.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = AppImages.loadImage(AppImages.composeImgPath(AppImages.currentSkin, AppImages.RES_32X32_STR) + IMAGE_NAME, AppImages.SIZE_32);
            AppImages.HASH_IMAGES_32.put(IMAGE_NAME, result);
        }
        if ((result == null) || result.isDisposed()) {
            logger.warn(AppImages.STR_RESOURCE + IMAGE_NAME + AppImages.NOT_FOUND);
            result = AppImages.IMAGE_NOT_FOUND_32X32;
            AppImages.HASH_IMAGES_32.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImage32Focus(final String IMAGE_NAME) {
        Image img = AppImages.HASH_FOCUS_IMAGES_32.get(IMAGE_NAME);
        if (img != null) {
            return img;
        }
        img = AppImages.getImageFocus(AppImages.getImage32(IMAGE_NAME));
        AppImages.HASH_FOCUS_IMAGES_32.put(IMAGE_NAME, img);
        return img;
    }

    public static Image getGrayImage32(final String IMAGE_NAME) {
        Image result;
        result = AppImages.HASH_GRAY_IMAGES_32.get(IMAGE_NAME);
        if ((result == null) || result.isDisposed()) {
            result = new Image(Display.getDefault(), AppImages.getImage32(IMAGE_NAME), SWT.IMAGE_GRAY);
            AppImages.HASH_GRAY_IMAGES_32.put(IMAGE_NAME, result);
        }
        return result;
    }

    public static Image getImageNotFound(final int width, final int height) {
        Image result = null;
        try {
            Image img = new Image(Display.getDefault(), width, height);
            GC gc = new GC(img);
            gc.setTextAntialias(SWT.ON);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);
            gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_CYAN));
            gc.setLineWidth(1);
            gc.drawLine(0, 0, width - 1, height - 1);
            gc.drawLine(height - 1, 0, 0, height - 1);
            gc.drawRectangle(1, 1, width - 2, height - 2);

            gc.dispose();
            result = new Image(Display.getDefault(), img.getImageData(), img.getImageData());
            img.dispose();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    private static Image getBlankImage(final int width, final int height) {
        Image result = null;
        try {
            Image img = new Image(Display.getDefault(), width, height);
            GC gc = new GC(img);
            gc.setTextAntialias(SWT.ON);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);
            gc.dispose();
            result = new Image(Display.getDefault(), img.getImageData(), img.getImageData());
            img.dispose();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
        return result;
    }

    /**
     * Returns an icon representing the specified file.
     *
     * @param file
     * @return
     */
    public static Image getIcon(final File file) {
        if (file.isDirectory()) {
            return AppImages.getImage16(AppImages.IMG_EXPAND);
        }

        int lastDotPos = file.getName().indexOf('.');
        if (lastDotPos == -1) {
            return AppImages.IMAGE_NOT_FOUND_16X16;
        }

        Image image = AppImages.getIcon(file.getName().substring(lastDotPos + 1));
        return image == null ? AppImages.IMAGE_NOT_FOUND_16X16 : image;
    }

    /**
     * Returns the icon for the file type with the specified extension.
     *
     * @param extension
     * @return
     */
    private static Image getIcon(final String extension) {
        if (AppImages.imageRegistry == null) {
            AppImages.imageRegistry = new ImageRegistry();
        }
        Image image = AppImages.imageRegistry.get(extension);
        if (image != null) {
            return image;
        }

        Program program = Program.findProgram(extension);
        ImageData imageData = (program == null ? null : program.getImageData());
        if (imageData != null) {
            image = new Image(Display.getCurrent(), imageData);
            AppImages.imageRegistry.put(extension, image);
        } else {
            image = AppImages.IMAGE_NOT_FOUND_16X16;
        }

        return image;
    }

    public static Image getImageUserUnknown() {
        if (AppImages.IMAGE_USER_UNKNOWN_128_128 == null) {
            AppImages.IMAGE_USER_UNKNOWN_128_128 = AppImages.getImageMiscByName(AppImages.IMG_MISC_USER_UNKNOWN);
        }
        return AppImages.IMAGE_USER_UNKNOWN_128_128;
    }

    private static Image getImageFocus(final Image image) {
        ImageData imgData = image.getImageData();
        int width = imgData.width;
        int height = imgData.height;
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                double distance = Math.sqrt(Math.pow(col, 2.0) + Math.pow(row, 2.0));
                if (distance > width) {
                    continue;
                }
                RGB rgb = imgData.palette.getRGB(imgData.getPixel(row, col));
                int red = rgb.red;
                int green = rgb.green;
                int blue = rgb.blue;
                int add = (int) (width - distance);
                add *= 1.5;
                if (width < 128) {
                    add *= 1.7;// for small images need more contrast
                }
                if (width < 64) {
                    add *= 1.5;// added from tests
                }
                if (width < 32) {
                    add *= 1.5;
                }
                red += add;
                if (red > 255) {
                    red = 255;
                }
                green += add;
                if (green > 255) {
                    green = 255;
                }
                blue += add;
                if (blue > 255) {
                    blue = 255;
                }
                int pixel = 0;
                pixel |= (imgData.palette.redShift < 0 ? red << -imgData.palette.redShift : red >>> imgData.palette.redShift)
                        & imgData.palette.redMask;
                pixel |= (imgData.palette.greenShift < 0 ? green << -imgData.palette.greenShift : green >>> imgData.palette.greenShift)
                        & imgData.palette.greenMask;
                pixel |= (imgData.palette.blueShift < 0 ? blue << -imgData.palette.blueShift : blue >>> imgData.palette.blueShift)
                        & imgData.palette.blueMask;

                imgData.setPixel(row, col, pixel);
            }
        }
        return new Image(Display.getCurrent(), imgData);
    }

    public static Image addImage(final Image imgSrc, final Image imgAdd, final int x, final int y) {
        try {
            GC gc = new GC(imgSrc);
            gc.setAntialias(SWT.ON);
            gc.setInterpolation(SWT.HIGH);
            gc.drawImage(imgAdd, x, y);
            gc.dispose();
            return new Image(Display.getDefault(), imgSrc.getImageData());
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return imgSrc;
        }
    }

    public static Image getImageForExtension(String extension) {
        Image image = FILE_TYPE_IMAGES.get(extension);
        if (image == null || image.isDisposed()) {
            image = new Image(Display.getDefault(), Program.findProgram(extension).getImageData());
            FILE_TYPE_IMAGES.put(extension, image);
        }
        return image;
    }

    public static Image getRatingStars(int starCount) {
        Image starImage = HASH_STARS.get(starCount);
        if (starImage != null && !starImage.isDisposed()) {
            return starImage;
        }
        if (starCount == 0) {
            return getGrayRatingStars(StarRating.DEFAULT_MAX_NUMBERS_OF_STARS);
        }
        if (starCount == StarRating.DEFAULT_MAX_NUMBERS_OF_STARS) {
            return getMarkedRatingStars(StarRating.DEFAULT_MAX_NUMBERS_OF_STARS);
        }
        if (starCount > StarRating.DEFAULT_MAX_NUMBERS_OF_STARS || starCount < 0) {
            throw new IllegalArgumentException("Invalid star count. Must be an integer between 0 to 5.");
        }
        Image temp = merge(getMarkedRatingStars(starCount).getImageData(), getGrayRatingStars(StarRating.DEFAULT_MAX_NUMBERS_OF_STARS - starCount).getImageData(), SWT.HORIZONTAL);
        HASH_STARS.put(starCount, temp);
        return temp;
    }

    public static Image getMarkedRatingStars(int starCount) {
        if (starCount == 1) {
            HASH_MARKED_STARS.put(1, AppImages.getImage16(AppImages.STAR_MARK_16));
        }
        Image starImage = HASH_MARKED_STARS.get(starCount);
        if (starImage != null && !starImage.isDisposed()) {
            return starImage;
        }
        Image star = getMarkedRatingStars(1);
        int i = 0;
        while (i++ < starCount) {
            Image temp = merge(getMarkedRatingStars(i).getImageData(), star.getImageData(), SWT.HORIZONTAL);
            HASH_MARKED_STARS.put(i + 1, temp);
        }
        return HASH_MARKED_STARS.get(starCount);
    }

    public static Image getGrayRatingStars(int starCount) {
        if (starCount == 1) {
            HASH_GRAY_STARS.put(1, AppImages.getImage16(AppImages.STAR_GRAY_16));
        }
        Image starImage = HASH_GRAY_STARS.get(starCount);
        if (starImage != null && !starImage.isDisposed()) {
            return starImage;
        }
        Image star = getGrayRatingStars(1);
        int i = 0;
        while (i++ < starCount) {
            Image temp = merge(getGrayRatingStars(i).getImageData(), star.getImageData(), SWT.HORIZONTAL);
            HASH_GRAY_STARS.put(i + 1, temp);
        }
        return HASH_GRAY_STARS.get(starCount);
    }

    public static Image merge(ImageData sourceData1, ImageData sourceData2, int alignment) {
        ImageData targetData;

        if (alignment == SWT.HORIZONTAL) {
            targetData = new ImageData(sourceData1.width + sourceData2.width,
                    sourceData1.height, sourceData1.depth, sourceData1.palette);
            merge(sourceData1, sourceData2, targetData, sourceData1.width, sourceData1.y);
        } else {
            targetData = new ImageData(sourceData1.width, sourceData1.height
                    + sourceData2.height, sourceData1.depth, sourceData1.palette);
            merge(sourceData1, sourceData2, targetData, sourceData1.x, sourceData1.height);
        }
        return new Image(Display.getDefault(), targetData);
    }

    private static void merge(ImageData sourceData1, ImageData sourceData2,
                              ImageData targetData, int startX, int startY) {
        // Merge the 1st Image
        merge(sourceData1, targetData, sourceData1.x, sourceData1.x);

        // Merge the 2nd Image
        merge(sourceData2, targetData, startX, startY);
    }

    private static void merge(ImageData sourceData, ImageData targetData, int startX, int startY) {
        int i;
        i = sourceData.x;

        for (; i < sourceData.width; i++) {
            int j = sourceData.y;
            for (; j < sourceData.height; j++) {
                targetData.setPixel(startX + i, startY + j, sourceData.getPixel(i, j));
            }
        }
        //transparent background for beautiful display
        int whitePixel = targetData.palette.getPixel(new RGB(255, 255, 255));
        targetData.transparentPixel = whitePixel;
    }
}
