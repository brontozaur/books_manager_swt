package com.papao.books.ui.bones.impl;

import com.papao.books.FiltruAplicatie;
import com.papao.books.model.AbstractDB;
import com.papao.books.ui.AppImages;
import com.papao.books.ui.bones.AbstractBone;
import com.papao.books.ui.bones.filter.AbstractBoneFilter;
import com.papao.books.ui.bones.impl.bones.ViewModeDetails;
import com.papao.books.ui.bones.impl.filters.AbstractFilterViewMode;
import com.papao.books.ui.providers.tree.SimpleTextNode;
import com.papao.books.util.BorgDateUtil;
import com.papao.books.util.Constants;
import com.papao.books.util.FontUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import java.lang.reflect.Method;
import java.util.*;

public final class BoneUtil {

    private BoneUtil() {
    }

    public static final String SPECIAL_AZ_STR = "#";

    private static final Logger logger = Logger.getLogger(BoneUtil.class);

    /**
     * @param startMap   a "raw" map of DbElements
     * @param clazz      some class extending AbstractDB
     * @param methodName some method of that class
     * @return a structured map, ready to be processed by a method that actually creates a tree structure of items, usually in the left corner of the component.
     */
    public static Map<Object, TreeMap<String, AbstractDB>> processDBElements(final Map<String, AbstractDB> startMap, final Class<? extends AbstractDB> clazz, final String methodName) {
        Map<Object, TreeMap<String, AbstractDB>> map = new TreeMap<>();
        AbstractDB adb;
        Method meth;
        try {
            if (startMap == null) {
                return map;
            }
            meth = clazz.getMethod(methodName, (Class<?>[]) null);
            if (meth == null) {
                throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " doesnt have the specified method [" + methodName + "]");
            }
            final Iterator<AbstractDB> iterStartMap = startMap.values().iterator();
            while (iterStartMap.hasNext()) {
                adb = iterStartMap.next();
                final Object currentKey = meth.invoke(adb, (Object[]) null);
                TreeMap<String, AbstractDB> temp = map.get(currentKey);
                if (temp == null) {
                    temp = new TreeMap();
                }
                temp.put(adb.getId(), adb);
                map.put(currentKey, temp);
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return new TreeMap<>();
        }
        return map;
    }

    /**
     * @param startMap   a "raw" map of DbElements
     * @param clazz      some class extending AbstractDB
     * @param methodName some method of that class
     * @return a structured map, based on letters, parsed by the string result of invocation of the specified method, having <code>methodName</code> name, ready to be processed by a method that
     * actually creates a tree structure of items, usually in the left corner of the component.
     */
    public static Map<Object, TreeMap<String, AbstractDB>> processDBElementsAZ(final Map<String, AbstractDB> startMap, final Class<? extends AbstractDB> clazz, final String methodName) {
        Map<Object, TreeMap<String, AbstractDB>> map = new TreeMap<>();
        AbstractDB adb;
        Method meth;
        try {
            if (startMap == null) {
                return map;
            }
            meth = clazz.getMethod(methodName, (Class<?>[]) null);
            if (meth == null) {
                throw new IllegalArgumentException("Class " + clazz.getCanonicalName() + " doesnt have the specified method [" + methodName + "]");
            }
            final Iterator<AbstractDB> iterStartMap = startMap.values().iterator();
            while (iterStartMap.hasNext()) {
                adb = iterStartMap.next();
                final Object value = meth.invoke(adb, (Object[]) null);
                String key;
                if (value instanceof String) {
                    String valueStr = (String) value;
                    if (StringUtils.isEmpty(valueStr)) {
                        key = BoneUtil.SPECIAL_AZ_STR;
                    } else if (!Character.isLetter(valueStr.charAt(0))) {
                        key = BoneUtil.SPECIAL_AZ_STR;
                    } else {
                        key = valueStr.substring(0, 1).toUpperCase();
                    }
                } else {
                    key = BoneUtil.SPECIAL_AZ_STR;
                }
                TreeMap<String, AbstractDB> temp = map.get(key);
                if (temp == null) {
                    temp = new TreeMap<String, AbstractDB>();
                }
                temp.put(adb.getId(), adb);
                map.put(key, temp);
            }

        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return new TreeMap<>();
        }
        return map;
    }


