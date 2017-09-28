package com.papao.books.ui.view;

import com.papao.books.ui.AppImages;
import com.papao.books.ui.auth.LoggerMyWay;
import com.papao.books.ui.util.FontUtil;
import com.papao.books.ui.util.StringUtil;
import com.papao.books.ui.util.WidgetCompositeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;

/**
 * @returns one of the : SWT.OK, SWT.YES, SWT.NO, SWT.CLOSE, SWT.RETRY, SWT.ABORT
 */

public class MsgBox implements Listener {

    private static Logger logger = Logger.getLogger(MsgBox.class);

    private Shell shell;
    private Canvas canvasImg;
    private int result = SWT.NO;
    private Composite compButtons;
    private Composite compErrorActions;
    private Button buttonOk;
    private Button buttonYes;
    private Button buttonNo;
    private Button buttonCancel;
    private Button buttonRetry;
    private Button buttonAbort;
    private Button buttonDetails;
    private Button buttonOpenFile;
    private Button buttonOptions;
    private StyledText textDetaliiEroare;
    private Throwable throwed;
    private boolean isInfo;
    private boolean isError;
    private boolean isQuestion;
    private boolean isWarning;
    private String canonicalFileName;

    private final static String APPLICATION_ERROR = "Eroare in aplicatie";
    private final static String APPLICATION_ERROR_MESSAGE = "Mesaj:";
    private final static String APPLICATION_ERROR_DETAILS = "Detalii tehnice:";

    public static final String BOX_ERROR = "Mesaj de eroare";
    public static final String BOX_INFO = "Mesaj informativ";
    public static final String BOX_WARNING = "Mesaj de atentionare";
    public static final String BOX_QUESTION = "Confirmare actiune";

    private Point shellSize;
    private StringBuilder bufferErrorDetails;
    private StyleRange[] ranges;
    private String[] exceptionDetails;

    private final static Font STYLED_FONT = new Font(Display.getDefault(), "Verdana", 10, SWT.ITALIC | SWT.BOLD);

    private static final Font FONT_CREDITS = new Font(Display.getDefault(), "Courier New", 12, SWT.NORMAL);

