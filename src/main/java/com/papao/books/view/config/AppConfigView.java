package com.papao.books.view.config;

import com.papao.books.FiltruAplicatie;
import com.papao.books.view.AppImages;
import com.papao.books.view.interfaces.AbstractIConfigAdapter;
import com.papao.books.view.interfaces.IConfig;
import com.papao.books.view.interfaces.IEncodeReset;
import com.papao.books.view.providers.UnifiedStyledLabelProvider;
import com.papao.books.view.providers.tree.ITreeNode;
import com.papao.books.view.providers.tree.SimpleTextNode;
import com.papao.books.view.providers.tree.TreeContentProvider;
import com.papao.books.view.util.*;
import com.papao.books.view.view.AbstractCView;
import com.papao.books.view.view.AbstractView;
import com.papao.books.view.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppConfigView extends AbstractCView implements Listener, IEncodeReset {

    private static final Logger logger = Logger.getLogger(AppConfigView.class);

    private final UnifiedStyledLabelProvider leftTreeColumnProvider;

    private final Map<String, IConfig> mapSettings = new TreeMap<String, IConfig>();

    private final static String ITEM_PREFERINTE_VIZUALE = "Preferinte vizuale";
    private final static String ITEM_SYSTEM_TRAY = "System Tray";
    private final static String ITEM_APPLICATIE = "Aplicatie";
    private final static String ITEM_RAPOARTE = "Rapoarte";
    private final static String ITEM_DATE_FORMAT = "Format data/ora";
    private final static String ITEM_ROOT = "Categorii setari";

    private final static String[] ITEMS = new String[]{AppConfigView.ITEM_APPLICATIE, AppConfigView.ITEM_PREFERINTE_VIZUALE, AppConfigView.ITEM_SYSTEM_TRAY, AppConfigView.ITEM_RAPOARTE,
            ITEM_DATE_FORMAT};

    private ViewForm rightForm;
    private Text textSearch;
    private TreeViewer treeViewer;

    public AppConfigView(final Shell parent) {
        super(parent, AbstractView.MODE_NONE);

        this.leftTreeColumnProvider = new UnifiedStyledLabelProvider();

        addComponents();

        hookListeners();
    }

    private void addComponents() {
        Composite compLeft;
        SashForm sash;

        sash = new SashForm(getContainer(), SWT.HORIZONTAL | SWT.SMOOTH);
        sash.SASH_WIDTH = 4;
        GridDataFactory.fillDefaults().grab(true, true).minSize(500, 400).applyTo(sash);

        compLeft = new Composite(sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compLeft);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(0, 0).applyTo(compLeft);

        this.textSearch = new Text(compLeft, SWT.SEARCH);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(this.textSearch);
        this.textSearch.setMessage(IConfig.FILTRARE_DEFAULT);
        this.textSearch.addListener(SWT.Modify, this);

        this.treeViewer = new TreeViewer(compLeft, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
        this.treeViewer.setUseHashlookup(true);
        this.treeViewer.setContentProvider(new TreeContentProvider());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.treeViewer.getTree());
        this.treeViewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);

        final TreeViewerColumn treeCol = new TreeViewerColumn(this.treeViewer, SWT.NONE);
        treeCol.getColumn().setText("Optiuni");
        treeCol.getColumn().setWidth(160);
        treeCol.getColumn().setAlignment(SWT.CENTER);
        treeCol.getColumn().setResizable(true);
        treeCol.getColumn().setMoveable(false);
        treeCol.setLabelProvider(this.leftTreeColumnProvider);

        SimpleTextNode invisibleRoot = new SimpleTextNode(null);
        invisibleRoot.setNodes(new String[]{AppConfigView.ITEM_ROOT});
        ((SimpleTextNode) invisibleRoot.getChildrens().get(0)).setNodes(AppConfigView.ITEMS);
        this.treeViewer.setInput(invisibleRoot);
        WidgetCursorUtil.addHandCursorListener(this.treeViewer.getTree());

        this.rightForm = new ViewForm(sash, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.rightForm);

        this.rightForm.setContent(new Overview());

        sash.setWeights(new int[]{1, 2});

        WidgetCompositeUtil.addColoredFocusListener2Childrens(getContainer());

        this.treeViewer.getTree().setFocus();
    }

    private void hookListeners() {
        this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                // if the selection is empty clear the label
                if (event.getSelection().isEmpty()) {
                    return;
                }
                if (event.getSelection() instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    for (Iterator<ITreeNode> iterator = selection.iterator(); iterator.hasNext(); ) {
                        SimpleTextNode domain = (SimpleTextNode) iterator.next();
                        String value = domain.getName();
                        if (StringUtils.isNotEmpty(value)) {
                            actionPerformed(value);
                        }
                    }
                }
            }
        });
    }

    private void actionPerformed(final String catName) {
        if (StringUtils.isEmpty(catName)) {
            return;
        }
        if (catName.intern() == AppConfigView.ITEM_APPLICATIE.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_APPLICATIE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_APPLICATIE));
            } else {
                this.rightForm.setContent(new ConfigApp());
            }
            updateDetailMessage("Setari aplicatie");
        } else if (catName.intern() == AppConfigView.ITEM_PREFERINTE_VIZUALE.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_PREFERINTE_VIZUALE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_PREFERINTE_VIZUALE));
            } else {
                this.rightForm.setContent(new ConfigPreferinteVizuale());
            }
            updateDetailMessage("Configurare aspect aplicatie");
        } else if (catName.intern() == AppConfigView.ITEM_SYSTEM_TRAY.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_SYSTEM_TRAY) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_SYSTEM_TRAY));
            } else {
                this.rightForm.setContent(new ConfigSystemTray());
            }
            updateDetailMessage("Setari componenta System Tray");
        } else if (catName.intern() == AppConfigView.ITEM_RAPOARTE.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_RAPOARTE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_RAPOARTE));
            } else {
                this.rightForm.setContent(new ConfigReports());
            }
            updateDetailMessage("Setari generare rapoarte");
        } else if (catName.intern() == AppConfigView.ITEM_ROOT.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_ROOT) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_ROOT));
            } else {
                this.rightForm.setContent(new Overview());
            }
            updateDetailMessage("Index categorii setari");
        } else if (catName.intern() == AppConfigView.ITEM_DATE_FORMAT.intern()) {
            if (this.mapSettings.get(AppConfigView.ITEM_DATE_FORMAT) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_DATE_FORMAT));
            } else {
                this.rightForm.setContent(new AppDateFormat());
            }
            updateDetailMessage("Format data/ora");
        } else {
            return;
        }

        this.mapSettings.put(catName, (IConfig) this.rightForm.getContent());

        TreeItem[] items = WidgetTreeUtil.getTreeItemsX(this.treeViewer.getTree());
        for (TreeItem it : items) {
            if (it.getText().equalsIgnoreCase(catName)) {
                this.treeViewer.getTree().setSelection(it);
                break;
            }
        }
    }

    @Override
    public void customizeView() {
        setShellStyle(SWT.MIN | SWT.CLOSE | SWT.RESIZE);
        setViewOptions(AbstractView.ADD_CANCEL | AbstractView.ADD_OK);
        setBigViewMessage("Configurare parametri vizuali ai aplicatiei");
        setBigViewImage(AppImages.getImage32(AppImages.IMG_CONFIG));
        setShellText("Setari aplicatie");
        setShellImage(AppImages.getImage16(AppImages.IMG_CONFIG));
    }

    @Override
    public void saveData() {
        for (IConfig cfg : this.mapSettings.values()) {
            cfg.save();
        }
    }

    @Override
    public final void reset() {
        this.treeViewer.getTree().setSelection(this.treeViewer.getTree().getItem(0));
        this.mapSettings.clear();
        actionPerformed(AppConfigView.ITEM_ROOT);
    }

    @Override
    protected boolean validate() {
        try {
            for (IConfig cfg : this.mapSettings.values()) {
                if (!cfg.validate()) {
                    actionPerformed(cfg.getCatName());
                    return false;
                }
            }
            return true;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            SWTeXtension.displayMessageE(exc.getMessage(), exc);
            return false;
        }
    }

    @Override
    public void handleEvent(final Event e) {
        if (e.type == SWT.Modify) {
            if (e.widget == this.textSearch) {
                this.leftTreeColumnProvider.setSearchText(this.textSearch.getText());
                this.treeViewer.setFilters(SimpleTextNode.getFilter(this.textSearch.getText()));
                if (StringUtils.isEmpty(this.textSearch.getText())) {
                    updateDetailMessage("Toate categoriile sunt vizibile.");
                } else {
                    updateDetailMessage("Sunt vizibile doar categoriile care contin textul \'" + this.textSearch.getText() + "\'");
                }
                this.treeViewer.expandToLevel(AbstractTreeViewer.ALL_LEVELS);
            }
        }
    }

    private class Overview extends AbstractIConfigAdapter {

        public Overview() {
            super(AppConfigView.this.rightForm, SWT.DOUBLE_BUFFERED | SWT.NO_REDRAW_RESIZE);

            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(20, 20).applyTo(this);

            createContents();
            populateFields();
        }

        @Override
        public final void createContents() {
            Label tmp;

            tmp = new Label(this, SWT.NONE);
            tmp.setText("Setari");
            GridDataFactory.fillDefaults().span(2, 1).applyTo(tmp);

            tmp = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
            GridDataFactory.fillDefaults().span(2, 1).applyTo(tmp);

            for (int i = 0; i < AppConfigView.ITEMS.length; i++) {
                final Label imageLabel = new Label(this, SWT.NONE);
                imageLabel.setImage(AppImages.getGrayImage16(AppImages.IMG_CONFIG));
                final Link link = new Link(this, SWT.NONE);
                link.setText("<a>" + AppConfigView.ITEMS[i] + "...</a>");
                link.setData(AppConfigView.ITEMS[i]);
                link.addListener(SWT.Selection, new Listener() {
                    @Override
                    public final void handleEvent(final Event e) {
                        imageLabel.setImage(AppImages.getImage16(AppImages.IMG_CONFIG));
                        actionPerformed(link.getData().toString());
                    }
                });
            }
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_ROOT;
        }
    }

    private class ConfigSystemTray extends AbstractIConfigAdapter {

        private Button buttonFolosireSystemTray;
        private Button buttonSystemTrayAfiseazaMesaje;

        public ConfigSystemTray() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
        }

        @Override
        public final void createContents() {
            Group group;
            GridData gd;
            CLabel labelName;

            labelName = new CLabel(this, SWT.BORDER);
            labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
            labelName.setForeground(ColorUtil.COLOR_WHITE);
            labelName.setText("System Tray");
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            group = new Group(this, SWT.NONE);
            group.setText("Componenta System Tray");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            group.setLayoutData(gd);
            group.setLayout(new GridLayout(2, true));

            this.buttonFolosireSystemTray = new Button(group, SWT.CHECK);
            this.buttonFolosireSystemTray.setText("Folosire System Tray");
            this.buttonFolosireSystemTray.setToolTipText("Selectand aceasta optiune, la minimizarea "
                    + "aplicatiei, aceasta va fi minimizata pe componenta Tray a sistemului, in loc de bara de unelte a suprafetei de lucru (desktop),"
                    + "si devine accesibil un meniu cu diverse optiuni, pe aceasta componenta");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.horizontalSpan = ((GridLayout) group.getLayout()).numColumns;
            this.buttonFolosireSystemTray.setLayoutData(gd);
            this.buttonFolosireSystemTray.addListener(SWT.Selection, this);
            WidgetCursorUtil.addHandCursorListener(this.buttonFolosireSystemTray);

            this.buttonSystemTrayAfiseazaMesaje = new Button(group, SWT.CHECK);
            this.buttonSystemTrayAfiseazaMesaje.setText("System Tray afiseaza mesaje");
            this.buttonSystemTrayAfiseazaMesaje.setToolTipText("Selectand aceasta optiune, impreuna cu activarea utilizarii componentei"
                    + "System Tray, aplicatia va afisa diverse mesaje in partea de jos a ecranului");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.horizontalSpan = ((GridLayout) group.getLayout()).numColumns;
            this.buttonSystemTrayAfiseazaMesaje.setLayoutData(gd);
            WidgetCursorUtil.addHandCursorListener(this.buttonSystemTrayAfiseazaMesaje);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(group);

        }

        @Override
        public final void populateFields() {
//            this.buttonFolosireSystemTray.setSelection(FiltruAplicatie.isUsingTray());
//            this.buttonSystemTrayAfiseazaMesaje.setSelection(FiltruAplicatie.isTraySendingMessages());
            WidgetCompositeUtil.enableGUI(this.buttonFolosireSystemTray.getParent(), this.buttonFolosireSystemTray.getSelection());
            this.buttonFolosireSystemTray.setEnabled(true);
        }

        @Override
        public void save() {
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_APP_IS_USING_TRAY, this.buttonFolosireSystemTray.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_APP_TRAY_SEND_MESSAGES, this.buttonSystemTrayAfiseazaMesaje.getSelection());
        }

        @Override
        public void handleEvent(final Event e) {
            if (e.type == SWT.Selection) {
                if (e.widget == this.buttonFolosireSystemTray) {
                    WidgetCompositeUtil.enableGUI(this.buttonFolosireSystemTray.getParent(), this.buttonFolosireSystemTray.getSelection());
                    this.buttonFolosireSystemTray.setEnabled(true);
                }
            }
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_SYSTEM_TRAY;
        }

    }

    private class ConfigReports extends AbstractIConfigAdapter {
        private Button buttonReportsShowOptions;

        public ConfigReports() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
        }

        @Override
        public final void createContents() {
            Group group;
            GridData gd;
            CLabel labelName;

            labelName = new CLabel(this, SWT.BORDER);
            labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
            labelName.setForeground(ColorUtil.COLOR_WHITE);
            labelName.setText("Rapoarte");
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            group = new Group(this, SWT.NONE);
            group.setText("Setari rapoarte");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            group.setLayoutData(gd);
            group.setLayout(new GridLayout(2, true));

            this.buttonReportsShowOptions = new Button(group, SWT.CHECK);
            this.buttonReportsShowOptions.setText("Afisare optiuni");
            this.buttonReportsShowOptions.setToolTipText("Implicit, la orice fel de export se deschide o"
                    + " fereastra de configurare parametri export. Debifand optiunea curenta, acest lucru nu se va mai intampla");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.horizontalSpan = ((GridLayout) group.getLayout()).numColumns;
            this.buttonReportsShowOptions.setLayoutData(gd);
            WidgetCursorUtil.addHandCursorListener(this.buttonReportsShowOptions);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(group);
        }

        @Override
        public final void populateFields() {
            this.buttonReportsShowOptions.setSelection(FiltruAplicatie.isReportsShowingOptions());
        }

        @Override
        public void save() {
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_APP_REPORTS_SHOW_OPTIONS, this.buttonReportsShowOptions.getSelection());
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_RAPOARTE;
        }
    }

    private class ConfigPreferinteVizuale extends AbstractIConfigAdapter {
        private Button buttonRichWindows;
        private Button buttonUseCoords;
        private Button buttonUseTablePrefs;
        private Button buttonWindowsAsk;
        private Button buttonWindowsReenterData;
        private Button buttonHighlightUseBold;
        private Button buttonHighlightUseColor;
        private Composite compColorTab;

        public ConfigPreferinteVizuale() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
        }

        @Override
        public final void createContents() {
            Group group;
            CLabel labelName;

            labelName = new CLabel(this, SWT.BORDER);
            labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
            labelName.setForeground(ColorUtil.COLOR_WHITE);
            labelName.setText("Preferinte vizuale");
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.BEGINNING).applyTo(labelName);

            group = new Group(this, SWT.NONE);
            group.setText("Setari ferestre");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(2, true));

            this.buttonRichWindows = new Button(group, SWT.CHECK);
            this.buttonRichWindows.setText("Ferestre detaliate");
            this.buttonRichWindows.setToolTipText("Selectand aceasta optiune, pentru " + "fiecare fereastra din aplicatie se vor afisa detalii suplimentare, in partea de sus a acesteia");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonRichWindows);
            WidgetCursorUtil.addHandCursorListener(this.buttonRichWindows);

            this.buttonUseCoords = new Button(group, SWT.CHECK);
            this.buttonUseCoords.setText("Restabilire coordonate si dimensiune");
            this.buttonUseCoords.setToolTipText("Selectand aceasta optiune, " + "fiecare fereastra din aplicatie se va afisa in pozitia in care s-a aflat la ultima "
                    + "inchidere, si va avea ultima dimensiune utilizata");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonUseCoords);
            WidgetCursorUtil.addHandCursorListener(this.buttonUseCoords);

            this.buttonUseTablePrefs = new Button(group, SWT.CHECK);
            this.buttonUseTablePrefs.setText("Restabilire coloane vizibile si dimensiuni coloane");
            this.buttonUseTablePrefs.setToolTipText("Selectand aceasta optiune, " + "se vor restabili coloanele vizibile, dimensiunea si aliniera acestora, pentru orice tabela din aplicatie, "
                    + "folosind valorile configurate ultima data de catre utilizatorul curent");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonUseTablePrefs);
            WidgetCursorUtil.addHandCursorListener(this.buttonUseTablePrefs);

            this.buttonWindowsAsk = new Button(group, SWT.CHECK);
            this.buttonWindowsAsk.setText("Confirmare la inchidere");
            this.buttonWindowsAsk.setToolTipText("Selectand aceasta optiune, la " + "inchiderea unei ferestre veti fi intrebat daca doriti acest lucru, daca butonul de salvare este vizibil");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonWindowsAsk);
            WidgetCursorUtil.addHandCursorListener(this.buttonWindowsAsk);

            this.buttonWindowsReenterData = new Button(group, SWT.CHECK);
            this.buttonWindowsReenterData.setText("Reintroducere date");
            this.buttonWindowsReenterData.setToolTipText("Cu aceasta optiune selectata, daca veti salva un document in orice fereastra,"
                    + "aceasta va reintra pe modul de adaugare a unui nou document. Ciclul se opreste daca apasati renuntare sau inchideti fereastra.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonWindowsReenterData);
            WidgetCursorUtil.addHandCursorListener(this.buttonWindowsReenterData);

            group = new Group(this, SWT.NONE);
            group.setText("Cautare instanta");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(3, true));

            this.buttonHighlightUseBold = new Button(group, SWT.CHECK);
            this.buttonHighlightUseBold.setText("Marcheaza rezultatele folosind bold text");
            this.buttonHighlightUseBold.setToolTipText("Selectarea optiunii va marca potrivirile de text folosind un stil bold pt textele gasite.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonHighlightUseBold);
            WidgetCursorUtil.addHandCursorListener(this.buttonHighlightUseBold);

            this.buttonHighlightUseColor = new Button(group, SWT.CHECK);
            this.buttonHighlightUseColor.setText("Culoare rezultate");
            this.buttonHighlightUseColor
                    .setToolTipText("Selectarea optiunii va marca potrivirile de text folosind o anumita culoare. Daca bifati aceasta optiune, va trebui sa specificati si culoarea dorita");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.buttonHighlightUseColor);
            WidgetCursorUtil.addHandCursorListener(this.buttonHighlightUseColor);
            this.buttonHighlightUseColor.addListener(SWT.Selection, this);

            this.compColorTab = new Composite(group, SWT.BORDER);
            GridDataFactory.fillDefaults().hint(16, 16).align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.compColorTab);
            GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(this.compColorTab);
            this.compColorTab.addListener(SWT.MouseDown, this);
            this.compColorTab.addListener(SWT.Dispose, this);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.buttonRichWindows.setSelection(FiltruAplicatie.isWindowsUsingRichWindows());
            this.buttonUseCoords.setSelection(FiltruAplicatie.isWindowsUsingCoords());
            this.buttonUseTablePrefs.setSelection(FiltruAplicatie.isTableUsingPrefs());
            this.buttonWindowsAsk.setSelection(FiltruAplicatie.isWindowsAskingOnClose());
            this.buttonWindowsReenterData.setSelection(FiltruAplicatie.isWindowsReenteringData());
            this.buttonHighlightUseColor.setSelection(FiltruAplicatie.isHighlightUsingColor());
            this.buttonHighlightUseBold.setSelection(FiltruAplicatie.isHighlightUsingBold());
            this.compColorTab.setBackground(FiltruAplicatie.getHighlightColor());
            this.compColorTab.setEnabled(this.buttonHighlightUseColor.getSelection());
        }

        @Override
        public boolean validate() {
            try {
                if (!this.buttonHighlightUseBold.getSelection() && !this.buttonHighlightUseColor.getSelection()) {
                    SWTeXtension.displayMessageW("Cel putin una din optiunile '" + this.buttonHighlightUseBold.getText() + "' sau '" + this.buttonHighlightUseColor.getText() + "' trebuie selectata!");
                    return false;
                }
                if ((this.buttonHighlightUseColor.getSelection() && (this.compColorTab.getBackground() == null)) || this.compColorTab.getBackground().isDisposed()) {
                    SWTeXtension.displayMessageW("Daca bifati optiunea '" + this.buttonHighlightUseColor.getText() + "', trebuie sa selectati si o culoare.");
                    return false;
                }
                return true;
            } catch (Exception exc) {
                logger.error(exc.getMessage(), exc);
                SWTeXtension.displayMessageE(exc.getMessage(), exc);
                return false;
            }
        }

        @Override
        public void save() {

//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_RICH_APP_WINDOWS, this.buttonRichWindows.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_WINDOWS_USE_COORDS, this.buttonUseCoords.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_TABLE_USE_PREFS, this.buttonUseTablePrefs.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_WINDOWS_ASK_ON_CLOSE, this.buttonWindowsAsk.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_WINDOWS_REENTER_DATA, this.buttonWindowsReenterData.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_HIGHLIGHT_SEARCH_USE_BOLD, this.buttonHighlightUseBold.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_HIGHLIGHT_SEARCH_USE_COLOR, this.buttonHighlightUseColor.getSelection());
            if (this.buttonHighlightUseColor.getSelection()) {
//                FiltruAplicatie.saveHighlightRgbCodes(this.compColorTab.getBackground());
            }
        }

        @Override
        public void handleEvent(final Event e) {
            if (e.type == SWT.Selection) {
                if (e.widget == this.buttonHighlightUseColor) {
                    this.compColorTab.setEnabled(this.buttonHighlightUseColor.getSelection());
                }
            } else if (e.type == SWT.MouseDown) {
                if (!this.compColorTab.isEnabled()) {
                    return;
                }
                selectColor();
            } else if (e.type == SWT.Dispose) {
                disposeLabelColor();
            }
        }

        private void selectColor() {
            RGB result = new ColorDialog(getShell()).open();
            if (result == null) {
                return;
            }
            disposeLabelColor();
            this.compColorTab.setBackground(new Color(Display.getDefault(), result));
        }

        private void disposeLabelColor() {
            if ((this.compColorTab.getBackground() != null) && !(this.compColorTab.getBackground()).isDisposed()
                    && !(this.compColorTab.getBackground().getRGB().equals(FiltruAplicatie.HIGHLIGHT_COLOR_DEFAULT.getRGB()))) {
                this.compColorTab.getBackground().dispose();
            }
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_PREFERINTE_VIZUALE;
        }

    }

    private class ConfigApp extends AbstractIConfigAdapter {

        private Button buttonShowAll;
        private Button buttonShowRecentActivity;
        private Button buttonAutopopulateTabs;
        private FormattedText textCount;

        public ConfigApp() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
        }

        @Override
        public final void createContents() {
            Group group;
            Label tmp;

            CLabel labelName;

            labelName = new CLabel(this, SWT.BORDER);
            labelName.setBackground(ColorUtil.COLOR_ALBASTRU_FACEBOOK);
            labelName.setForeground(ColorUtil.COLOR_WHITE);
            labelName.setText("Aplicatie");
            labelName.setFont(FontUtil.TAHOMA12_BOLD);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(labelName);

            group = new Group(this, SWT.NONE);
            group.setText("Setari in aplicatie");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(2, true));

            this.buttonShowAll = new Button(group, SWT.CHECK);
            this.buttonShowAll.setText("afisare nod 'toate' in cadrul grupajelor");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowAll);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowAll);

            this.buttonShowRecentActivity = new Button(group, SWT.CHECK);
            this.buttonShowRecentActivity.setText("afisare nod 'operatii recente' in cadrul grupajelor");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowRecentActivity);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowRecentActivity);

            this.buttonAutopopulateTabs = new Button(group, SWT.CHECK);
            this.buttonAutopopulateTabs.setText("afisare inregistrari la deschiderea unei componente");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonAutopopulateTabs);
            WidgetCursorUtil.addHandCursorListener(this.buttonAutopopulateTabs);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