    /**
     * @param details an DTO containing all the necessary data to perform computations :
     *                <ol>
     *                <li><b>treeViewer</b> the viewer to set the resulted input to. Cannot be null, or his control disposed.</li>
     *                <li><b>clazz</b> base class extracted from db. Cannot be null.</li>
     *                <li><b>fkMethodName</b> Foreign key method name for the base class. Method must exists, value cannot be null.</li>
     *                <li><b>clazzFK</b> foreign key class, having the <code>id</code> as a property of <code>class</code>. Cannot be null.</li>
     *                <li><b>extraOptions</b> some extra query options, if we need to narrow the results. Can be null.</li>
     *                <li><b>methodName</b> the name of an existing public method of the <code>clazzFK</code> type. It will be used to render some fancy text as a tree item in the viewer, and as a root to
     *                associated X elements in the current node. Cannot be null, and the type MUST declare a public method with this name.</li>
     *                <li><b>imageMethodName</b> if specified, will be invoked on the FK class to extract an image from the current object. Can be null. If specified, the method must be declare in the
     *                <code>clazzFK </code> type.</li>
     *                <li><b>showNumbers</b> if specified, along with some fancy text extracted by the <code>methodName</code> method invocation, will appear a strange suffix like <code> (x) </code>,
     *                where x = nr of elements of the current node. This value doesnt really matter.</li>
     *                <li><b>FiltruAplicatie.isLeftTreeShowingAll()</b> if specified, the resulting nodes will not be attached directly to the invisible root node, but an extra node will be created as the
     *                SINGLE child of the invisible node, and all other nodes will descend from this node. This special, all-mighty node will have the same number of dbElements as the all-powerful
     *                invisible root node.</li>
     *                <li><b>cacheMap</b> By traversing the result of the {@link #processDBElements(Map, Class, String)}, we'll encounter collections of <code><Long(clazzFK), <Long, clazz>></code>
     *                elements. So far, so good. But the keys of the <code>clazzFK</code> elements maps to other db elements, not available in the raw, unsorted map. So, we must extract them. To avoid
     *                excessib db usage, a cache map MUST be specified, having the db object's ids as keys.</li>
     *                <li><b>globalImage</b> If the refered dbo doesnt have an image method associated, or that image is too big to be displayed in a grid, but for reasons yet to be understood we still
     *                wanna show an image to each node, this is the way. A null imageMethodName and a global image passed in the details. Will be ignored if an imageMethodName is specified. Finally, i
     *                have decided that items in the tree should always have an image, so {@link AppImages#IMG_LISTA} is supplied if none of the above.</li>
     *                </ol>
     */
    public static void populateTreeByLongFK(final ViewModeDetails details, final AbstractBone bone) {
        Map<Object, TreeMap<String, AbstractDB>> map;
        SimpleTextNode invisibleRoot;
        Map<String, AbstractDB> mapStart = new HashMap<>();
        TreeViewer treeViewer;
        String fkMethodName;
        String methodName;
        String imageMethodName;
        SimpleTextNode baseNode;
        Font font;
        Color color;
        Image globalImage;
        AbstractBoneFilter filter;
        if (details == null) {
            return;
        }
        treeViewer = details.getTreeViewer();
        fkMethodName = details.getFkMethodName();
        methodName = details.getUiMethodName();
        imageMethodName = details.getImageMethodName();
        font = details.getFont();
        color = details.getColor();
        globalImage = details.getGlobalImage();
        filter = bone.getFiltru();
        if ((treeViewer == null) || treeViewer.getControl().isDisposed()) {
            return;
        }

        SimpleTextNode root = (SimpleTextNode) treeViewer.getInput();
        if (root != null) {
            mapStart = root.getDbElements();
        } else {
//                mapStart = (Map<Long, AbstractDB>) Database.getAbstractDBObjectsWithLongKey(filter.getClassObject(), filter.parse());
        }
        map = BoneUtil.processDBElements(mapStart, filter.getClassObject(), fkMethodName);

        if ((map == null) || map.isEmpty()) {
            treeViewer.setInput(null);
            return;
        }

        invisibleRoot = new SimpleTextNode(null);
        invisibleRoot.setDbElements(mapStart);

        if (details.isAddRecentOpNode() && FiltruAplicatie.isLeftTreeShowRecentActivity()) {
            SimpleTextNode nodeRecent = new SimpleTextNode(invisibleRoot, SimpleTextNode.RECENT_NODE);
            nodeRecent.setDbElements(new HashMap<String, AbstractDB>());
            if (filter.isTreeShowingElementCount()) {
                nodeRecent.setName(SimpleTextNode.RECENT_NODE + nodeRecent.getItemCountStr());
            }
            nodeRecent.setImage(AppImages.getImage16(AppImages.IMG_HOME));
            invisibleRoot.add(nodeRecent);
        }

        if (details.isAddAllNode() && FiltruAplicatie.isLeftTreeShowingAll()) {
            SimpleTextNode allNode = new SimpleTextNode(ViewModeDetails.ALL_STR);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setFont(FontUtil.TAHOMA8_BOLD);
            allNode.getDbElements().putAll(mapStart);
            if (filter.isTreeShowingElementCount()) {
                allNode.setName(ViewModeDetails.ALL_STR + allNode.getItemCountStr());
            }
            invisibleRoot.add(allNode);
            baseNode = allNode;
        } else {
            baseNode = invisibleRoot;
        }

//            final Map<Long, ? extends AbstractDB> cacheMap = Database.getAbstractDBObjectsWithLongKey(details.getFkClass(), null);
        Map<Long, ? extends AbstractDB> cacheMap = new HashMap<>();
        final Iterator<Map.Entry<Object, TreeMap<String, AbstractDB>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Object, TreeMap<String, AbstractDB>> entry = iterator.next();
            final long idObject = ((Long) entry.getKey()).longValue();
            AbstractDB obj = cacheMap.get(idObject);
            String itemText = "";
            Image itemImage = null;
            if (obj != null) {
//                Object value = ObjectUtil.getMethod(details.getFkClass(), methodName).invoke(obj, (Object[]) null);
                Object value = "";
                if (value instanceof String) {
                    itemText = (String) value;
                } else {
                    itemText = value + "";
                }
                if (imageMethodName != null) {
                    Object image = "";
//                    Object image = ObjectUtil.getMethod(details.getFkClass(), imageMethodName).invoke(obj, (Object[]) null);
                    if (image instanceof Image) {
                        Image img = (Image) image;
                        if (!img.isDisposed()) {
                            itemImage = img;
                        }
                    }
                }
            }
            if ((imageMethodName == null) && (globalImage != null) && !globalImage.isDisposed()) {
                itemImage = globalImage;
            }
            if ((itemImage == null) || itemImage.isDisposed()) {
                itemImage = AppImages.getImage16(AppImages.IMG_LISTA);
            }
            if (StringUtils.isEmpty(itemText)) {
                itemText = Constants.NOT_AVAILABLE;
            }
            if (filter.isTreeShowingElementCount()) {
                itemText += " (" + entry.getValue().size() + ")";
            }

            SimpleTextNode node = new SimpleTextNode(itemText);
            node.setDbElements(entry.getValue());
            node.setDbElement(obj);
            if ((font != null) && !font.isDisposed()) {
                node.setFont(font);
            }
            if ((color != null) && !color.isDisposed()) {
                node.setForeground(color);
            }
            if (itemImage != null) {
                node.setImage(itemImage);
            }
            baseNode.add(node);
        }
        treeViewer.setInput(invisibleRoot);
    }

    public static void populateTreeByDateExt(final TreeViewer treeViewer, final AbstractBone bone, final String methodName) {
        Map<String, AbstractDB> mapStart = new HashMap<>();
        Map<Object, TreeMap<String, AbstractDB>> map;
        SimpleTextNode invisibleRoot;
        String keyAn; // cheie string de genul "2009"
        String keyLuna; // cheie string de genul "2009-10"
        String keyZi; // cheie string de genul "2009-10-12"
        StringBuilder sbItem = new StringBuilder();
        if ((treeViewer == null) || treeViewer.getControl().isDisposed()) {
            return;
        }
        AbstractBoneFilter filter = bone.getFiltru();

        SimpleTextNode root = (SimpleTextNode) treeViewer.getInput();
        if (root != null) {
            mapStart = root.getDbElements();
        } else {
//            mapStart = (Map<Long, AbstractDB>) Database.getAbstractDBObjectsWithLongKey(filter.getClassObject(), filter.parse());
        }
        map = BoneUtil.processDBElements(mapStart, filter.getClassObject(), methodName);

        if ((map == null) || map.isEmpty()) {
            treeViewer.setInput(null);
            return;
        }

        invisibleRoot = new SimpleTextNode(null);
        invisibleRoot.setDbElements(mapStart);

        if (FiltruAplicatie.isLeftTreeShowRecentActivity()) {
            SimpleTextNode nodeRecent = new SimpleTextNode(invisibleRoot, SimpleTextNode.RECENT_NODE);
            nodeRecent.setDbElements(new HashMap<String, AbstractDB>());
            if (filter.isTreeShowingElementCount()) {
                nodeRecent.setName(SimpleTextNode.RECENT_NODE + nodeRecent.getItemCountStr());
            }
            nodeRecent.setImage(AppImages.getImage16(AppImages.IMG_HOME));
            invisibleRoot.add(nodeRecent);
        }

        final Iterator<Map.Entry<Object, TreeMap<String, AbstractDB>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Object, TreeMap<String, AbstractDB>> entry = iterator.next();
            final java.sql.Date data = (java.sql.Date) entry.getKey();
            Calendar cal = Calendar.getInstance();
            cal.setTime(data);
            /**
             * verificare/creare item an
             */
            keyAn = String.valueOf(cal.get(Calendar.YEAR));
            SimpleTextNode anNode = invisibleRoot.getChildren(keyAn);
            if (anNode == null) {
                anNode = new SimpleTextNode(keyAn);
                anNode.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
                anNode.setFont(FontUtil.TAHOMA8_BOLD);
                invisibleRoot.add(anNode);
            }
            anNode.getDbElements().putAll(entry.getValue());

            sbItem.setLength(0);
            sbItem.append(keyAn);
            if (filter.isTreeShowingElementCount()) {
                anNode.setName(keyAn + anNode.getItemCountStr());
            }

            /**
             * verificare/creare nod luna pe nod an
             */
            String luna = String.valueOf(cal.get(Calendar.MONTH) + 1);
            if (luna.length() == 1) {
                luna = "0" + luna;
            }
            keyLuna = String.valueOf(cal.get(Calendar.YEAR)) + '-' + luna;

            SimpleTextNode lunaNode = anNode.getChildren(keyLuna);
            if (lunaNode == null) {
                lunaNode = new SimpleTextNode(keyLuna);
                lunaNode.setImage(AppImages.getImage16(AppImages.IMG_COLLAPSE));
                anNode.add(lunaNode);
            }
            lunaNode.getDbElements().putAll(entry.getValue());

            sbItem.setLength(0);
            if (filter.getTreeDateFormatIndex() == AbstractFilterViewMode.AFISARE_FULL) {
                sbItem.append(BorgDateUtil.getMonthInRO(cal, false));
            } else if (filter.getTreeDateFormatIndex() == AbstractFilterViewMode.AFISARE_TIP_NUME_SCURT) {
                sbItem.append(BorgDateUtil.getMonthInRO(cal, true));
            } else {
                sbItem.append(luna);
            }
            if (filter.isTreeShowingElementCount()) {
                sbItem.append(" (").append(lunaNode.getDbElements().size()).append(")");
            }
            lunaNode.setName(sbItem.toString());

            /**
             * verificare/creare nod zi pe nod luna
             */
            String ziua = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if (ziua.length() == 1) {
                ziua = "0" + ziua;
            }
            keyZi = String.valueOf(cal.get(Calendar.YEAR)) + '-' + luna + '-' + ziua;

            SimpleTextNode ziNode = lunaNode.getChildren(keyZi);
            if (ziNode == null) {
                ziNode = new SimpleTextNode(keyZi);
                ziNode.setImage(AppImages.getImage16(AppImages.IMG_ARROW_RIGHT));
                lunaNode.add(ziNode);
            }
            ziNode.getDbElements().putAll(entry.getValue());

            sbItem.setLength(0);
            sbItem.append(ziua);
            if (filter.getTreeDateFormatIndex() != AbstractFilterViewMode.AFISARE_TIP_NUMERIC) {
                sbItem.append("(").append(BorgDateUtil.getDayInRO(cal)).append(")");
            }
            if (filter.isTreeShowingElementCount()) {
                sbItem.append(" (").append(ziNode.getDbElements().size()).append(")");
            }
            ziNode.setName(sbItem.toString());
        }
        treeViewer.setInput(invisibleRoot);
    }

    public static void populateTreeByAZ(final TreeViewer treeViewer, final AbstractBoneFilter filter, final String methodName, final boolean addToti) {
        Map<String, AbstractDB> mapStart = new HashMap<>();
        Map<Object, TreeMap<String, AbstractDB>> map;
        SimpleTextNode invisibleRoot;
        String keyLetter; // cheie string de genul "A", "B", etc..prima litera a numelui
        StringBuilder sbItem = new StringBuilder();
        SimpleTextNode baseNode;
        if ((treeViewer == null) || treeViewer.getControl().isDisposed()) {
            return;
        }

        SimpleTextNode root = (SimpleTextNode) treeViewer.getInput();
        if (root != null) {
            mapStart = root.getDbElements();
        } else {
//            mapStart = (TreeMap<Long, AbstractDB>) Database.getAbstractDBObjectsWithLongKey(filter.getClassObject(), null);
        }
        map = BoneUtil.processDBElementsAZ(mapStart, filter.getClassObject(), methodName);

        if ((map == null) || map.isEmpty()) {
            treeViewer.setInput(null);
            return;
        }

        invisibleRoot = new SimpleTextNode(null);
        invisibleRoot.setDbElements(mapStart);

        if (FiltruAplicatie.isLeftTreeShowRecentActivity()) {
            SimpleTextNode nodeRecent = new SimpleTextNode(invisibleRoot, SimpleTextNode.RECENT_NODE);
            nodeRecent.setDbElements(new HashMap<String, AbstractDB>());
            if (filter.isTreeShowingElementCount()) {
                nodeRecent.setName(SimpleTextNode.RECENT_NODE + nodeRecent.getItemCountStr());
            }
            nodeRecent.setImage(AppImages.getImage16(AppImages.IMG_HOME));
            invisibleRoot.add(nodeRecent);
        }

        if (addToti) {
            SimpleTextNode allNode = new SimpleTextNode(ViewModeDetails.ALL_STR);
            allNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            allNode.setFont(FontUtil.TAHOMA8_BOLD);
            allNode.getDbElements().putAll(mapStart);
            if (filter.isTreeShowingElementCount()) {
                allNode.setName(ViewModeDetails.ALL_STR + allNode.getItemCountStr());
            }
            invisibleRoot.add(allNode);
            baseNode = allNode;
        } else {
            baseNode = invisibleRoot;
        }

        final Iterator<Map.Entry<Object, TreeMap<String, AbstractDB>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Object, TreeMap<String, AbstractDB>> entry = iterator.next();
            keyLetter = (String) entry.getKey();
            SimpleTextNode letterNode = new SimpleTextNode(keyLetter);
            letterNode.setImage(AppImages.getImage16(AppImages.IMG_LISTA));
            letterNode.setFont(FontUtil.TAHOMA8_BOLD);
            letterNode.getDbElements().putAll(entry.getValue());
            sbItem.setLength(0);
            sbItem.append(keyLetter);
            if (filter.isTreeShowingElementCount()) {
                letterNode.setName(keyLetter + letterNode.getItemCountStr());
            }
            baseNode.add(letterNode);
        }
        treeViewer.setInput(invisibleRoot);
    }
}
