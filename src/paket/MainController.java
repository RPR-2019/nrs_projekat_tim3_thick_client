package paket;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

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
    public ObservableList<Proizvod> obsProizvodi = FXCollections.observableArrayList();
    public Tab tabCategories;
    public TableView tbEmployees;
    public TableColumn<Osobe, String> colFullName;
    public TableColumn colPhone;
    public TableColumn colDatum_zapsl;
    public TableColumn colJMBG;
    public TableColumn colNazivLok;
    public TableView tbWarehouses;
    public TableColumn colNazivWarehouse;
    public TableColumn colLocationWarehouse;
    public TabPane tabPane;
    public TableView tbAvailableProducts;
    public TableColumn colAvName;
    public TableColumn colAvManufacturer;
    public TableColumn colAvCategory;
    public TableColumn colAvPrice;
    public ChoiceBox spinnerSkl;
    public TextField fldPretraga;
    private Skladiste sk;
    private Skladiste sk1;
    private int br = 0;
    public ObservableList<Kategorija> kategorije = FXCollections.observableArrayList();
    public TableView tbCategories;
    public TableColumn colNameCat;
    public ChoiceBox spinnerLanguage;


    // public MainController(SkladisteDAO model){
    //     this.model = model;
    // }

    @FXML
    public void initialize() {
        this.model = new SkladisteDAO();
        colName.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("naziv"));
        colManufacturer.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("proizvodjac"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("kategorija"));
        colPrice.setCellValueFactory(new PropertyValueFactory<Proizvod, Integer>("cijena"));

        obsProizvodi.clear();
        ObservableList<Skladiste> skladista = model.getSkladista();
        String skl = "";
        if (spinnerSkladiste.getItems().size() != 0) {
            skl = spinnerSkladiste.getSelectionModel().getSelectedItem().toString();  // Prije nego sto se obrise spasi zadnji selektovani item
            spinnerSkladiste.getItems().clear();
        }

        br++;

        for (int i = 0; i < skladista.size(); i++) {
            spinnerSkladiste.getItems().add(skladista.get(i).getNaziv());
        }

        if (skl != null) spinnerSkladiste.getSelectionModel().select(skl);


        sk = skladista.get(0);

        if (br == 1) {
            ArrayList<Proizvod> proizvods = model.getProizvodiSkladista(skladista.get(0));
            for (int j = 0; j < proizvods.size(); j++) {
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
                if (newValue != null) {
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
                    tbProducts.getItems().clear();
                    ArrayList<Proizvod> ps = model.getProizvodiSkladista(sk);
                    ObservableList<Proizvod> pr = FXCollections.observableArrayList();
                    for(int i=0 ; i<ps.size() ; i++) pr.add(ps.get(i));
                    tbProducts.setItems(pr);
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

        colNameCat.setCellValueFactory(new PropertyValueFactory<Kategorija, String>("naziv"));
        // colName.setCellValueFactory(new PropertyValueFactory<Kategorija,Integer>("nadkategorija"));
        ObservableList<Kategorija> categories = modelKat.getCategories();
        kategorije.clear();

        for (int i = 0; i < categories.size(); i++) {
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
        colPhone.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("Telefon"));
        colDatum_zapsl.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("datum_zaposljavanja"));
        colJMBG.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("JMBG"));
        colNazivLok.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("naziv_lokacije"));

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

        colNazivWarehouse.setCellValueFactory(new PropertyValueFactory<Skladiste, String>("naziv"));
        colLocationWarehouse.setCellValueFactory(new PropertyValueFactory<Skladiste, String>("naziv_lokacije"));

        ObservableList<Skladiste> skladistes = FXCollections.observableArrayList();

        for (int i = 0; i < skladista.size(); i++)
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

        if (spinnerLanguage.getItems().size() != 0) {
            spinnerLanguage.getItems().clear();
        }

        spinnerLanguage.getSelectionModel().select(Locale.getDefault().getLanguage());
        spinnerLanguage.getItems().add("bs");
        spinnerLanguage.getItems().add("en");


        spinnerLanguage.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (newValue != null && oldValue != null) {   // Potrebno i oldValue!=null inace ce me vratiti na prvo skladiste uvijek
                    //         spinnerLanguage.getSelectionModel().select(newValue);
                    // System.out.println(oldValue + " " + newValue);
                    if (newValue.equals("bs")) {
                        //  spinnerLanguage.getSelectionModel().select("Bosnian");
                        Locale.setDefault(new Locale("bs", "BA"));
                        try {
                            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                            Stage stage = (Stage) tabPane.getScene().getWindow();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), bundle);
                            stage.setScene(new Scene(loader.load()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Locale.setDefault(new Locale("en", "US"));
                        try {
                            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
                            Stage stage = (Stage) tabPane.getScene().getWindow();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"), bundle);
                            stage.setScene(new Scene(loader.load()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        colAvName.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("naziv"));
        colAvManufacturer.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("proizvodjac"));
        colAvCategory.setCellValueFactory(new PropertyValueFactory<Proizvod, String>("kategorija"));
        colAvPrice.setCellValueFactory(new PropertyValueFactory<Proizvod, Integer>("cijena"));

        ObservableList<Proizvod> dostupni = FXCollections.observableArrayList();

        String skl1 = "";
        if (spinnerSkl.getItems().size() != 0) {
            skl1 = spinnerSkl.getSelectionModel().getSelectedItem().toString();  // Prije nego sto se obrise spasi zadnji selektovani item
            spinnerSkl.getItems().clear();
        }


        for (int i = 0; i < skladista.size(); i++) {
            spinnerSkl.getItems().add(skladista.get(i).getNaziv());
        }

        if (skl1 != null) spinnerSkl.getSelectionModel().select(skl1);


        sk1 = skladista.get(0);
        dostupni.clear();
   //     if (br == 1) {
            ArrayList<Proizvod> proizvods = model.getProizvodiSkladista(sk);
            for (int j = 0; j < proizvods.size(); j++) {
                if (proizvods.get(j).getKolicina() > 0) {
                    dostupni.add(proizvods.get(j));
                }
            }
            if(br == 1)
            spinnerSkl.getSelectionModel().select(skladista.get(0).getNaziv());
     //   }

        FilteredList<Proizvod> filteredData = new FilteredList<>(dostupni,b->true);

        fldPretraga.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(proizvod -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (proizvod.getNaziv().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false; // Does not match.
            });
        });

        SortedList<Proizvod> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tbAvailableProducts.comparatorProperty());

        tbAvailableProducts.setItems(sortedData);
        //    tbProducts.setItems(obsKolicine);

        spinnerSkl.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                dostupni.clear();
                //    System.out.println(oldValue + " " + newValue);
                //   System.out.println(newValue);
                if (newValue != null) {
                    spinnerSkl.getSelectionModel().select(newValue);
                    for (int i = 0; i < skladista.size(); i++) {
                        //   System.out.println(newValue + " " + skladista.get(i).getNaziv());
                        if (newValue.equals(skladista.get(i).getNaziv())) {
                            ArrayList<Proizvod> proizvodi = model.getProizvodiSkladista(skladista.get(i));
                            for (int j = 0; j < proizvodi.size(); j++) {
                                if (proizvodi.get(j).getKolicina() > 0) {
                                    dostupni.add(proizvodi.get(j));
                                }
                            }
                            sk1 = skladista.get(i);
                            break;
                        }
                    }
                //    if(tbAvailableProducts.getItems().size() != 0)
              //      tbAvailableProducts.getItems().clear();
                    ArrayList<Proizvod> ps = model.getProizvodiSkladista(sk1);
                    ObservableList<Proizvod> pr = FXCollections.observableArrayList();
                    for(int i=0 ; i<ps.size() ; i++) pr.add(ps.get(i));
                    tbAvailableProducts.setItems(pr);
                    pomocna();
                }
            }
        });

    }

    public void pomocna(){
        ObservableList<Proizvod> dostupni = FXCollections.observableArrayList();

     //   sk1 = skladista.get(0);
        dostupni.clear();
        //     if (br == 1) {
        ArrayList<Proizvod> proizvods = model.getProizvodiSkladista(sk1);
        for (int j = 0; j < proizvods.size(); j++) {
            if (proizvods.get(j).getKolicina() > 0) {
                dostupni.add(proizvods.get(j));
            }
        }

        FilteredList<Proizvod> filteredData = new FilteredList<>(dostupni,b->true);

        fldPretraga.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(proizvod -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (proizvod.getNaziv().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false; // Does not match.
            });
        });

        SortedList<Proizvod> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tbAvailableProducts.comparatorProperty());

        tbAvailableProducts.setItems(sortedData);
    }

    public void actAdd(ActionEvent actionEvent) {
        Stage editProductWindow = new Stage();
        Parent root = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("Translation");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit.fxml"), bundle);
            EditController editController = new EditController(null,sk);
            loader.setController(editController);
            root = loader.load();
            editProductWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editProductWindow.setResizable(false);
            editProductWindow.show();


            editProductWindow.setOnHiding(event -> {
                Proizvod product = editController.getProduct();
                if (product != null) {
                    //    int kolicina = editController.getKolicina();
                    //    product.setKolicina(kolicina);
                    model.addProduct(product, sk,product.getDobavljac());
                    //  model.addProductWarehouse(product,sk,kolicina);
                    //       obsProizvodi.clear();
                  //  initialize();
                          tbProducts.getItems().clear();
                    //       model = new SkladisteDAO();
                    //        tbProducts.setItems(model.getProducts());
                    ArrayList<Proizvod> ps = model.getProizvodiSkladista(sk);
                    ObservableList<Proizvod> p = FXCollections.observableArrayList();
                    for(int i=0 ; i<ps.size() ; i++) p.add(ps.get(i));
                    tbProducts.setItems(p);
                    pomocna();

                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actEdit(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit.fxml"), bundle);
        Proizvod proizvod = model.getCurrentProduct();
        EditController editController = new EditController(proizvod,sk);
        Stage editProductWindow = new Stage();
        loader.setController(editController);
        Parent root = null;
        try {
            root = loader.load();
            editProductWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editProductWindow.setResizable(false);
            editProductWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        editProductWindow.setOnHiding(Event -> {
            Proizvod p = editController.getProduct();
            if (p != null && editController.getCancel() == false) {
                //     int kolicina = editController.getKolicina();
                //     p.setKolicina(kolicina);
                //     model.updateCurrentProductWarehouse(p,sk.getId(),kolicina);
                Dobavljac dob = editController.getDobavljac();
                model.updateCurrentProduct(p,sk,p.getDobavljac(),dob);
                obsProizvodi.clear();
                ArrayList<Proizvod> ps = model.getProizvodiSkladista(sk);
                ObservableList<Proizvod> pr = FXCollections.observableArrayList();
                for(int i=0 ; i<ps.size() ; i++) pr.add(ps.get(i));
                tbProducts.setItems(pr);
                pomocna();
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
             //   model.deleteFromWarehouse(model.getCurrentProduct());
                model.deleteProduct(model.getCurrentProduct(),sk);
                obsProizvodi.clear();
                ArrayList<Proizvod> ps = model.getProizvodiSkladista(sk);
                ObservableList<Proizvod> pr = FXCollections.observableArrayList();
                for(int i=0 ; i<ps.size() ; i++) pr.add(ps.get(i));
                tbProducts.setItems(pr);
                tbProducts.getSelectionModel().selectLast();
                pomocna();

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
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editCategory.fxml"), bundle);
            EditCategoryController editCategoryController = new EditCategoryController(null);
            loader.setController(editCategoryController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding(event -> {
                Kategorija k = editCategoryController.getKategorija();
                if (k != null) {
                    model.addCategory(k);
                    tbCategories.getItems().clear();
                    tbCategories.setItems(model.getCategories());

                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void actEditCat(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editCategory.fxml"), bundle);
            Kategorija kat = model.getCurrentCategory();
            EditCategoryController editCategoryController = new EditCategoryController(kat);
            loader.setController(editCategoryController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding(event -> {
                Kategorija k = editCategoryController.getKategorija();
                if (k != null) {
                    model.updateCurrentCategory(k);
                    tbCategories.getItems().clear();
                    tbCategories.setItems(model.getCategories());
                }

            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean DaLiProizvodImaKat(Kategorija k){
        ArrayList<Proizvod> proizvodi = model.getProizvodiSkladista(sk);
        for(Proizvod p : proizvodi){
            if(p.getKategorija().getNaziv().equals(k.getNaziv())){
                System.out.println(p.getNaziv() + " " + p.getKategorija().getNaziv());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("");
                alert.setContentText("There are products with this category!");
                alert.showAndWait();
                return true;
            }
        }
        return false;
    }

    public void actDeleteCat(ActionEvent actionEvent) {
        if (model.getCurrentCategory() != null) {
            boolean imali = DaLiProizvodImaKat(model.getCurrentCategory());
            if (imali == false) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("");
                alert.setContentText("Are you sure you want to delete this category?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    model.deleteCategory(model.getCurrentCategory());
                    System.out.println("OMG");
                    tbCategories.getItems().clear();
                    tbCategories.setItems(model.getCategories());
                    tbCategories.getSelectionModel().selectLast();
                }
            }
        }
    }

    public void actAddEmployee(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEmployee.fxml"), bundle);
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
            if (o != null) {
                model.addEmployee(o);
                tbEmployees.getItems().clear();
                tbEmployees.setItems(model.getEmployees());
            }
        });
    }

    public void actEditEmployee(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        Stage editCategoryWindow = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editEmployee.fxml"), bundle);
            Osobe o = model.getCurrentEmployee();
            EditEmployeeController editController = new EditEmployeeController(o);
            loader.setController(editController);
            root = loader.load();
            editCategoryWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            editCategoryWindow.setResizable(false);
            editCategoryWindow.show();


            editCategoryWindow.setOnHiding(event -> {
                Osobe employee = editController.getEmployee();
                if (employee != null) {
                    model.updateCurrentEmployee(employee);
                    tbEmployees.getItems().clear();
                    tbEmployees.setItems(model.getEmployees());
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
                model.deleteEmployee(model.getCurrentEmployee());
                //  System.out.println("OMG");
                tbEmployees.getItems().clear();
                tbEmployees.setItems(model.getEmployees());
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
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editWarehouse.fxml"), bundle);
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
            if (sk != null) {
                try {
                    model.addSkladiste(sk);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                tbWarehouses.getItems().clear();
                tbWarehouses.setItems(model.getSkladista());
            }
        });
    }

    public void actEditWarehouse(ActionEvent actionEvent) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editWarehouse.fxml"), bundle);
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
            if (sk != null) {
                try {
                    model.updateSkladiste(sk);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                tbWarehouses.getItems().clear();
                tbWarehouses.setItems(model.getSkladista());
            }
        });
    }

    public boolean ImaLiWarehouseIteme(Skladiste sk){
        ArrayList<Proizvod> skk = model.getProizvodiSkladista(sk);

        if(skk.size() != 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("");
            alert.setContentText("This warehouse has items!");

            alert.showAndWait();
            return true;
        }
        return false;
    }

    public void actDeleteWarehouse(ActionEvent actionEvent) {
        if (model.getCurrentWarehouse() != null) {
            boolean imali = ImaLiWarehouseIteme(model.getCurrentWarehouse());
            if (imali == false) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("");
                alert.setContentText("Are you sure you want to delete this warehouse?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    model.deleteWarehouse(model.getCurrentWarehouse());
                    //  System.out.println("OMG");
                    tbWarehouses.getItems().clear();
                    tbWarehouses.setItems(model.getSkladista());
                    tbWarehouses.getSelectionModel().selectLast();
                }
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