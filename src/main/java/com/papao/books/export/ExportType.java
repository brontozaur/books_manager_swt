package com.papao.books.export;

import com.papao.books.ui.AppImages;

public enum ExportType {

    PDF(AppImages.IMG_ADOBE),
    TXT(AppImages.IMG_EXPORT),
    RTF(AppImages.IMG_WORD2),
    HTML(AppImages.IMG_BROWSER),
    XLS(AppImages.IMG_EXCEL);

    private String image;

    ExportType(String image) {
        this.image = image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
