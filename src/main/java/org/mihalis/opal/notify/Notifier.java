/*******************************************************************************
 * Copyright (c) 2011 Laurent CARON
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Laurent CARON (laurent.caron at gmail dot com) - initial API and implementation 
 *******************************************************************************/
package org.mihalis.opal.notify;

import com.papao.books.ui.AppImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.mihalis.opal.notify.NotifierColorsFactory.NotifierTheme;
import org.mihalis.opal.utils.SWTGraphicUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides a notifier window, which is a window that appears in the
 * bottom of the screen and slides.
 */
public class Notifier {

    private static final int MAX_DURATION_FOR_OPENING = 500;
    private static final int STEP = 5;

    /**
     * If specified, the shell will use this parent's shell as a parent shell.
     * By default, it will create a new Shell with null parent.
     */
    private static Scrollable parent;

    private static NotifierSettings settings = new NotifierSettings();

    private static java.util.List<Shell> activeShells = new ArrayList<>();

    /**
     * Starts a notification. A window will appear in the bottom of the screen,
     * then will disappear after 4.5 s
     *
     * @param title the title of the popup window
     * @param text  the text of the notification
     */
    public static void notify(final String title, final String text) {
        notify(null, title, text, settings.getTheme());
    }

    /**
     * Starts a notification. A window will appear in the bottom of the screen,
     * then will disappear after 4.5 s
     *
     * @param image the image to display (if <code>null</code>, a default image
     *              is displayed)
     * @param title the title of the popup window
     * @param text  the text of the notification
     */
    public static void notify(final Image image, final String title, final String text) {
        notify(image, title, text, settings.getTheme());
    }

    /**
     * Starts a notification. A window will appear in the bottom of the screen,
     * then will disappear after 4.5 s
     *
     * @param title the title of the popup window
     * @param text  the text of the notification
     * @param theme the graphical theme. If <code>null</code>, the yellow theme
     *              is used
     * @see NotifierTheme
     */
    public static void notify(final String title, final String text, final NotifierTheme theme) {
        notify(null, title, text, theme);
    }

