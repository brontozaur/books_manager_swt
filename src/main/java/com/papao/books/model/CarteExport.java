package com.papao.books.model;

import com.papao.books.ApplicationService;

import java.util.Arrays;

public class CarteExport {

    private String id;
    private String titlu;
    private String autori;
    private String subtitlu;
    private String editura;
    private String anAparitie;
    private String editia;
    private String latime;
    private String inaltime;
    private String greutate;
    private String nrPagini;
    private String serie;
    private String isbn;
    private String autoriIlustratii;
    private String tehnoredactori;
    private String imprimerie;
    private String personaje;
    private String tipCoperta;
    private String limba;
    private String premii;
    private String goodreadsUrl;
    private String wikiUrl;
    private String website;
    private String genLiterar;

    private String tags;
    private String descriere;
    private String motto;
    private String editiaOriginala;
    private String traducatori;
    private String traducereDin;
    private String locatie;

    public CarteExport(Carte carte) {
        this.id = carte.getId() != null ? carte.getId().toString() : "";
        this.titlu = carte.getTitlu();
        this.autori = ApplicationService.getBookController().getBookAuthorNamesOrderByNumeComplet(carte);
        this.subtitlu = carte.getSubtitlu();
        this.editia = carte.getEditia();
        this.editura = carte.getEditura();
        this.anAparitie = carte.getAnAparitie();
        this.latime = carte.getLatime() + "";
        this.inaltime = carte.getInaltime() + "";
        this.greutate = carte.getGreutate() + "";
        this.nrPagini = carte.getNrPagini() + "";
        this.serie = carte.getSerie().getFormattedValue();
        this.isbn = carte.getIsbn();
        this.autoriIlustratii = Arrays.deepToString(carte.getAutoriIlustratii().toArray());
        this.tehnoredactori = Arrays.deepToString(carte.getTehnoredactori().toArray());
        this.imprimerie = carte.getImprimerie();
        this.personaje = Arrays.deepToString(carte.getPersonaje().toArray());
        this.tipCoperta = carte.getTipCoperta().name();
        this.limba = carte.getLimba().name();
        this.premii = Arrays.deepToString(carte.getPremii().toArray());
        this.goodreadsUrl = carte.getGoodreadsUrl();
        this.wikiUrl = carte.getWikiUrl();
        this.website = carte.getWebsite();
        this.genLiterar = Arrays.deepToString(carte.getGenLiterar().toArray());
        this.tags = Arrays.deepToString(carte.getTags().toArray());
        this.descriere = carte.getDescriere();
        this.motto = carte.getMotto();
        this.editiaOriginala = carte.getEditiaOriginala().toString();
        this.traducatori = Arrays.deepToString(carte.getTraducatori().toArray());
        this.traducereDin = carte.getTraducereDin().name();
        this.locatie = carte.getLocatie();
    }

    public String getId() {
        return id;
    }

    public String getTitlu() {
        return titlu;
    }

    public String getAutori() {
        return autori;
    }

    public String getSubtitlu() {
        return subtitlu;
    }

    public String getEditura() {
        return editura;
    }

    public String getAnAparitie() {
        return anAparitie;
    }

    public String getEditia() {
        return editia;
    }

    public String getLatime() {
        return latime;
    }

    public String getInaltime() {
        return inaltime;
    }

    public String getGreutate() {
        return greutate;
    }

    public String getNrPagini() {
        return nrPagini;
    }

    public String getSerie() {
        return serie;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAutoriIlustratii() {
        return autoriIlustratii;
    }

    public String getTehnoredactori() {
        return tehnoredactori;
    }

    public String getImprimerie() {
        return imprimerie;
    }

    public String getPersonaje() {
        return personaje;
    }

    public String getTipCoperta() {
        return tipCoperta;
    }

    public String getLimba() {
        return limba;
    }

    public String getPremii() {
        return premii;
    }

    public String getGoodreadsUrl() {
        return goodreadsUrl;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public String getWebsite() {
        return website;
    }

    public String getGenLiterar() {
        return genLiterar;
    }

    public String getTags() {
        return tags;
    }

    public String getDescriere() {
        return descriere;
    }

    public String getMotto() {
        return motto;
    }

    public String getEditiaOriginala() {
        return editiaOriginala;
    }

    public String getTraducatori() {
        return traducatori;
    }

    public String getTraducereDin() {
        return traducereDin;
    }

    public String getLocatie() {
        return locatie;
    }
}
