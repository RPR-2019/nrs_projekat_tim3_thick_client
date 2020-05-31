package paket;

public class Proizvodjac {
    private int id;
    private String naziv;

    public Proizvodjac() {
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

    public Proizvodjac(int id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }
}
