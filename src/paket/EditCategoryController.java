package paket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditCategoryController {
    public TextField fldNaziv;
    public Button btnOk;
    public Kategorija kategorija = null;
    public SkladisteDAO model;
    public ChoiceBox<String> choiceNadKat;
    private ObservableList<String> obskategorije = FXCollections.observableArrayList();

    public EditCategoryController(Kategorija kategorija){
        this.kategorija = kategorija;
    }

    public ObservableList<String> DajKategorije(){
        ObservableList<Kategorija> categories = model.getCategories();

        for(int i=0 ; i<categories.size() ; i++){
            obskategorije.add(categories.get(i).getNaziv());
        }

        return obskategorije;
    }



    @FXML
    public void initialize(){
        model = new SkladisteDAO();
        choiceNadKat.setItems(DajKategorije());
      //  choiceNadKat.getItems().add(0,null);

        if(kategorija == null){
            choiceNadKat.setValue("");
            fldNaziv.getStyleClass().add("poljeNijeIspravno");
        }
        else {
            fldNaziv.setText(kategorija.getNaziv());
            choiceNadKat.setValue(kategorija.getNadKategorija());
            fldNaziv.getStyleClass().add("poljeIspravno");
        }

        fldNaziv.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && !provjeraKat(newIme)) {
                fldNaziv.getStyleClass().removeAll("poljeNijeIspravno");
                fldNaziv.getStyleClass().add("poljeIspravno");
            } else {
                fldNaziv.getStyleClass().removeAll("poljeIspravno");
                fldNaziv.getStyleClass().add("poljeNijeIspravno");
            }
        });

    }

    public boolean provjeraKat(String str){
        ObservableList<Kategorija> categories = model.getCategories();
        boolean postoji = false;

        for(int i=0 ; i<categories.size() ; i++){
            if(categories.get(i).getNaziv().equals(str)){
                postoji = true;
                break;
            }
        }

        return postoji;
    }

    public void actOk(ActionEvent actionEvent) {
        if(fldNaziv.getText().length() !=0 && !provjeraKat(fldNaziv.getText())){
            if(kategorija == null) kategorija = new Kategorija();
            kategorija.setNaziv(fldNaziv.getText());
            kategorija.setNadKategorija(choiceNadKat.getValue().toString());
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
