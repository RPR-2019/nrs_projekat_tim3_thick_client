package paket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {

    public TableColumn colName;
    public TableColumn colManufacturer;
    public TableColumn colCategory;
    public SkladisteDAO model;
    public TableView tbProducts;
    public TableColumn colPrice;
    public SkladisteDAO skladisteDAO;

    public MainController(SkladisteDAO skladisteDAO){
        this.skladisteDAO = skladisteDAO;
    }

    @FXML
    public void initialize(){
        this.model = new SkladisteDAO();
        colName.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("naziv"));
        colManufacturer.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("proizvodjac"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("kategorija"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Proizvod,Integer>("cijena"));
        tbProducts.setItems(model.getProducts());


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


            editProductWindow.setOnHiding(Event -> {
                Proizvod p = editController.getProduct();
                model.addProduct(p);
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
            model.updateCurrentProduct(p);
        });
    }

    @FXML
    public void actDelete(ActionEvent actionEvent) {
        if (model.getCurrentProduct() != null) {       // Samo ako se klikne na neku knjigu
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you ok with this?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteProduct();
                tbProducts.getSelectionModel().selectFirst();
            }
        }
    }
}
