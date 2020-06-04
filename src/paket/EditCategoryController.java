package paket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditCategoryController {
    public TextField fldNaziv;
    public TextField fldNadKategorija;
    public Button btnOk;
    public Kategorija kategorija = null;

    public EditCategoryController(Kategorija kategorija){
        this.kategorija = kategorija;
    }

    @FXML
    public void initialize(){
        if(kategorija != null){
            fldNaziv.setText(kategorija.getNaziv());
            fldNadKategorija.setText(kategorija.getNadKategorija());
        }
    }

    public void actOk(ActionEvent actionEvent) {
        if(kategorija == null){
            kategorija = new Kategorija(fldNaziv.getText(),fldNadKategorija.getText());
        }
        kategorija = new Kategorija(fldNaziv.getText(),fldNadKategorija.getText());
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
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
