package paket;

public class Skladiste {
    private int id;
    private String naziv,naziv_lokacije;

    public Skladiste() {
    }

    public Skladiste(int id, String naziv, String naziv_lokacije) {
        this.id = id;
        this.naziv = naziv;
        this.naziv_lokacije = naziv_lokacije;
    }

    public Skladiste(String naziv, String naziv_lokacije) {
        this.naziv = naziv;
        this.naziv_lokacije = naziv_lokacije;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv_lokacije() {
        return naziv_lokacije;
    }

    public void setNaziv_lokacije(String naziv_lokacije) {
        this.naziv_lokacije = naziv_lokacije;
    }
}