    /**
     * Starts a notification. A window will appear in the bottom of the screen,
     * then will disappear after 4.5 s
     *
     * @param image the image to display (if <code>null</code>, a default image
     *              is displayed)
     * @param title the title of the popup window
     * @param text  the text of the notification
     * @param theme the graphical theme. If <code>null</code>, the yellow theme
     *              is used
     * @see NotifierTheme
     */
    public static void notify(final Image image, final String title, final String text, final NotifierTheme theme) {
        final Shell shell = createNotificationWindow(image, title, text, NotifierColorsFactory.getColorsForTheme(theme));
        shell.addListener(SWT.Dispose, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                settings.reset();
                activeShells.remove(shell);
//                colors.dispose();
            }
        });
        makeShellAppears(shell);
        activeShells.add(shell);
    }

    /**
     * Creates a notification window
     *
     * @param image  image. If <code>null</code>, a default image is used
     * @param title  title, the title of the window
     * @param text   text of the window
     * @param colors color set
     * @return the notification window as a shell object
     */
    private static Shell createNotificationWindow(final Image image, final String title, final String text, final NotifierColors colors) {
        Shell shell = null;
        if (parent != null && !parent.isDisposed()) {
            shell = new Shell(parent.getShell(), SWT.NO_TRIM | SWT.NO_FOCUS);
        } else {
            shell = new Shell(SWT.NO_TRIM | SWT.NO_FOCUS);
        }
        shell.setLayout(new GridLayout(2, false));
        shell.setBackgroundMode(SWT.INHERIT_FORCE);

        createTitle(shell, title, colors);
        createImage(shell, image);
        createText(shell, text, colors);
        createBackground(shell, colors);
        createCloseAction(shell);

        shell.pack();
        shell.setMinimumSize(settings.getShellWidth(), settings.getShellHeight());
        return shell;
    }

    /**
     * Creates the title part of the window
     *
     * @param shell  the window
     * @param title  the title
     * @param colors the color set
     */
    private static void createTitle(final Shell shell, final String title, final NotifierColors colors) {
        final Label titleLabel = new Label(shell, SWT.WRAP);
        final GridData gdLabel = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        gdLabel.horizontalIndent = 40;
        titleLabel.setLayoutData(gdLabel);
        final Color titleColor = colors.titleColor;
        titleLabel.setForeground(titleColor);

        final Font titleFont = SWTGraphicUtil.buildFontFrom(titleLabel, SWT.BOLD, settings.getFontSize());
        titleLabel.setFont(titleFont);
        titleLabel.setText(title);
        SWTGraphicUtil.dispose(shell, titleFont);
    }

    /**
     * Creates the image part of the window
     *
     * @param shell the window
     * @param image the image
     */
    private static void createImage(final Shell shell, final Image image) {
        final Label labelImage = new Label(shell, SWT.NONE);
        final GridData gdImage = new GridData(GridData.CENTER, GridData.BEGINNING, false, true);
        gdImage.horizontalIndent = 10;
        labelImage.setLayoutData(gdImage);
        if (image == null) {
            labelImage.setImage(Display.getDefault().getSystemImage(settings.getIconStyle()));
        } else {
            labelImage.setImage(image);
        }
    }

    /**
     * Creates the text part of the window
     *
     * @param shell  the window
     * @param text   the text
     * @param colors the color set
     */
    private static void createText(final Shell shell, final String text, final NotifierColors colors) {
        final StyledText textLabel = new StyledText(shell, SWT.WRAP | SWT.READ_ONLY);
        final GridData gdText = new GridData(GridData.FILL, GridData.FILL, true, true);
        gdText.horizontalIndent = 15;
        gdText.verticalIndent = 10;
        textLabel.setLayoutData(gdText);
        textLabel.setEnabled(false);
        final Font textFont = SWTGraphicUtil.buildFontFrom(textLabel, SWT.NONE, settings.getFontSize());
        textLabel.setFont(textFont);

        final Color textColor = colors.textColor;
        textLabel.setForeground(textColor);

        textLabel.setText(text);
        SWTGraphicUtil.applyHTMLFormating(textLabel);

        SWTGraphicUtil.dispose(shell, textFont);

    }

    /**
     * Creates the background of the window
     *
     * @param shell  the window
     * @param colors the color set of the window
     */
    private static void createBackground(final Shell shell, final NotifierColors colors) {
        shell.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                final Rectangle rect = shell.getClientArea();
                final Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
                final GC gc = new GC(newImage);
                gc.setAntialias(SWT.ON);

                final Color borderColor = colors.borderColor;
                final Color fillColor1 = colors.leftColor;
                final Color fillColor2 = colors.rightColor;

                gc.setBackground(borderColor);
                gc.fillRoundRectangle(0, 0, rect.width, rect.height, 8, 8);

                gc.setBackground(fillColor1);
                gc.fillRoundRectangle(1, 1, rect.width - 2, rect.height - 2, 8, 8);

                gc.setBackground(fillColor2);
                gc.fillRoundRectangle(30, 1, rect.width - 32, rect.height - 2, 8, 8);
                gc.fillRectangle(30, 1, 10, rect.height - 2);

                final Image closeImage = AppImages.getImage16(AppImages.IMG_CLOSE_NEW);
                gc.drawImage(closeImage, rect.width - 21, 5);

                gc.dispose();

                shell.setBackgroundImage(newImage);
            }
        });
    }

    /**
     * @param shell shell that will appear
     */
    private static void makeShellAppears(final Shell shell) {
        if (shell == null || shell.isDisposed()) {
            return;
        }

        if (!activeShells.isEmpty()) {
            List<Shell> modifiable = new ArrayList<>(activeShells);
            Collections.reverse(modifiable);
            for (Shell oldShell : modifiable) {
                Point curLoc = oldShell.getLocation();
                oldShell.setLocation(curLoc.x, curLoc.y - settings.getShellHeight());
                if (curLoc.y - settings.getShellHeight() < 0) {
                    activeShells.remove(oldShell);
                    oldShell.dispose();
                }
            }
        }

        Rectangle clientArea = Display.getDefault().getPrimaryMonitor().getClientArea();
        if (parent != null && !parent.isDisposed() && settings.isShowOnParent()) {
            clientArea = parent.getBounds();
        }
        final int startX = clientArea.x + clientArea.width - shell.getSize().x - 5;

        final int stepForPosition = MAX_DURATION_FOR_OPENING / shell.getSize().y * STEP;
        final int stepForAlpha = STEP * 255 / shell.getSize().y;

        final int lastPosition = clientArea.y + clientArea.height - shell.getSize().y;

        shell.setAlpha(0);
        shell.setLocation(startX, clientArea.y + clientArea.height);
        shell.open();

        shell.getDisplay().timerExec(stepForPosition, new Runnable() {

            @Override
            public void run() {

                if (shell.isDisposed()) {
                    return;
                }

                shell.setLocation(startX, shell.getLocation().y - STEP);
                shell.setAlpha(shell.getAlpha() + stepForAlpha);
                if (shell.getLocation().y >= lastPosition) {
                    shell.getDisplay().timerExec(stepForPosition, this);
                } else {
                    shell.setAlpha(255);
                    Display.getDefault().timerExec(settings.getVisibleMiliseconds(), fadeOut(shell, false));
                }
            }
        });

    }

    /**
     * @param shell shell that will disappear
     * @param fast  if true, the fading is much faster
     * @return a runnable
     */
    private static Runnable fadeOut(final Shell shell, final boolean fast) {
        return new Runnable() {

            @Override
            public void run() {
                if (shell == null || shell.isDisposed()) {
                    return;
                }

                int currentAlpha = shell.getAlpha();
                currentAlpha -= settings.getFadeOutStep() * (fast ? 8 : 1);

                if (currentAlpha <= 0) {
                    shell.setAlpha(0);
                    shell.dispose();
                    return;
                }

                shell.setAlpha(currentAlpha);

                Display.getDefault().timerExec(settings.getFadeTimer(), this);

            }

        };
    }

    /**
     * Add a listener to the shell in order to handle the clicks on the close
     * button
     *
     * @param shell associated shell
     */
    private static void createCloseAction(final Shell shell) {
        shell.addListener(SWT.MouseDown, new Listener() {

            @Override
            public void handleEvent(final Event event) {
                final Rectangle rect = shell.getClientArea();
                final int startingX = rect.width - 21;
                final int upperY = 5;

                if (event.x >= startingX && event.x <= rect.width && event.y >= upperY && event.y <= upperY + 16) {
                    Display.getDefault().timerExec(0, fadeOut(shell, true));
                }
            }
        });
    }

    public static Scrollable getParent() {
        return parent;
    }

    public static void setParent(Scrollable parent) {
        Notifier.parent = parent;
    }

    public static NotifierSettings getSettings() {
        return settings;
    }
}