    public MsgBox(final int drawButtonsCfg, final String message, final String boldMessage, final int windowStyle, final Throwable throwed, final String canonicalFileName) {
        int blockSize = 0;
        Label msg;
        try {
            this.throwed = throwed;
            this.canonicalFileName = canonicalFileName;
            int shell_style = SWT.TITLE | SWT.CLOSE | SWT.APPLICATION_MODAL;
            if (this.throwed != null) {
                shell_style |= SWT.RESIZE;
            }
            this.shell = new Shell(Display.getDefault().getActiveShell(), shell_style);
            GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).extendedMargins(10, 10, 5, 5).applyTo(this.shell);

            this.isInfo = (windowStyle & SWT.ICON_INFORMATION) == SWT.ICON_INFORMATION;
            this.isError = (windowStyle & SWT.ICON_ERROR) == SWT.ICON_ERROR;
            this.isQuestion = (windowStyle & SWT.ICON_QUESTION) == SWT.ICON_QUESTION;
            this.isWarning = (windowStyle & SWT.ICON_WARNING) == SWT.ICON_WARNING;

            String strTextName = "Mesaj";
            if (this.isInfo) {
                strTextName = MsgBox.BOX_INFO;
            } else if (this.isError) {
                strTextName = MsgBox.BOX_ERROR;
            } else if (this.isQuestion) {
                strTextName = MsgBox.BOX_QUESTION;
            } else if (this.isWarning) {
                strTextName = MsgBox.BOX_WARNING;
            }
            this.shell.setText(strTextName);
            this.shell.setImages(new Image[]{
                    Display.getDefault().getSystemImage(windowStyle)});

            if ((drawButtonsCfg & SWT.OK) == SWT.OK) {
                blockSize++;
            }
            if ((drawButtonsCfg & SWT.YES) == SWT.YES) {
                blockSize++;
            }
            if ((drawButtonsCfg & SWT.NO) == SWT.NO) {
                blockSize++;
            }
            if ((drawButtonsCfg & SWT.CANCEL) == SWT.CANCEL) {
                blockSize++;
            }
            if ((drawButtonsCfg & SWT.ABORT) == SWT.ABORT) {
                blockSize++;
            }
            if ((drawButtonsCfg & SWT.RETRY) == SWT.RETRY) {
                blockSize++;
            }
            /**
             * test pentru butonul details
             */
            if ((drawButtonsCfg & SWT.BUTTON1) == SWT.BUTTON1) {
                blockSize++;
            }
            /**
             * test pentru butonul openFile
             */
            if ((drawButtonsCfg & SWT.BUTTON2) == SWT.BUTTON2) {
                blockSize++;
            }

            String text = "";
            if (message != null) {
                text = message;
            } else {
                if (this.isInfo) {
                    text = "Mesaj informativ.";
                } else if (this.isError) {
                    text = "A intervenit o eroare!";
                } else if (this.isQuestion) {
                    text = "Va rugam confirmati sau nu actiunea dorita:";
                } else if (this.isWarning) {
                    text = "Mesaj de avertizare.";
                }
            }
            String[] str = StringUtil.splitStrByDelimAndLength(text, " ", blockSize == 1 ? blockSize * 50 : blockSize * 30);
            int nr_labels = 0;
            if (str == null) {
                nr_labels = 1;
            } else {
                nr_labels = str.length;
            }

            Composite comp = new Composite(this.shell, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).spacing(0, 2).applyTo(comp);

            this.canvasImg = new Canvas(comp, SWT.NONE);
            GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gridData.widthHint = 48;
            gridData.heightHint = 32;
            gridData.verticalSpan = nr_labels + 1;
            this.canvasImg.setLayoutData(gridData);
            this.canvasImg.addListener(SWT.Paint, this);

            if (StringUtils.isNotEmpty(boldMessage)) {
                Label temp = new Label(comp, SWT.NONE);
                temp.setFont(FontUtil.TAHOMA12_NORMAL);
                temp.setText(boldMessage);
            }

            if (nr_labels > 0) {
                comp = new Composite(comp, SWT.NONE);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(comp);
                GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).spacing(0, 2).extendedMargins(15, 0, 5, 0).applyTo(comp);
            }

            GridData data = null;

            for (int i = 0; i < nr_labels; i++) {
                msg = new Label(comp, SWT.NONE);
                if (nr_labels == 1) {
                    msg.setAlignment(SWT.CENTER);
                } else {
                    msg.setAlignment(SWT.LEFT_TO_RIGHT);
                }
                String mesaj = str == null ? text : str[i].trim();
                msg.setText(mesaj);
                data = new GridData(GridData.FILL_HORIZONTAL);
                msg.setLayoutData(data);
            }

            comp.setSize(comp.computeSize(comp.getSize().x, SWT.DEFAULT));

            msg = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(msg);
            msg.setLayoutData(data);

            initUI(drawButtonsCfg);

