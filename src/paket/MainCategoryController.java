package paket;

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
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainCategoryController {
    public TextField fldNaziv;
    public TextField fldNadKategorija;
    public TableView tbCategories;
    public TableColumn colName;
    public TableColumn colSupCategory;
    public SkladisteDAO model;
    public ObservableList<Kategorija> kategorije = FXCollections.observableArrayList();

    public MainCategoryController(){}

    @FXML
    public void initialize(){
        this.model = new SkladisteDAO();
        colName.setCellValueFactory(new PropertyValueFactory<Kategorija,String>("naziv"));
       // colName.setCellValueFactory(new PropertyValueFactory<Kategorija,Integer>("nadkategorija"));
        ArrayList<Kategorija> categories = model.getCategories();
        kategorije.clear();

        for(int i=0 ; i<categories.size() ; i++){
            kategorije.add(categories.get(i));
        }
      //  tbCategories.setItems(kategorije);
        tbCategories.setItems(kategorije);

        tbCategories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Kategorija>() {
            @Override
            public void changed(ObservableValue<? extends Kategorija> observableValue, Kategorija oldCategory, Kategorija newCategory) {
                if (oldCategory != null) {
                }
                if (newCategory == null) {

                } else {
                    Kategorija k = (Kategorija) tbCategories.getSelectionModel().getSelectedItem();
                    model.setCurrentCategory(k);
                }
                tbCategories.refresh();
            }
        });

    }

    public void actAdd(ActionEvent actionEvent) {
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editCategory.fxml"));
            EditCategoryController editCategoryController = new EditCategoryController(null);
            loader.setController(editCategoryController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding( event -> {
                Kategorija k = editCategoryController.getKategorija();
                if (k != null) {
                    model.addCategory(k);
                    initialize();
                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void actEdit(ActionEvent actionEvent) {
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editCategory.fxml"));
            Kategorija kat = model.getCurrentCategory();
            EditCategoryController editCategoryController = new EditCategoryController(kat);
            loader.setController(editCategoryController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding( event -> {
                Kategorija k = editCategoryController.getKategorija();
                if (k != null) {
                    model.updateCurrentCategory(k);
                    initialize();
                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actDelete(ActionEvent actionEvent) {
        if (model.getCurrentCategory() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete this category?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteCategory();
                initialize();
                tbCategories.getSelectionModel().selectLast();
            }
        }
    }
}
