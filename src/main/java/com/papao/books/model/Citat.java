package com.papao.books.model;

import com.papao.books.ui.custom.ComboElement;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

public class Citat {

    private String nrPagina;
    private String content;

    @CreatedDate
    private String createdAt;

    @LastModifiedDate
    private String lastModifiedAt;
    private String lastModifiedBy;

    public String getContent() {
        if (content == null) {
            return "";
        }
        return content;
    }

    public String getNrPagina() {
        if (nrPagina == null) {
            return "";
        }
        return nrPagina;
    }

    public void setNrPagina(String nrPagina) {
        this.nrPagina = nrPagina;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(String lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Citat)) {
            return false;
        }
        if (this.getCreatedAt() == null) {
            return getContent().equals(((Citat) obj).getContent());
        }
        return this.getCreatedAt().equals(((Citat) obj).getCreatedAt()) && ((Citat) obj).getContent().equals(getContent());
    }
}
