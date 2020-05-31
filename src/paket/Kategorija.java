package paket;

public class Kategorija {
    private int id;
    private String naziv,nadKategorija;

    public Kategorija() {
    }

    public Kategorija(int id, String naziv, String nadKategorija) {
        this.id = id;
        this.naziv = naziv;
        this.nadKategorija = nadKategorija;
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

    public String getNadKategorija() {
        return nadKategorija;
    }

    public void setNadKategorija(String nadKategorija) {
        this.nadKategorija = nadKategorija;
    }
}
