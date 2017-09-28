package com.papao.books.ui.config;

import com.papao.books.config.StringSetting;
import com.papao.books.controller.SettingsController;
import com.papao.books.model.config.GeneralSetting;
import com.papao.books.model.config.SearchEngine;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.interfaces.AbstractIConfigAdapter;
import com.papao.books.ui.interfaces.IConfig;
import com.papao.books.ui.interfaces.IReset;
import com.papao.books.ui.providers.UnifiedStyledLabelProvider;
import com.papao.books.ui.providers.tree.ITreeNode;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.ui.providers.tree.TreeContentProvider;
import com.papao.books.ui.util.*;
import com.papao.books.ui.view.AbstractCView;
import com.papao.books.ui.view.AbstractView;
import com.papao.books.ui.view.SWTeXtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.*;
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

import static com.papao.books.config.BooleanSetting.*;

public class AppConfigView extends AbstractCView implements Listener, IReset {

    private static final Logger logger = Logger.getLogger(AppConfigView.class);

    private final UnifiedStyledLabelProvider leftTreeColumnProvider;

    private final Map<String, IConfig> mapSettings = new TreeMap<String, IConfig>();

    private final static String ITEM_PREFERINTE_VIZUALE = "Preferinte vizuale";
    private final static String ITEM_SYSTEM_TRAY = "System Tray";
    private final static String ITEM_APPLICATIE = "Aplicatie";
    private final static String ITEM_MAIN_PERSPECTIVE = "Perspectiva";
    private final static String ITEM_RAPOARTE = "Rapoarte";
    private final static String ITEM_ROOT = "Categorii setari";

