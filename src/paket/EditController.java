package paket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditController {

    public TextField fldNaziv;
    public TextField fldProizvodjac;
    public TextField fldKategorija;
    public TextField fldCijena;
    public Proizvod product = null;
    public Button btnCancel;
    public Button btnOk;

    public EditController(Proizvod product){
        this.product = product;
    }

    public void initialize(){
        fldNaziv.setText("");
        fldKategorija.setText("");
        fldCijena.setText("");
        fldProizvodjac.setText("");
    }

    public void actOk(ActionEvent actionEvent) {
        if(product == null) {
            product = new Proizvod();
        }
        product.setNaziv(fldNaziv.getText());
        product.setKategorija(fldKategorija.getText());
        product.setCijena(Integer.parseInt(fldCijena.getText()));
        product.setProizvodjac(fldProizvodjac.getText());

        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void actCancel(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public Proizvod getProduct() {
        return product;
    }

    public void setProduct(Proizvod product) {
        this.product = product;
    }
}
