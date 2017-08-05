package com.papao.books.ui.view;

import com.papao.books.config.BooleanSetting;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.AbstractMongoDB;
import com.papao.books.model.config.WindowSetting;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.EncodePlatform;
import com.papao.books.ui.custom.CWaitDlgClassic;
import com.papao.books.ui.interfaces.*;
import com.papao.books.ui.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.mihalis.opal.notify.Notifier;

import java.util.Observable;

public abstract class AbstractView extends Observable {

    private static Logger logger = Logger.getLogger(AbstractView.class);

    /**
     * Style used to add objects to db or elsewhere
     */
    public final static int MODE_ADD = 1;
    /**
     * Style suggesting the modification of an object
     */
    public final static int MODE_MODIFY = 2;
    /**
     * Style used for object erase. Use with caution!
     */
    public final static int MODE_DELETE = 3;
    /**
     * Style for rendering properties of an object. Button save should be disposed or method reimplemented, to avoid an update on the rendered object.
     */
    public final static int MODE_VIEW = 4;
    /**
     * I really dont know what that is :)
     */
    public final static int MODE_NONE = 5;
    /**
     * a special view mode, to allow the users to modify some of the fields, resulting in partial update of the window's DBO.
     */
    public final static int MODE_MODIFY_PARTIAL = 6;
    /**
     * Style suggesting the clone of the object
     */
    public final static int MODE_CLONE = 7;

    /**
     * adds the Save button
     */
    public final static int ADD_OK = 1 << 1;
    /**
     * adds the Close button
     */
    public final static int ADD_CANCEL = 1 << 2;
    /**
     * adds a details button, for displaying facts, events, in real-time
     */
    public final static int ADD_DETAILS = 1 << 3;

    /**
     * if specified, will trigger the human-friendly names for Add, Mod, Del to be drawn.
     * implementation by the caller, or will do exactly nothing.
     */
    public final static int SHOW_OPS_LABELS = 1 << 4;

    /**
     * when true, the attempt of closing the current view, not using Save button will prompt user for confirmation if the ADD_OK flag is specified.
     */
    private boolean addCloseListener;
    private boolean createUpperCompLeftArea;
    private boolean createUpperCompRightArea;

    private boolean automaticallyShowSaveOKMessage = false;

    private Shell shell;
    private Shell parent;

    private Button buttonOk;
    private Button buttonCancel;
    private Button buttonDetails;

    private ToolBar barNavigare;
    private ToolBar mainToolbar;

    private ToolItem toolItemAdd;
    private ToolItem toolItemMod;
    private ToolItem toolItemDel;
    private ToolItem toolItemRefresh;

    private ToolItem toolItemHelp;
    private ToolItem toolItemBack;
    private ToolItem toolItemNext;
    private ToolItem toolItemPrint;
    private ToolItem toolItemExport;
    private ToolItem toolItemReset;

    private GridLayout widgetLayout = new GridLayout(1, false);

    private Composite upperComp;
    private Composite upperCompRightArea;
    private Composite lowerComp;
    private Composite compHIRE;
    private Composite compSaveButtons;
    private Composite compSearchWithHighlight;

    private Label bigLabelText;
    private Label bigLabelImage;
    private StyledText textDetail;

    private int exitChoice;
    private int shellWidth;
    private int shellHeight;
    private int shellStyle;
    private int viewMode;
    private int viewOptions;
    private String shellText;
    private GridData widgetGridData;
    private Image shellImage;

    private Listener viewListener;

    private Class<? extends Widget> widgetClass;

    private Widget widget;

    private Point shellLocation;

    private final Rectangle parentPos;

    private String bigViewMessage = "";
    private String detailMessage = "";
    private String objectName;

    protected final static String SPACESX5 = "     ";

    private Image bigViewImage;

    private final boolean addHelp;
    private final boolean addExport;
    private final boolean addPrint;
    private final boolean addReset;
    private final boolean addRefresh;
    private final boolean addNavigation;
    private final boolean addSearchWithHighlight;
    private boolean showOpsLabels;
    private final boolean addAdd;
    private final boolean addMod;
    private final boolean addDel;

    private boolean useCoords = true;
    private boolean useDocking = true;

    private static final String ERR_SAVE_STR = "A intervenit o eroare la salvarea datelor!";

    private CWaitDlgClassic waitDlg;
    private ToolItem dockingItem;
    private ToolBar dockingBar;

    private Text textSearchWithHighlight;

    private Scrollable notificationParent;

    private String observableProperty;
    private AbstractMongoDB observableObject;
    private int textSearchWithHighlightWidth = 100;

    private boolean addEscapeTraverseClose;

    private Listener saveTriggerFilter;

    public AbstractView(final Shell parent, final Class<? extends Widget> widgetClass, final int viewMode) {
        this(parent, widgetClass, null, viewMode);
    }

    public AbstractView(final Shell parent, final Class<? extends Widget> widgetClass, final Rectangle parentPos, final int viewMode) {
        super();
        this.addHelp = this instanceof IHelp;
        this.addExport = this instanceof IExport;
        this.addReset = this instanceof IReset;
        this.addPrint = this instanceof IPrint;
        this.addNavigation = this instanceof INavigation;
        this.addRefresh = this instanceof IRefresh;
        this.addSearchWithHighlight = this instanceof ISearchWithHighlight;
        this.addAdd = this instanceof IAdd;
        this.addMod = this instanceof IModify;
        this.addDel = this instanceof IDelete;

        int width;
        int height;
        /*
         * passing the params
         */
        setParent(parent);
        setWidgetClass(widgetClass);
        this.parentPos = parentPos;
        setViewMode(viewMode);

        if (getWidgetClass() == null) {
            throw new IllegalArgumentException("Widget param was null..please specify a valid param.");
        }
        /*
         * init default values
         */
        setExitChoice(SWT.CANCEL);
        setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(SWT.NONE);

//        if (Display.getDefault().getPrimaryMonitor().getBounds().width > 1024) {
//            width = 800;
//        } else {
//            width = 640;
//        }
//        if (Display.getDefault().getPrimaryMonitor().getBounds().height > 768) {
//            height = 600;
//        } else {
//            height = 480;
//        }
//        setShellWidth(width);
//        setShellHeight(height);
    }

    /**
     * There is no shell created here, so, watch out. Example of valid methods :
     * <ol>
     * <li>setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);</li>
     * <li>setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);</li>
     * <li>setBigViewMessage("Configurare parametri vizuali ai aplicatiei");</li>
     * <li>setBigViewImage(AppImages.getImage32(AppImages.IMG_CONFIG));</li>
     * <li>setShellText("Setari aplicatie");</li>
     * <li>setShellImage(AppImages.getImage16(AppImages.IMG_CONFIG));</li>
     * <ol>
     * <p>
     * Notes :
     * </p>
     * <ul>
     * <li>Calling {@link AbstractView#setBigViewImage(Image)} must also call {@link #setBigViewMessage(String)} and will trigger the detailed window mode, if checked in config app.</li>
     * </ul>
     */
    protected abstract void customizeView();

