package paket;

import java.time.LocalDate;

public class Osobe {
    private int id,JMBG;
    private String ime,prezime,telefon,naziv_lokacije;
    private LocalDate datum_zaposljavanja;

    public Osobe() {
    }

    public Osobe(int id, int JMBG, String ime, String prezime, String telefon, String naziv_lokacije, LocalDate datum_zaposljavanja) {
        this.id = id;
        this.JMBG = JMBG;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.naziv_lokacije = naziv_lokacije;
        this.datum_zaposljavanja = datum_zaposljavanja;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJMBG() {
        return JMBG;
    }

    public void setJMBG(int JMBG) {
        this.JMBG = JMBG;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getNaziv_lokacije() {
        return naziv_lokacije;
    }

    public void setNaziv_lokacije(String naziv_lokacije) {
        this.naziv_lokacije = naziv_lokacije;
    }

    public LocalDate getDatum_zaposljavanja() {
        return datum_zaposljavanja;
    }

    public void setDatum_zaposljavanja(LocalDate datum_zaposljavanja) {
        this.datum_zaposljavanja = datum_zaposljavanja;
    }
}
