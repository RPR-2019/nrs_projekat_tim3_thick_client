package paket;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditController {

    public TextField fldNaziv;
    public TextField fldProizvodjac;
    public TextField fldKategorija;
    public TextField fldCijena;
    public TextField fldKolicina;
    public TextField fldDobavljac;
    public Proizvod product = null;
    public Button btnCancel;
    public Button btnOk;
    public int kolicina = 0;
    private SkladisteDAO model;

    public static boolean DaLiJeBroj(String str){
        String regex = "[0-9]+";
        if(!(str.matches(regex))) return false;

        return true;
    }

    public boolean IspravnaKategorija(String str){
        ArrayList<Kategorija> categories = model.getCategories();

        for(int i=0 ; i<categories.size() ; i++){
            if(categories.get(i).getNaziv().equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean IspravanProizvodjac(String str){
        ArrayList<Proizvodjac> manufacturers = model.getManufacturers();

        for(int i=0 ; i<manufacturers.size() ; i++){
            if(manufacturers.get(i).getNaziv().equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean ispravanDobavljac(String str){
        ArrayList<String> dobavljaci = model.getDobavljaci();

        for(int i=0 ; i<dobavljaci.size() ; i++){
            if(dobavljaci.get(i).equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean ProvjeraDaLiVecPostojiProizvod(String str){
        ObservableList<Proizvod> products = model.getProducts();

        for(Proizvod p : products){
            if(p.getNaziv().equals(str)){
                return true;
            }
        }
        return false;
    }

    public EditController(Proizvod product){
        this.product = product;
    }

    public void initialize(){
        this.model = new SkladisteDAO();
        if(product == null) {
            fldNaziv.setText("");
            fldKategorija.setText("");
            fldCijena.setText("");
            fldProizvodjac.setText("");
            fldKolicina.setText("");
            fldDobavljac.setText("");

            fldNaziv.getStyleClass().add("poljeNijeIspravno");
            fldKategorija.getStyleClass().add("poljeNijeIspravno");
            fldCijena.getStyleClass().add("poljeNijeIspravno");
            fldProizvodjac.getStyleClass().add("poljeNijeIspravno");
            fldKolicina.getStyleClass().add("poljeNijeIspravno");
            fldDobavljac.getStyleClass().add("poljeNijeIspravno");

        } else {
            fldNaziv.setText(product.getNaziv());
            fldProizvodjac.setText(product.getProizvodjac());
            fldKategorija.setText(product.getKategorija());
            fldCijena.setText(String.valueOf(product.getCijena()));
            fldKolicina.setText(String.valueOf(product.getKolicina()));
            fldDobavljac.setText(product.getDobavljac());


            fldNaziv.getStyleClass().add("poljeIspravno");
            fldKategorija.getStyleClass().add("poljeIspravno");
            fldCijena.getStyleClass().add("poljeIspravno");
            fldProizvodjac.getStyleClass().add("poljeIspravno");
            fldKolicina.getStyleClass().add("poljeIspravno");
            fldDobavljac.getStyleClass().add("poljeIspravno");

        }

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && !ProvjeraDaLiVecPostojiProizvod(newIme) && newIme.length() > 3) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldProizvodjac.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && IspravanProizvodjac(newIme)) {             // Proizvodjac mora bit neki iz baze
                fldProizvodjac.getStyleClass().removeAll("poljeNijeIspravno");
                fldProizvodjac.getStyleClass().add("poljeIspravno");
            } else {
                fldProizvodjac.getStyleClass().removeAll("poljeIspravno");
                fldProizvodjac.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldKategorija.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && IspravnaKategorija(newIme)) {
                fldKategorija.getStyleClass().removeAll("poljeNijeIspravno");
                fldKategorija.getStyleClass().add("poljeIspravno");
            } else {
                fldKategorija.getStyleClass().removeAll("poljeIspravno");
                fldKategorija.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldCijena.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && DaLiJeBroj(newIme)) {
                fldCijena.getStyleClass().removeAll("poljeNijeIspravno");
                fldCijena.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldKolicina.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && DaLiJeBroj(newIme)) {                               // Kolicina i cijena moraju biti broj
                fldKolicina.getStyleClass().removeAll("poljeNijeIspravno");
                fldKolicina.getStyleClass().add("poljeIspravno");
            } else {
                fldKolicina.getStyleClass().removeAll("poljeIspravno");
                fldKolicina.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldDobavljac.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && ispravanDobavljac(newIme)) {                               // Kolicina i cijena moraju biti broj
                fldDobavljac.getStyleClass().removeAll("poljeNijeIspravno");
                fldDobavljac.getStyleClass().add("poljeIspravno");
            } else {
                fldDobavljac.getStyleClass().removeAll("poljeIspravno");
                fldDobavljac.getStyleClass().add("poljeNijeIspravno");
            }
        });

    }

    public Proizvod getProduct() {
        return product;
    }

    public void setProduct(Proizvod product) {
        this.product = product;
    }

    public void actOk(ActionEvent actionEvent) {
        if(fldNaziv.getText().length() != 0  && (product == null  && !ProvjeraDaLiVecPostojiProizvod(fldNaziv.getText()) || (product != null )) && DaLiJeBroj(fldKolicina.getText()) && DaLiJeBroj(fldCijena.getText()) && ispravanDobavljac(fldDobavljac.getText()) && IspravnaKategorija(fldKategorija.getText()) && IspravanProizvodjac(fldProizvodjac.getText())) {
            if (product == null) {
                product = new Proizvod(fldNaziv.getText(), fldProizvodjac.getText(), fldKategorija.getText(), Integer.parseInt(fldCijena.getText()));
                kolicina = Integer.parseInt(fldKolicina.getText());
                product.setKolicina(Integer.parseInt(fldKolicina.getText()));
                product.setDobavljac(fldDobavljac.getText());
            }
            product = new Proizvod(fldNaziv.getText(), fldProizvodjac.getText(), fldKategorija.getText(), Integer.parseInt(fldCijena.getText()));
            kolicina = Integer.parseInt(fldKolicina.getText());
            product.setKolicina(Integer.parseInt(fldKolicina.getText()));
            product.setDobavljac(fldDobavljac.getText());
            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }

    }

    public void actCancel(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public int getKolicina(){
        return kolicina;
    }
}
