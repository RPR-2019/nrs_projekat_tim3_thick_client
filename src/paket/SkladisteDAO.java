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
    private PreparedStatement dajKolicineUpit;
    private PreparedStatement getAdminsUpit, getProductsUpit, getWarehousesUpit, getManufacturersUpit, getCategoriesUpit, addProductUpit, odrediIdProizvoda, currentProductUpit, deleteProductUpit, updateCurrentProductUpit, getProizvodiSkladistaUpit, addProductWarehouseUpit, deleteProductWarehouseUpit, updateCurrentProductWarehouseUpit;
    private ArrayList<Proizvodjac> manufacturers = new ArrayList<>();
    private ArrayList<Kategorija> categories = new ArrayList<>();
    private ObservableList<Proizvod> products = FXCollections.observableArrayList();
    private SimpleObjectProperty<Proizvod> currentProduct = null;

    public static SkladisteDAO getInstance() {
        if (instance == null) instance = new SkladisteDAO();
        return instance;
    }

    public static void deleteInstance() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        instance = null;
    }

    public SkladisteDAO() {
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
            getWarehousesUpit = connection.prepareStatement("SELECT * from skladista");
            getProizvodiSkladistaUpit = connection.prepareStatement("SELECT * from proizvodi_skladista");
            addProductWarehouseUpit = connection.prepareStatement("INSERT INTO proizvodi_skladista VALUES(?,?,?)");
            deleteProductWarehouseUpit = connection.prepareStatement("DELETE FROM proizvodi_skladista where proizvod_id=?");
            dajKolicineUpit = connection.prepareStatement("SELECT kolicina from proizvodi_skladista");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Administrator> getAdmins() {
        ArrayList<Administrator> admini = new ArrayList<>();
        try {

            ResultSet rs = getAdminsUpit.executeQuery();
            while (rs.next()) {
                String pass = rs.getString(4);
                String email = rs.getString(5);
                Administrator admin = new Administrator(email, pass);
                admini.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admini;
    }

    public ObservableList<Proizvod> getProducts() {
        try {
            ResultSet rs = getProductsUpit.executeQuery();
            ArrayList<Proizvodjac> proizvodjaci = getManufacturers();
            ArrayList<Kategorija> kategorije = getCategories();

            while (rs.next()) {
                int id = rs.getInt(1);
                String naziv = rs.getString(2);
                Integer proizvodjac = rs.getInt(3);
                String manufa = "";
                for (int i = 0; i < proizvodjaci.size(); i++) {
                    if (proizvodjaci.get(i).getId() == proizvodjac) {
                        manufa = manufacturers.get(i).getNaziv();
                        break;
                    }
                }
                Integer kategorija = rs.getInt(4);
                String kat = "";
                for (int i = 0; i < kategorije.size(); i++) {
                    if (kategorija == kategorije.get(i).getId()) {
                        kat = kategorije.get(i).getNaziv();
                    }
                }
                int cijena = rs.getInt(5);
                Proizvod p = new Proizvod(id, naziv, manufa, kat, cijena);
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public ArrayList<Proizvodjac> getManufacturers() {
        try {
            ResultSet rs = getManufacturersUpit.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String naziv = rs.getString(2);
                Proizvodjac p = new Proizvodjac(id, naziv);
                manufacturers.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return manufacturers;
    }

    public ArrayList<Kategorija> getCategories() {
        try {
            ResultSet rs = getCategoriesUpit.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String naziv = rs.getString(2);
                String nadKategorija = rs.getString(3);
                Kategorija k = new Kategorija(id, naziv, nadKategorija);
                categories.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void addProduct(Proizvod proizvod) {
        try {
            ResultSet rs = odrediIdProizvoda.executeQuery();
            int id = 1;
            if (rs.next()) {
                id = rs.getInt(1);
            }
            ArrayList<Proizvodjac> proizvodjaci = getManufacturers();
            ArrayList<Kategorija> kategorije = getCategories();
            int proizv = 0;
            for (int i = 0; i < proizvodjaci.size(); i++) {
                if (proizvodjaci.get(i).getNaziv().equals(proizvod.getProizvodjac())) {
                    proizv = i + 1;
                    break;
                }
            }
            int kat = 0;
            for (int i = 0; i < kategorije.size(); i++) {
                if (kategorije.get(i).getNaziv().equals(proizvod.getKategorija())) {
                    kat = i + 1;
                    break;
                }
            }
            addProductUpit.setInt(1, id);
            addProductUpit.setString(2, proizvod.nazivProperty().get());
            addProductUpit.setInt(3, proizv);
            addProductUpit.setInt(4, kat);
            addProductUpit.setInt(5, proizvod.getCijena());

            addProductUpit.executeUpdate();
            products.add(proizvod);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProductWarehouse(Proizvod proizvod, Skladiste skladiste, int kolicina) {
        try {
            int id = 0;
            ObservableList<Proizvod> p = getProducts();
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getNaziv().equals(proizvod.getNaziv())) {
                    id = p.get(i).getId();
                }
            }
            addProductWarehouseUpit.setInt(1, id);
            addProductWarehouseUpit.setInt(2, skladiste.getId());
            addProductWarehouseUpit.setInt(3, kolicina);

            addProductWarehouseUpit.executeUpdate();
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

    public void deleteProduct() {
        try {
            if (currentProduct != null) {
                deleteProductUpit.setInt(1, currentProduct.get().getId());
                deleteProductUpit.executeUpdate();
                products.remove(currentProduct.get());
                currentProduct = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentProduct(Proizvod proizvod) {
        try {
            int id_proizvodjaca = 0;
            ArrayList<Proizvodjac> p = getManufacturers();
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getNaziv().equals(proizvod.getKategorija())) {
                    id_proizvodjaca = p.get(i).getId();
                }
            }

            int id_kategorije = 0;
            ArrayList<Kategorija> k = getCategories();
            for (int i = 0; i < k.size(); i++) {
                //     System.out.println(k.get(i).getNaziv() + " " + proizvod.getKategorija());
                if (k.get(i).getNaziv().equals(proizvod.getProizvodjac())) {
                    id_kategorije = k.get(i).getId();
                    System.out.println(id_kategorije);
                }
            }
            updateCurrentProductUpit.setString(1, proizvod.nazivProperty().get());
            updateCurrentProductUpit.setInt(2, id_proizvodjaca);
            updateCurrentProductUpit.setInt(3, id_kategorije);
            updateCurrentProductUpit.setInt(4, proizvod.getCijena());
            updateCurrentProductUpit.setInt(5, currentProduct.get().getId());

            updateCurrentProductUpit.executeUpdate();

            products.set(products.indexOf(currentProduct.get()), proizvod);
            currentProduct.set(proizvod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentProductWarehouse(Proizvod proizvod, int sk_id, int kol) {
        try {
            int id = 0;
            ObservableList<Proizvod> p = getProducts();
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getNaziv().equals(proizvod.getNaziv())) {
                    id = p.get(i).getId();
                }
            }
            updateCurrentProductWarehouseUpit.setInt(1, id);
            updateCurrentProductWarehouseUpit.setInt(2, sk_id);
            updateCurrentProductWarehouseUpit.setInt(3, kol);

            updateCurrentProductWarehouseUpit.executeUpdate();

            products.set(products.indexOf(currentProduct.get()), proizvod);
            currentProduct.set(proizvod);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Skladiste> getSkladista() {
        ArrayList<Skladiste> skladista = new ArrayList<>();
        try {
            ResultSet rs = getWarehousesUpit.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String naziv = rs.getString(2);
                String naziv_lokacije = rs.getString(3);
                Skladiste sk = new Skladiste(id, naziv, naziv_lokacije);
                skladista.add(sk);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skladista;
    }

    public ArrayList<Proizvod> getProizvodiSkladista(Skladiste skl) {
        ArrayList<Proizvod> p_skladista = new ArrayList<>();
        try {
            ResultSet rs = getProizvodiSkladistaUpit.executeQuery();
            ObservableList<Proizvod> proizvodi = getProducts();
            ArrayList<Skladiste> skladista = getSkladista();

            while (rs.next()) {
                int proizvod = rs.getInt(1);
                int skladiste = rs.getInt(2);

                Proizvod p = null;
                for (int i = 0; i < proizvodi.size(); i++) {
                    //     System.out.println(proizvodi.get(i).getNaziv() + " " + proizvod);
                    if (proizvodi.get(i).getId() == proizvod) {
                        if (skl.getId() == skladiste) {
                            p = proizvodi.get(i);
                        }
                    }
                }
                if (p != null)
                    p_skladista.add(p);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return p_skladista;
    }

    public void deleteFromWarehouse(Proizvod proizvod) {
        try {
            int id = 0;
            ObservableList<Proizvod> p = getProducts();
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).getNaziv().equals(proizvod.getNaziv())) {
                    id = p.get(i).getId();
                }
            }
            deleteProductWarehouseUpit.setInt(1, id);
            deleteProductWarehouseUpit.executeUpdate();
            products.remove(currentProduct.get());
            currentProduct = null;

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
/*
    public ObservableList<Proizvodi_skladista> getProizvodiSkladistaObservable(Skladiste skl) {
        ObservableList<Proizvodi_skladista> p_skladista = FXCollections.observableArrayList();
        try {
            ResultSet rs = getProizvodiSkladistaUpit.executeQuery();
            ObservableList<Proizvod> proizvodi = getProducts();
            ArrayList<Skladiste> skladista = getSkladista();

            while (rs.next()) {
                int proizvod = rs.getInt(1);
                int skladiste = rs.getInt(2);
                int kolicina = rs.getInt(3);

                Proizvod p = null;
                for (int i = 0; i < proizvodi.size(); i++) {
                    //     System.out.println(proizvodi.get(i).getNaziv() + " " + proizvod);
                    if (proizvodi.get(i).getId() == proizvod) {
                        if (skl.getId() == skladiste) {
                            p = proizvodi.get(i);
                        }
                    }
                }
                if (p != null) {
                    Proizvodi_skladista p_s = new Proizvodi_skladista(proizvod,skladiste,kolicina);
                    p_skladista.add(p_s);
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return p_skladista;
    }
    */

}