//            this.buttonShowAll.setSelection(FiltruAplicatie.isLeftTreeShowingAll());
//            this.buttonShowRecentActivity.setSelection(FiltruAplicatie.isLeftTreeShowRecentActivity());
//            this.buttonAutopopulateTabs.setSelection(FiltruAplicatie.isAutopopulateTabs());
        }

        @Override
        public void save() {
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_LEFT_TREE_SHOW_ALL, this.buttonShowAll.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_LEFT_TREE_SHOW_RECENT_ACTIVITY, this.buttonShowRecentActivity.getSelection());
//            FiltruAplicatie.putBoolean(FiltruAplicatie.KEY_AUTOPOPULATE_TABS, this.buttonAutopopulateTabs.getSelection());
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_APPLICATIE;
        }
    }

    private class AppDateFormat extends AbstractIConfigAdapter {
        private Combo comboDateFormat;
        private Combo comboTimeFormat;
        private Label labelDatePreview;
        private Label labelTimePreview;

        public AppDateFormat() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
            previewDate();
            previewTime();
        }

        @Override
        public final void createContents() {
            Group group;

            group = new Group(this, SWT.NONE);
            group.setText("Format data/ora");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(2, true));

            Label tmp = new Label(group, SWT.NONE);
            tmp.setText("Format afisare data");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.comboDateFormat = new Combo(group, SWT.READ_ONLY);
            this.comboDateFormat.setItems(FiltruAplicatie.AVAILABLE_DATE_FORMATS);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.comboDateFormat);
            this.comboDateFormat.addListener(SWT.Selection, this);

            tmp = new Label(group, SWT.NONE);
            tmp.setText("Vizualizare format data");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.labelDatePreview = new Label(group, SWT.NONE);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.labelDatePreview);

            tmp = new Label(group, SWT.NONE);
            tmp.setText("Format afisare ora");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.comboTimeFormat = new Combo(group, SWT.READ_ONLY);
            this.comboTimeFormat.setItems(FiltruAplicatie.AVAILABLE_TIME_FORMATS);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.comboTimeFormat);
            this.comboTimeFormat.addListener(SWT.Selection, this);

            tmp = new Label(group, SWT.NONE);
            tmp.setText("Vizualizare format ora");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.labelTimePreview = new Label(group, SWT.NONE);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.labelTimePreview);
        }

        @Override
        public final void populateFields() {
            this.comboDateFormat.select(this.comboDateFormat.indexOf(FiltruAplicatie.getAppDateFormat()));
            this.comboTimeFormat.select(this.comboTimeFormat.indexOf(FiltruAplicatie.getAppTimeFormat()));
        }

        @Override
        public void save() {
//            FiltruAplicatie.put(FiltruAplicatie.KEY_APP_DATE_FORMAT, this.comboDateFormat.getText());
//            FiltruAplicatie.put(FiltruAplicatie.KEY_APP_TIME_FORMAT, this.comboTimeFormat.getText());
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_DATE_FORMAT;
        }

        @Override
        public final void handleEvent(final Event e) {
            if (e.type == SWT.Selection) {
                if (e.widget == this.comboDateFormat) {
                    previewDate();
                } else if (e.widget == this.comboTimeFormat) {
                    previewTime();
                }
            }
        }

        private void previewDate() {
            final Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat(this.comboDateFormat.getText());
            this.labelDatePreview.setText(df.format(today));
        }

        private void previewTime() {
            final Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat(this.comboTimeFormat.getText());
            this.labelTimePreview.setText(df.format(today));
        }
    }

}
