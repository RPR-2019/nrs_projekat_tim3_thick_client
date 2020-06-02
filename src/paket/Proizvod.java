package paket;

import javafx.beans.property.SimpleStringProperty;

public class Proizvod {
    private int id;
    private SimpleStringProperty naziv,kategorija,proizvodjac;
    private int cijena;

    public Proizvod(String naziv, String proizvodjac, String kategorija,int cijena) {
        this.naziv = new SimpleStringProperty(naziv);
        this.proizvodjac = new SimpleStringProperty(proizvodjac);
        this.kategorija = new SimpleStringProperty(kategorija);
        this.cijena = cijena;
    }

    public Proizvod(int id, String naziv, String proizvodjac, String kategorija,int cijena) {
        this.id = id;
        this.naziv = new SimpleStringProperty(naziv);
        this.kategorija = new SimpleStringProperty(proizvodjac);
        this.proizvodjac = new SimpleStringProperty(kategorija);
        this.cijena = cijena;
    }

    public Proizvod() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv.get();
    }

    public SimpleStringProperty nazivProperty() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public String getKategorija() {
        return kategorija.get();
    }

    public SimpleStringProperty kategorijaProperty() {
        return kategorija;
    }

    public void setKategorija(String kategorija) {
        this.kategorija.set(kategorija);
    }

    public String getProizvodjac() {
        return proizvodjac.get();
    }

    public SimpleStringProperty proizvodjacProperty() {
        return proizvodjac;
    }

    public void setProizvodjac(String proizvodjac) {
        this.proizvodjac.set(proizvodjac);
    }

    public int getCijena() {
        return cijena;
    }

    public void setCijena(int cijena) {
        this.cijena = cijena;
    }
}
