package com.papao.books.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Document(collection = "carte")
public class Carte extends AbstractDB {

    @Id
    private String id;

    private List<String> autori = new ArrayList<>();
    private String titlu = "";
    private String editura = "";
    private String anAparitie = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    private String titluOriginal = "";
    private String editia = "";
    private int latime = 0;
    private int lungime = 0;
    private int greutate = 0;
    private int nrPagini = 0;
    private String serie = "";
    private List<String> traducatori = new ArrayList<>();
    private String isbn = "";
    private boolean cuIlustratii;
    private List<String>autorIlustratii = new ArrayList<>();
    private List<String>tehnoredactor = new ArrayList<>();
    private String tiparitLa = "";
    private List<Personaj> personaje = new ArrayList<>();
    private TipCoperta tipCoperta = TipCoperta.BROSATA;
    private Limba limba = Limba.Romana;
    private Limba traducereDin = Limba.Romana;
    private Limba limbaOriginala = Limba.Romana;
    private List<String> distinctiiAcordate = new ArrayList<>();
    private boolean autograf;

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
            return "#";
        }
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public List<String> getAutori() {
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
        return editura;
    }

    public void setEditura(String editura) {
        this.editura = editura;
    }

    public String getAnAparitie() {
        return anAparitie;
    }

    public void setAnAparitie(String anAparitie) {
        this.anAparitie = anAparitie;
    }

    public String getTitluOriginal() {
        return titluOriginal;
    }

    public void setTitluOriginal(String titluOriginal) {
        this.titluOriginal = titluOriginal;
    }

    public String getEditia() {
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

    public int getLungime() {
        return lungime;
    }

    public void setLungime(int lungime) {
        this.lungime = lungime;
    }

    public int getGreutate() {
        return greutate;
    }

    public void setGreutate(int greutate) {
        this.greutate = greutate;
    }

    public int getNrPagini() {
        return nrPagini;
    }

    public void setNrPagini(int nrPagini) {
        this.nrPagini = nrPagini;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public List<String> getTraducatori() {
        return traducatori;
    }

    public void setTraducatori(List<String> traducatori) {
        this.traducatori = traducatori;
    }

    public String getIsbn() {
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

    public List<String> getAutorIlustratii() {
        return autorIlustratii;
    }

    public void setAutorIlustratii(List<String> autorIlustratii) {
        this.autorIlustratii = autorIlustratii;
    }

    public List<String> getTehnoredactor() {
        return tehnoredactor;
    }

    public void setTehnoredactor(List<String> tehnoredactor) {
        this.tehnoredactor = tehnoredactor;
    }

    public String getTiparitLa() {
        return tiparitLa;
    }

    public void setTiparitLa(String tiparitLa) {
        this.tiparitLa = tiparitLa;
    }

    public List<Personaj> getPersonaje() {
        return personaje;
    }

    public void setPersonaje(List<Personaj> personaje) {
        this.personaje = personaje;
    }

    public TipCoperta getTipCoperta() {
        return tipCoperta;
    }

    public void setTipCoperta(TipCoperta tipCoperta) {
        this.tipCoperta = tipCoperta;
    }

    public Limba getLimba() {
        return limba;
    }

    public void setLimba(Limba limba) {
        this.limba = limba;
    }

    public Limba getTraducereDin() {
        return traducereDin;
    }

    public void setTraducereDin(Limba traducereDin) {
        this.traducereDin = traducereDin;
    }

    public Limba getLimbaOriginala() {
        return limbaOriginala;
    }

    public void setLimbaOriginala(Limba limbaOriginala) {
        this.limbaOriginala = limbaOriginala;
    }

    public List<String> getDistinctiiAcordate() {
        return distinctiiAcordate;
    }

    public void setDistinctiiAcordate(List<String> distinctiiAcordate) {
        this.distinctiiAcordate = distinctiiAcordate;
    }

    public boolean isAutograf() {
        return autograf;
    }

    public void setAutograf(boolean autograf) {
        this.autograf = autograf;
    }

    public List<InfoCititori> getCititori() {
        return cititori;
    }

    public void setCititori(List<InfoCititori> cititori) {
        this.cititori = cititori;
    }
}