            this.shell.setSize(this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));

            if ((drawButtonsCfg & SWT.YES) == SWT.YES) {
                this.shell.setDefaultButton(this.buttonYes);
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void ensureMinimalSize() {
        boolean correctionNeeded = false;
        try {
            Rectangle rect = this.shell.getBounds();
            if (rect.width < 350) {
                rect.width = 350;
                correctionNeeded = true;
            }
            if (rect.height < 100) {
                rect.height = 100;
                correctionNeeded = true;
            }
            if (correctionNeeded) {
                this.shell.setBounds(rect);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void initUI(final int drawButtonsCfg) {
        int numColsCompButtons = 0;

        try {
            if ((drawButtonsCfg & SWT.OK) == SWT.OK) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.YES) == SWT.YES) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.NO) == SWT.NO) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.CANCEL) == SWT.CANCEL) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.ABORT) == SWT.ABORT) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.RETRY) == SWT.RETRY) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.BUTTON1) == SWT.BUTTON1) {
                numColsCompButtons++;
            }
            if ((drawButtonsCfg & SWT.BUTTON2) == SWT.BUTTON2) {
                numColsCompButtons++;
            }

            if (numColsCompButtons > 0) {
                this.compButtons = new Composite(this.shell, SWT.NONE);
                GridLayoutFactory.fillDefaults().numColumns(numColsCompButtons).equalWidth(true).extendedMargins(0, 0, 0, 0).applyTo(this.compButtons);
                GridDataFactory.fillDefaults().grab(true, false).align(SWT.END, SWT.CENTER).span(((GridLayout) this.shell.getLayout()).numColumns, 1).hint(numColsCompButtons * 95 + numColsCompButtons
                                * 2,
                        SWT.DEFAULT).applyTo(this.compButtons);
                if ((drawButtonsCfg & SWT.OK) == SWT.OK) {

                    this.buttonOk = new Button(this.compButtons, SWT.PUSH);
                    this.buttonOk.setText("Închide");
                    this.buttonOk.setToolTipText("Închide fereastra curentă");
                    this.buttonOk.setImage(AppImages.getImage16(AppImages.IMG_OK));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonOk);
                    this.buttonOk.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonOk, AppImages.IMG_OK);
                }

                /**
                 * butonul details, pt afisarea detaliilor (de ex in caz de eroare)
                 */
                if ((drawButtonsCfg & SWT.BUTTON1) == SWT.BUTTON1) {

                    this.buttonDetails = new Button(this.compButtons, SWT.TOGGLE);
                    this.buttonDetails.setText("Detalii");
                    this.buttonDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                    this.buttonDetails.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
                    this.buttonDetails.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonDetails, AppImages.IMG_DETAILS_NEW);
                    this.compButtons.pack();
                }

                if ((drawButtonsCfg & SWT.BUTTON2) == SWT.BUTTON2) {

                    this.buttonOpenFile = new Button(this.compButtons, SWT.PUSH);
                    this.buttonOpenFile.setText("Consultare");
                    this.buttonOpenFile.setToolTipText("Deschidere fișier");
                    this.buttonOpenFile.setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonOpenFile);
                    this.buttonOpenFile.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonOpenFile, AppImages.IMG_EXPORT);
                }

                if ((drawButtonsCfg & SWT.YES) == SWT.YES) {

                    this.buttonYes = new Button(this.compButtons, SWT.PUSH);
                    this.buttonYes.setText("&Da");
                    this.buttonYes.setToolTipText("Confirmare acțiune");
                    this.buttonYes.setImage(AppImages.getImage16(AppImages.IMG_OK));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonYes);
                    this.buttonYes.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonYes, AppImages.IMG_OK);
                }

                if ((drawButtonsCfg & SWT.NO) == SWT.NO) {

                    this.buttonNo = new Button(this.compButtons, SWT.PUSH);
                    this.buttonNo.setText("&Nu");
                    this.buttonNo.setToolTipText("Refuzare acțiune");
                    this.buttonNo.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonNo);
                    this.buttonNo.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonNo, AppImages.IMG_CANCEL);
                }

                if ((drawButtonsCfg & SWT.CANCEL) == SWT.CANCEL) {

                    this.buttonCancel = new Button(this.compButtons, SWT.PUSH);
                    this.buttonCancel.setText("&Renunț");
                    this.buttonCancel.setToolTipText("Închide fereastra curentă");
                    this.buttonCancel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonCancel);
                    this.buttonCancel.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonCancel, AppImages.IMG_CANCEL);
                }

                if ((drawButtonsCfg & SWT.ABORT) == SWT.ABORT) {

                    this.buttonAbort = new Button(this.compButtons, SWT.PUSH);
                    this.buttonAbort.setText("&Abandon");
                    this.buttonAbort.setToolTipText("Nu se mai reîncearcă operația");
                    this.buttonAbort.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonAbort);
                    this.buttonAbort.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonAbort, AppImages.IMG_CANCEL);
                }

                if ((drawButtonsCfg & SWT.RETRY) == SWT.RETRY) {

                    this.buttonRetry = new Button(this.compButtons, SWT.PUSH);
                    this.buttonRetry.setText("Re&try");
                    this.buttonRetry.setToolTipText("Reîncercare operație");
                    this.buttonRetry.setImage(AppImages.getImage16(AppImages.IMG_PLUS));
                    GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.buttonRetry);
                    this.buttonRetry.addListener(SWT.Selection, this);
                    SWTeXtension.addImageChangeListener16(this.buttonRetry, AppImages.IMG_PLUS);
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public void open() {
        open(true);
    }

    public void open(final boolean pack) {
        try {
            if (pack) {
                this.shell.pack();
            }
            WidgetCompositeUtil.centerInDisplay(this.shell);
            ensureMinimalSize();

            this.shellSize = this.shell.getSize();

            this.shell.open();

            while (!this.shell.isDisposed()) {
                if (!Display.getDefault().readAndDispatch()) {
                    Display.getDefault().sleep();
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public int getResult() {
        return this.result;
    }

    private void drawCanvasImg(final Event event) {
        try {
            if (this.isInfo) {
                event.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 0, 0);
            } else if (this.isQuestion) {
                event.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 0, 0);
            } else if (this.isWarning) {
                event.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_WARNING), 0, 0);
            } else if (this.isError) {
                event.gc.drawImage(Display.getDefault().getSystemImage(SWT.ICON_ERROR), 0, 0);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void showDetails() {
        try {
            if (this.buttonDetails.getSelection()) {
                this.textDetaliiEroare = new StyledText(this.shell, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
                GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                gd.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                gd.heightHint = 150;
                this.textDetaliiEroare.setLayoutData(gd);
                this.textDetaliiEroare.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

                this.compErrorActions = new Composite(this.shell, SWT.NONE);
                this.compErrorActions.setLayout(new GridLayout(1, true));
                gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
                gd.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                this.compErrorActions.setLayoutData(gd);

                this.buttonOptions = new Button(this.compErrorActions, SWT.PUSH);
                this.buttonOptions.setText("Optiuni..");
                this.buttonOptions.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
                this.buttonOptions.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
                this.buttonOptions.setMenu(createButtonOptionsMenu());
                this.buttonOptions.addListener(SWT.Selection, this);
                SWTeXtension.addImageChangeListener16(this.buttonOptions, AppImages.IMG_MODIFICARE);

                Rectangle rect = this.shell.getBounds();
                rect.height = rect.height + 300;
                this.shell.setBounds(rect);

                if (this.bufferErrorDetails != null) {
                    this.textDetaliiEroare.setText(this.bufferErrorDetails.toString());
                    this.textDetaliiEroare.setStyleRanges(this.ranges);
                    return;
                }

                if (MsgBox.this.throwed != null) {
                    this.exceptionDetails = org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseStackTrace(this.throwed);
                } else {
                    this.exceptionDetails = new String[]{
                            "", ""};
                }

                this.ranges = new StyleRange[3];
                this.bufferErrorDetails = new StringBuilder();

                this.bufferErrorDetails.append("\t");
                this.bufferErrorDetails.append(MsgBox.APPLICATION_ERROR).append("\n\n");
                this.bufferErrorDetails.append(MsgBox.APPLICATION_ERROR_MESSAGE).append("\n");

				/*
                 * daca exceptia nu este nula, afisam la mesaj cauza care a generat-o.
				 */
                if (StringUtils.isNotEmpty(this.exceptionDetails[0])) {
                    this.bufferErrorDetails.append("\t\t").append(this.exceptionDetails[0]).append("\n");
                } else {
                    this.bufferErrorDetails.append("\t\tN/A\n");
                }

                this.bufferErrorDetails.append(MsgBox.APPLICATION_ERROR_DETAILS).append("\n");

				/*
				 * 
				 */
                this.bufferErrorDetails.append(this.exceptionDetails[1]);
                this.textDetaliiEroare.setText(this.bufferErrorDetails.toString());

                this.ranges[0] = new StyleRange(1, MsgBox.APPLICATION_ERROR.length(), Display.getDefault().getSystemColor(SWT.COLOR_BLACK), Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                this.ranges[0].font = MsgBox.FONT_CREDITS;
                this.ranges[0].underline = true;
                this.ranges[0].underlineStyle = SWT.UNDERLINE_SINGLE;
                this.ranges[0].fontStyle = SWT.BOLD;

                this.ranges[1] = new StyleRange(
                        this.bufferErrorDetails.toString().indexOf(MsgBox.APPLICATION_ERROR_MESSAGE),
                        MsgBox.APPLICATION_ERROR_MESSAGE.length(),
                        Display.getDefault().getSystemColor(SWT.COLOR_BLACK),
                        Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                this.ranges[1].font = MsgBox.STYLED_FONT;

                this.ranges[2] = new StyleRange(
                        this.bufferErrorDetails.toString().indexOf(MsgBox.APPLICATION_ERROR_DETAILS),
                        MsgBox.APPLICATION_ERROR_DETAILS.length(),
                        Display.getDefault().getSystemColor(SWT.COLOR_BLACK),
                        Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                this.ranges[2].font = MsgBox.STYLED_FONT;

                this.textDetaliiEroare.setStyleRanges(this.ranges);

            } else {
                if ((this.textDetaliiEroare != null) && !this.textDetaliiEroare.isDisposed()) {
                    this.textDetaliiEroare.dispose();
                }
                if ((this.compErrorActions != null) && !this.compErrorActions.isDisposed()) {
                    this.compErrorActions.dispose();
                }
                this.shell.setSize(this.shellSize.x, this.shellSize.y);
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private Menu createButtonOptionsMenu() {
        MenuItem menuItem = null;
        final Menu menu = new Menu(this.buttonOptions);
        try {
            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Copiaza mesajul afisat");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    copyErrorIntoClipboard();
                }
            });

            menuItem = new MenuItem(menu, SWT.NONE);
            menuItem.setText("Consultare log aplicatie");
            menuItem.setImage(AppImages.getImage16(AppImages.IMG_OK));
            menuItem.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    showAppLog();
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
        return menu;
    }

    private void copyErrorIntoClipboard() {
        Clipboard cpb;
        try {
            cpb = new Clipboard(Display.getDefault());
            cpb.setContents(new String[]{
                    this.textDetaliiEroare.getText()}, new TextTransfer[]{
                    TextTransfer.getInstance()});
            /**
             * acum textul ar tb sa fie in clipboard-ul sistemului
             */
            cpb.dispose();
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void showAppLog() {
        try {
            Program.launch(LoggerMyWay.applicationLog);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void close(final int action) {
        this.result = action;
        this.shell.close();
    }

    private void handlePaintEvents(final Event e) {
        if (e.widget == this.canvasImg) {
            drawCanvasImg(e);
        }
    }

    private void handleSelectionEvents(final Event e) {
        if (e.widget == this.buttonOk) {
            close(SWT.OK);
        } else if (e.widget == this.buttonDetails) {
            showDetails();
        } else if (e.widget == this.buttonYes) {
            close(SWT.YES);
        } else if (e.widget == this.buttonNo) {
            close(SWT.NO);
        } else if (e.widget == this.buttonCancel) {
            close(SWT.CLOSE);
        } else if (e.widget == this.buttonAbort) {
            close(SWT.ABORT);
        } else if (e.widget == this.buttonRetry) {
            close(SWT.RETRY);
        } else if (e.widget == this.buttonOpenFile) {
            launchFile();
            close(SWT.CLOSE);
        } else if (e.widget == this.buttonOptions) {
            this.buttonOptions.getMenu().setVisible(true);
        }
    }

    private void launchFile() {
        if (StringUtils.isNotEmpty(this.canonicalFileName)) {
            Program.launch(this.canonicalFileName);
        }
    }

    @Override
    public void handleEvent(final Event e) {
        try {
            switch (e.type) {
                case SWT.Paint: {
                    handlePaintEvents(e);
                    break;
                }
                case SWT.Selection: {
                    handleSelectionEvents(e);
                    break;
                }
                default:
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final boolean isDisposed() {
        return (this.canvasImg == null) || this.canvasImg.isDisposed();
    }
}
