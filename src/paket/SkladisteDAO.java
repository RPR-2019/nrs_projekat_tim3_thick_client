package paket;

import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

public class SkladisteDAO {
    private static SkladisteDAO instance;
    private static Connection connection;
    private PreparedStatement getAdminsUpit,getProductsUpit,getWarehousesUpit,getManufacturersUpit,getCategoriesUpit,addProductUpit,odrediIdProizvoda,currentProductUpit,deleteProductUpit,updateCurrentProductUpit;
    private ArrayList<Proizvodjac> manufacturers = new ArrayList<>();
    private ArrayList<Kategorija> categories = new ArrayList<>();
    private ObservableList<Proizvod> products = FXCollections.observableArrayList();
    private SimpleObjectProperty<Proizvod> currentProduct = null;

    public static SkladisteDAO getInstance(){
        if(instance == null) instance = new SkladisteDAO();
        return instance;
    }

    public static void deleteInstance(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        instance = null;
    }

    public SkladisteDAO(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/skladista", "root", "root");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        try {
            getAdminsUpit = connection.prepareStatement("SELECT * from korisnicki_racuni");
            getProductsUpit = connection.prepareStatement("SELECT * from proizvodi");
            getManufacturersUpit = connection.prepareStatement("SELECT * from proizvodjaci");
            getCategoriesUpit = connection.prepareStatement("SELECT * from kategorije");
            addProductUpit = connection.prepareStatement("INSERT INTO proizvodi VALUES(?,?,?,?,?)");
            odrediIdProizvoda = connection.prepareStatement("SELECT Max(id)+1 FROM proizvodi");
            currentProductUpit = connection.prepareStatement("SELECT * from proizvodi where id=?");
            deleteProductUpit = connection.prepareStatement("DELETE FROM proizvodi where id=?");
            updateCurrentProductUpit = connection.prepareStatement("UPDATE proizvodi SET naziv=?,proizvodjac=?,kategorija=?,cijena=? WHERE id=?");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Administrator> getAdmins(){
        ArrayList<Administrator> admini = new ArrayList<>();
        try {

            ResultSet rs = getAdminsUpit.executeQuery();
            while(rs.next()){
                String pass = rs.getString(4);
                String email = rs.getString(5);
                Administrator admin = new Administrator(email,pass);
                admini.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admini;
    }

    public ObservableList<Proizvod> getProducts(){
        try {
            ResultSet rs =  getProductsUpit.executeQuery();
            ArrayList<Proizvodjac> proizvodjaci = getManufacturers();
            ArrayList<Kategorija> kategorije = getCategories();

            while(rs.next()){
                String naziv = rs.getString(2);
                Integer proizvodjac = rs.getInt(3);
                String manufa = "";
                for(int i=0 ; i<proizvodjaci.size() ; i++){
                    if(proizvodjaci.get(i).getId() == proizvodjac){
                        manufa = manufacturers.get(i).getNaziv();
                        break;
                    }
                }
                Integer kategorija = rs.getInt(4);
                String kat = "";
                for(int i=0 ; i<kategorije.size() ; i++){
                    if(kategorija == kategorije.get(i).getId()){
                        kat = kategorije.get(i).getNaziv();
                    }
                }
                int cijena = rs.getInt(5);
                Proizvod p = new Proizvod(naziv,manufa,kat,cijena);
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public ArrayList<Proizvodjac> getManufacturers(){
        try {
            ResultSet rs = getManufacturersUpit.executeQuery();
            while(rs.next()){
                Integer id = rs.getInt(1);
                String naziv = rs.getString(2);
                Proizvodjac p = new Proizvodjac(id,naziv);
                manufacturers.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manufacturers;
    }

    public ArrayList<Kategorija> getCategories(){
        try {
            ResultSet rs = getCategoriesUpit.executeQuery();
            while(rs.next()){
                Integer id = rs.getInt(1);
                String naziv = rs.getString(2);
                String nadKategorija = rs.getString(3);
                Kategorija k = new Kategorija(id,naziv,nadKategorija);
                categories.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void addProduct(Proizvod proizvod){
        try {
        ResultSet rs = odrediIdProizvoda.executeQuery();
        int id = 1;
        if(rs.next()){
            id = rs.getInt(1);
        }
        addProductUpit.setInt(1,id);
        addProductUpit.setString(2,proizvod.nazivProperty().get());
        addProductUpit.setString(3,proizvod.proizvodjacProperty().get());
        addProductUpit.setString(4,proizvod.kategorijaProperty().get());
        addProductUpit.setInt(5,proizvod.getCijena());

        addProductUpit.executeUpdate();
        products.add(proizvod);

    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    public Proizvod getCurrentProduct() {
        if (currentProduct == null) {
            return null;
        }
        return currentProduct.get();
    }

    public void setCurrentBook(Proizvod currentProduct) {
        if (this.currentProduct == null) {
            this.currentProduct = new SimpleObjectProperty<>(currentProduct);
        } else {
            this.currentProduct.set(currentProduct);
        }

    }

    public void deleteProduct(){
        try {
            if(currentProduct != null) {
                currentProductUpit.setInt(1, currentProduct.get().getId());
                currentProductUpit.executeUpdate();
                products.remove(currentProduct.get());
                currentProduct = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentProduct(Proizvod proizvod) {
        try {
            updateCurrentProductUpit.setString(1, proizvod.nazivProperty().get());
            updateCurrentProductUpit.setString(2, proizvod.proizvodjacProperty().get());
            updateCurrentProductUpit.setString(3, proizvod.kategorijaProperty().get());
            updateCurrentProductUpit.setInt(4, proizvod.getCijena());
            updateCurrentProductUpit.setInt(5, currentProduct.get().getId());

            updateCurrentProductUpit.executeUpdate();

            products.set(products.indexOf(currentProduct.get()), proizvod);
            currentProduct.set(proizvod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
