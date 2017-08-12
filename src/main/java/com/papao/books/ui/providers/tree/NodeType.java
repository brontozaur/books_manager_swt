package com.papao.books.ui.providers.tree;

public enum NodeType {

    STRING(""),
    ALL("Toate"),
    DAY("Zi"),
    MONTH("Lună"),
    YEAR("An"),

    FARA_IMAGINE("Fără imagine"),
    FARA_PRET("Fără preț"),
    FARA_DATA_CUMPARARII("Fără data cumpărării"),
    FARA_EDITURA("Fără editură"),
    FARA_TRADUCATOR("Fără traducător"),
    FARA_TIP_COPERTA("Fără tip copertă"),
    FARA_LOCATIE("Fără locație"),
    NECITITA("Necitită"),
    CITITA("Citită"),
    FARA_GEN_LITERAR("Fără gen literar"),
    FARA_TAGURI("Fără taguri"),
    FARA_ISBN("Fără ISBN"),
    FARA_AN_APARITIE("Fără an apariție"),
    CU_REVIEW("Cu review"),
    FARA_REVIEW("Fără review");;

    private String nodeName;

    NodeType(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
