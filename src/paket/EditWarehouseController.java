package paket;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditWarehouseController {

    public TextField fldNaziv;
    public TextField fldLokacija;
    public Button btnOk;
    public Button btnCancel;
    private Skladiste skladiste;

    public EditWarehouseController(Skladiste skladiste){
        this.skladiste = skladiste;
    }

    public void initialize(){
        if(skladiste == null){
            fldNaziv.getStyleClass().add("poljeNijeIspravno");
            fldLokacija.getStyleClass().add("poljeNijeIspravno");
        }
        else {
            fldNaziv.getStyleClass().add("poljeIspravno");
            fldLokacija.getStyleClass().add("poljeIspravno");

            fldNaziv.setText(skladiste.getNaziv());
            fldLokacija.setText(skladiste.getNaziv_lokacije());
        }

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && newIme.length() > 3) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldLokacija.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && newIme.length() > 3) {
                fldLokacija.getStyleClass().removeAll("poljeNijeIspravno");
                fldLokacija.getStyleClass().add("poljeIspravno");
            } else {
                fldLokacija.getStyleClass().removeAll("poljeIspravno");
                fldLokacija.getStyleClass().add("poljeNijeIspravno");
            }
        });
    }

    public void actOk(ActionEvent actionEvent) {
        skladiste = new Skladiste(fldNaziv.getText(),fldLokacija.getText());
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }

    public void actCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public Skladiste getSkladiste() {
        return skladiste;
    }

    public void setSkladiste(Skladiste skladiste) {
        this.skladiste = skladiste;
    }
}
