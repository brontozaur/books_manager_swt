package com.papao.books.model;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Document(collection = "autor")
public class Autor extends AuditObject implements Serializable {

    @Id
    private ObjectId id;

    @Indexed
    private String numeComplet = "";
    private int anNastere;
    private int lunaNastere;
    private int ziNastere;
    private int anDeces;
    private int lunaDeces;
    private int ziDeces;
    private DocumentData mainImage;
    private DocumentData[] documents;
    private List<GenLiterar> genLiterar;
    private String website;
    private String twitter;
    private String facebook;
    private String wiki;
    private String descriere;
    private String loculNasterii;
    private String tara;
    private String titlu;

    @Override
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNumeComplet() {
        if (this.numeComplet == null) {
            return "";
        }
        return numeComplet;
    }

    public String getNumeSiTitlu() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(numeComplet)) {
            stringBuilder.append(numeComplet);
        } else {
            stringBuilder.append("???");
        }
        if (StringUtils.isNotBlank(titlu)) {
            stringBuilder.append(", ").append(titlu);
        }
        return stringBuilder.toString();
    }

    public void setNumeComplet(String numeComplet) {
        this.numeComplet = numeComplet;
    }

    public int getAnNastere() {
        return anNastere;
    }

    public void setAnNastere(int anNastere) {
        this.anNastere = anNastere;
    }

    public int getLunaNastere() {
        return lunaNastere;
    }

    public void setLunaNastere(int lunaNastere) {
        this.lunaNastere = lunaNastere;
    }

    public int getZiNastere() {
        return ziNastere;
    }

    public void setZiNastere(int ziNastere) {
        this.ziNastere = ziNastere;
    }

    public int getAnDeces() {
        return anDeces;
    }

    public void setAnDeces(int anDeces) {
        this.anDeces = anDeces;
    }

    public int getLunaDeces() {
        return lunaDeces;
    }

    public void setLunaDeces(int lunaDeces) {
        this.lunaDeces = lunaDeces;
    }

    public int getZiDeces() {
        return ziDeces;
    }

    public void setZiDeces(int ziDeces) {
        this.ziDeces = ziDeces;
    }

    public DocumentData getMainImage() {
        return mainImage;
    }

    public void setMainImage(DocumentData mainImage) {
        this.mainImage = mainImage;
    }

    public DocumentData[] getDocuments() {
        return documents;
    }

    public void setDocuments(DocumentData[] documents) {
        this.documents = documents;
    }

    public String getWiki() {
        if (wiki == null) {
            return "";
        }
        return wiki;
    }

    public void setWiki(String wiki) {
        this.wiki = wiki;
    }

    public List<GenLiterar> getGenLiterar() {
        if (genLiterar == null) {
            return Collections.emptyList();
        }
        return genLiterar;
    }

    public void setGenLiterar(List<GenLiterar> genLiterar) {
        this.genLiterar = genLiterar;
    }

    public String getWebsite() {
        if (this.website == null) {
            return "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        if (this.twitter == null) {
            return "";
        }
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        if (this.facebook == null) {
            return "";
        }
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getDescriere() {
        if (this.descriere == null) {
            return "";
        }
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public AnLunaZiData getDataNasterii(boolean showLabels) {
        return new AnLunaZiData(this.anNastere, this.lunaNastere, this.ziNastere, showLabels);
    }

    public AnLunaZiData getDataMortii(boolean showLabels) {
        return new AnLunaZiData(this.anDeces, this.lunaDeces, this.ziDeces, showLabels);
    }

    public String getLoculNasterii() {
        if (loculNasterii == null) {
            return "";
        }
        return loculNasterii;
    }

    public void setLoculNasterii(String loculNasterii) {
        this.loculNasterii = loculNasterii;
    }

    public String getTara() {
        if (tara == null) {
            return "";
        }
        return tara;
    }

    public void setTara(String tara) {
        this.tara = tara;
    }

    public String getTitlu() {
        if (this.titlu == null) {
            return "";
        }
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }
}
