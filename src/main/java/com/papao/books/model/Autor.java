package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "autor")
public class Autor extends AbstractDB implements Serializable {

    @Id
    private String id;

    private String numeComplet;
    private Date dataNastere;
    private Date dataMortii;
    private DocumentData mainImage;
    private DocumentData[] documents;
    private GenLiterar genLiterar;
    private String website;
    private String twitter;
    private String facebook;
    private String descriere;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeComplet() {
        return numeComplet;
    }

    public void setNumeComplet(String numeComplet) {
        this.numeComplet = numeComplet;
    }

    public Date getDataNastere() {
        return dataNastere;
    }

    public void setDataNastere(Date dataNastere) {
        this.dataNastere = dataNastere;
    }

    public Date getDataMortii() {
        return dataMortii;
    }

    public void setDataMortii(Date dataMortii) {
        this.dataMortii = dataMortii;
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

    public GenLiterar getGenLiterar() {
        if (genLiterar == null) {
            return GenLiterar.NESPECIFICAT;
        }
        return genLiterar;
    }

    public void setGenLiterar(GenLiterar genLiterar) {
        this.genLiterar = genLiterar;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }
}
