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
   // public TextField fldKolicina;
    public Proizvod product = null;
    public Button btnCancel;
    public Button btnOk;
    public int kolicina = 0;

    public EditController(Proizvod product){
        this.product = product;
    }

    public void initialize(){
        if(product == null) {
            fldNaziv.setText("");
            fldKategorija.setText("");
            fldCijena.setText("");
            fldProizvodjac.setText("");
       //     fldKolicina.setText("");
        } else {
            fldNaziv.setText(product.getNaziv());
            fldProizvodjac.setText(product.getProizvodjac());
            fldKategorija.setText(product.getKategorija());
            fldCijena.setText(String.valueOf(product.getCijena()));
         //   fldKolicina.setText(String.valueOf(1));
        }
    }

    public Proizvod getProduct() {
        return product;
    }

    public void setProduct(Proizvod product) {
        this.product = product;
    }

    public void actOk(ActionEvent actionEvent) {
            if(product == null){
                product = new Proizvod(fldNaziv.getText(),fldProizvodjac.getText(),fldKategorija.getText(),Integer.parseInt(fldCijena.getText()));
             //   kolicina = Integer.parseInt(fldKolicina.getText());

            }
        product = new Proizvod(fldNaziv.getText(),fldProizvodjac.getText(),fldKategorija.getText(),Integer.parseInt(fldCijena.getText()));
      //  kolicina = Integer.parseInt(fldKolicina.getText());

        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();

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
