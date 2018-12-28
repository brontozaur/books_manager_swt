package com.papao.books.ui.providers;

import com.papao.books.controller.SettingsController;
import com.papao.books.ui.providers.tree.ITreeNode;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * See <a href=http://www.vogella.de/articles/EclipseJFaceTable/ar01s09.html> this page.</a>
 * This implementation can be used in two ways:
 * <ul>
 * <li>For {@link TreeViewer} usually the beans from the viewer input will implement {@link ITreeNode} and therefore all node properties (text, font, image, color, etc) can be specified on each
 * node. See {@link } for example. This is required to make possible the diversity of fonts, images, foregrounds, etc in a tree.</li>
 * <li>On the other hand, for a {@link TableViewer}, the objects tend to have a more predictible behaviour, and therefore, there is no need for the bean to implement {@link ITreeNode}. Instead,
 * the {@link #getText(Object)}, {@link #getImage(Object)}, {@link #getFont(Object)}, {@link #getBackground(Object)} and {@link #getForeground(Object)} can be overriden for each table column, and
 * also, for each table column, an instance of this class must be specified as label provider. Usually for tables, a {@link ColumnLabelProvider} is specified, but this provider is more advanced,
 * allowing the highlight of text occurences in the table columns.</li>
 * </ul>
 */
public class UnifiedStyledLabelProvider extends StyledCellLabelProvider implements IFontProvider, IColorProvider, ILabelProvider {
    private String searchText;

    public void setSearchText(final String searchText) {
        if ((searchText == null) || searchText.equals("<filtrare>")) {
            this.searchText = "";
        } else {
            this.searchText = searchText;
        }
    }

    @Override
    public void update(final ViewerCell cell) {
        String text = "";
        if (cell.getElement() instanceof ITreeNode) {
            ITreeNode node = (ITreeNode) cell.getElement();
            cell.setText(node.getName());
            text = node.getName();
            cell.setImage(node.getImage());
            cell.setForeground(node.getForeground());
            cell.setBackground(node.getBackground());
            cell.setFont(node.getFont());
        } else {
            cell.setText(getText(cell.getElement()));
            text = getText(cell.getElement());
            cell.setImage(getImage(cell.getElement()));
            cell.setForeground(getForeground(cell.getElement()));
            cell.setBackground(getBackground(cell.getElement()));
            cell.setFont(getFont(cell.getElement()));
        }
        if (StringUtils.isNotEmpty(this.searchText)) {
            int intRangesCorrectSize[] = UnifiedStyledLabelProvider.getSearchTermOccurrences(this.searchText, text);
            List<StyleRange> styleRange = new ArrayList<StyleRange>();
            for (int i = 0; i < intRangesCorrectSize.length / 2; i++) {
                StyleRange myStyleRange = new StyleRange(0, 0, null, null);
                myStyleRange.start = intRangesCorrectSize[i];
                myStyleRange.length = intRangesCorrectSize[++i];
//					myStyleRange.font = FontUtil.TAHOMA12_NORMAL;
                myStyleRange.background = SettingsController.getHighlightColor();
                styleRange.add(myStyleRange);
            }
            cell.setStyleRanges(styleRange.toArray(new StyleRange[styleRange.size()]));
        } else {
            cell.setStyleRanges(null);
        }

        super.update(cell);

    }

    /**
     * Searches "searchTerm" in "content" and returns an array of int pairs (index, length) for each occurrence. The search is case-sensitive. The consecutive occurrences are merged together.<code>
     * Examples:
     * content = "123123x123"
     * searchTerm = "1"
     * --> [0, 1, 3, 1, 7, 1]
     * content = "123123x123"
     * searchTerm = "123"
     * --> [0, 6, 7, 3]
     * </code>
     *
     * @param searchTerm can be null or empty. int[0] is returned in this case!
     * @param content    a not-null string (can be empty!)
     * @return an array of int pairs (index, length)
     */
    public static int[] getSearchTermOccurrences(final String searchTerm, final String content) {
        if (content == null || searchTerm == null) {
            return new int[0];
        }
        List<StyleRange> styleRange;
        List<Integer> ranges;
        StyleRange myStyleRange = new StyleRange(0, 0, null, SettingsController.getHighlightColor());

		/*
         * reset the StyleRange-Array for each new field
		 */
        styleRange = new ArrayList<StyleRange>();
		/*
		 * reset the ranges-array empty search term ==> return an empty StyleRange array
		 */
        ranges = new ArrayList<Integer>();
        if (StringUtils.isEmpty(searchTerm)) {
            return new int[]{};
        }

		/*
		 * determine all occurrences of the searchText and write the beginning and length of each occurrence into an array
		 */
        for (int i = 0; i < content.length(); i++) {
            if ((i + searchTerm.length() <= content.length()) && content.substring(i, i + searchTerm.length()).equalsIgnoreCase(searchTerm)) {
				/*
				 * ranges format: n->start of the range, n+1->length of the range
				 */
                ranges.add(i);
                ranges.add(searchTerm.length());
            }
        }
		/*
		 * convert the list into an int[] and make sure that overlapping search term occurrences are are merged
		 */
        int[] intRanges = new int[ranges.size()];
        int arrayIndexCounter = 0;
        for (int listIndexCounter = 0; listIndexCounter < ranges.size(); listIndexCounter++) {
            if (listIndexCounter % 2 == 0) {
                if ((searchTerm.length() > 1) && (listIndexCounter != 0) && (ranges.get(listIndexCounter - 2) + ranges.get(listIndexCounter - 1) >= ranges.get(listIndexCounter))) {
                    intRanges[arrayIndexCounter - 1] = 0 - ranges.get(listIndexCounter - 2) + ranges.get(listIndexCounter) + ranges.get(++listIndexCounter);
                } else {
                    intRanges[arrayIndexCounter++] = ranges.get(listIndexCounter);
                }
            } else {
                intRanges[arrayIndexCounter++] = ranges.get(listIndexCounter);
                styleRange.add(myStyleRange);
            }
        }
		/*
		 * if there have been any overlappings we need to reduce the size of the array to avoid conflicts in the setStyleRanges method
		 */
        int[] intRangesCorrectSize = new int[arrayIndexCounter];
        System.arraycopy(intRanges, 0, intRangesCorrectSize, 0, arrayIndexCounter);

        return intRangesCorrectSize;
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        return null;
    }

    @Override
    public Color getForeground(Object element) {
        return null;
    }

    @Override
    public Color getBackground(Object element) {
        return null;
    }

    @Override
    public Font getFont(Object element) {
        return null;
    }

}
