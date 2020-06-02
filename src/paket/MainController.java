package paket;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {

    public TableColumn colName;
    public TableColumn colManufacturer;
    public TableColumn colCategory;
    public SkladisteDAO model;
    public TableView tbProducts;
    public TableColumn colPrice;
    public ChoiceBox spinnerSkladiste;
    public  ObservableList<Proizvod> obsProizvodi = FXCollections.observableArrayList();
    private Skladiste sk;

    public MainController(SkladisteDAO model){
        this.model = model;
    }

    @FXML
    public void initialize(){
     //   this.model = new SkladisteDAO();
        colName.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("naziv"));
        colManufacturer.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("proizvodjac"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("kategorija"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Proizvod,Integer>("cijena"));

        ArrayList<Skladiste> skladista = model.getSkladista();
        spinnerSkladiste.getItems().clear();

        for(int i=0 ; i<skladista.size() ; i++){
            spinnerSkladiste.getItems().add(skladista.get(i).getNaziv());
        }

        spinnerSkladiste.getSelectionModel().select(skladista.get(0).getNaziv());
        sk = skladista.get(0);

        ArrayList<Proizvod> proizvods = model.getProizvodiSkladista(skladista.get(0));
        for(int j=0 ; j<proizvods.size() ; j++){
            obsProizvodi.add(proizvods.get(j));
        }

        tbProducts.setItems(obsProizvodi);

        spinnerSkladiste.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            obsProizvodi.clear();
            //System.out.println(newValue);
            for(int i=0 ; i<skladista.size() ; i++){
                if(newValue.equals(skladista.get(i).getNaziv())){
                    ArrayList<Proizvod> proizvodi = model.getProizvodiSkladista(skladista.get(i));
                    for(int j=0 ; j<proizvodi.size() ; j++){
                        obsProizvodi.add(proizvodi.get(j));
                    }
                    sk = skladista.get(i);
                    break;
                }

            }
            tbProducts.setItems(obsProizvodi);

        });

        tbProducts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Proizvod>() {
            @Override
            public void changed(ObservableValue<? extends Proizvod> observableValue, Proizvod oldBook, Proizvod newBook) {
                if (oldBook != null) {
                }
                if (newBook == null) {

                } else {
                    Proizvod pr = (Proizvod) tbProducts.getSelectionModel().getSelectedItem();
                    model.setCurrentBook(pr);
                }
                tbProducts.refresh();
            }
        });


    }


    public void actAdd(ActionEvent actionEvent) {
        Stage editProductWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit.fxml"));
            EditController editController = new EditController(null);
            loader.setController(editController);
            root = loader.load();
            editProductWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editProductWindow.setResizable(false);
            editProductWindow.show();


            editProductWindow.setOnHiding( event -> {
                Proizvod product = editController.getProduct();
                if (product != null) {
                    model.addProduct(product);
                    int kolicina = editController.getKolicina();
                    model.addProductWarehouse(product,sk,kolicina);
                    obsProizvodi.clear();
                    initialize();
                //    tbProducts.setItems(model.getProducts());

                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void actEdit(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit.fxml"));
        Proizvod proizvod = model.getCurrentProduct();
        EditController editController = new EditController(proizvod);
        Stage editBookWindow = new Stage();
        loader.setController(editController);
        Parent root = null;
        try {
            root = loader.load();
            editBookWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editBookWindow.setResizable(false);
            editBookWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editBookWindow.setOnHiding(Event -> {
            Proizvod p = editController.getProduct();
            if(p != null) {
                model.updateCurrentProduct(p);
                int kolicina = editController.getKolicina();
                obsProizvodi.clear();
                initialize();
            }
        });
    }

    @FXML
    public void actDelete(ActionEvent actionEvent) {
        if (model.getCurrentProduct() != null) {       // Samo ako se klikne na neku knjigu
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete this product?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteFromWarehouse(model.getCurrentProduct());
                model.deleteProduct();
                obsProizvodi.clear();
                initialize();
                tbProducts.getSelectionModel().selectLast();
            }
        }
    }
}
