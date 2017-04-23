package com.papao.books.view.auth;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.awt.*;

public class SplashPanelUse {

    private Color splashColor = null;
    private SplashPanel splash;
    private String imgJarLocation, appVersion;
    private int horizontalCoord, verticalCoord;

    private static String defaultImgJarLocation = "/com/encode/borg/images/skin01/misc/sigla.jpg";
    private static int defaultHorizontalCoord = 300;
    private static int defaultVerticalCoord = 210;

	private static Logger logger = Logger.getLogger(SplashPanelUse.class);

    public SplashPanelUse() {
        this(null,
            SplashPanelUse.defaultImgJarLocation,
            SplashPanelUse.defaultHorizontalCoord,
            SplashPanelUse.defaultVerticalCoord);
    }

    public SplashPanelUse(final Color color,
                          final String imgJarLocation,
                          final int horizontalCoord,
                          final int verticalCoord) {
        setColor(color);
        setImgJarLocation(imgJarLocation);
        setHorizontalCoord(horizontalCoord);
        setVerticalCoord(verticalCoord);
    }

    private void setColor(final Color color) {
        if (color != null) {
            this.splashColor = color;
            return;
        }
        this.splashColor = new Color(42, 70, 133);
    }

    private void setImgJarLocation(final String str) {
		if (StringUtils.isNotEmpty(str)) {
            this.imgJarLocation = str;
            return;
        }
        this.imgJarLocation = SplashPanelUse.defaultImgJarLocation;
    }

    private void setHorizontalCoord(final int horizontalCoord) {
        if (horizontalCoord > 0) {
            this.horizontalCoord = horizontalCoord;
            return;
        }
        this.horizontalCoord = SplashPanelUse.defaultHorizontalCoord;
    }

    private void setVerticalCoord(final int verticalCoord) {
        if (verticalCoord > 0) {
            this.verticalCoord = verticalCoord;
            return;
        }
        this.verticalCoord = SplashPanelUse.defaultVerticalCoord;
    }

    public void open() {
        /**
         * begin with a splash image, with progress bar
         */
        try {
            this.splash = new SplashPanel(this.splashColor, this.imgJarLocation, this.appVersion, this.horizontalCoord, this.verticalCoord);
            this.splash.reset();
        } catch (Exception e) {
			logger.fatal(e, e);
        }
    }

    /**
     * Advances the splash progress bar.
     */
    public void advance() {
        if (this.splash != null) {
            this.splash.advance();
        }
    }

    public void close() {
        if (this.splash != null) {
        	//calling dispose() here causes problems on MacOS
//            this.splash.dispose();
        	this.splash.setVisible(false);
        	this.splash = null;
        }
    }
}
