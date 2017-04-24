package org.mihalis.opal.notify;

import org.eclipse.swt.SWT;

public class NotifierSettings {

    /*
        Specified the width of the notification shell.
        Default value: 320
     */
    private int shellWidth;
    /*
        Specifies the height of the notification shell
        Default value: 100
     */
    private int shellHeight;
    /*
        The duration in miliseconds for the shell to be visible
        Default value: 4500
     */
    public int visibleMiliseconds;
    /*
        The amount of time in which the shell will fade out
        Default value: 50
     */
    private int fadeTimer;
    /*
        The amount of fade out step.
        Default value: 8
     */
    private int fadeOutStep;
    /*
        The notifier default theme
        Default value: YELLOW_THEME. Available themes: YELLOW_THEME, GRAY_THEME, BLUE_THEME
     */
    private NotifierColorsFactory.NotifierTheme theme;

    /*
        If set to true, the shell will try to use it's parent bottom right client area as a reference.
        If not (default), it will use the primary monitor's bottom right client area.
     */
    private boolean showOnParent;

    /*
        Controls the notification font size.
     */
    private int fontSize;

    /*
        The notification style
     */
    private int iconStyle;

    public int getShellWidth() {
        if (shellWidth == 0) {
            shellWidth = 320;
        }
        return shellWidth;
    }

    public void setShellWidth(int shellWidth) {
        this.shellWidth = shellWidth;
    }

    public int getShellHeight() {
        if (shellHeight <= 0) {
            shellHeight = 100;
        }
        return shellHeight;
    }

    public void setShellHeight(int shellHeight) {
        this.shellHeight = shellHeight;
    }

    public int getVisibleMiliseconds() {
        if (visibleMiliseconds <= 0) {
            visibleMiliseconds = 4500;
        }
        return visibleMiliseconds;
    }

    public void setVisibleMiliseconds(int visibleMiliseconds) {
        this.visibleMiliseconds = visibleMiliseconds;
    }

    public int getFadeTimer() {
        if (fadeTimer <= 0) {
            fadeTimer = 50;
        }
        return fadeTimer;
    }

    public void setFadeTimer(int fadeTimer) {
        this.fadeTimer = fadeTimer;
    }

    public int getFadeOutStep() {
        if (fadeOutStep <= 0) {
            fadeOutStep = 8;
        }
        return fadeOutStep;
    }

    public void setFadeOutStep(int fadeOutStep) {
        this.fadeOutStep = fadeOutStep;
    }

    public NotifierColorsFactory.NotifierTheme getTheme() {
        if (theme == null) {
            return NotifierColorsFactory.NotifierTheme.YELLOW_THEME;
        }
        return theme;
    }

    public void setTheme(NotifierColorsFactory.NotifierTheme theme) {
        this.theme = theme;
    }

    public boolean isShowOnParent() {
        return showOnParent;
    }

    public void setShowOnParent(boolean showOnParent) {
        this.showOnParent = showOnParent;
    }

    public int getFontSize() {
        if (fontSize <= 0) {
            fontSize = 10;
        }
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getIconStyle() {
        return iconStyle;
    }

    public void setIconStyle(int iconStyle) {
        this.iconStyle = iconStyle;
    }

    public void reset() {
        this.shellWidth = 320;
        this.shellHeight = 100;
        this.iconStyle = SWT.ICON_INFORMATION;
        this.visibleMiliseconds = 4500;
        this.fadeTimer = 50;
        this.fadeOutStep = 8;
        this.theme = NotifierColorsFactory.NotifierTheme.YELLOW_THEME;
        this.showOnParent = true;
        this.fontSize = 10;
    }
}
