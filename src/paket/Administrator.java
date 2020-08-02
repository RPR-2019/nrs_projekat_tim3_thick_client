package paket;

import java.time.LocalDate;

public class Administrator extends Osobe {
    private String email,password;

    public Administrator(int id, String ime, String prezime, String telefon, LocalDate datum_zaposljavanja, String JMBG, String naziv_lokacije, Uloga uloga, String email, String password) {
        super(id, ime, prezime, telefon, datum_zaposljavanja, JMBG, naziv_lokacije, uloga);
        this.email = email;
        this.password = password;
    }

    public Administrator() {
    }

    public Administrator(String email, String password) {
        this.email = email;
        this.password = password;
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

