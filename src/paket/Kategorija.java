package paket;

public class Kategorija {
    private int id;
    private String naziv,nadKategorija = null;
  //  private Kategorija nadKategorija = null;
    int nad_k;

    public Kategorija() {
    }

    public Kategorija(int id, String naziv, String nadKategorija) {
        this.id = id;
        this.naziv = naziv;
        this.nadKategorija = nadKategorija;
    }

    public Kategorija(int id, String naziv, int nad_k) {
        this.id = id;
        this.naziv = naziv;
        this.nad_k = nad_k;
    }

    public Kategorija(String naziv, String nadKategorija) {
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

    public int getNad_k() {
        return nad_k;
    }

    public void setNad_k(int nad_k) {
        this.nad_k = nad_k;
    }

    @Override
    public String toString() {
        return naziv;
    }

}
