package paket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditCategoryController {
    public TextField fldNaziv;
    public TextField fldNadKategorija;
    public Button btnOk;
    public Kategorija kategorija = null;
    public SkladisteDAO model;

    public EditCategoryController(Kategorija kategorija){
        this.kategorija = kategorija;
    }

    @FXML
    public void initialize(){
        model = new SkladisteDAO();
        if(kategorija == null){
            fldNaziv.getStyleClass().add("poljeNijeIspravno");
        }
        else {
            fldNaziv.setText(kategorija.getNaziv());
            fldNadKategorija.setText(kategorija.getNadKategorija());

            fldNaziv.getStyleClass().add("poljeIspravno");
            fldNadKategorija.getStyleClass().add("poljeIspravno");
        }

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty()) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });

        fldNadKategorija.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && provjeriNadKategoriju(newIme)) {            // Nadkategorija mora biti jednaka nekoj kategoriji
                fldNadKategorija.getStyleClass().removeAll("poljeNijeIspravno");
                fldNadKategorija.getStyleClass().add("poljeIspravno");
            } else {
                fldNadKategorija.getStyleClass().removeAll("poljeIspravno");
                fldNadKategorija.getStyleClass().add("poljeNijeIspravno");
            }
        });

    }

    public boolean provjeriNadKategoriju(String str){
        ArrayList<Kategorija> categories = model.getCategories();
        boolean validno = false;

        for(int i=0 ; i<categories.size() ; i++){
            if(categories.get(i).getNaziv().equals(str)){
                validno = true;
                break;
            }
        }

        return validno;
    }

    public void actOk(ActionEvent actionEvent) {
        if(fldNaziv.getText().length() !=0 && (provjeriNadKategoriju(fldNadKategorija.getText()) || fldNadKategorija.getText().length() == 0)) {
            kategorija = new Kategorija(fldNaziv.getText(), fldNadKategorija.getText());
         //   System.out.println(kategorija.getNaziv() + "  " + kategorija.getNadKategorija());
            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
    }

    public Kategorija getKategorija(){
        return kategorija;
    }

    public void actCancel(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }
}
