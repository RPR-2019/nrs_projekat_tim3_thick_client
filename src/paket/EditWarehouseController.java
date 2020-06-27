package paket;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class EditWarehouseController {

    public TextField fldNaziv;
    public TextField fldLokacija;
    public Button btnOk;
    public Button btnCancel;
    private Skladiste skladiste;
    private SkladisteDAO model;

    public EditWarehouseController(Skladiste skladiste){
        this.skladiste = skladiste;
    }

    public boolean provjeraDaLiPostojiSkl(String str){
        ArrayList<Skladiste> skladista = model.getSkladista();

        for(Skladiste s : skladista){
            if(s.getNaziv().equals(str)) return false;
        }
        return true;
    }

    public void initialize(){
        this.model = new SkladisteDAO();
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

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && newIme.length() > 3 && provjeraDaLiPostojiSkl(newIme)) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldLokacija.textProperty().addListener((obs, oldIme, newIme) -> {
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
        if(fldNaziv.getText().length() > 3 && fldLokacija.getText().length() > 3 && provjeraDaLiPostojiSkl(fldNaziv.getText())) {
            skladiste = new Skladiste(fldNaziv.getText(), fldLokacija.getText());
            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
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
