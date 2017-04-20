package com.papao.books.view.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Clasa contine metode de validare a unor coduri cum ar fi : CNP, CIF, IBAN, ISBN, etc.
 */
public final class ValidareCoduri {

	private final static Logger logger = Logger.getLogger(ValidareCoduri.class);

    private ValidareCoduri() {}

    /**
     * @param cnp
     *            sirul de caractere presupus a fi un CNP valid
     * @return da sau ba, daca respecta algoritmul de validare de la http://www.validari.ro/cnp
     *         <p>
     *         Codul Numeric Personal constituie numarul de ordine atribuit de Evidenta Populatiei unui individ la nastere. Conform articolului 5 din
     *         Legea nr.105 din 25 septembrie 1996 privind evidenta populatiei si cartea de identitate, fiecarei persoane fizice i se atribuie,
     *         incepand de la nastere, un cod numeric personal care se inscrie in actele si certificatele de stare civila si se preia in celelalte
     *         acte cu caracter oficial, emise pe numele persoanei respective, precum si in Registrul permanent de evidenta a populatiei. Codul
     *         numeric personal reprezinta un numar semnificativ ce individualizeaza o persoana fizica si constituie singurul identificator pentru
     *         toate sistemele informatice care prelucreaza date nominale privind persoana fizica. Gestionarea si verificarea atribuirii codului
     *         numeric personal revine Ministerului de Interne, prin formatiunile de evidenta a populatiei. Pentru persoanele fizice romane cu
     *         domiciliul in Romania codul de identificare fiscala este codul numeric personal atribuit de Ministerul de Interne.Persoanele fizice
     *         straine si persoanele fizice romane fara domiciliu in Romania vor beneficia de numar de identificare fiscala (NIF).
     *         <p>
     *         Un CNP este alcatuit astfel :
     *         <p>
     *         |S| |AA| |LL| |ZZ| |JJ| |ZZZ| |C|
     *         </p>
     *         <ol>
     *         <li>--> Cifra sexului (M/F) pentru: *
     *         <ul>
     *         <li>1/2 - cetateni romani nascuti intre 1 ian 1900 si 31 dec 1999</li>
     *         <li>3/4 - cetateni romani nascuti intre 1 ian 1800 si 31 dec 1899</li>
     *         <li>5/6 - cetateni romani nascuti intre 1 ian 2000 si 31 dec 2099</li>
     *         <li>7/8 - rezidenti</li>
     *         <li>Persoanele de cetatenie straina se identifica cu cifra "9"</li>
     *         </ul>
     *         </li>
     *         <li>--> Anul nasterii</li>
     *         <li>--> Luna nasterii</li>
     *         <li>--> Ziua nasterii</li>
     *         <li>--> Codul judetului</li>
     *         <li>--> Numarul de ordine atribuit persoanei</li>
     *         <li>--> Cifra de control</li>
     *         </ol>
     *         </p>
     *         <ul>
     *         <li>Algoritmul de validare al unui cod CNP</li>
     *         <li>Pas preliminar: Se testeaza daca codul respecta formatul unui cod CNP. Adica prima cifra sa fie cuprinsa in intervalul 1 - 6 sau sa
     *         fie 9 pentru straini. Urmatoarele sase cifre trebuie sa constituie o data calendaristica valida in formatul AALLZZ.</li>
     *         <li>Pas 1: Se foloseste cheia de testare "279146358279". Primele douasprezece cifre se inmultesc pe rand de la stanga spre dreapta cu
     *         cifra corespunzatoare din cheia de testare.</li>
     *         <li>Pas 2: Cele douasprezece produse obtinute se aduna si suma obtinuta se imparte la 11.</li>
     *         <li>
     *         <ul>
     *         <li>Daca restul impartirii la 11 este mai mic ca 10, atunci acesta va reprezenta cifra de control.</li>
     *         <li>Daca restul impartirii este 10 atunci cifra de control este 1.</li>
     *         </ul>
     *         <li>Pentru un CNP valid cifra de control va trebui sa coincida cu cifra de pe pozitia treisprezece din CNP-ul initial.</li>
     *         </ul>
     */
    public static boolean validareCNP(final String cnp) {
        // 279146358279 - cheie de verificare.
        final int[] cheieTestare = new int[] {
                2, 7, 9, 1, 4, 6, 3, 5, 8, 2, 7, 9 };
        try {
			if (StringUtils.isEmpty(cnp) || (cnp.length() != 13)) {
                return false;
            }
            char[] cnpArray = cnp.toCharArray();
            int[] cifreCNP = new int[cnpArray.length];
            for (int i = 0; i < cnpArray.length; i++) {
                if (Character.isDigit(cnpArray[i])) {
                    cifreCNP[i] = Integer.parseInt(String.valueOf(cnpArray[i]));
                    continue;
                }
                return false;
            }
            if ((cifreCNP[0] < 1) || ((cifreCNP[0] > 6) && (cifreCNP[0] != 9))) {
                return false;
            }
            StringBuilder sb = new StringBuilder();

            sb.append(cnpArray[1]);
            sb.append(cnpArray[2]);
            sb.append("-");
            sb.append(cnpArray[3]);
            sb.append(cnpArray[4]);
            sb.append("-");
            sb.append(cnpArray[5]);
            sb.append(cnpArray[6]);

            try {
                new SimpleDateFormat("yy-MM-dd").parse(sb.toString());
            } catch (ParseException exc) {
				logger.warn(exc);
                return false;
            }

            final int[] validator = new int[cifreCNP.length - 1];
            int sum = 0;
            for (int i = 0; i < cheieTestare.length; i++) {
                validator[i] = cheieTestare[i] * cifreCNP[i];
                sum += validator[i];
            }
            int cifraControl = sum % 11;
            if (cifraControl == 10) {
                cifraControl = 1;
            }
            return (cifraControl == cifreCNP[cifreCNP.length - 1]);
        } catch (Exception exc) {
			logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    /**
     * @param cifStr
     *            cif-ul de verificat
     * @return boolean, daca e valid sau nu
     *         <p>
     *         CIF - codul de identificare fiscala este un cod numeric constituind codul unic de identificare a unui comerciant.Acesta se mai numeste
     *         si cod fiscal sau cod unic de identificare. Pana la 1 ianuarie 2007 s-a numit Cod Unic de Inregistrare (CUI). Conform legii nr. 359 din
     *         8 septembrie 2004, privind simplificarea formalitatilor la inregistrarea in registrul comertului a persoanelor fizice, asociatiilor
     *         familiale si persoanelor juridice, inregistrarea fiscala a acestora, precum si la autorizarea functionarii persoanelor juridice:
     *         Solicitarea inregistrarii fiscale a unui comerciant se face prin depunerea cererii de inregistrare la biroul unic din cadrul oficiului
     *         registrului comertului de pe langa tribunal, iar atribuirea codului unic de inregistrare de catre Ministerul Finantelor Publice este
     *         conditionata de admiterea cererii de inregistrare in registrul comertului de catre judecatorul-delegat.
     *         </p>
     *         <ul>
     *         <li>1. Pentru asociatiile familiale, precum si pentru persoanele juridice prevazute la art. 2 structura codului unic de inregistrare se
     *         stabileste de Ministerul Finantelor Publice, Ministerul Muncii, Solidaritatii Sociale si Familiei, Ministerul Sanatatii, Ministerul
     *         Administratiei si Internelor si Ministerul Justitiei.</li>
     *         <li>2. Pentru persoanele fizice codul unic de inregistrare coincide cu codul numeric personal atribuit de Ministerul Administratiei si
     *         Internelor sau, dupa caz, cu numarul de identificare fiscala atribuit de Ministerul Finantelor Publice.</li>
     *         </ul>
     *         Atributul fiscal atasat codului unic de inregistrare este un cod alfanumeric avand
     *         semnificatia categoriei de platitor de taxe si impozite la bugetul de stat.Daca
     *         atributul fiscal are valoarea "RO", acesta atesta ca persoana juridica a fost luata
     *         in evidenta organului fiscal ca platitor de T.V.A. </p> Un CIF este alcatuit astfel
     *         :</p>
     *         <ul>
     *         <li>[ |ZZZZZZZZZ| ] |C|</li>
     *         <li>|_________| |_|</li>
     *         <li>--> Cifra ce control</li>
     *         <li>--> Numarul de ordine (maxim 9 caractere)</li>
     *         </ul>
     *         <p>
     *         Algoritmul de validare al unui cod CIF
     *         </p>
     *         </ul> <li>Pas preliminar: Se testeaza daca codul respecta formatul unui cod CIF. Adica lungimea maxima sa fie de 10 cifre si sa contina
     *         doar caractere numerice.</li> <li>Pas 1: Se foloseste cheia de testare "753217532". Se inverseaza ordinea cifrelor codului CIF precum
     *         si a cheii de testare.</li> <li>Pas 2: Se ignora prima cifra din codul CIF inversat (aceasta este cifra de control) si se inmulteste
     *         fiecare cifra cu cifra corespunzatoare din cheia de testare inversata.</li> <li>Pas 3: Se aduna toate produsele obtinute. Suma
     *         rezultata se inmulteste cu 10 si produsul este impartit la 11. Cifra obtinuta, in urma operatiei MODULO 11 reprezita cifra de
     *         verificare. Daca in urma impartirii s-a obtinut restul 10 atunci cifra de verificare va fi 0.</li> <li>Pas 4: Pentru un CIF valid cifra
     *         de verificare va trebui sa corespunda cu cifra de control a codului CIF initial.</li>
     */
    public static boolean validareCIF(final String cifStr) {

        // 753217532 - cheie de verificare. Aceasta se va inversa.
        final int[] cheieMFInversata = new int[] {
                2, 3, 5, 7, 1, 2, 3, 5, 7 };
        int sum = 0;

        try {
            String cui = cifStr.trim();
			if (StringUtils.isEmpty(cui)) {
                return false;
            }
            if (cui.length() < 2) {
                return false;
            }
            if (cui.length() > 10) {
                return false;
            }
            char[] cuiArray = cui.toCharArray();
            int[] cifreCUI = new int[cuiArray.length];
            for (int i = 0; i < cuiArray.length; i++) {
                if (Character.isDigit(cuiArray[i])) {
                    cifreCUI[i] = Integer.parseInt(String.valueOf(cuiArray[i]));
                    continue;
                }
                return false;
            }
            final int[] cifreInversate = new int[cifreCUI.length];
            for (int i = cifreCUI.length; i > 0; i--) {
                cifreInversate[cifreInversate.length - i] = cifreCUI[i - 1];
            }
            final int[] validator = new int[cifreCUI.length - 1];
            for (int i = 1; i < cifreInversate.length; i++) {
                validator[i - 1] = cifreInversate[i] * cheieMFInversata[i - 1];
                sum += validator[i - 1];
            }
            sum = sum * 10;
            int cifraVerificare = sum % 11;
            if (cifraVerificare == 10) {
                cifraVerificare = 0;
            }
            if (cifraVerificare != cifreInversate[0]) {
                return false;
            }
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
        return true;
    }

    /**
     * @param codBancnota
     *            codul de validat
     * @return boolean, daca se valideaza codul
     *         <p>
     *         Euro (�) este moneda oficiala a Uniunii Europene. Doar 15 state membre ale Uniunii au adoptat-o, restul statelor membre urmand a trece
     *         la Euro dupa indeplinirea anumitor conditii economice impuse. Moneda a fost adoptata in 1999 ca inlocuitor fizic al Monedei Unice
     *         Europene (ECU).
     *         </p>
     *         <p>
     *         Bancnotele Euro contin o serie care se poate verifica printr-un algoritm si care este unul din elementele de securitate ale bancnotei.
     *         Iata algoritmul:
     *         </p>
     *         <ol>
     *         <li>- Se inlocuieste litera de la inceputul seriei cu numarul ASCII corespunzator;</li>
     *         <li>- Se aduna toate cifrele numarului obtinut si se imparte la 9;</li>
     *         <li>- Bancnota este autentica daca restul impartirii la 9 este 0.</li>
     *         </ol>
     */
    public static boolean validareBancnotaEuro(final String codBancnota) {
        try {
			if (StringUtils.isEmpty(codBancnota)) {
                return false;
            }
            String cod = codBancnota.toUpperCase();
            if (Character.isDigit(cod.charAt(0))) {
                return false;
            }
            char codTara = cod.charAt(0);
            int codNumericTara = StringUtil.getAsciiCode(codTara);
            cod = String.valueOf(codNumericTara).concat(cod.substring(1, cod.length()));
            char[] codArray = cod.toCharArray();
            int sum = 0;
            for (char aCodArray : codArray) {
                if (!Character.isDigit(aCodArray)) {
                    return false;
                }
                sum += Integer.valueOf(String.valueOf(aCodArray));
            }
            return sum % 9 == 0;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    /**
     * @param codEAN the ean code to be validated
     * @return <p>
     *         Codul de bare EAN 13 (European Article Number) a fost creat ca un superset al codului UPC 12(Universal Product Code) dezvoltat in SUA.
     *         Codul de bare EAN 13 este folosit in domeniul vanzarii cu amanuntul a bunurilor, pe baza lui facandu-se corespondenta intre tipul
     *         bunului achizitionat si pretul acestuia.
     *         </p>
     *         <p>
     *         Codul de bare EAN 8 este derivat din codul EAN 13, fiind folosit in special pentru produsele mici, pe care spatiul de tiparire a
     *         codului este redus (Ex: pachetele de tigari).
     *         </p>
     *         <p>
     *         Codul EAN 13 contine 4 campuri distincte: primele 1, 2 sau 3 cifre reprezinta codul de tara conform listei GS1, urmatoarele 4, 5 sau 6
     *         cifre (in functie de codul de tara) reprezinta codul producatorului, penultimele 5 cifre reprezinta codul produsului si ultima cifra
     *         este o cifra de control. Codurile ISBN-10 si ISMN-10 se pot converti in EAN 13 prin prefixarea cu "978" sau "979", iar ISSN-10 prin
     *         prefixare cu "977".
     *         </p>
     *         <p>
     *         Verificarea codului de bare se face astfel: se aduna produsele dintre cifrele numarului si ponderea asociata pozitiei (de la DREAPTA
     *         spre STANGA - exceptand ultima cifra - pozitiile impare au pondere 3, iar pozitiile pare au ponderea 1). Codul este valid daca valoarea
     *         cu care se completeaza suma pana la primul multiplu de 10 este egala cu cifra de control.
     *         </p>
     */
    public static boolean validareEAN13(final String codEAN) {
        try {
			if (StringUtils.isEmpty(codEAN)) {
                return false;
            }
            char[] codEANarray = codEAN.toCharArray();
            // pt codurile de bara mai vechi, se va face un test de length <8 sau 10 aici.
            if (codEANarray.length != 13) {
                return false;
            }
            int suma = 0;
            for (int i = 0; i < codEANarray.length - 1; i++) {
                if (!Character.isDigit(codEANarray[i])) {
                    return false;
                }
                int pondere = (i % 2 == 0 ? 1 : 3);
                suma += Integer.valueOf(String.valueOf(codEANarray[i])) * pondere;
            }
            int diferentaPanaLa10 = 0;
            if (suma % 10 != 0) {
                diferentaPanaLa10 = 10 - (suma % 10);
            }
            return diferentaPanaLa10 == Integer.valueOf(String.valueOf(codEANarray[codEANarray.length - 1]));
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    /**
     * @param codIBAN codul IBAN care va fi validat
     * @return <p>
     *         Codul IBAN este un standard international pentru numerotarea codurilor bancare. A fost adoptat pentru prima oara de catre Comitetul
     *         European pentru Standarde Bancare iar in prezent este cunoscut ca ISO 13616:2003. IBAN-ul consista dintr-un cod de tara ISO 3166-1 de
     *         doua litere, urmat de doua cifre de control, si pana la 30 de caractere alfanumerice pentru codul contului bancar domestic (BBAN -
     *         Basic Bank Account Number).
     *         </p>
     *         <p>
     *         Pentru Romania, primele patru caractere ale codului BBAN constituie codul national al bancii de care apartine contul respectiv.
     *         Lungimea codului BBAN este fixata de comitetul bancii nationale al fiecarei tari si trebuie sa fie aceeasi pentru toata tara.
     *         </p>
     *         <p>
     *         In format electronic codul IBAN trebuie sa nu contina spatii sau alte caractere despartitoare, iar atunci cand este tiparit pe hartie
     *         este exprimat in grupuri de cate patru caractere, cu ultimul grup de lungime variabila.
     *         </p>
     *         <p>
     *         Un IBAN este alcatuit astfel :
     *         </p>
     *         <ul>
     *         <li>|RO| |XX| |yyyy| |ZZZZZZZZZZZZZZZZ|</li>
     *         <li>|__| |__| |____| |________________|</li>
     *         <li>: : : :</li>
     *         <li>: : : :</li>
     *         <li>: : : --> 16 caractere care identifica in mod unic unitatea</li>
     *         <li>: : : teritoriala a institutiei si contul clientului</li>
     *         <li>: : : deschis la respectiva unitate teritoriala</li>
     *         <li>: : : (alfanumerice, majuscule)</li>
     *         <li>: : :</li>
     *         <li>: : --> 4 caractere de identificare a institutiei (alfabetice, : : majuscule), reprezentand primele patru caractere ale codului : :
     *         BIC al institutiei : :</li>
     *         <li>: --> 2 caractere de verificare (numerice, de la 0 la 9)</li>
     *         <li>--> codul de tara (2 caractere alfabetice, majuscule) - pentru Romania este RO.</li>
     *         </ul>
     *         <p>
     *         <b> Algoritmul de validare al unui cod IBAN </b>
     *         </p>
     *         <ul>
     *         <li>Pas preliminar: In cazul in care codul IBAN este prezentat pe suport hartie, se converteste formatul acestuia la formatul
     *         electronic prin stergerea spatiilor de separare. EXEMPLU: un cod IBAN de tipul RO49 AAAA 1B31 0075 9384 0000 devine
     *         RO49AAAA1B31007593840000.</li>
     *         <li>Pasul 1: Se muta primele patru caractere (simbolizand codul de tara si caracterele de verificare) la dreapta codului IBAN. EXEMPLU:
     *         RO49AAAA1B31007593840000 devine AAAA1B31007593840000RO49.</li>
     *         <li>Pasul 2: Se face conversia literelor in numere, conform tabelei urmatoare(codurile ascii) :
     *         <ul>
     *         <li>A = 10 G = 16 M = 22 S = 28 Y = 34</li>
     *         <li>B = 11 H = 17 N = 23 T = 29 Z = 35</li>
     *         <li>C = 12 I = 18 O = 24 U = 30</li>
     *         <li>D = 13 J = 19 P = 25 V = 31</li>
     *         <li>E = 14 K = 20 Q = 26 W = 32</li>
     *         <li>F = 15 L = 21 R = 27 X = 33</li>
     *         </ul>
     *         EXEMPLU: AAAA1B31007593840000RO49 devine 1010101011131007593840000272449</li>
     *         <li>Pasul 3: Se aplica algoritmul MOD 97-10 (conform ISO 7064). Pentru ca cele 2 caractere de verificare sa fie corecte (codul IBAN sa
     *         fie corect), restul impartirii numarului astfel obtinut la 97 trebuie sa fie 1. EXEMPLU: Restul impartirii lui
     *         1010101011131007593840000272449 la 97 este 1.</li>
     *         </ul>
     */
    public static boolean validareIBAN(final String codIBAN) {
        try {
			if (StringUtils.isEmpty(codIBAN)) {
                return false;
            }
			String cod = codIBAN.replaceAll(" ", "").toUpperCase();
            if (cod.length() < 7) {
                return false;
            }
            String tmp = cod.substring(0, 4);
            cod = cod.substring(4, cod.length()).concat(tmp);

            StringBuilder str = new StringBuilder();
            char[] arrayCod = cod.toCharArray();
            for (char c : arrayCod) {
                if (Character.isDigit(c)) {
                    str.append(c);
                    continue;
                }
                if (c == 'A') {
                    str.append(10);
                } else if (c == 'B') {
                    str.append(11);
                } else if (c == 'C') {
                    str.append(12);
                } else if (c == 'D') {
                    str.append(13);
                } else if (c == 'E') {
                    str.append(14);
                } else if (c == 'F') {
                    str.append(15);
                } else if (c == 'G') {
                    str.append(16);
                } else if (c == 'H') {
                    str.append(17);
                } else if (c == 'I') {
                    str.append(18);
                } else if (c == 'J') {
                    str.append(19);
                } else if (c == 'K') {
                    str.append(20);
                } else if (c == 'L') {
                    str.append(21);
                } else if (c == 'M') {
                    str.append(22);
                } else if (c == 'N') {
                    str.append(23);
                } else if (c == 'O') {
                    str.append(24);
                } else if (c == 'P') {
                    str.append(25);
                } else if (c == 'Q') {
                    str.append(26);
                } else if (c == 'R') {
                    str.append(27);
                } else if (c == 'S') {
                    str.append(28);
                } else if (c == 'T') {
                    str.append(29);
                } else if (c == 'U') {
                    str.append(30);
                } else if (c == 'V') {
                    str.append(31);
                } else if (c == 'W') {
                    str.append(32);
                } else if (c == 'X') {
                    str.append(33);
                } else if (c == 'Y') {
                    str.append(34);
                } else if (c == 'Z') {
                    str.append(35);
                }
            }
            BigInteger bi = new BigInteger(str.toString());
            return bi.divideAndRemainder(new BigInteger("97"))[1].equals(BigInteger.ONE);
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    /**
     * @param codISBN13 codul ISBN 13
     * @return <p>
     *         ISBN este un numar de inregistrare pentru carti,cu caracter international. El este eliberat de Biroul National ISBN si contine
     *         indicativul editurii care l-a achizitionat. Un numar ISBN este asociat unui singur titlu de carte si nu mai poate fi atribuit niciodata
     *         altui titlu.
     *         </p>
     *         <p>
     *         Conceperea sistemului ISBN dateaza din anul 1965 si se datoreaza distribuitorului englez W.H. Smith & Son Ltd si Asociatiei Editorilor
     *         din Marea Britanie. Din anul 1970, Numarul Standard International al Cartii-ISBN a fost recunoscut international ca un sistem de
     *         identificare in domeniul cartii. Din anul 1989, sistemul de numerotare standardizata a cartilor ISBN a fost introdus si in Romania.
     *         </p>
     *         <p>
     *         Un cod ISBN insoteste o publicatie monografica de la editarea sa si mai departe, in tot lantul de distributie. Codul ISBN se foloseste
     *         ca element cheie al sistemelor de inregistrare si inventar pentru editori, distribuitori, comercianti, biblioteci si alte organizatii.
     *         </p>
     *         <p>
     *         Standardul ISO 2108 revizuit a fost publicat la inceputul anului 2005 si reprezinta prima schimbare aparuta in structura ISBN de la
     *         infiintarea acestui sistem. Noile prevederi ale standardului vor fi aplicate incepand cu 1 ianuarie 2007. Pana la aceasta data va
     *         exista o perioada de tranzitie, cand se vor folosi in paralel ambele variante ale codului ISBN, atat ISBN-10 (ISBN format din 10 cifre)
     *         cat si ISBN-13 (ISBN format din 13 cifre). Prevederile standardului ISO 2108/2005 se aplica atat publicatiilor tiparite cat si celor in
     *         format electronic precum si tuturor celorlalte documente identificate prin cod ISBN.
     *         </p>
     *         <p>
     *         Structura codului ISBN-10
     *         </p>
     *         <p>
     *         Codul de 10 cifre este impartit in patru parti variabile ca lungime :
     *         </p>
     *         <ul>
     *         <li>Prima parte : Grupul. Aceasta parte identifica locatia nationala sau geografica a editurii.</li>
     *         <li>A doua parte : Editura. Reprezinta o anume editura in cadrul unei tari sau grupari.</li>
     *         <li>A treia parte : Titlul. Aceasta parte identifica un anumit titlu sau editie al unui anumit editor.</li>
     *         <li>A patra parte : Cifra de control. Aceasta este o singura cifra sau litera alfabetica X.</li>
     *         <ul>
     *         <p>
     *         Structura codului ISBN-13
     *         </p>
     *         <p>
     *         Codul ISBN-10 va fi prefixat cu 978 si i se va recalcula cifra de control. Dupa ce se vor epuiza toate codurile ISBN-10 , urmatoarele
     *         coduri ISBN-13 se vor prefixa cu 979.
     *         </p>
     *         <p>
     *         Algoritmul de validare al unui cod ISBN - 13
     *         </p>
     *         <ul>
     *         <li>Pasul preliminar: Se elimina spatiile si cratimele. Ultima cifra se ignora (este cifra de control).</li>
     *         <li>Pasul 1: Se inmulteste fiecare cifra cu ponderea asociata ei. Ponderile se atribuie pentru fiecare cifra, incepand cu prima cifra,
     *         sub forma 1,3,1,3...</li>
     *         <ul>
     *         <li>Ponderi 1 3 1 3 1 3 1 3 1 3 1 3 1</li>
     *         <li>ISBN 9 7 8 0 9 0 1 6 9 0 6 6 1</li>
     *         <li>Valori 9 21 8 0 9 0 1 18 9 0 6 18 1</li>
     *         </ul>
     *         <li>Pasul 2: Se aduna valorile obtinute</li> <li>Pasul 3: Se imparte suma obtinuta la 10 si se extrage restul (MODULO 10).</li> <li>
     *         Pasul 4: Daca restul este 0 atunci cifra de control trebuie sa fie 0.Daca restul este diferit de 0, atunci se scade restul obtinut din
     *         10. Rezultatul reprezinta cifra de control. Pentru un ISBN-13 valid cifra de control rezultata va trebui sa fie egala cu ultima cifra a
     *         codului (cifra 13).</li> </ul>
     *         <p>
     *         Algoritmul de validare al unui cod ISBN - 10
     *         </p>
     *         <ul>
     *         <li>Pasul preliminar: Se elimina spatiile si cratimele. Ultimul caracter se ignora (este caracterul de control).</li>
     *         <li>Pasul 1: Se inmulteste fiecare cifra cu ponderea asociata ei. Ponderile se atribuie pentru fiecare cifra, incepand cu prima cifra,
     *         sub forma (11-pozitia cifrei)</li>
     *         <ul>
     *         <li>Ponderi 10 9 8 7 6 5 4 3 2</li>
     *         <li>ISBN 0 9 4 0 0 1 6 6 1</li>
     *         <li>Valori 0 81 32 0 0 5 24 18 2</li>
     *         </ul>
     *         <li>Pasul 2: Se aduna valorile obtinute</li> <li>Pasul 3: Se imparte suma obtinuta la 11 si se extrage restul (MODULO 11).</li> <li>
     *         Pasul 4: Daca restul este 0 atunci caracterul de control trebuie sa fie 0.Daca restul este 10 atunci caracterul de control este 'X'.
     *         Daca restul este diferit de 0, atunci se scade restul obtinut din 11. Rezultatul reprezinta caracterul de control. Pentru un ISBN-10
     *         valid caracterul de control rezultat va trebui sa fie egal cu ultimul caracter al codului (caracterul 10).</li> </ul>
     */
    public static boolean validareISBN13(final String codISBN13) {
        try {
			if (StringUtils.isEmpty(codISBN13)) {
                return false;
            }
            String cod = codISBN13;
			cod = cod.replaceAll(" ", "");
			cod = cod.replaceAll("-", "");
            if (cod.length() != 13) {
                return false;
            }
            char[] codISBNArray = cod.toCharArray();

            char c = codISBNArray[codISBNArray.length - 1];
            if (!Character.isDigit(c)) {
                return false;
            }
            int suma = 0;
            for (int i = 0; i < codISBNArray.length - 1; i++) {
                if (!Character.isDigit(codISBNArray[i])) {
                    return false;
                }
                int pondere = i % 2 == 0 ? 1 : 3;
                suma += Integer.valueOf(String.valueOf(codISBNArray[i])) * pondere;
            }
            int rest = suma % 10;
            int cifraControl = Integer.valueOf(String.valueOf(c));
            if (rest == 0) {
                return cifraControl == 0;
            }
            rest = 10 - rest;
            return rest == cifraControl;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    /**
     * @param codISBN10 codul isbn 10
     * @return vezi {@link ValidareCoduri#validareISBN13(String)}
     */
    public static boolean validareISBN10(final String codISBN10) {
        try {
			if (StringUtils.isEmpty(codISBN10)) {
                return false;
            }
            String cod = codISBN10;
			cod = cod.replaceAll(" ", "");
			cod = cod.replaceAll("-", "");
            if (cod.length() != 10) {
                return false;
            }

            char[] codISBNArray = cod.toCharArray();
            int suma = 0;
            for (int i = 0; i < codISBNArray.length - 1; i++) {
                if (!Character.isDigit(codISBNArray[i])) {
                    return false;
                }
                int pondere = 11 - (i + 1);
                suma += Integer.valueOf(String.valueOf(codISBNArray[i])) * pondere;
            }
            int rest = suma % 11;
            char c = codISBNArray[codISBNArray.length - 1];
            int cifraControl;
            if (Character.isDigit(c)) {
                cifraControl = Integer.valueOf(String.valueOf(c));
            } else if ((c == 'x') || (c == 'X')) {
                cifraControl = 10;
            } else {
                return false;
            }
            if (rest == 0) {
                return cifraControl == 0;
            }
            rest = 11 - rest;
            return rest == cifraControl;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }

    public static String convertISBN10toISBN13(final String codISBN10) {
        try {
            if (!ValidareCoduri.validareISBN10(codISBN10)) {
                return "Codul nu poate fi convertit (nu este valid ISBN10).";
            }
            String cod = codISBN10;
            cod = cod.substring(0, cod.length() - 1);
            String codTemp = cod;
			codTemp = codTemp.replaceAll("-", "");
			codTemp = codTemp.replaceAll(" ", "");
            codTemp = "978".concat(codTemp);
            char[] codTempArray = codTemp.toCharArray();
            int suma = 0;
            for (int i = 0; i < codTemp.length(); i++) {
                int pondere = i % 2 == 0 ? 1 : 3;
                suma += Integer.valueOf(String.valueOf(codTempArray[i])) * pondere;
            }
            int rest = suma % 10;
            if (rest == 0) {
                return "978".concat("-").concat(cod).concat("0");
            }
            rest = 10 - rest;
            return "978".concat("-").concat(cod).concat(String.valueOf(rest));
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return "Exceptie la generarea codului ISBN13";
        }
    }

    public static boolean validateISBN(final String codISBN) {
		if (StringUtils.isEmpty(codISBN)) {
            return false;
        }
        String cod = codISBN;
		cod = cod.replaceAll("-", "");
		cod = cod.replaceAll(" ", "");
        if (cod.length() == 10) {
            return ValidareCoduri.validareISBN10(codISBN);
            /*
              apelul asta va duce la aceleasi rezultate, dar nu are sens aici, pt ca s-ar parsa de
              2 ori input-ul
              <p>
              return ValidareCoduri.validareISBN13(ValidareCoduri.convertISBN10toISBN13(codISBN));
              </p>
             */
        }
        if (cod.length() == 13) {
            return ValidareCoduri.validareISBN13(codISBN);
        }
        return false;
    }

    public static String convertISBNtoValidEAN13(final String codISBN) {
		if (StringUtils.isEmpty(codISBN)) {
            return "Conversie imposibila";
        }
        if (!ValidareCoduri.validateISBN(codISBN)) {
            return "Cod ISBN invalid";
        }
        String cod = codISBN;
		cod = cod.replaceAll("-", "");
		cod = cod.replaceAll(" ", "");
        String result;
        if (cod.length() == 10) {
            result = ValidareCoduri.convertISBN10toISBN13(codISBN);
			result = result.replaceAll("-", "");
			result = result.replaceAll(" ", "");
            return result;
        }
        if (cod.length() == 13) {
            result = codISBN;
			result = result.replaceAll("-", "");
			result = result.replaceAll(" ", "");
            return result;
        }
        return "Conversie imposibila";
    }

    /**
     * @param nrCard numarul cardului
     * @return <p>
     *         Exista doua mari categorii de carduri : cardul de credit si cardul de debit .
     *         </p>
     *         <p>
     *         Cele doua mari tipuri de carduri sunt net diferite. Fiecare are avantaje specifice in functie de profilul utilizatorului. Aspectul
     *         important care le uneste, este menirea lor de instrumente de plata.
     *         </p>
     *         <p>
     *         Cardurile de credit isi au originea in Statele Unite ale Americii in anii 1920. Atunci diferite companii, cum ar fi lanturile hoteliere
     *         si companiile de petrol, au inceput sa emita astfel de carduri clientilor lor pentru a efectua plata produselor oferite de ei. Aceasta
     *         tendinta a inceput sa se accentueze dupa Al Doilea Razboi Mondial.
     *         </p>
     *         <p>
     *         Primul card de credit universal, care putea fi folosit ca metoda de plata la o varietate mare de magazine, a fost introdus de Diners
     *         Club in 1950.
     *         </p>
     *         <p>
     *         Sistemul de numerotare adoptat pentru carduri este ANSI Standard X4.13-1983.
     *         </p>
     *         <p>
     *         Structura unui cod de card
     *         </p>
     *         <ul>
     *         <li>|S|BBB BB|NN NNNN NNN|C|</li>
     *         <li>|_|____ __|___ _____ ____|_|</li>
     *         <li>: : : :</li>
     *         <li>: : : --> Cifra ce control</li>
     *         <li>: : :</li>
     *         <li>: : --> Numarul contului</li>
     *         <li>: :</li>
     *         <li>: --> Identificatorul emitentului</li>
     *         <li>:</li>
     *         <li>--> Tipul de card sau Major Industry Identifier (MII)</li>
     *         </ul>
     *         <p>
     *         Major Industry Identifier
     *         </p>
     *         <p>
     *         Prima cifra a unui cod de card este "Major Industry Identifier (MII)", ceea ce reprezinta categoria emitentului:
     *         </p>
     *         <ul>
     *         <li>0 ISO/TC 68 si alte industrii</li>
     *         <li>1 Companii aeriene</li>
     *         <li>2 Companii aeriene</li>
     *         <li>3 Calatorii si agrement</li>
     *         <li>4 Domeniu bancar and financiar</li>
     *         <li>5 Domeniu bancar and financiar</li>
     *         <li>6 Comert si domeniu bancar</li>
     *         <li>7 Industrie petroliera</li>
     *         <li>8 Telecomunicatii</li>
     *         <li>9 Domeniul public</li>
     *         </ul>
     *         <p>
     *         Spre exemplu, American Express, Diner's Club si Carte Blanche se incadreaza in categoria Calatorii si agrement, VISA, MasterCard si
     *         Discover sunt in categoria Domeniu bancar si financiar, iar SUN Oil si Exxon sunt in categoria Industrie petroliera.
     *         </p>
     *         <p>
     *         Identificatorul emitentului
     *         </p>
     *         <p>
     *         Primele 6 cifre din codul de card (inclusiv cifra MII) reprezinta identificatorul emitentului. Aceasta inseamna ca numarul total de
     *         posibili emitenti este de un milion.
     *         </p>
     *         <p>
     *         Cei mai cunoscuti emitenti sunt :
     *         </p>
     *         <ul>
     *         <li>Emitent Identificator Lungimea codului de card</li>
     *         <li>Diner's Club/Carte Blanche 300xxx-305xxx,36xxxx, 38xxxx 14</li>
     *         <li>American Express 34xxxx, 37xxxx 15</li>
     *         <li>VISA 4xxxxx 13,16</li>
     *         <li>MasterCard 51xxxx-55xxxx 16</li>
     *         <li>Discover 6011xx 16</li>
     *         <li>JCB 2131xx,1800xxx 15,16</li>
     *         <li>enRoute 2014xx,2149xx 15</li>
     *         </ul>
     *         <p>
     *         Daca cifra MII este 9, atunci urmatoarele 3 cifre ale emitentului sunt codul tarii definit de ISO 3166, si urmatoarele doua cifre pot
     *         fi definite prin standarde nationale.
     *         </p>
     *         <p>
     *         Numarul contului
     *         </p>
     *         <p>
     *         Cifrele de la 7 la (n-1) din codul de card reprezinta identificatorul contului. Lungimea maxima a unui cod de card este de 19 cifre
     *         deci lungima maxima a numarului contului este de 12 cifre. Asta inseamna ca fiecare emitent dispune de un trilion de numere de cont
     *         posibile.
     *         </p>
     *         <p>
     *         Cifra de control
     *         </p>
     *         <p>
     *         Ultima cifra a codului de card reprezinta cifra de control. Algoritmul folosit pentru a verifica cifra de control se numeste Algortimul
     *         Luhn, dupa numele omului de stiinta Hans Peter Luhn (1896-1964). El a primit premiul US Patent 2950048
     *         ("Computer for Verifying Numbers") pentru acest algoritm in anul 1960.
     *         </p>
     *         <p>
     *         Algoritmul Luhn de validare al unui cod de card
     *         </p>
     *         <ul>
     *         <li>Pas 1: Se inmulteste fiecare cifra din codul de card cu ponderea sa. Daca un card are un numar par de cifre, prima cifra are o
     *         pondere de 2, daca nu, cifra are o pondere de 1. Dupa aceea , ponderile cifrelor alterneaza 1,2,1,2.</li>
     *         <li>Pas 2: Daca orice cifra are o valoare ponderata mai mare decat 9, se scade 9 din valoarea ei.</li>
     *         <li>Pas 3: Se aduna toate valorile ponderate si se calculeaza restul impartirii la 10 (MODULO 10).</li>
     *         <li>Pas 4: Un cod de card este valid daca rezultatul operatiei MODULO 10 este 0.</li>
     *         <ul>
     */
    public static boolean validareCardBancar(final String nrCard) {
        try {
			if (StringUtils.isEmpty(nrCard)) {
                return false;
            }
			String cod = nrCard.replace(" ", "");
            if (cod.length() < 8) {
                return false;
            }
            char[] cifreNrCard = cod.toCharArray();
            int suma = 0;
            int pondere = (cifreNrCard.length % 2 == 0 ? 2 : 1);
            for (int i = 0; i < cifreNrCard.length; i++) {
                if (!Character.isDigit(cifreNrCard[i])) {
                    return false;
                }
                if (i != 0) {
                    pondere = (pondere == 2 ? 1 : 2);
                }
                int nr = Integer.valueOf(String.valueOf(cifreNrCard[i])) * pondere;
                if (nr > 9) {
                    nr -= 9;
                }
                suma += nr;
            }
            return suma % 10 == 0;
        } catch (Exception exc) {
            logger.error(exc.getMessage(), exc);
            return false;
        }
    }
}
