package paket;

public class Proizvod {
    private int id;
    private String naziv;
    private Kategorija kategorija;
    private Proizvodjac proizvodjac;
    private int cijena;
    private int kolicina;
    private Dobavljac dobavljac;


    public Proizvod(int id,String naziv, Proizvodjac proizvodjac, Kategorija kategorija,int cijena,int kolicina) {
        this.id = id;
        this.naziv = naziv;
        this.proizvodjac = proizvodjac;
        this.kategorija = kategorija;
        this.cijena = cijena;
        this.kolicina = kolicina;
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
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public Proizvodjac getProizvodjac() {
        return proizvodjac;
    }

    public void setProizvodjac(Proizvodjac proizvodjac) {
        this.proizvodjac = proizvodjac;
    }

    public int getCijena() {
        return cijena;
    }

    public void setCijena(int cijena) {
        this.cijena = cijena;
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        this.kolicina = kolicina;
    }

    public Dobavljac getDobavljac() {
        return dobavljac;
    }

    public void setDobavljac(Dobavljac dobavljac) {
        this.dobavljac = dobavljac;
    }
}
