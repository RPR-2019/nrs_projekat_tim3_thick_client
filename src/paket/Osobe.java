package paket;

import java.time.LocalDate;

public class Osobe {
    private int id;
  //  private Long JMBG;
    private String ime,prezime,telefon,naziv_lokacije,email,password,JMBG;
    private LocalDate datum_zaposljavanja;

    public Osobe() {
    }

    public Osobe(int id, String ime, String prezime, String telefon,LocalDate datum_zaposljavanja,String JMBG, String naziv_lokacije) {
        this.id = id;
        this.JMBG = JMBG;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.naziv_lokacije = naziv_lokacije;
        this.datum_zaposljavanja = datum_zaposljavanja;
    }

    public Osobe(String ime, String prezime, String telefon,LocalDate datum_zaposljavanja,String JMBG, String naziv_lokacije) {
        this.id = id;
        this.JMBG = JMBG;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.naziv_lokacije = naziv_lokacije;
        this.datum_zaposljavanja = datum_zaposljavanja;
    }

    public Osobe(String ime, String prezime, String telefon,LocalDate datum_zaposljavanja,String JMBG, String naziv_lokacije,String password,String email) {
        this.id = id;
        this.JMBG = JMBG;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.naziv_lokacije = naziv_lokacije;
        this.datum_zaposljavanja = datum_zaposljavanja;
        this.email = email;
        this.password = password;
    }
    public Osobe(int id,String ime, String prezime, String telefon,LocalDate datum_zaposljavanja,String JMBG, String naziv_lokacije,String password,String email) {
        this.id = id;
        this.JMBG = JMBG;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.naziv_lokacije = naziv_lokacije;
        this.datum_zaposljavanja = datum_zaposljavanja;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJMBG() {
        return JMBG;
    }

    public void setJMBG(String JMBG) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
