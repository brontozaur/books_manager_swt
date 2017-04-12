package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "carte")
public class Carte extends AbstractDB implements Serializable {

    public static String REPLACEMENT_FOR_NOT_SET = "###";

    @Id
    private String id;

    private List<String> autori;
    private String titlu;
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
    private String isbn;
    private boolean cuIlustratii;
    private List<String> autoriIlustratii;
    private List<String> tehnoredactori;
    private String imprimerie;
    private List<Personaj> personaje;
    private TipCoperta tipCoperta;
    private Limba limba;
    private Limba traducereDin;
    private Limba limbaOriginala;
    private List<String> distinctiiAcordate;
    private boolean cuAutograf;
    private String goodreadsUrl;
    private String wikiUrl;
    private GridFsImageData copertaFata;
    private GridFsImageData copertaSpate;
    private GridFsImageData autograf;

    List<InfoCititori> cititori = new ArrayList<>();

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

    public List<String> getAutori() {
        if (autori == null) {
            return new ArrayList<>();
        }
        return autori;
    }

    public void setAutori(List<String> autori) {
        this.autori = autori;
    }

    public String getNumeAutori(List<String> autori) {
        StringBuilder numeAutori = new StringBuilder();
        for (String autor : autori) {
            if (numeAutori.length() > 0) {
                numeAutori.append(", ");
            }
            numeAutori.append(autor);
        }
        return numeAutori.toString();
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
            return new ArrayList<>();
        }
        return traducatori;
    }

    public void setTraducatori(List<String> traducatori) {
        this.traducatori = traducatori;
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

    public boolean isCuIlustratii() {
        return cuIlustratii;
    }

    public void setCuIlustratii(boolean cuIlustratii) {
        this.cuIlustratii = cuIlustratii;
    }

    public List<String> getAutoriIlustratii() {
        if (autoriIlustratii == null) {
            return new ArrayList<>();
        }
        return autoriIlustratii;
    }

    public void setAutoriIlustratii(List<String> autoriIlustratii) {
        this.autoriIlustratii = autoriIlustratii;
    }

    public List<String> getTehnoredactori() {
        if (tehnoredactori == null) {
            return new ArrayList<>();
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
            return new ArrayList<>();
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

    public List<String> getDistinctiiAcordate() {
        if (distinctiiAcordate == null) {
            return new ArrayList<>();
        }
        return distinctiiAcordate;
    }

    public void setDistinctiiAcordate(List<String> distinctiiAcordate) {
        this.distinctiiAcordate = distinctiiAcordate;
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

    public List<InfoCititori> getCititori() {
        if (cititori == null) {
            return new ArrayList<>();
        }
        return cititori;
    }

    public GridFsImageData getCopertaFata() {
        if (copertaFata == null) {
            return new GridFsImageData();
        }
        return copertaFata;
    }

    public void setCopertaFata(GridFsImageData copertaFata) {
        this.copertaFata = copertaFata;
    }

    public GridFsImageData getCopertaSpate() {
        if (copertaSpate == null) {
            return new GridFsImageData();
        }
        return copertaSpate;
    }

    public void setCopertaSpate(GridFsImageData copertaSpate) {
        this.copertaSpate = copertaSpate;
    }

    public GridFsImageData getAutograf() {
        return autograf;
    }

    public void setAutograf(GridFsImageData autograf) {
        this.autograf = autograf;
    }

    public void setCititori(List<InfoCititori> cititori) {
        this.cititori = cititori;
    }
}
