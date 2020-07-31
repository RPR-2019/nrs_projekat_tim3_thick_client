package paket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditController {

    public TextField fldNaziv;
    public TextField fldCijena;
    public TextField fldKolicina;
    public Proizvod product = null;
    public Button btnCancel;
    public Button btnOk;
    public int kolicina = 0;
    public ChoiceBox<String> choiceProizvodjac;
    public ChoiceBox<String> choiceKategorija;
    public ChoiceBox<String> choiceDobavljac;
    private SkladisteDAO model;
    private Skladiste sk;
    private ObservableList<String> obsDobavljaci = FXCollections.observableArrayList();
    private ObservableList<Proizvodjac> obsManufacturers = FXCollections.observableArrayList();
    private ObservableList<Kategorija> obskategorije = FXCollections.observableArrayList();

    public static boolean DaLiJeBroj(String str){
        String regex = "[0-9]+";
        if(!(str.matches(regex))) return false;

        return true;
    }

    public ObservableList<Kategorija> DajKategorije(){
        ArrayList<Kategorija> categories = model.getCategories();

        for(int i=0 ; i<categories.size() ; i++){
            obskategorije.add(categories.get(i));
        }

        return obskategorije;
    }

    public ObservableList<Proizvodjac> DajProizvodjace(){
        ArrayList<Proizvodjac> manufacturers = model.getManufacturers();

        for(int i=0 ; i<manufacturers.size() ; i++){
            obsManufacturers.add(manufacturers.get(i));
        }

        return obsManufacturers;
    }

    public ObservableList<String> dajDobavljace(){
        ObservableList<Dobavljac> dobavljaci = model.getDobavljaci();
        ObservableList<String> dob = FXCollections.observableArrayList();
        for(int i=0 ; i<dobavljaci.size() ; i++){
            dob.add(dobavljaci.get(i).getNaziv());
        }
        return dob;
    }

    public boolean ProvjeraDaLiVecPostojiProizvod(String str,Skladiste sk){
        ArrayList<Proizvod> products = model.getProizvodiSkladista(sk);

        for(Proizvod p : products){
            if(p.getNaziv().equals(str)){
                return true;
            }
        }
        return false;
    }

    public Proizvodjac findProizvodjac(String proizvodjac){
        Proizvodjac p = new Proizvodjac();
        for(int i=0 ; i<obsManufacturers.size() ; i++){
            if(obsManufacturers.get(i).getNaziv().equals(proizvodjac)){
                p = obsManufacturers.get(i);
            }
        }
        return p;
    }

    public Kategorija findKategorija(String kategorija){
        Kategorija k = new Kategorija();
        for(int i=0 ; i<obskategorije.size() ; i++){
            if(obskategorije.get(i).getNaziv().equals(kategorija)){
                k = obskategorije.get(i);
            }
        }
        return k;
    }

    public Dobavljac findDobavljac(String dobavljac){
        ObservableList<Dobavljac> dobavljaci = model.getDobavljaci();
        Dobavljac d = new Dobavljac();
        for(int i=0 ; i<dobavljaci.size() ; i++){
            if(dobavljaci.get(i).getNaziv().equals(dobavljac)){
                d = dobavljaci.get(i);
            }
        }
        return d;
    }

    public EditController(Proizvod product,Skladiste sk){
        this.product = product;
        this.sk = sk;
    }

    public void initialize(){
        this.model = new SkladisteDAO();
        obsManufacturers = DajProizvodjace();
        ObservableList<String> manu = FXCollections.observableArrayList();
        for(int i=0 ; i<obsManufacturers.size() ; i++){
            manu.add(obsManufacturers.get(i).getNaziv());
        }
        obskategorije = DajKategorije();
        ObservableList<String> kate = FXCollections.observableArrayList();
        for(int i=0 ; i<obskategorije.size() ; i++){
            kate.add(obskategorije.get(i).getNaziv());
        }
        choiceProizvodjac.setItems(manu);
        choiceKategorija.setItems(kate);

        choiceDobavljac.setItems(dajDobavljace());
        if(product == null) {
            fldNaziv.setText("");
            fldCijena.setText("");
            fldKolicina.setText("");

            fldNaziv.getStyleClass().add("poljeNijeIspravno");
            fldCijena.getStyleClass().add("poljeNijeIspravno");
            fldKolicina.getStyleClass().add("poljeNijeIspravno");

        } else {
            fldNaziv.setText(product.getNaziv());
            choiceProizvodjac.setValue(product.getProizvodjac().getNaziv());
            String value = choiceProizvodjac.getSelectionModel().getSelectedItem().toString();
            System.out.println(product.getDobavljac());
            choiceKategorija.setValue(product.getKategorija().getNaziv());
            choiceDobavljac.setValue(product.getDobavljac().getNaziv());
            fldCijena.setText(String.valueOf(product.getCijena()));
            fldKolicina.setText(String.valueOf(product.getKolicina()));

            fldNaziv.getStyleClass().add("poljeIspravno");
            fldCijena.getStyleClass().add("poljeIspravno");
            fldKolicina.getStyleClass().add("poljeIspravno");

        }

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && !ProvjeraDaLiVecPostojiProizvod(newIme,sk) && newIme.length() > 3) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
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

    }

    public Proizvod getProduct() {
        return product;
    }

    public void actOk(ActionEvent actionEvent) {
        if(fldNaziv.getText().length() != 0  && (product == null  && !ProvjeraDaLiVecPostojiProizvod(fldNaziv.getText(),sk) || (product != null )) && DaLiJeBroj(fldKolicina.getText()) && DaLiJeBroj(fldCijena.getText())) {
            if(product == null) {
                product = new Proizvod();
            }
            product.setNaziv(fldNaziv.getText());
            product.setProizvodjac(findProizvodjac(choiceProizvodjac.getSelectionModel().getSelectedItem().toString()));
            product.setKategorija(findKategorija(choiceKategorija.getSelectionModel().getSelectedItem().toString()));
            product.setCijena(Integer.parseInt(fldCijena.getText()));
            kolicina = Integer.parseInt(fldKolicina.getText());
            product.setKolicina(Integer.parseInt(fldKolicina.getText()));
            product.setDobavljac(findDobavljac(choiceDobavljac.getValue().toString()));
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
