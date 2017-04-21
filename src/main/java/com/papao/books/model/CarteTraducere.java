package com.papao.books.model;

import java.util.Collections;
import java.util.List;

public class CarteTraducere {

    private List<String> traducatori;
    private Limba traducereDin;
    private CalitateTraducere calitateTraducere;

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

    public CalitateTraducere getCalitateTraducere() {
        if (this.calitateTraducere == null) {
            return CalitateTraducere.Nespecificat;
        }
        return calitateTraducere;
    }

    public void setCalitateTraducere(CalitateTraducere calitateTraducere) {
        this.calitateTraducere = calitateTraducere;
    }
}