    private final static String[] ITEMS = new String[]{
            AppConfigView.ITEM_APPLICATIE,
            AppConfigView.ITEM_PREFERINTE_VIZUALE,
            AppConfigView.ITEM_SYSTEM_TRAY,
            AppConfigView.ITEM_MAIN_PERSPECTIVE,
            AppConfigView.ITEM_RAPOARTE};

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
        GridDataFactory.fillDefaults().grab(true, true).applyTo(sash);

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
        if (catName.equals(AppConfigView.ITEM_APPLICATIE)) {
            if (this.mapSettings.get(AppConfigView.ITEM_APPLICATIE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_APPLICATIE));
            } else {
                this.rightForm.setContent(new ConfigApp());
            }
            updateDetailMessage("Setari aplicatie");
        } else if (catName.equals(AppConfigView.ITEM_PREFERINTE_VIZUALE)) {
            if (this.mapSettings.get(AppConfigView.ITEM_PREFERINTE_VIZUALE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_PREFERINTE_VIZUALE));
            } else {
                this.rightForm.setContent(new ConfigPreferinteVizuale());
            }
            updateDetailMessage("Configurare aspect aplicatie");
        } else if (catName.equals(AppConfigView.ITEM_MAIN_PERSPECTIVE)) {
            if (this.mapSettings.get(AppConfigView.ITEM_MAIN_PERSPECTIVE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_MAIN_PERSPECTIVE));
            } else {
                this.rightForm.setContent(new MainPerspective());
            }
            updateDetailMessage("Configurare aspect aplicatie");
        } else if (catName.equals(AppConfigView.ITEM_SYSTEM_TRAY)) {
            if (this.mapSettings.get(AppConfigView.ITEM_SYSTEM_TRAY) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_SYSTEM_TRAY));
            } else {
                this.rightForm.setContent(new ConfigSystemTray());
            }
            updateDetailMessage("Setari componenta System Tray");
        } else if (catName.equals(AppConfigView.ITEM_RAPOARTE)) {
            if (this.mapSettings.get(AppConfigView.ITEM_RAPOARTE) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_RAPOARTE));
            } else {
                this.rightForm.setContent(new ConfigReports());
            }
            updateDetailMessage("Setari generare rapoarte");
        } else if (catName.equals(AppConfigView.ITEM_ROOT)) {
            if (this.mapSettings.get(AppConfigView.ITEM_ROOT) != null) {
                this.rightForm.setContent((Composite) this.mapSettings.get(AppConfigView.ITEM_ROOT));
            } else {
                this.rightForm.setContent(new Overview());
            }
            updateDetailMessage("Index categorii setari");
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
            group.setLayout(new GridLayout(2, false));

            this.buttonFolosireSystemTray = new Button(group, SWT.CHECK);
            this.buttonFolosireSystemTray.setText("Folosire System Tray");
            this.buttonFolosireSystemTray.setToolTipText("Selectând această opțiune, la minimizarea "
                    + "aplicației, aceasta va fi minimizată pe componenta Tray a sistemului, în loc de bara de unelte a suprafeței de lucru (desktop),"
                    + "și devine accesibil un meniu cu diverse opțiuni, pe această componentă.");
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
            this.buttonFolosireSystemTray.setSelection(SettingsController.getBoolean(APP_USE_SYSTEM_TRAY));
            this.buttonSystemTrayAfiseazaMesaje.setSelection(SettingsController.getBoolean(SYSTEM_TRAY_MESSAGES));
            WidgetCompositeUtil.enableGUI(this.buttonFolosireSystemTray.getParent(), this.buttonFolosireSystemTray.getSelection());
            this.buttonFolosireSystemTray.setEnabled(true);
        }

        @Override
        public void save() {
            SettingsController.saveBooleanSetting(APP_USE_SYSTEM_TRAY, buttonFolosireSystemTray.getSelection());
            SettingsController.saveBooleanSetting(SYSTEM_TRAY_MESSAGES, buttonSystemTrayAfiseazaMesaje.getSelection());
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
            group.setLayout(new GridLayout(2, false));

            this.buttonReportsShowOptions = new Button(group, SWT.CHECK);
            this.buttonReportsShowOptions.setText("Afisare optiuni");
            this.buttonReportsShowOptions.setToolTipText("Implicit, la orice fel de export se deschide o"
                    + " fereastră de configurare parametri export. Debifând opțiunea curentă, acest lucru nu se va mai întâmpla.");
            gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
            gd.horizontalSpan = ((GridLayout) group.getLayout()).numColumns;
            this.buttonReportsShowOptions.setLayoutData(gd);
            WidgetCursorUtil.addHandCursorListener(this.buttonReportsShowOptions);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(group);
        }

        @Override
        public final void populateFields() {
            this.buttonReportsShowOptions.setSelection(SettingsController.getBoolean(REPORT_SHOW_OPTIONS));
        }

        @Override
        public void save() {
            SettingsController.saveBooleanSetting(REPORT_SHOW_OPTIONS, buttonReportsShowOptions.getSelection());
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
            group.setLayout(new GridLayout(2, false));

            this.buttonRichWindows = new Button(group, SWT.CHECK);
            this.buttonRichWindows.setText("Ferestre detaliate");
            this.buttonRichWindows.setToolTipText("Selectând această opțiune, pentru " + "fiecare fereastră din aplicație se vor afișa detalii suplimentare, în partea de sus a acesteia");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonRichWindows);
            WidgetCursorUtil.addHandCursorListener(this.buttonRichWindows);

            this.buttonUseCoords = new Button(group, SWT.CHECK);
            this.buttonUseCoords.setText("Restabilire coordonate și dimensiune");
            this.buttonUseCoords.setToolTipText("Selectând această opțiune, " + "fiecare fereastră din aplicație se va afișa în poziția în care s-a aflat la ultima "
                    + "închidere, și va avea ultima dimensiune utilizată.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonUseCoords);
            WidgetCursorUtil.addHandCursorListener(this.buttonUseCoords);

            this.buttonUseTablePrefs = new Button(group, SWT.CHECK);
            this.buttonUseTablePrefs.setText("Restabilire coloane vizibile și dimensiuni coloane");
            this.buttonUseTablePrefs.setToolTipText("Selectând această opțiune, " + "se vor restabili coloanele vizibile, dimensiunea și alinierea acestora, pentru orice tabelă din aplicație, "
                    + "folosind valorile configurate ultima data de către utilizatorul curent.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonUseTablePrefs);
            WidgetCursorUtil.addHandCursorListener(this.buttonUseTablePrefs);

            this.buttonWindowsAsk = new Button(group, SWT.CHECK);
            this.buttonWindowsAsk.setText("Confirmare la inchidere");
            this.buttonWindowsAsk.setToolTipText("Selectând această opțiune, la închiderea unei ferestre veți fi întrebat dacă doriți acest lucru, dacă butonul de salvare este vizibil");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonWindowsAsk);
            WidgetCursorUtil.addHandCursorListener(this.buttonWindowsAsk);

            this.buttonWindowsReenterData = new Button(group, SWT.CHECK);
            this.buttonWindowsReenterData.setText("Reintroducere date");
            this.buttonWindowsReenterData.setToolTipText("Cu această opțiune selectată, dacă veți salva un document în orice fereastră,"
                    + "aceasta va reintra pe modul de adăugare a unui nou document. Ciclul se oprește dacă apăsați renunțare sau închideți fereastra.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonWindowsReenterData);
            WidgetCursorUtil.addHandCursorListener(this.buttonWindowsReenterData);

            group = new Group(this, SWT.NONE);
            group.setText("Cautare instanta");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(3, true));

            this.buttonHighlightUseBold = new Button(group, SWT.CHECK);
            this.buttonHighlightUseBold.setText("Marcheaza rezultatele folosind bold text");
            this.buttonHighlightUseBold.setToolTipText("Selectarea opțiunii va marca potrivirile de text folosind un stil bold pt textele găsite.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(((GridLayout) group.getLayout()).numColumns, 1).applyTo(this.buttonHighlightUseBold);
            WidgetCursorUtil.addHandCursorListener(this.buttonHighlightUseBold);

            this.buttonHighlightUseColor = new Button(group, SWT.CHECK);
            this.buttonHighlightUseColor.setText("Culoare rezultate");
            this.buttonHighlightUseColor
                    .setToolTipText("Selectarea opțiunii va marca potrivirile de text folosind o anumită culoare. Daca bifați această opțiune, va trebui să specificați și culoarea dorită.");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.buttonHighlightUseColor);
            WidgetCursorUtil.addHandCursorListener(this.buttonHighlightUseColor);
            this.buttonHighlightUseColor.addListener(SWT.Selection, this);

            this.compColorTab = new Composite(group, SWT.BORDER);
            GridDataFactory.fillDefaults().hint(16, 16).align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(this.compColorTab);
            GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).applyTo(this.compColorTab);
            this.compColorTab.addListener(SWT.MouseDown, this);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.buttonRichWindows.setSelection(SettingsController.getBoolean(SHOW_RICH_WINDOWS));
            this.buttonUseCoords.setSelection(SettingsController.getBoolean(WINDOWS_USE_COORDS));
            this.buttonUseTablePrefs.setSelection(SettingsController.getBoolean(TABLES_USE_CONFIG));
            this.buttonWindowsAsk.setSelection(SettingsController.getBoolean(WINDOWS_ASK_ON_CLOSE));
            this.buttonWindowsReenterData.setSelection(SettingsController.getBoolean(WINDOWS_REENTER_DATA));
            this.buttonHighlightUseColor.setSelection(SettingsController.getBoolean(SEARCH_HIGHLIGHT_USES_COLOR));
            this.buttonHighlightUseBold.setSelection(SettingsController.getBoolean(SEARCH_HIGHLIGHT_USES_BOLD));
            this.compColorTab.setBackground(SettingsController.HIGHLIGHT_COLOR);
            this.compColorTab.setEnabled(this.buttonHighlightUseColor.getSelection());
        }

        @Override
        public boolean validate() {
            try {
                if (!this.buttonHighlightUseBold.getSelection() && !this.buttonHighlightUseColor.getSelection()) {
                    SWTeXtension.displayMessageW("Cel puțin una din opțiunile '" + this.buttonHighlightUseBold.getText() + "' sau '" + this.buttonHighlightUseColor.getText() + "' trebuie selectată!");
                    return false;
                }
                if ((this.buttonHighlightUseColor.getSelection() && (this.compColorTab.getBackground() == null)) || this.compColorTab.getBackground().isDisposed()) {
                    SWTeXtension.displayMessageW("Daca bifați opțiunea '" + this.buttonHighlightUseColor.getText() + "', trebuie să selectați și o culoare.");
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
            SettingsController.saveBooleanSetting(SHOW_RICH_WINDOWS, buttonRichWindows.getSelection());
            SettingsController.saveBooleanSetting(WINDOWS_USE_COORDS, buttonUseCoords.getSelection());
            SettingsController.saveBooleanSetting(TABLES_USE_CONFIG, buttonUseTablePrefs.getSelection());
            SettingsController.saveBooleanSetting(WINDOWS_ASK_ON_CLOSE, buttonWindowsAsk.getSelection());
            SettingsController.saveBooleanSetting(WINDOWS_REENTER_DATA, buttonWindowsReenterData.getSelection());
            SettingsController.saveBooleanSetting(SEARCH_HIGHLIGHT_USES_BOLD, buttonHighlightUseBold.getSelection());
            SettingsController.saveBooleanSetting(SEARCH_HIGHLIGHT_USES_COLOR, buttonHighlightUseColor.getSelection());
            if (this.buttonHighlightUseColor.getSelection()) {
                GeneralSetting searchHighlightColor = SettingsController.getGeneralSetting("searchHighlightColor");
                if (searchHighlightColor == null) {
                    searchHighlightColor = new GeneralSetting();
                    searchHighlightColor.setKey("searchHighlightColor");
                }
                RGB rgb = this.compColorTab.getBackground().getRGB();
                searchHighlightColor.setValue(new int[]{rgb.red, rgb.green, rgb.blue});
                SettingsController.saveGeneralSetting(searchHighlightColor);
                SettingsController.HIGHLIGHT_COLOR = this.compColorTab.getBackground();
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
            }
        }

        private void selectColor() {
            RGB result = new ColorDialog(getShell()).open();
            if (result == null) {
                return;
            }
            this.compColorTab.setBackground(new Color(Display.getDefault(), result));
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_PREFERINTE_VIZUALE;
        }

    }

    private class ConfigApp extends AbstractIConfigAdapter {

        private Button buttonShowAll;
        private Button buttonShowRecentActivity;
        private Button buttonShowNumbers;

        private Combo comboDateFormat;
        private Combo comboTimeFormat;
        private Combo comboTreeDateFormat;
        private Label labelDatePreview;
        private Label labelTimePreview;

        private GeneralSetting stilAfisareDataInTree = null;

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
            group.setLayout(new GridLayout(2, false));

            this.buttonShowAll = new Button(group, SWT.CHECK);
            this.buttonShowAll.setText("afisare nod 'toate' in cadrul grupajelor");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowAll);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowAll);

            this.buttonShowRecentActivity = new Button(group, SWT.CHECK);
            this.buttonShowRecentActivity.setText("afisare nod 'operatii recente' in cadrul grupajelor");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowRecentActivity);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowRecentActivity);

            this.buttonShowNumbers = new Button(group, SWT.CHECK);
            this.buttonShowNumbers.setText("afisare nr inregistrari in grupaje");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowNumbers);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowNumbers);

            new Label(group, SWT.NONE).setText("Stil afisare data");
            this.comboTreeDateFormat = new Combo(group, SWT.READ_ONLY);
            this.comboTreeDateFormat.setItems(StilAfisareData.STIL_AFISARE_DATA);
            WidgetCursorUtil.addHandCursorListener(this.comboTreeDateFormat);

            group = new Group(this, SWT.NONE);
            group.setText("Format data/ora");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(2, false));

            Label tmp = new Label(group, SWT.NONE);
            tmp.setText("Format afisare data");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.comboDateFormat = new Combo(group, SWT.READ_ONLY);
            this.comboDateFormat.setItems(SettingsController.AVAILABLE_DATE_FORMATS);
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
            this.comboTimeFormat.setItems(SettingsController.AVAILABLE_TIME_FORMATS);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.comboTimeFormat);
            this.comboTimeFormat.addListener(SWT.Selection, this);

            tmp = new Label(group, SWT.NONE);
            tmp.setText("Vizualizare format ora");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(tmp);

            this.labelTimePreview = new Label(group, SWT.NONE);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(this.labelTimePreview);

            WidgetCompositeUtil.addColoredFocusListener2Childrens(this);
        }

        @Override
        public final void populateFields() {
            this.buttonShowAll.setSelection(SettingsController.getBoolean(LEFT_TREE_SHOW_ALL));
            this.buttonShowRecentActivity.setSelection(SettingsController.getBoolean(LEFT_TREE_SHOW_RECENT));
            this.buttonShowNumbers.setSelection(SettingsController.getBoolean(LEFT_TREE_SHOW_NUMBERS));

            stilAfisareDataInTree = SettingsController.getGeneralSetting("stilAfisareDataInTree");
            if (stilAfisareDataInTree == null) {
                stilAfisareDataInTree = new GeneralSetting();
                stilAfisareDataInTree.setKey("stilAfisareDataInTree");
                stilAfisareDataInTree.setValue(StilAfisareData.AFISARE_LUNI_IN_CIFRE);
            }
            comboTreeDateFormat.select(Integer.valueOf(stilAfisareDataInTree.getValue().toString()));

            this.comboDateFormat.select(this.comboDateFormat.indexOf(SettingsController.getString(StringSetting.APP_DATE_FORMAT)));
            this.comboTimeFormat.select(this.comboTimeFormat.indexOf(SettingsController.getString(StringSetting.APP_TIME_FORMAT)));

            previewDate();
            previewTime();
        }

        @Override
        public void save() {
            SettingsController.saveBooleanSetting(LEFT_TREE_SHOW_ALL, buttonShowAll.getSelection());
            SettingsController.saveBooleanSetting(LEFT_TREE_SHOW_RECENT, buttonShowRecentActivity.getSelection());
            SettingsController.saveBooleanSetting(LEFT_TREE_SHOW_NUMBERS, buttonShowNumbers.getSelection());

            SettingsController.saveStringSetting(StringSetting.APP_DATE_FORMAT, comboDateFormat.getText());
            SettingsController.saveStringSetting(StringSetting.APP_TIME_FORMAT, comboTimeFormat.getText());

            stilAfisareDataInTree.setValue(comboTreeDateFormat.getSelectionIndex());
            SettingsController.saveGeneralSetting(stilAfisareDataInTree);
        }

        private void previewDate() {
            if (StringUtils.isEmpty(this.comboDateFormat.getText())) {
                return;
            }
            final Date today = Calendar.getInstance().getTime();
            DateFormat df = new SimpleDateFormat(this.comboDateFormat.getText());
            this.labelDatePreview.setText(df.format(today));
        }

        private void previewTime() {
            if (StringUtils.isEmpty(this.comboTimeFormat.getText())) {
                return;
            }
            final Date today = Calendar.getInstance().getTime();
            final DateFormat df = new SimpleDateFormat(comboTimeFormat.getText());
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    while (!comboTimeFormat.isDisposed()) {
                        labelTimePreview.setText(df.format(today));
                        Display.getDefault().readAndDispatch();
                    }
                }
            });
        }

        @Override
        public String getCatName() {
            return AppConfigView.ITEM_APPLICATIE;
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
    }

    private class MainPerspective extends AbstractIConfigAdapter {

        private Button buttonShowGallery;
        private Button buttonAutorLink;
        private Combo comboSearchEngine;
        private GeneralSetting searchEngineConfig = null;

        public MainPerspective() {
            super(AppConfigView.this.rightForm);
            GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(this);
            createContents();
            populateFields();
        }

        @Override
        public void createContents() {
            Group group = new Group(this, SWT.NONE);
            group.setText("Perspectiva principala");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(group);
            group.setLayout(new GridLayout(2, false));

            this.buttonShowGallery = new Button(group, SWT.CHECK);
            this.buttonShowGallery.setText("afisare galerie carti");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonShowGallery);
            WidgetCursorUtil.addHandCursorListener(this.buttonShowGallery);

            this.buttonAutorLink = new Button(group, SWT.CHECK);
            this.buttonAutorLink.setText("click suport pt autor");
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(this.buttonAutorLink);
            WidgetCursorUtil.addHandCursorListener(this.buttonAutorLink);

            new Label(group, SWT.NONE).setText("Motor cautare:");
            this.comboSearchEngine = new Combo(group, SWT.READ_ONLY);
            this.comboSearchEngine.setItems(SearchEngine.getComboItems());
            WidgetCursorUtil.addHandCursorListener(this.comboSearchEngine);
        }

        @Override
        public void populateFields() {
            this.buttonShowGallery.setSelection(SettingsController.getBoolean(PERSPECTIVE_SHOW_GALLERY));
            this.buttonAutorLink.setSelection(SettingsController.getBoolean(PERSPECTIVE_AUTHOR_LINKS));
            searchEngineConfig = SettingsController.getGeneralSetting("searchEngine");
            if (searchEngineConfig == null) {
                searchEngineConfig = new GeneralSetting();
                searchEngineConfig.setKey("searchEngine");
                searchEngineConfig.setValue(comboSearchEngine.indexOf("librarie.net"));
            }
            comboSearchEngine.select(Integer.valueOf(searchEngineConfig.getValue().toString()));
        }

        @Override
        public void save() {
            SettingsController.saveBooleanSetting(PERSPECTIVE_SHOW_GALLERY, buttonShowGallery.getSelection());
            SettingsController.saveBooleanSetting(PERSPECTIVE_AUTHOR_LINKS, buttonAutorLink.getSelection());
            searchEngineConfig.setValue(comboSearchEngine.getSelectionIndex());
            SettingsController.saveGeneralSetting(searchEngineConfig);
        }

        @Override
        public String getCatName() {
            return ITEM_MAIN_PERSPECTIVE;
        }
    }
}
