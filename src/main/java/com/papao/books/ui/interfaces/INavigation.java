/**
 * 
 */
package com.papao.books.ui.interfaces;

/**
 * Every
 * class that implements this must set some action performed on the Next and Forward tool
 * items..after the GUI has drawed.Also, the caller must include showNavigation =
 * true, so the buttons are actually drawed on the view.
 */
public interface INavigation {

    void goBack();

    void goForward();

}