    /**
     * calling the createGUI() creates a new Graphical User Interface with the specified params. Do NOT forget to call this in other implementations. It is not called directly here to allow the
     * customizeView() to perform his task, if any.
     */
    protected final void createGUI() {
        int numColsCompHIRE = 0;
        int numColsCompSaveBtn = 0;
        int numColsLowerComp = 0;
        Composite bigUpperComp;
        Label separator;
        boolean appIsUsingRichWindows;
        try {
            setAddCloseListener(true);

            createViewListener();
            initViewMode();

            appIsUsingRichWindows = StringUtils.isNotEmpty(this.bigViewMessage) || this.bigViewImage != null;
            if (appIsUsingRichWindows) {
                appIsUsingRichWindows = true;
            }

            getWidgetLayout().marginTop = getWidgetLayout().marginBottom = 0;
            getWidgetLayout().marginLeft = getWidgetLayout().marginRight = 5;

            setWidgetGridData(new GridData(SWT.FILL, SWT.FILL, true, true));

            if (getParent() != null) {
                setShell(new Shell(getParent(), getShellStyle()));
            }
            if (this.shell == null) {
                setShell(new Shell(Display.getDefault(), getShellStyle()));
            }
            //we set the current shell as the parent shell for notifications. For just in case ;-)
            this.notificationParent = this.shell;
            Notifier.setParent(this.shell);

            if ((viewMode & ADD_OK) != 0) {
                saveTriggerFilter = new Listener() {
                    public void handleEvent(Event e) {
                        if (SWTeXtension.getSaveTrigger(e))
                            saveAndClose(true);
                    }
                };
                shell.getDisplay().addFilter(SWT.KeyDown, saveTriggerFilter);
            }

            this.shell.addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    if (getDockingItem() != null && !getDockingItem().isDisposed()) {
                        getDockingItem().dispose();
                    }
                    //we restore the original notification parent
                    Notifier.setParent(notificationParent);

                    if (saveTriggerFilter != null) {
                        shell.getDisplay().removeFilter(SWT.KeyDown, saveTriggerFilter);
                    }
                }
            });
            if (addEscapeTraverseClose) {
                this.shell.addListener(SWT.Traverse, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        if (event.detail == SWT.TRAVERSE_ESCAPE) {
                            getShell().close();
                            event.detail = SWT.TRAVERSE_NONE;
                            event.doit = true;
                        }
                    }
                });
            }
            this.shell.setLayout(new GridLayout(1, true));
            ((GridLayout) this.shell.getLayout()).verticalSpacing = 0;
            this.shell.setSize(getShellWidth(), getShellHeight());
            if (getShellLocation() == null) {
                WidgetCompositeUtil.centerInDisplay(this.shell);
                setShellLocation(this.shell.getLocation());
            } else {
                this.shell.setLocation(getShellLocation());
            }

            this.shell.addListener(SWT.Close, this.viewListener);
            this.shell.addListener(SWT.Iconify, this.viewListener);

            if (StringUtils.isNotEmpty(this.getShellText())) {
                this.shell.setText(getShellText());
            }

            if ((getShellImage() != null) && !getShellImage().isDisposed()) {
                this.shell.setImage(getShellImage());
            }

            this.showOpsLabels = (getViewOptions() & AbstractView.SHOW_OPS_LABELS) == AbstractView.SHOW_OPS_LABELS;

            if (appIsUsingRichWindows) {
                GridData data;

                ((GridLayout) this.shell.getLayout()).marginWidth = 0;
                ((GridLayout) this.shell.getLayout()).marginHeight = 0;

                separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
                data = new GridData(SWT.FILL, SWT.END, true, false);
                data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                separator.setLayoutData(data);

                bigUpperComp = new Composite(this.shell, SWT.DOUBLE_BUFFERED);
                GridLayout lay = new GridLayout(2, false);
                lay.marginWidth = 0;
                lay.marginLeft = 10;
                lay.marginRight = 5;
                bigUpperComp.setLayout(lay);
                data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
                bigUpperComp.setLayoutData(data);
                bigUpperComp.setBackgroundMode(SWT.INHERIT_FORCE);
                WidgetCompositeUtil.renderXEfect(bigUpperComp, ColorUtil.COLOR_ALBASTRU_DESCHIS, ColorUtil.COLOR_WHITE);

                setBigLabelText(new Label(bigUpperComp, SWT.NONE));
                data = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
                getBigLabelText().setLayoutData(data);
                getBigLabelText().setFont(FontUtil.TAHOMA12_NORMAL);
                getBigLabelText().setText(getBigViewMessage());

                setBigLabelImage(new Label(bigUpperComp, SWT.NONE));
                data = new GridData(SWT.END, SWT.BEGINNING, false, true);
                data.verticalSpan = 2;
                getBigLabelImage().setLayoutData(data);
                getBigLabelImage().setImage(getBigViewImage());

                this.textDetail = new StyledText(bigUpperComp, SWT.WRAP);
                this.textDetail.setEditable(false);
                this.textDetail.setAlignment(SWT.FILL);
                data = new GridData(SWT.FILL, SWT.CENTER, true, false);
                this.textDetail.setLayoutData(data);
                this.textDetail.setEnabled(false);
                this.textDetail.setForeground(ColorUtil.COLOR_ALBASTRU_INCHIS_ATOM);
                this.textDetail.setFont(FontUtil.TAHOMA10_NORMAL);
                if (this.detailMessage != null) {
                    this.textDetail.setText(getDetailMessage());
                }

                separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
                data = new GridData(SWT.FILL, SWT.END, true, false);
                data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                separator.setLayoutData(data);

            }

            final boolean hasLeftArea = this.addAdd || this.addMod || this.addDel || this.addRefresh || createUpperCompLeftArea;
            final boolean hasRightArea = this.addSearchWithHighlight || this.addNavigation || createUpperCompRightArea;

            if (hasLeftArea || hasRightArea) {
                if (hasRightArea) {
                    Composite temp = new Composite(shell, SWT.NONE);
                    GridLayoutFactory.fillDefaults().numColumns(1).margins(5, 5).applyTo(temp);
                    GridDataFactory.fillDefaults().grab(true, false).applyTo(temp);
                    this.upperComp = new CBanner(temp, SWT.NONE);
                    ((CBanner) this.upperComp).setSimple(true);
                } else {
                    this.upperComp = new Composite(shell, SWT.NONE);
                    GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(this.upperComp);
                }
                GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(this.upperComp);

                /**
                 * daca s-a lansat o fereastra care extinde aceasta clasa, cu cel putin unul din butoanele Adaugare, Modificare, Stergere vizibile, atunci, pentru optiunile de lansare adaugam listener
                 * aici pe MouseEnter si MouseExit, pentru schimbarea imaginii pe buton, ca sa sugeram un Focus asupra acelui buton. Acest lucru este echivalentul metodei setHotImage() pe un tool
                 * item. Actiunile specifice insa vor tb implementate in clasa copil, pe SWT.Selection, si implementand interfata InterfaceAMDView.
                 * <p>
                 * Acest composite este cel mai "cu cantec", in sensul ca : - daca nu exista navigare, se va alinia la dreapta. - daca nu exista refresh, se va alinia la stanga. - daca exista si
                 * navigare si refresh, se va alinia la centru :D
                 */

                if (hasLeftArea) {
                    this.mainToolbar = new ToolBar(this.upperComp, SWT.FLAT);

                    if (this.addAdd) {
                        this.toolItemAdd = new ToolItem(this.mainToolbar, SWT.PUSH | SWT.FLAT);
                        this.toolItemAdd.setImage(AppImages.getImage24(AppImages.IMG_PLUS));
                        this.toolItemAdd.setHotImage(AppImages.getImage24Focus(AppImages.IMG_PLUS));
                        this.toolItemAdd.setToolTipText("Adaugare");
                        if (this.showOpsLabels) {
                            this.toolItemAdd.setText("&Adaugare");
                        }
                        this.toolItemAdd.addListener(SWT.Selection, this.viewListener);
                    }
                    if (this.addMod) {
                        this.toolItemMod = new ToolItem(this.mainToolbar, SWT.PUSH | SWT.FLAT);
                        this.toolItemMod.setImage(AppImages.getImage24(AppImages.IMG_MODIFICARE));
                        this.toolItemMod.setHotImage(AppImages.getImage24Focus(AppImages.IMG_MODIFICARE));
                        this.toolItemMod.setToolTipText("Modificare");
                        if (this.showOpsLabels) {
                            this.toolItemMod.setText("&Modificare");
                        }
                        this.toolItemMod.addListener(SWT.Selection, this.viewListener);
                    }
                    if (this.addDel) {
                        this.toolItemDel = new ToolItem(this.mainToolbar, SWT.PUSH | SWT.FLAT);
                        this.toolItemDel.setImage(AppImages.getImage24(AppImages.IMG_CANCEL));
                        this.toolItemDel.setHotImage(AppImages.getImage24Focus(AppImages.IMG_CANCEL));
                        this.toolItemDel.setToolTipText("Stergere");
                        if (this.showOpsLabels) {
                            this.toolItemDel.setText("Stergere");
                        }
                        this.toolItemDel.addListener(SWT.Selection, this.viewListener);
                    }
                    if (this.addRefresh) {
                        new ToolItem(this.mainToolbar, SWT.SEPARATOR);
                        this.toolItemRefresh = new ToolItem(this.mainToolbar, SWT.PUSH | SWT.FLAT);
                        this.toolItemRefresh.setImage(AppImages.getImage24(AppImages.IMG_REFRESH));
                        this.toolItemRefresh.setHotImage(AppImages.getImage24Focus(AppImages.IMG_REFRESH));
                        this.toolItemRefresh.setToolTipText("Reactualizare informatii");
                        if (this.showOpsLabels) {
                            this.toolItemRefresh.setText("Refresh");
                        }
                        this.toolItemRefresh.addListener(SWT.Selection, this.viewListener);
                    }
                    if (upperComp instanceof CBanner) {
                        ((CBanner) this.upperComp).setLeft(mainToolbar);
                    }
                }

                if (hasRightArea) {
                    upperCompRightArea = new Composite(upperComp, SWT.NONE);
                    int rightAreaNumColumns = 0;
                    if (addNavigation) {
                        rightAreaNumColumns++;
                    }
                    if (addSearchWithHighlight) {
                        rightAreaNumColumns++;
                    }
                    GridLayoutFactory.fillDefaults().numColumns(rightAreaNumColumns == 0 ? 1 : rightAreaNumColumns).equalWidth(false).applyTo(upperCompRightArea);
                    GridDataFactory.fillDefaults().grab(true, true).align(SWT.CENTER, SWT.CENTER).applyTo(upperCompRightArea);

                    if (addNavigation) {
                        this.barNavigare = new ToolBar(this.upperCompRightArea, SWT.FLAT);

                        this.toolItemBack = new ToolItem(this.barNavigare, SWT.PUSH | SWT.FLAT);
                        this.toolItemBack.setImage(AppImages.getImage16(AppImages.IMG_ARROW_LEFT));
                        this.toolItemBack.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_LEFT));
                        this.toolItemBack.setToolTipText("Inapoi/Anterior");
                        this.toolItemBack.addListener(SWT.Selection, this.viewListener);

                        this.toolItemNext = new ToolItem(this.barNavigare, SWT.PUSH | SWT.FLAT);
                        this.toolItemNext.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
                        this.toolItemNext.setHotImage(AppImages.getImage16Focus(AppImages.IMG_ARROW_RIGHT));
                        this.toolItemNext.setToolTipText("Inainte/Urmatorul");
                        this.toolItemNext.addListener(SWT.Selection, this.viewListener);
                    }

                    if (this.addSearchWithHighlight) {
                        this.compSearchWithHighlight = new Composite(this.upperCompRightArea, SWT.NONE);
                        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, true).applyTo(this.compSearchWithHighlight);
                        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 4, 0, 4).equalWidth(false).applyTo(this.compSearchWithHighlight);
                        new Label(this.compSearchWithHighlight, SWT.NONE).setText("Cautare");
                        this.textSearchWithHighlight = new Text(this.compSearchWithHighlight, SWT.BORDER);
                        GridDataFactory.fillDefaults().hint(textSearchWithHighlightWidth, SWT.DEFAULT).grab(true, false).align(SWT.CENTER, SWT.CENTER).applyTo(this.textSearchWithHighlight);
                        this.textSearchWithHighlight.addListener(SWT.Modify, this.viewListener);
                        SWTeXtension.addColoredFocusListener(this.textSearchWithHighlight, ColorUtil.COLOR_FOCUS_YELLOW);
                    }

                    ((CBanner) this.upperComp).setRight(upperCompRightArea);
                }
            }

            getWidgetGridData().horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;

            if (getWidgetClass().getName().equals(Composite.class.getName())) {
                setWidget(new Composite(this.shell, SWT.DOUBLE_BUFFERED));
                ((Composite) getWidget()).setLayout(getWidgetLayout());
                ((Composite) getWidget()).setLayoutData(getWidgetGridData());
                if (!appIsUsingRichWindows) {
                    separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
                    GridData data = new GridData(SWT.FILL, SWT.END, true, false);
                    data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                    separator.setLayoutData(data);
                }
            } else if (getWidgetClass().getName().equals(Group.class.getName())) {
                Composite compTemp = new Composite(this.shell, SWT.DOUBLE_BUFFERED);
                compTemp.setLayout(new GridLayout(1, true));
                ((GridLayout) this.shell.getLayout()).marginTop = 0;
                compTemp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                setWidget(new Group(compTemp, SWT.NONE));
                ((Group) getWidget()).setLayout(getWidgetLayout());
                ((Group) getWidget()).setLayoutData(getWidgetGridData());
            } else if (getWidgetClass().getName().equals(Canvas.class.getName().intern())) {
                Composite compTemp = new Composite(this.shell, SWT.DOUBLE_BUFFERED);
                compTemp.setLayout(new GridLayout(1, true));
                ((GridLayout) this.shell.getLayout()).marginTop = 0;
                compTemp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                setWidget(new Canvas(compTemp, SWT.NONE));
                ((Canvas) getWidget()).setLayout(getWidgetLayout());
                ((Canvas) getWidget()).setLayoutData(getWidgetGridData());
                ((Canvas) getWidget()).addListener(SWT.Paint, new Listener() {
                    @Override
                    public final void handleEvent(final Event e) {
                        GC gc = e.gc;
                        gc.setForeground(ColorUtil.COLOR_ALBASTRU_INCHIS);
                        Rectangle rect = ((Canvas) e.widget).getClientArea();
                        gc.drawRectangle(rect.x, rect.y, rect.width - 1, rect.height - 1);
                    }
                });
            } else if (getWidgetClass().getName().equals(SashForm.class.getName())) {
                Composite compTemp = new Composite(this.shell, SWT.DOUBLE_BUFFERED);
                compTemp.setLayout(new GridLayout(1, true));
                ((GridLayout) this.shell.getLayout()).marginTop = 0;
                compTemp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                setWidget(new SashForm(compTemp, SWT.HORIZONTAL | SWT.SMOOTH));
                ((SashForm) getWidget()).setLayoutData(getWidgetGridData());
            }

            if (appIsUsingRichWindows && !(this instanceof AbstractGView)) {
                separator = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
                GridData data = new GridData(SWT.FILL, SWT.END, true, false);
                data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                separator.setLayoutData(data);
            }

            if (((getViewOptions() & AbstractView.ADD_OK) == AbstractView.ADD_OK)) {
                numColsCompSaveBtn++;
            }
            if ((getViewOptions() & AbstractView.ADD_CANCEL) == AbstractView.ADD_CANCEL) {
                numColsCompSaveBtn++;
            }
            if ((getViewOptions() & AbstractView.ADD_DETAILS) == AbstractView.ADD_DETAILS) {
                numColsCompSaveBtn++;
            }

            if (numColsCompSaveBtn > 0) {
                numColsLowerComp++;
            }

            if (this.addHelp) {
                numColsCompHIRE++;
            }
            if (this.addPrint) {
                numColsCompHIRE++;
            }
            if (this.addExport) {
                numColsCompHIRE++;
            }
            if (this.addReset) {
                numColsCompHIRE++;
            }

            if (numColsCompHIRE > 0) {
                numColsLowerComp++;
            }

            if (numColsLowerComp > 0) {
                setLowerComp(new Composite(this.shell, SWT.NONE));
                GridLayout lay = new GridLayout(numColsLowerComp, false);
                if (appIsUsingRichWindows) {
                    lay.marginHeight = 5;
                    lay.marginWidth = 5;
                } else {
                    lay.verticalSpacing = 0;
                    lay.marginHeight = 2;
                }
                getLowerComp().setLayout(lay);
                GridData data = new GridData(SWT.FILL, SWT.END, true, false);
                data.horizontalSpan = ((GridLayout) this.shell.getLayout()).numColumns;
                getLowerComp().setLayoutData(data);

                if (numColsCompHIRE > 0) {
                    setCompHIRE(new Composite(getLowerComp(), SWT.NONE));
                    lay = new GridLayout(numColsCompHIRE, false);
                    lay.verticalSpacing = 0;
                    lay.horizontalSpacing = 2;
                    lay.marginHeight = 0;
                    lay.marginWidth = 0;
                    getCompHIRE().setLayout(lay);
                    data = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
                    if (numColsCompSaveBtn == 0) {
                        data.horizontalSpan = numColsLowerComp;
                    } else {
                        data.horizontalSpan = numColsLowerComp - 1;
                    }
                    getCompHIRE().setLayoutData(data);

                    if (this.addReset) {
                        this.toolItemReset = new ToolItem(new ToolBar(getCompHIRE(), SWT.FLAT), SWT.FLAT | SWT.PUSH);
                        this.toolItemReset.setImage(AppImages.getImage24(AppImages.IMG_RESTORE));
                        this.toolItemReset.setHotImage(AppImages.getImage24Focus(AppImages.IMG_RESTORE));
                        this.toolItemReset.setToolTipText("Reset");
                        this.toolItemReset.addListener(SWT.Selection, this.viewListener);
                        if ((getViewMode() == AbstractView.MODE_VIEW) || (getViewMode() == AbstractView.MODE_MODIFY_PARTIAL)) {
                            this.toolItemReset.setEnabled(false);
                        }
                    }

                    if (this.addHelp) {
                        this.toolItemHelp = new ToolItem(new ToolBar(getCompHIRE(), SWT.FLAT), SWT.FLAT | SWT.PUSH);
                        this.toolItemHelp.setImage(AppImages.getImage24(AppImages.IMG_HELP));
                        this.toolItemHelp.setHotImage(AppImages.getImage24Focus(AppImages.IMG_HELP));
                        this.toolItemHelp.setToolTipText("Afisare informatii");
                        this.toolItemHelp.addListener(SWT.Selection, this.viewListener);
                    }

                    if (this.addPrint) {
                        this.toolItemPrint = new ToolItem(new ToolBar(getCompHIRE(), SWT.FLAT), SWT.DROP_DOWN);
                        this.toolItemPrint.setImage(AppImages.getImage24(AppImages.IMG_PRINT));
                        this.toolItemPrint.setHotImage(AppImages.getImage24Focus(AppImages.IMG_PRINT));
                        this.toolItemPrint.setToolTipText("Tiparire date");
                        createPrintMenu(this.toolItemPrint.getParent());
                        this.toolItemPrint.addListener(SWT.Selection, this.viewListener);
                    }

                    if (this.addExport) {
                        this.toolItemExport = new ToolItem(new ToolBar(getCompHIRE(), SWT.FLAT), SWT.DROP_DOWN);
                        this.toolItemExport.setImage(AppImages.getImage24(AppImages.IMG_EXPORT));
                        this.toolItemExport.setHotImage(AppImages.getImage24Focus(AppImages.IMG_EXPORT));
                        this.toolItemExport.setToolTipText("Export date");
                        createExportMenu(this.toolItemExport.getParent());
                        this.toolItemExport.addListener(SWT.Selection, this.viewListener);
                    }
                }

                if (numColsCompSaveBtn > 0) {
                    this.compSaveButtons = new Composite(getLowerComp(), SWT.NONE);
                    lay = new GridLayout(numColsCompSaveBtn, true);
                    lay.verticalSpacing = 0;
                    lay.horizontalSpacing = 5;
                    lay.marginHeight = 0;
                    lay.marginWidth = 0;
                    lay.marginRight = 2;
                    this.compSaveButtons.setLayout(lay);
                    data = new GridData(SWT.END, SWT.CENTER, false, false);
                    if (numColsCompHIRE == 0) {
                        data = new GridData(SWT.END, SWT.CENTER, true, false);
                    }
                    if (numColsCompHIRE == 0) {
                        data.horizontalSpan = numColsLowerComp;
                    } else {
                        data.horizontalSpan = numColsLowerComp - 1;
                    }
                    this.compSaveButtons.setLayoutData(data);

                    if (((getViewOptions() & AbstractView.ADD_OK) == AbstractView.ADD_OK)) {
                        this.buttonOk = new Button(this.compSaveButtons, SWT.PUSH);
                        this.buttonOk.setText("&Salvare");
                        this.buttonOk.setToolTipText("Salvare date/confirmare actiune");
                        this.buttonOk.setImage(AppImages.getImage16(AppImages.IMG_OK));
                        data = new GridData(SWT.END, SWT.CENTER, false, false);
                        this.buttonOk.setLayoutData(data);
                        this.buttonOk.addListener(SWT.Selection, this.viewListener);
                        SWTeXtension.addImageChangeListener16(this.buttonOk, AppImages.IMG_OK);
                        this.buttonOk.setVisible(getViewMode() != AbstractView.MODE_VIEW);
                    }
                    if ((getViewOptions() & AbstractView.ADD_CANCEL) == AbstractView.ADD_CANCEL) {
                        this.buttonCancel = new Button(this.compSaveButtons, SWT.PUSH);
                        this.buttonCancel.setText("In&chide");
                        this.buttonCancel.setToolTipText("Renuntare/Inchide fereastra curenta");
                        this.buttonCancel.setImage(AppImages.getImage16(AppImages.IMG_CANCEL));
                        data = new GridData(SWT.END, SWT.CENTER, false, false);
                        this.buttonCancel.setLayoutData(data);
                        this.buttonCancel.addListener(SWT.Selection, this.viewListener);
                        SWTeXtension.addImageChangeListener16(this.buttonCancel, AppImages.IMG_CANCEL);
                    }
                    if ((getViewOptions() & AbstractView.ADD_DETAILS) == AbstractView.ADD_DETAILS) {
                        this.buttonDetails = new Button(this.compSaveButtons, SWT.PUSH);
                        data = new GridData(SWT.END, SWT.CENTER, false, false);
                        this.buttonDetails.setLayoutData(data);
                        this.buttonDetails.setText("Detalii");
                        this.buttonDetails.setImage(AppImages.getImage16(AppImages.IMG_DETAILS_NEW));
                        SWTeXtension.addImageChangeListener16(this.buttonDetails, AppImages.IMG_DETAILS_NEW);
                        this.buttonDetails.addListener(SWT.Selection, this.viewListener);
                    }
                }
            }

            getWidgetGridData().minimumWidth = 300;
            getWidgetGridData().minimumHeight = 100;

            this.shell.setSize(getShellWidth(), getShellHeight());
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void initViewMode() {
        switch (getViewMode()) {
            case MODE_ADD: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_PLUS));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Adaugare " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_PLUS));
                break;
            }
            case MODE_MODIFY: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Modificare " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_MODIFICARE));
                break;
            }
            case MODE_CLONE: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_COPY));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Duplicare " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_COPY));
                break;
            }
            case MODE_MODIFY_PARTIAL: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_MODIFICARE));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Modificare partiala " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_MODIFICARE));
                break;
            }
            case MODE_DELETE: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_CANCEL));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Stergere " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_CANCEL));
                break;
            }
            case MODE_VIEW: {
                if (getShellImage() == null) {
                    setShellImage(AppImages.getImage16(AppImages.IMG_INFO));
                }
                if (StringUtils.isNotEmpty(this.objectName)) {
                    if (StringUtils.isEmpty(getShellText())) {
                        setShellText("Vizualizare " + this.objectName);
                    }
                    if (StringUtils.isEmpty(getBigViewMessage())) {
                        setBigViewMessage(StringUtil.capitalizeCharAtIdx(this.objectName, 0));
                    }
                }
                setBigViewImage(AppImages.getImage24(AppImages.IMG_INFO));
                setAddCloseListener(false);
                break;
            }
            default:
        }
    }

    public void open() {
        open(parentPos == null, true);
    }

    public void open(final boolean centerInDisplay, boolean automaticallyComputeSize) {
        try {
            if (automaticallyComputeSize && (this.shellHeight > 0 || this.shellWidth > 0)) {
                throw new IllegalArgumentException("You cannot specify both manual and automatic shell sizes!");
            }
            if ((this.shell == null) || this.shell.isDisposed()) {
                return;
            }
            if (isOpened()) {
                return;
            }
            WindowSetting setting = null;
            if (useCoords && SettingsController.getBoolean(BooleanSetting.WINDOWS_USE_COORDS)) {
                setting = SettingsController.getWindowSetting(getClass().getCanonicalName());
            }
            if (setting != null && setting.isValid()) {
                this.shell.setBounds(new Rectangle(setting.getX(), setting.getY(), setting.getWidth(), setting.getHeight()));
            } else {
                if (this.parentPos != null) {
                    this.shell.pack();
                    this.shell.setLocation(SWTeXtension.computeChildLocation(this.parentPos, this.shell.getBounds()));
                } else {
                    if (centerInDisplay) {
                        if (automaticallyComputeSize) {
                            WidgetCompositeUtil.centerInDisplay(this.shell);
                        } else {
                            WidgetCompositeUtil.centerInDisplayForFixedWidthsShells(this.shell);
                        }
                    }
                    if (automaticallyComputeSize) {
                        this.shell.setVisible(false);
                        this.shell.setSize(this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                        this.shell.setVisible(true);
                    }
                }
            }
            this.shell.open();

            while ((this.shell != null) && !this.shell.isDisposed() && !shell.getDisplay().isDisposed()) {
                if (!shell.getDisplay().readAndDispatch()) {
                    this.shell.getDisplay().sleep();
                }
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    protected final Button getButtonOk() {
        return this.buttonOk;
    }

    protected final Button getButtonCancel() {
        return this.buttonCancel;
    }

    protected final Button getButtonDetails() {
        return this.buttonDetails;
    }

    protected final ToolItem getToolItemHelp() {
        return this.toolItemHelp;
    }

    protected final ToolItem getToolItemPrint() {
        return this.toolItemPrint;
    }

    protected final ToolItem getToolItemExport() {
        return this.toolItemExport;
    }

    protected final GridData getWidgetGridData() {
        return this.widgetGridData;
    }

    /**
     * validare campuri forma curenta
     *
     * @return <p>
     * <code> true</code> - ok to proceed to save();
     * </p>
     * <p>
     * <code> false</code> - show message and return;
     * </p>
     */
    protected abstract boolean validate();

    protected abstract void saveData() throws Exception;

    public final void createExportMenu(final Control exportMainControl) {
        Menu menu = null;
        MenuItem item;
        try {
            if ((exportMainControl == null) || exportMainControl.isDisposed()) {
                return;
            }

            menu = new Menu(exportMainControl);
            exportMainControl.setMenu(menu);

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Export fisier PDF");
            item.setImage(AppImages.getImage16(AppImages.IMG_ADOBE));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IExport) AbstractView.this).exportPDF();
                }
            });

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Export fisier Excel");
            item.setImage(AppImages.getImage16(AppImages.IMG_EXCEL));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IExport) AbstractView.this).exportExcel();
                }
            });

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Export fisier TXT");
            item.setImage(AppImages.getImage16(AppImages.IMG_EXPORT));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IExport) AbstractView.this).exportTxt();
                }
            });

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Export fisier RTF");
            item.setImage(AppImages.getImage16(AppImages.IMG_WORD2));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IExport) AbstractView.this).exportRTF();
                }
            });

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Export fisier HTML");
            item.setImage(AppImages.getImage16(AppImages.IMG_BROWSER));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IExport) AbstractView.this).exportHTML();
                }
            });

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void createPrintMenu(final Control printMainControl) {
        Menu menu = null;
        MenuItem item;
        try {
            if ((printMainControl == null) || printMainControl.isDisposed()) {
                return;
            }
            menu = new Menu(printMainControl);
            printMainControl.setMenu(menu);

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Tiparire pe ecran");
            item.setImage(AppImages.getImage16(AppImages.IMG_ADOBE));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IPrint) AbstractView.this).printPDF();
                }
            });

            item = new MenuItem(menu, SWT.NULL);
            item.setText("Tiparire la imprimanta");
            item.setImage(AppImages.getImage16(AppImages.IMG_PRINT));
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    ((IPrint) AbstractView.this).printPrinter();
                }
            });
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    protected void saveAndClose(final boolean closeShell) {
        try {
            if (!validate()) {
                return;
            }
            if (this.buttonCancel != null) {
                this.buttonCancel.setEnabled(true);
            }
            saveData();
            if (closeShell) {
                close(SWT.OK);
            }
            if (AbstractView.this.automaticallyShowSaveOKMessage) {
                SWTeXtension.displayMessageI("Operatia s-a executat cu succes.", "Salvare OK");
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE("A intervenit o eroare!", exc);
        }
    }

    private final void shellCloseEvent(final Event e) {
        try {
            if (this.useCoords) {
                SettingsController.saveWindowCoords(getShell().getBounds(), getClass().getCanonicalName());
            }

            if ((getDockingItem() != null) && !getDockingItem().isDisposed()) {
                getDockingItem().dispose();
                getDockingBar().layout();
                getDockingBar().setVisible(getDockingBar().getItemCount() > 0);
                getDockingBar().getParent().layout();
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    public final void close(final int choice) {
        setExitChoice(choice);
        if (!this.shell.isDisposed()) {
            this.shell.close();
        }
    }

    private final void createViewListener() {
        try {
            this.viewListener = new Listener() {
                @Override
                public final void handleEvent(final Event e) {
                    switch (e.type) {
                        case SWT.Close: {
                            if (e.widget == shell) {
                                shellCloseEvent(e);
                            }
                            break;
                        }
                        case SWT.Selection: {
                            handleSWTSelection(e);
                            break;
                        }
                        case SWT.Iconify: {
                            if (!AbstractView.this.useDocking || (getDockingBar() == null) || getDockingBar().isDisposed()) {
                                return;
                            }
                            shell.setVisible(false);
                            if ((getDockingItem() == null) || getDockingItem().isDisposed()) {
                                setDockingItem(new ToolItem(getDockingBar(), SWT.NONE));
                                String name = shell.getText();
                                if (name.length() > 15) {
                                    name = name.substring(0, 13).concat("..");
                                }
                                getDockingItem().setData(AbstractView.this);
                                getDockingItem().setText(name);
                                getDockingItem().setToolTipText(shell.getText());
                                if (shell.getImage() != null) {
                                    getDockingItem().setImage(shell.getImage());
                                } else {
                                    getDockingItem().setImage(AppImages.getImage16(AppImages.IMG_HOME));
                                }
                                getDockingItem().addListener(SWT.Selection, new Listener() {
                                    @Override
                                    public void handleEvent(final Event event) {
                                        shell.setVisible(true);
                                        shell.setMinimized(false);
                                    }
                                });
                                getDockingBar().getParent().layout();
                            }
                            break;
                        }
                        case SWT.Modify: {
                            if (e.widget == AbstractView.this.textSearchWithHighlight) {
                                ((ISearchWithHighlight) AbstractView.this).searchWithHighlight();
                            }
                            break;
                        }
                        default:
                    }
                }
            };
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
        }
    }

    private void handleSWTSelection(final Event e) {
        if (e.widget == this.toolItemHelp) {
            ((IHelp) AbstractView.this).showHelp();
        } else if (e.widget == this.toolItemPrint) {
            WidgetMenuUtil.customizeMenuLocation(this.toolItemPrint.getParent().getMenu(), this.toolItemPrint);
            this.toolItemPrint.getParent().getMenu().setVisible(true);
        } else if (e.widget == this.toolItemExport) {
            WidgetMenuUtil.customizeMenuLocation(this.toolItemExport.getParent().getMenu(), this.toolItemExport);
            this.toolItemExport.getParent().getMenu().setVisible(true);
        } else if (e.widget == this.toolItemReset) {
            ((IReset) AbstractView.this).reset();
        } else if (e.widget == this.buttonOk) {
            saveAndClose(true);
        } else if (e.widget == this.buttonCancel) {
            close(SWT.CANCEL);
        } else if (e.widget == this.buttonDetails) {
            SWTeXtension.displayMessageI("Not implemented yet!");
        } else if (e.widget == this.toolItemAdd) {
            ((IAdd) AbstractView.this).add();
        } else if (e.widget == this.toolItemMod) {
            ((IModify) AbstractView.this).modify();
        } else if (e.widget == this.toolItemDel) {
            ((IDelete) AbstractView.this).delete();
        } else if (e.widget == this.toolItemNext) {
            ((INavigation) AbstractView.this).goForward();
        } else if (e.widget == this.toolItemBack) {
            ((INavigation) AbstractView.this).goBack();
        } else if (e.widget == this.toolItemRefresh) {
            ((IRefresh) AbstractView.this).refresh();
        } else if (e.widget == this.toolItemPrint) {
            ((IPrint) AbstractView.this).printPDF();
        }
    }

    public final StyledText getTextDetail() {
        return this.textDetail;
    }

    public final void updateDetailMessage(final String message) {
        if ((this.textDetail == null) || this.textDetail.isDisposed()) {
            return;
        }
        if (StringUtils.isEmpty(message)) {
            setDetailMessage(AbstractView.SPACESX5);
            this.textDetail.setText(getDetailMessage());
            return;
        }
        this.textDetail.setText(message);
        this.shell.layout();
    }

    public final int getUserAction() {
        return getExitChoice();
    }

    protected final Composite getLowerComp() {
        return this.lowerComp;
    }

    protected final Composite getUpperComp() {
        return this.upperComp;
    }

    public Composite getUpperCompRightArea() {
        return upperCompRightArea;
    }

    private void setShell(final Shell shell) {
        this.shell = shell;
    }

    public final Shell getShell() {
        return this.shell;
    }

    protected final int getViewOptions() {
        return this.viewOptions;
    }

    protected final void setViewOptions(final int viewOptions) {
        this.viewOptions = viewOptions;
    }

    private final boolean isAddCloseListener() {
        return this.addCloseListener;
    }

    protected final void setAddCloseListener(final boolean addCloseListener) {
        this.addCloseListener = addCloseListener;
    }

    private void setWidgetGridData(final GridData widgetGridData) {
        this.widgetGridData = widgetGridData;
    }

    protected void setWidgetLayout(final GridLayout widgetLayout) {
        this.widgetLayout = widgetLayout;
    }

    protected final GridLayout getWidgetLayout() {
        return this.widgetLayout;
    }

    protected final ToolItem getToolItemBack() {
        return this.toolItemBack;
    }

    protected final ToolItem getToolItemNext() {
        return this.toolItemNext;
    }

    protected final void setToolItemHelp(final ToolItem toolItemHelp) {
        this.toolItemHelp = toolItemHelp;
    }

    protected final void setShellHeight(final int shellHeight) {
        this.shellHeight = shellHeight;
    }

    protected final int getShellHeight() {
//        if (this.shellHeight < 100) {
//            this.shellHeight = 100;
//        }
        return this.shellHeight;
    }

    protected final void setShellWidth(final int shellWidth) {
        this.shellWidth = shellWidth;
    }

    protected final int getShellWidth() {
//        if (this.shellWidth < 300) {
//            this.shellWidth = 300;
//        }
        return this.shellWidth;
    }

    private final int getShellStyle() {
        return this.shellStyle;
    }

    protected final void setShellStyle(final int shellStyle) {
        this.shellStyle = shellStyle;
    }

    protected void setShellText(final String shellText) {
        this.shellText = shellText;
    }

    private final Shell getParent() {
        return this.parent;
    }

    private final void setParent(final Shell parent) {
        this.parent = parent;
    }

    protected final Image getShellImage() {
        return this.shellImage;
    }

    protected final void setShellImage(final Image shellImage) {
        this.shellImage = shellImage;
    }

    protected final Point getShellLocation() {
        return this.shellLocation;
    }

    protected final void setShellLocation(final Point shellLocation) {
        this.shellLocation = shellLocation;
    }

    protected final void setExitChoice(final int exitChoice) {
        this.exitChoice = exitChoice;
    }

    protected int getExitChoice() {
        return this.exitChoice;
    }

    protected final int getViewMode() {
        return this.viewMode;
    }

    protected final void setViewMode(final int viewMode) {
        this.viewMode = viewMode;
    }

    private void setLowerComp(final Composite lowerComp) {
        this.lowerComp = lowerComp;
    }

    private void setWidgetClass(final Class<? extends Widget> widgetClass) {
        this.widgetClass = widgetClass;
    }

    protected final Class<? extends Widget> getWidgetClass() {
        return this.widgetClass;
    }

    private void setWidget(final Widget widget) {
        this.widget = widget;
    }

    /**
     * Returns the class instance for the container. Should NOT be used directly, exposing the caller to ClassCastException, but rather from specific implementations of this class. Example :
     * <p>
     * protected SomeView(...) extends EncodeCanvasView
     * <p>
     * This way, the <b>getWidget()</b> method (renamed for convenience <b>getContainer()</b> in EncodeCanvas) will perform the correct cast of the widget param to Canvas, and an instance of Canvas
     * will be returned.
     */
    protected final Widget getWidget() {
        return this.widget;
    }

    private void setCompHIRE(final Composite compHIRE) {
        this.compHIRE = compHIRE;
    }

    protected final Composite getCompHIRE() {
        return this.compHIRE;
    }

    protected final String getShellText() {
        return this.shellText;
    }

    protected final String getBigViewMessage() {
        return this.bigViewMessage;
    }

    protected final void setBigViewMessage(final String bigViewMessage) {
        this.bigViewMessage = bigViewMessage;
    }

    protected final Image getBigViewImage() {
        return this.bigViewImage;
    }

    protected final void setBigViewImage(final Image bigViewImage) {
        this.bigViewImage = bigViewImage;
    }

    protected final ToolItem getToolItemReset() {
        return this.toolItemReset;
    }

    protected final void setToolItemReset(final ToolItem toolItemReset) {
        this.toolItemReset = toolItemReset;
    }

    protected final Label getBigLabelText() {
        return this.bigLabelText;
    }

    protected final void setBigLabelText(final Label bigLabelText) {
        this.bigLabelText = bigLabelText;
    }

    protected final Label getBigLabelImage() {
        return this.bigLabelImage;
    }

    protected final void setBigLabelImage(final Label bigLabelImage) {
        this.bigLabelImage = bigLabelImage;
    }

    protected final void setDetailMessage(final String detailMessage) {
        this.detailMessage = AbstractView.SPACESX5 + detailMessage;
    }

    protected final String getDetailMessage() {
        return this.detailMessage;
    }

    protected final Text getTextSearchWithHighlight() {
        return this.textSearchWithHighlight;
    }

    protected final ToolItem getToolItemAdd() {
        return this.toolItemAdd;
    }

    protected final void setToolItemAdd(final ToolItem toolItemAdd) {
        this.toolItemAdd = toolItemAdd;
    }

    protected final ToolItem getToolItemMod() {
        return this.toolItemMod;
    }

    protected final void setToolItemMod(final ToolItem toolItemMod) {
        this.toolItemMod = toolItemMod;
    }

    protected final ToolItem getToolItemDel() {
        return this.toolItemDel;
    }

    protected final void setToolItemDel(final ToolItem toolItemDel) {
        this.toolItemDel = toolItemDel;
    }

    protected final ToolItem getToolItemRefresh() {
        return this.toolItemRefresh;
    }

    protected final void setToolItemRefresh(final ToolItem toolItemRefresh) {
        this.toolItemRefresh = toolItemRefresh;
    }

    protected final int getWidgetNumCols() {
        return getWidgetLayout() != null ? getWidgetLayout().numColumns : 1;
    }

    /**
     * this method allows the callers to override the reposition and resize of the window, based in saved coords
     *
     * @param useCoords the only good value here would be <code>false</code>, since the default is <code> true</code>
     */
    protected final void setIsUsingCoords(final boolean useCoords) {
        this.useCoords = useCoords;
    }

    private CWaitDlgClassic getWaitDlg(final String message, final boolean open) {
        if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
            this.waitDlg = new CWaitDlgClassic(message);
        }
        if (open) {
            this.waitDlg.open();
        }
        return this.waitDlg;
    }

    protected CWaitDlgClassic getWaitDlg(final String message, final int MAX, final boolean open) {
        if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
            this.waitDlg = new CWaitDlgClassic(message, MAX);
        } else if (this.waitDlg.getMax() <= 0) {
            this.waitDlg.close();
            this.waitDlg = new CWaitDlgClassic(message, MAX);
        }
        if (open) {
            this.waitDlg.open();
        }
        return this.waitDlg;
    }

    protected final void advance(final int MAX) {
        if ((this.waitDlg == null) || this.waitDlg.isClosed() || (this.waitDlg.getMax() <= 0)) {
            getWaitDlg("", MAX, true);
        }
        this.waitDlg.setMessageLabel("Inregistrarea curenta: " + String.valueOf(this.waitDlg.getSelection()) + " din " + MAX + "...");
        this.waitDlg.advance();
    }

    protected final void resetWaitDlg(final int NEW_MAX) {
        if ((this.waitDlg == null) || this.waitDlg.isClosed() || (this.waitDlg.getMax() <= 0)) {
            return;
        }
        this.waitDlg.setMessageLabel("");
        this.waitDlg.setMax(NEW_MAX);
    }

    protected final void setDlgMessage(final String message) {
        if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
            getWaitDlg(message, true);
            return;
        }
        if (message == null) {
            return;
        }
        this.waitDlg.setMessageLabel(message);
    }

    protected final void closeDlg() {
        if ((this.waitDlg == null) || this.waitDlg.isClosed()) {
            return;
        }
        this.waitDlg.close();
    }

    protected final void setUseCoords(final boolean useCoords) {
        this.useCoords = useCoords;
    }

    protected final boolean isViewEnabled() {
        return this.viewMode == AbstractView.MODE_ADD
                || this.viewMode == AbstractView.MODE_MODIFY
                || this.viewMode == AbstractView.MODE_CLONE;
    }

    protected void setShowSaveOKMessage(final boolean automaticallyShowSaveOKMessage) {
        this.automaticallyShowSaveOKMessage = automaticallyShowSaveOKMessage;
    }

    /**
     * if specified, the view automatically manages shell text, shell image, big view message and big view image.
     *
     * @param objName some String, ignored if null or empty
     */
    protected void setObjectName(final String objName) {
        this.objectName = objName;
    }

    protected boolean isOpened() {
        return (this.shell != null) && !this.shell.isDisposed() && this.shell.isVisible();
    }

    public ToolItem getDockingItem() {
        return this.dockingItem;
    }

    public void setDockingItem(final ToolItem dockingItem) {
        this.dockingItem = dockingItem;
    }

    private ToolBar getDockingBar() {
        if ((this.shellStyle & SWT.MIN) != SWT.MIN) {
            return null;
        }
        if ((this.dockingBar == null) || this.dockingBar.isDisposed()) {
            this.dockingBar = EncodePlatform.getBarDocking();
        }
        return this.dockingBar;
    }

    protected final void setUseDocking(final boolean useDocking) {
        this.useDocking = useDocking;
    }

    protected final Composite getCompSearchWithHighlight() {
        return this.compSearchWithHighlight;
    }

    public String getObservableProperty() {
        return observableProperty;
    }

    public void setTextSearchWithHighlightWidth(int textSearchWithHighlightWidth) {
        this.textSearchWithHighlightWidth = textSearchWithHighlightWidth;
    }

    public void setObservableProperty(String observableProperty) {
        this.observableProperty = observableProperty;
    }

    protected void encodeUrl(Text widget) {
        String initial = widget.getText();
        String decoded = StringUtil.decodeUrl(initial);
        if (!initial.equals(decoded)) {
            widget.setText(decoded);
        }
    }

    public boolean isAddEscapeTraverseClose() {
        return addEscapeTraverseClose;
    }

    public void setAddEscapeTraverseClose(boolean addEscapeTraverseClose) {
        this.addEscapeTraverseClose = addEscapeTraverseClose;
    }

    public ToolBar getMainToolBar() {
        return mainToolbar;
    }

    public AbstractMongoDB getObservableObject() {
        return observableObject;
    }

    public void setObservableObject(AbstractMongoDB observableObject) {
        this.observableObject = observableObject;
    }

    public void setCreateUpperCompLeftArea(boolean createUpperCompLeftArea) {
        this.createUpperCompLeftArea = createUpperCompLeftArea;
    }

    public void setCreateUpperCompRightArea(boolean createUpperCompRightArea) {
        this.createUpperCompRightArea = createUpperCompRightArea;
    }
}
