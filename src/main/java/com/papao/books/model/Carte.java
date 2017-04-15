package com.papao.books.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Document(collection = "carte")
public class Carte extends AbstractDB implements Serializable {

    public static String REPLACEMENT_FOR_NOT_SET = "";

    @Id
    private String id;

    private List<String> idAutori;
    private String titlu;
    private String subtitlu;
    private String editura;
    private String anAparitie;
    private String titluOriginal;
    private String editia;
    private int latime;
    private int inaltime;
    private double greutate;
    private int nrPagini;
    private String serie;
    private List<String> traducatori;
    private String isbn10;
    private String isbn13;
    private String asin;
    private boolean cuIlustratii;
    private List<String> autoriIlustratii;
    private List<String> tehnoredactori;
    private String imprimerie;
    private List<Personaj> personaje;
    private TipCoperta tipCoperta;
    private Limba limba;
    private Limba traducereDin;
    private Limba limbaOriginala;
    private List<PremiuLiterar> premii;
    private boolean cuAutograf;
    private String goodreadsUrl;
    private String wikiUrl;
    private String website;
    private List<GenLiterar> genLiterar;
    private DocumentData copertaFata;
    private DocumentData copertaSpate;
    private DocumentData autograf;
    private DocumentData fotoCoperta;
    private List<DocumentData> documents;
    private List<String> tags;
    private String descriere;
    private String anPrimaEditie;
    private TipCarte tipCarte;
    private String motto;
// ------ user specific data ------
    private List<Citat> citate;
    private List<CarteCitita> carteCitita;
    private List<BookRating> notaCarte;
    private List<TranslationRating> translationRatings;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date lastModifiedAt;

    private String lastModifiedBy;

