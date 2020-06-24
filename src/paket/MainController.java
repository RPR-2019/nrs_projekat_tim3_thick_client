package paket;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class MainController {

    public TableColumn colName;
    public TableColumn colManufacturer;
    public TableColumn colCategory;
    public TableColumn colKolicina;
    public SkladisteDAO model;
    public SkladisteDAO modelKat;
    public SkladisteDAO modelEmp;
    public TableView tbProducts;
    public TableColumn colPrice;
    public ChoiceBox spinnerSkladiste;
    public  ObservableList<Proizvod> obsProizvodi = FXCollections.observableArrayList();
    public Tab tabCategories;
    public TableView tbEmployees;
    public TableColumn<Osobe,String> colFullName;
    public TableColumn colPhone;
    public TableColumn colDatum_zapsl;
    public TableColumn colJMBG;
    public TableColumn colNazivLok;
    public TableView tbWarehouses;
    public TableColumn colNazivWarehouse;
    public TableColumn colLocationWarehouse;
    private Skladiste sk;
    private int br=0;
    public ObservableList<Kategorija> kategorije = FXCollections.observableArrayList();
    public TableView tbCategories;
    public TableColumn colNameCat;

    public MainController(SkladisteDAO model){
        this.model = model;
    }

    @FXML
    public void initialize(){
        this.model = new SkladisteDAO();
        colName.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("naziv"));
        colManufacturer.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("proizvodjac"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("kategorija"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Proizvod,Integer>("cijena"));

        obsProizvodi.clear();
        ArrayList<Skladiste> skladista = model.getSkladista();
        String skl = "";
        if(spinnerSkladiste.getItems().size() != 0) {
            skl = spinnerSkladiste.getSelectionModel().getSelectedItem().toString();  // Prije nego sto se obrise spasi zadnji selektovani item
            spinnerSkladiste.getItems().clear();
        }

        br++;

        for(int i=0 ; i<skladista.size() ; i++){
            spinnerSkladiste.getItems().add(skladista.get(i).getNaziv());
        }

        if(skl != null) spinnerSkladiste.getSelectionModel().select(skl);


        sk = skladista.get(0);

        if(br == 1){
            ArrayList<Proizvod> proizvods = model.getProizvodiSkladista(skladista.get(0));
            for(int j=0 ; j<proizvods.size() ; j++){
                obsProizvodi.add(proizvods.get(j));
            }
            spinnerSkladiste.getSelectionModel().select(skladista.get(0).getNaziv());
        }


        tbProducts.setItems(obsProizvodi);
    //    tbProducts.setItems(obsKolicine);

            spinnerSkladiste.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    obsProizvodi.clear();
                //    System.out.println(oldValue + " " + newValue);
                    //   System.out.println(newValue);
                    if(newValue != null) {
                        spinnerSkladiste.getSelectionModel().select(newValue);
                        for (int i = 0; i < skladista.size(); i++) {
                         //   System.out.println(newValue + " " + skladista.get(i).getNaziv());
                            if (newValue.equals(skladista.get(i).getNaziv())) {
                                ArrayList<Proizvod> proizvodi = model.getProizvodiSkladista(skladista.get(i));
                                for (int j = 0; j < proizvodi.size(); j++) {
                                    obsProizvodi.add(proizvodi.get(j));
                                }
                                sk = skladista.get(i);
                                break;
                            }
                        }
                    }
                }
            });
            
        tbProducts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Proizvod>() {
            @Override
            public void changed(ObservableValue<? extends Proizvod> observableValue, Proizvod oldProduct, Proizvod newProduct) {
                if (oldProduct != null) {
                }
                if (newProduct == null) {

                } else {
                    Proizvod pr = (Proizvod) tbProducts.getSelectionModel().getSelectedItem();
                    model.setCurrentBook(pr);
                }
                tbProducts.refresh();
            }
        });

       this.modelKat = new SkladisteDAO();

        colNameCat.setCellValueFactory(new PropertyValueFactory<Kategorija,String>("naziv"));
        // colName.setCellValueFactory(new PropertyValueFactory<Kategorija,Integer>("nadkategorija"));
        ArrayList<Kategorija> categories = modelKat.getCategories();
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

        colFullName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIme() + " " + cellData.getValue().getPrezime()));
        colPhone.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("Telefon"));
        colDatum_zapsl.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("datum_zaposljavanja"));
        colJMBG.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("JMBG"));
        colNazivLok.setCellValueFactory(new PropertyValueFactory<Proizvod,String>("naziv_lokacije"));

        modelEmp = new SkladisteDAO();
        tbEmployees.setItems(modelEmp.getEmployees());

        tbEmployees.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Osobe>() {
            @Override
            public void changed(ObservableValue<? extends Osobe> observableValue, Osobe oldOsoba, Osobe newOsoba) {
                if (oldOsoba != null) {
                }
                if (newOsoba == null) {

                } else {
                    Osobe o = (Osobe) tbEmployees.getSelectionModel().getSelectedItem();
                    model.setCurrentEmployee(o);
                }
                tbEmployees.refresh();
            }
        });

        colNazivWarehouse.setCellValueFactory(new PropertyValueFactory<Skladiste,String>("naziv"));
        colLocationWarehouse.setCellValueFactory(new PropertyValueFactory<Skladiste,String>("naziv_lokacije"));

        ObservableList<Skladiste> skladistes = FXCollections.observableArrayList();

        for(int i=0 ; i<skladista.size() ; i++)
            skladistes.add(skladista.get(i));
        tbWarehouses.setItems(skladistes);

        tbWarehouses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Skladiste>() {
            @Override
            public void changed(ObservableValue<? extends Skladiste> observableValue, Skladiste oldWarehouse, Skladiste newWarehouse) {
                if (oldWarehouse != null) {
                }
                if (newWarehouse == null) {

                } else {
                    Skladiste sk = (Skladiste) tbWarehouses.getSelectionModel().getSelectedItem();
                    model.setCurrentWarehouse(sk);
                }
                tbWarehouses.refresh();
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
                //    int kolicina = editController.getKolicina();
                //    product.setKolicina(kolicina);
                    model.addProduct(product,sk);
                  //  model.addProductWarehouse(product,sk,kolicina);
             //       obsProizvodi.clear();
                    initialize();
              //      tbProducts.getItems().clear();
             //       model = new SkladisteDAO();
            //        tbProducts.setItems(model.getProducts());

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
           //     int kolicina = editController.getKolicina();
           //     p.setKolicina(kolicina);
           //     model.updateCurrentProductWarehouse(p,sk.getId(),kolicina);
                model.updateCurrentProduct(p);
                obsProizvodi.clear();
                initialize();
            }
        });
    }

    @FXML
    public void actDelete(ActionEvent actionEvent) {
        if (model.getCurrentProduct() != null) {
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

    public void actIzvjestaj(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showReport(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actAddCat(ActionEvent actionEvent) {
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

    public void actEditCat(ActionEvent actionEvent) {
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

    public void actDeleteCat(ActionEvent actionEvent) {
        if (model.getCurrentCategory() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete this category?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteCategory();
                System.out.println("OMG");
                initialize();
                tbCategories.getSelectionModel().selectLast();
            }
        }
    }

    public void actAddEmployee(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEmployee.fxml"));
        EditEmployeeController editController = new EditEmployeeController(null);
        Stage editEmployeeWindow = new Stage();
        loader.setController(editController);
        Parent root = null;
        try {
            root = loader.load();
            editEmployeeWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editEmployeeWindow.setResizable(false);
            editEmployeeWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editEmployeeWindow.setOnHiding(Event -> {
            Osobe o = editController.getEmployee();
            if(o != null) {
                model.addEmployee(o);
                initialize();
            }
        });
    }

    public void actEditEmployee(ActionEvent actionEvent) {
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEmployee.fxml"));
            Osobe o = model.getCurrentEmployee();
            EditEmployeeController editController = new EditEmployeeController(o);
            loader.setController(editController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding( event -> {
                Osobe employee = editController.getEmployee();
                if (employee != null) {
                    model.updateCurrentEmployee(employee);
                    initialize();
                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actDeleteEmployee(ActionEvent actionEvent) {
        if (model.getCurrentEmployee() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete this employee?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteEmployee();
              //  System.out.println("OMG");
                initialize();
                tbEmployees.getSelectionModel().selectLast();
            }
        }
    }

    public void actIzvjestajEmployees(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showEmployeeReport(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actEmployeesLastMonth(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showEmployeeLastMonthReport(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actAvailableProducts(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showAvailableProducts(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actAddWarehouse(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editWarehouse.fxml"));
        EditWarehouseController editController = new EditWarehouseController(null);
        Stage editWarehouseWindow = new Stage();
        loader.setController(editController);
        Parent root = null;
        try {
            root = loader.load();
            editWarehouseWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editWarehouseWindow.setResizable(false);
            editWarehouseWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editWarehouseWindow.setOnHiding(Event -> {
            Skladiste sk = editController.getSkladiste();
            if(sk != null) {
                try {
                    model.addSkladiste(sk);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                initialize();
            }
        });
    }

    public void actEditWarehouse(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editWarehouse.fxml"));
        Skladiste skl = model.getCurrentWarehouse();
        EditWarehouseController editController = new EditWarehouseController(skl);
        Stage editWarehouseWindow = new Stage();
        loader.setController(editController);
        Parent root = null;
        try {
            root = loader.load();
            editWarehouseWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editWarehouseWindow.setResizable(false);
            editWarehouseWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editWarehouseWindow.setOnHiding(Event -> {
            Skladiste sk = editController.getSkladiste();
            if(sk != null) {
                try {
                    model.updateSkladiste(sk);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                initialize();
            }
        });
    }

    public void actDeleteWarehouse(ActionEvent actionEvent) {
        if (model.getCurrentWarehouse() != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("");
            alert.setContentText("Are you sure you want to delete this warehouse?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                model.deleteWarehouse();
                //  System.out.println("OMG");
                initialize();
                tbWarehouses.getSelectionModel().selectLast();
            }
        }
    }

    public void actMostPopularProduct(ActionEvent actionEvent) {
        try {
            new Izvjestaj().showMostPopularProduct(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }
}
