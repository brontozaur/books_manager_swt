package com.papao.books.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Document(collection = "carte")
@CompoundIndexes({
        @CompoundIndex(name = "uniqueAutoriAndTitle", def = "{'idAutori': 1, 'titlu': 1}", unique = true)})
public class Carte extends AuditObject implements Serializable {

    public static String REPLACEMENT_FOR_NOT_SET = "";

    @Id
    private ObjectId id;

    @Indexed
    private List<ObjectId> idAutori;
    @Indexed
    private String titlu;
    private String subtitlu;
    private String editura;
    private String anAparitie;
    private String editia;
    private int latime;
    private int inaltime;
    private double greutate;
    private int nrPagini;
    private String serie;
    private String isbn;
    private List<String> autoriIlustratii;
    private List<String> tehnoredactori;
    private String imprimerie;
    private List<Personaj> personaje;
    private TipCoperta tipCoperta;
    private Limba limba;
    private List<PremiuLiterar> premii;
    private String goodreadsUrl;
    private String wikiUrl;
    private String website;
    private List<GenLiterar> genLiterar;
    private DocumentData copertaFata;
    private DocumentData copertaSpate;
    private DocumentData autograf;
    private List<DocumentData> documents = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private String descriere;
    private String motto;
    private EditiaOriginala editiaOriginala;
    private List<String> traducatori;
    private Limba traducereDin;

    @Override
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public List<PremiuLiterar> getPremii() {
        if (premii == null) {
            return Collections.emptyList();
        }
        return premii;
    }

    public void setPremii(List<PremiuLiterar> premii) {
        this.premii = premii;
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
        if (autograf == null) {
            autograf = new DocumentData();
        }
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

    public List<ObjectId> getIdAutori() {
        if (idAutori == null) {
            return Collections.emptyList();
        }
        return idAutori;
    }

    public void setIdAutori(List<ObjectId> idAutori) {
        this.idAutori = idAutori;
    }

    public void setGenLiterar(List<GenLiterar> genLiterar) {
        this.genLiterar = genLiterar;
    }

    public String getSubtitlu() {
        if (subtitlu == null) {
            return "";
        }
        return subtitlu;
    }

    public void setSubtitlu(String subtitlu) {
        this.subtitlu = subtitlu;
    }

    public String getWebsite() {
        if (website == null) {
            return "";
        }
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
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
        if (descriere == null) {
            return "";
        }
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getMotto() {
        if (motto == null) {
            return "";
        }
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public EditiaOriginala getEditiaOriginala() {
        if (this.editiaOriginala == null) {
            this.editiaOriginala = new EditiaOriginala();
        }
        return editiaOriginala;
    }

    public void setEditiaOriginala(EditiaOriginala editiaOriginala) {
        this.editiaOriginala = editiaOriginala;
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

    public Limba getTraducereDin() {
        if (traducereDin == null) {
            return Limba.Nespecificat;
        }
        return traducereDin;
    }

    public void setTraducereDin(Limba traducereDin) {
        this.traducereDin = traducereDin;
    }

    public String getIsbn() {
        if (isbn == null) {
            return "";
        }
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void initCopy() {
        id = null;
        copertaFata = null;
        copertaSpate = null;
        autograf = null;
        documents = new ArrayList<>();
    }
}