    private String createdBy;
    private String updatedBy;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitlu() {
        if (titlu == null) {
            return "";
        }
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getEditura() {
        if (editura == null) {
            return "";
        }
        return editura;
    }

    public void setEditura(String editura) {
        this.editura = editura;
    }

    public String getAnAparitie() {
        if (anAparitie == null) {
            return "";
        }
        return anAparitie;
    }

    public void setAnAparitie(String anAparitie) {
        this.anAparitie = anAparitie;
    }

    public String getTitluOriginal() {
        if (titluOriginal == null) {
            return "";
        }
        return titluOriginal;
    }

    public void setTitluOriginal(String titluOriginal) {
        this.titluOriginal = titluOriginal;
    }

    public String getEditia() {
        if (editia == null) {
            return "";
        }
        return editia;
    }

    public void setEditia(String editia) {
        this.editia = editia;
    }

    public int getLatime() {
        return latime;
    }

    public void setLatime(int latime) {
        this.latime = latime;
    }

    public int getInaltime() {
        return inaltime;
    }

    public void setInaltime(int inaltime) {
        this.inaltime = inaltime;
    }

    public double getGreutate() {
        return greutate;
    }

    public void setGreutate(double greutate) {
        this.greutate = greutate;
    }

    public int getNrPagini() {
        return nrPagini;
    }

    public void setNrPagini(int nrPagini) {
        this.nrPagini = nrPagini;
    }

    public String getSerie() {
        if (serie == null) {
            return "";
        }
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public List<String> getTraducatori() {
        if (traducatori == null) {
            return Collections.emptyList();
        }
        return traducatori;
    }

    public void setTraducatori(List<String> traducatori) {
        this.traducatori = traducatori;
    }

    public String getIsbn10() {
        if (isbn10 == null) {
            return "";
        }
        return isbn10;
    }

    public void setIsbn10(String isbn10) {
        this.isbn10 = isbn10;
    }

    public boolean isCuIlustratii() {
        return cuIlustratii;
    }

    public void setCuIlustratii(boolean cuIlustratii) {
        this.cuIlustratii = cuIlustratii;
    }

    public List<String> getAutoriIlustratii() {
        if (autoriIlustratii == null) {
            return Collections.emptyList();
        }
        return autoriIlustratii;
    }

    public void setAutoriIlustratii(List<String> autoriIlustratii) {
        this.autoriIlustratii = autoriIlustratii;
    }

    public List<String> getTehnoredactori() {
        if (tehnoredactori == null) {
            return Collections.emptyList();
        }
        return tehnoredactori;
    }

    public void setTehnoredactori(List<String> tehnoredactori) {
        this.tehnoredactori = tehnoredactori;
    }

    public String getImprimerie() {
        if (imprimerie == null) {
            return "";
        }
        return imprimerie;
    }

    public void setImprimerie(String imprimerie) {
        this.imprimerie = imprimerie;
    }

    public List<Personaj> getPersonaje() {
        if (personaje == null) {
            return Collections.emptyList();
        }
        return personaje;
    }

    public void setPersonaje(List<Personaj> personaje) {
        this.personaje = personaje;
    }

    public TipCoperta getTipCoperta() {
        if (tipCoperta == null) {
            return TipCoperta.Nespecificat;
        }
        return tipCoperta;
    }

    public void setTipCoperta(TipCoperta tipCoperta) {
        this.tipCoperta = tipCoperta;
    }

    public Limba getLimba() {
        if (limba == null) {
            return Limba.Nespecificat;
        }
        return limba;
    }

    public void setLimba(Limba limba) {
        this.limba = limba;
    }

    public Limba getTraducereDin() {
        if (traducereDin == null) {
            return Limba.Nespecificat;
        }
        return traducereDin;
    }

    public void setTraducereDin(Limba traducereDin) {
        this.traducereDin = traducereDin;
    }

    public Limba getLimbaOriginala() {
        if (limbaOriginala == null) {
            return Limba.Nespecificat;
        }
        return limbaOriginala;
    }

    public void setLimbaOriginala(Limba limbaOriginala) {
        this.limbaOriginala = limbaOriginala;
    }

    public List<PremiuLiterar> getPremii() {
        if (premii == null) {
            return Collections.emptyList();
        }
        return premii;
    }

    public void setPremii(List<PremiuLiterar> premii) {
        this.premii = premii;
    }

    public boolean isCuAutograf() {
        return cuAutograf;
    }

    public void setCuAutograf(boolean cuAutograf) {
        this.cuAutograf = cuAutograf;
    }

    public String getGoodreadsUrl() {
        if (goodreadsUrl == null) {
            goodreadsUrl = "";
        }
        return goodreadsUrl;
    }

    public void setGoodreadsUrl(String goodreadsUrl) {
        this.goodreadsUrl = goodreadsUrl;
    }

    public String getWikiUrl() {
        if (wikiUrl == null) {
            wikiUrl = "";
        }
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public DocumentData getCopertaFata() {
        if (copertaFata == null) {
            return new DocumentData();
        }
        return copertaFata;
    }

    public void setCopertaFata(DocumentData copertaFata) {
        this.copertaFata = copertaFata;
    }

    public DocumentData getCopertaSpate() {
        if (copertaSpate == null) {
            return new DocumentData();
        }
        return copertaSpate;
    }

    public void setCopertaSpate(DocumentData copertaSpate) {
        this.copertaSpate = copertaSpate;
    }

    public DocumentData getAutograf() {
        return autograf;
    }

    public void setAutograf(DocumentData autograf) {
        this.autograf = autograf;
    }

    public List<GenLiterar> getGenLiterar() {
        if (genLiterar == null) {
            return Collections.emptyList();
        }
        return genLiterar;
    }

    public List<String> getIdAutori() {
        if (idAutori == null) {
            return Collections.emptyList();
        }
        return idAutori;
    }

    public void setIdAutori(List<String> idAutori) {
        this.idAutori = idAutori;
    }

    public void setGenLiterar(List<GenLiterar> genLiterar) {
        this.genLiterar = genLiterar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastModifiedAt() {
        return lastModifiedAt;
    }

    public void setLastModifiedAt(Date lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSubtitlu() {
        return subtitlu;
    }

    public void setSubtitlu(String subtitlu) {
        this.subtitlu = subtitlu;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public DocumentData getFotoCoperta() {
        return fotoCoperta;
    }

    public void setFotoCoperta(DocumentData fotoCoperta) {
        this.fotoCoperta = fotoCoperta;
    }

    public List<DocumentData> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentData> documents) {
        this.documents = documents;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getAnPrimaEditie() {
        return anPrimaEditie;
    }

    public void setAnPrimaEditie(String anPrimaEditie) {
        this.anPrimaEditie = anPrimaEditie;
    }

    public TipCarte getTipCarte() {
        return tipCarte;
    }

    public void setTipCarte(TipCarte tipCarte) {
        this.tipCarte = tipCarte;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public List<Citat> getCitate() {
        return citate;
    }

    public void setCitate(List<Citat> citate) {
        this.citate = citate;
    }

    public List<CarteCitita> getCarteCitita() {
        return carteCitita;
    }

    public void setCarteCitita(List<CarteCitita> carteCitita) {
        this.carteCitita = carteCitita;
    }

    public List<BookRating> getNotaCarte() {
        return notaCarte;
    }

    public void setNotaCarte(List<BookRating> notaCarte) {
        this.notaCarte = notaCarte;
    }

    public List<TranslationRating> getTranslationRatings() {
        return translationRatings;
    }

    public void setTranslationRatings(List<TranslationRating> translationRatings) {
        this.translationRatings = translationRatings;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
}
