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
    private PreparedStatement getManufacturer,getCategory,getNadCategory,getOsobeUpit,getCurrentEmployee,addEmployeeUpit,addEmployeeKorisnicki,odrediIDEmployee,updateCurrentEmployeeUpit,odrediIdKorisnicki,deleteEmployee,deleteEmployeeKor,updateCurrentKor;
    private PreparedStatement addCategoryUpit,deleteCategoryUpit,updateProizvodSkladistaUpit,odrediIdKat,updateCategoryUpit;
    private PreparedStatement getAdminsUpit, getProductsUpit, getWarehousesUpit, getManufacturersUpit, getCategoriesUpit, addProductUpit, odrediIdProizvoda, currentProductUpit, deleteProductUpit, updateCurrentProductUpit, getProizvodiSkladistaUpit, addProductWarehouseUpit, deleteProductWarehouseUpit;
    private ArrayList<Proizvodjac> manufacturers = new ArrayList<>();
    private ArrayList<Kategorija> categories = new ArrayList<>();
    private ObservableList<Proizvod> products = FXCollections.observableArrayList();
    private SimpleObjectProperty<Proizvod> currentProduct = null;
    private SimpleObjectProperty<Kategorija> currentCategory = null;
    private SimpleObjectProperty<Osobe> currentEmployee = null;
    private SimpleObjectProperty<Skladiste> currentWarehouse = null;

    private PreparedStatement getKorisnickiIdUpit,getDobavljacUpit,getAllDobavljaci,addProductDobavljacUpit,getDobavljacIdUpit,updateProductDobavljacUpit;
    private PreparedStatement addWarehouseUpit,updateWarehouseUpit,deleteWarehouseUpit,getIdSkladiste;

    public static SkladisteDAO getInstance() {
        if (instance == null) instance = new SkladisteDAO();
        return instance;
    }

    public Connection getConnection(){
        return connection;
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
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/skladista", "root", "");
         //   connection = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-05.cleardb.net/heroku_9bfc25ce30cf06d", "b222f6200b7fb5", "d2c2f06e");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        try {
            getAdminsUpit = connection.prepareStatement("SELECT * from korisnicki_racuni where pravo_pristupa=1");
            getManufacturersUpit = connection.prepareStatement("SELECT * from proizvodjaci");
            getCategoriesUpit = connection.prepareStatement("SELECT * from kategorije");
            addProductUpit = connection.prepareStatement("INSERT INTO proizvodi VALUES(?,?,?,?,?)");
            odrediIdProizvoda = connection.prepareStatement("SELECT Max(id)+1 FROM proizvodi");
            currentProductUpit = connection.prepareStatement("SELECT * from proizvodi where id=?");
            deleteProductUpit = connection.prepareStatement("DELETE FROM proizvodi where id=?");
            updateCurrentProductUpit = connection.prepareStatement("UPDATE proizvodi SET naziv=?,proizvodjac=?,kategorija=?,cijena=? WHERE id=?");
            updateProizvodSkladistaUpit = connection.prepareStatement("UPDATE proizvodi_skladista SET kolicina=? where proizvod_id=?");
            getWarehousesUpit = connection.prepareStatement("SELECT * from skladista");
            getProizvodiSkladistaUpit = connection.prepareStatement("SELECT * from proizvodi_skladista");
            addProductWarehouseUpit = connection.prepareStatement("INSERT INTO proizvodi_skladista VALUES(?,?,?)");
            deleteProductWarehouseUpit = connection.prepareStatement("DELETE FROM proizvodi_skladista where proizvod_id=?");
            addCategoryUpit = connection.prepareStatement("INSERT INTO kategorije VALUES(?,?,?)");
            odrediIdKat = connection.prepareStatement("SELECT Max(id)+1 from kategorije");
            deleteCategoryUpit = connection.prepareStatement("DELETE FROM kategorije where id=?");
            updateCategoryUpit = connection.prepareStatement("UPDATE kategorije SET naziv=?,nadkategorija=? where id=?");

            getProductsUpit = connection.prepareStatement("SELECT p.id,p.naziv,p.proizvodjac,p.kategorija,p.cijena,p1.kolicina,p2.dobavljac_id from proizvodi p,proizvodi_skladista p1,proizvodi_dobavljaca p2 where p1.proizvod_id = p.id AND p2.proizvod_id = p.id");

            getManufacturer = connection.prepareStatement("SELECT proizvodjac from proizvodi where id=?");
            getCategory = connection.prepareStatement("SELECT kategorija from proizvodi where id=?");
            getNadCategory = connection.prepareStatement("SELECT nadkategorija from kategorije where id=?");
            getOsobeUpit = connection.prepareStatement("SELECT o.id,o.ime,o.prezime,o.telefon,o.datum_zaposljavanja,o.jmbg,o.naziv_lokacije,k.password,k.email from osobe o,korisnicki_racuni k where k.osoba_id = o.id AND k.pravo_pristupa <> 1");  // Zanimaju nas samo usposlenici
            getCurrentEmployee = connection.prepareStatement("SELECT * from osobe where id=?");
            addEmployeeUpit = connection.prepareStatement("INSERT INTO osobe VALUES(?,?,?,?,?,?,?)");
            addEmployeeKorisnicki = connection.prepareStatement("INSERT INTO korisnicki_racuni VALUES (?,?,?,?,?)");
            odrediIDEmployee = connection.prepareStatement("SELECT MAX(id)+1 from osobe");
            updateCurrentEmployeeUpit = connection.prepareStatement("UPDATE osobe SET ime=?,prezime=?,telefon=?,datum_zaposljavanja=?,JMBG=?,naziv_lokacije=? WHERE id=?");
            odrediIdKorisnicki = connection.prepareStatement("SELECT Max(id)+1 from korisnicki_racuni");
            deleteEmployee = connection.prepareStatement("DELETE from osobe where id=?");
            deleteEmployeeKor = connection.prepareStatement("DELETE from korisnicki_racuni where osoba_id=?");
            updateCurrentKor = connection.prepareStatement("UPDATE korisnicki_racuni SET password=?,email=? where id=?");

            getKorisnickiIdUpit = connection.prepareStatement("SELECT id from korisnicki_racuni where password=? AND email=?");
            getDobavljacUpit = connection.prepareStatement("SELECT naziv from dobavljaci where id=?");
            getAllDobavljaci = connection.prepareStatement("SELECT * from dobavljaci");
            addProductDobavljacUpit = connection.prepareStatement("INSERT INTO proizvodi_dobavljaca VALUES(?,?)");
            getDobavljacIdUpit = connection.prepareStatement("SELECT id from dobavljaci where naziv=?");
            updateProductDobavljacUpit = connection.prepareStatement("UPDATE proizvodi_dobavljaca SET dobavljac_id=? where proizvod_id=?");

            addWarehouseUpit = connection.prepareStatement("INSERT INTO Skladista VALUES(?,?,?)");
            updateWarehouseUpit = connection.prepareStatement("UPDATE Skladista SET naziv=?,naziv_lokacije=? WHERE id=?");
            deleteWarehouseUpit = connection.prepareStatement("DELETE FROM Skladista where id=?");
            getIdSkladiste = connection.prepareStatement("SELECT Max(id) from skladista");
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

    public void addSkladiste(Skladiste sk) throws SQLException {
        ResultSet rs = getIdSkladiste.executeQuery();
        int id=1;
        if(rs.next()){
            id = rs.getInt(1);
        }
        id++;

        addWarehouseUpit.setInt(1,id);
        addWarehouseUpit.setString(2,sk.getNaziv());
        addWarehouseUpit.setString(3,sk.getNaziv_lokacije());

        addWarehouseUpit.executeUpdate();

    }

    public void updateSkladiste(Skladiste sk) throws SQLException {
        try {
            updateWarehouseUpit.setString(1,sk.getNaziv());
            updateWarehouseUpit.setString(2,sk.getNaziv_lokacije());
            updateWarehouseUpit.setInt(3,currentWarehouse.get().getId());

            updateWarehouseUpit.executeUpdate();
            currentWarehouse.setValue(sk);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Skladiste getCurrentWarehouse() {
        if(currentWarehouse == null) return null;
        return currentWarehouse.get();
    }

    public SimpleObjectProperty<Skladiste> currentWarehouseProperty() {
        return currentWarehouse;
    }

    public void setCurrentWarehouse(Skladiste currentWarehouse) {
        if (this.currentWarehouse == null) {
            this.currentWarehouse = new SimpleObjectProperty<>(currentWarehouse);
        } else {
            this.currentWarehouse.set(currentWarehouse);
        }
    }

    public void deleteWarehouse(){
        try {
            deleteWarehouseUpit.setInt(1,currentWarehouse.get().getId());
            deleteWarehouseUpit.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getDobavljaci(){
        ArrayList<String> dobavljaci = new ArrayList<>();

        try {
            ResultSet rs = getAllDobavljaci.executeQuery();

            while(rs.next()){
                String naziv = rs.getString(2);

                dobavljaci.add(naziv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dobavljaci;
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
                int kolicina = rs.getInt(6);
                int dobavljac_id = rs.getInt(7);

                getDobavljacUpit.setInt(1,dobavljac_id);
                ResultSet rs1 = getDobavljacUpit.executeQuery();

                String dobavljac="";
                if(rs1.next()){
                    dobavljac = rs1.getString(1);
                }

                Proizvod p = new Proizvod(id, naziv, manufa, kat, cijena,kolicina);

                if(dobavljac != null)
                p.setDobavljac(dobavljac);

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
                int nadKategorija = rs.getInt(3);

                String nad = "";

                for(int i=0 ; i<categories.size() ; i++) {
                    if (nadKategorija == categories.get(i).getId()) {
                        nad = categories.get(i).getNaziv();
                    }
                }

                Kategorija k = new Kategorija(id, naziv, nad);
                categories.add(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public void addCategory(Kategorija k){
        try {
            ResultSet rs = odrediIdKat.executeQuery();
            int id = 1;
            if(rs.next()){
                id = rs.getInt(1);
            }
            addCategoryUpit.setInt(1,id);
            addCategoryUpit.setString(2,k.getNaziv());
            String kat = k.getNadKategorija();
            ResultSet rs1 = getCategoriesUpit.executeQuery();
            int nad = 0;
            int br = 0;

            boolean imaNadKat = false;
                while (rs1.next()) {
                    if (rs1.getString(2).equals(k.getNadKategorija())) {
                        nad = br + 1;
                        imaNadKat = true;
                        break;
                    }
                    br++;
                }
                if(imaNadKat == false){
                    addCategoryUpit.setString(3,null);
                }
                else
            addCategoryUpit.setInt(3,nad);
            addCategoryUpit.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProduct(Proizvod proizvod,Skladiste skladiste) {
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

            addProductWarehouseUpit.setInt(1, id);
            addProductWarehouseUpit.setInt(2, skladiste.getId());
            addProductWarehouseUpit.setInt(3, proizvod.getKolicina());

            addProductWarehouseUpit.executeUpdate();

            String dobavljac = proizvod.getDobavljac();

            getDobavljacIdUpit.setString(1,dobavljac);
            ResultSet rs1 = getDobavljacIdUpit.executeQuery();
            int id_dobavljaca = 1;
            if(rs1.next()){
                id_dobavljaca = rs1.getInt(1);
            }
            addProductDobavljacUpit.setInt(1,id);
            addProductDobavljacUpit.setInt(2,id_dobavljaca);

            addProductDobavljacUpit.executeUpdate();

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

    public Kategorija getCurrentCategory() {
        if (currentCategory == null) {
            return null;
        }
        return currentCategory.get();
    }

    public SimpleObjectProperty<Kategorija> currentCategoryProperty() {
        return currentCategory;
    }

    public void setCurrentCategory(Kategorija currentCategory) {
        if (this.currentCategory == null) {
            this.currentCategory = new SimpleObjectProperty<>(currentCategory);
        } else {
            this.currentCategory.set(currentCategory);
        }
    }

    public void deleteCategory(){
        try {
            if(currentCategory != null) {
                deleteCategoryUpit.setInt(1,currentCategory.get().getId());
                deleteCategoryUpit.executeUpdate();
                categories.remove(currentCategory.get());
                currentCategory = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            if(proizvod != null) {
                int id_proizvodjaca = 0;
                int id_kategorije = 0;

                ArrayList<Proizvodjac> p = getManufacturers();
                ArrayList<Kategorija> k = getCategories();
                for (int i = 0; i < k.size(); i++) {
                    if (k.get(i).getNaziv().equals(proizvod.getKategorija())) {
                        id_proizvodjaca = k.get(i).getId();
                    }
                }


                for (int i = 0; i < p.size(); i++) {
                    //     System.out.println(k.get(i).getNaziv() + " " + proizvod.getKategorija());
                    if (p.get(i).getNaziv().equals(proizvod.getProizvodjac())) {
                        id_kategorije = p.get(i).getId();
                    //    System.out.println(id_kategorije);
                    }
                }
/*
                getManufacturer.setInt(1,proizvod.getId());
                ResultSet rs = getManufacturer.executeQuery();
                if(rs.next()){
                    id_proizvodjaca = rs.getInt(1);
                }
                getCategory.setInt(1,proizvod.getId());
                ResultSet rs1 = getCategory.executeQuery();
                if(rs1.next()){
                    id_kategorije = rs1.getInt(1);
                }
                System.out.println(id_kategorije);

 */
                updateCurrentProductUpit.setString(1, proizvod.nazivProperty().get());
                updateCurrentProductUpit.setInt(2, id_proizvodjaca);
                updateCurrentProductUpit.setInt(3, id_kategorije);
                updateCurrentProductUpit.setInt(4, proizvod.getCijena());
                updateCurrentProductUpit.setInt(5, currentProduct.get().getId());

                updateCurrentProductUpit.executeUpdate();

                updateProizvodSkladistaUpit.setInt(1,proizvod.getKolicina());
                updateProizvodSkladistaUpit.setInt(2,currentProduct.get().getId());

                updateProizvodSkladistaUpit.executeUpdate();

                String dobavljac = proizvod.getDobavljac();

                getDobavljacIdUpit.setString(1,dobavljac);
                ResultSet rs1 = getDobavljacIdUpit.executeQuery();
                int id_dobavljaca = 1;
                if(rs1.next()){
                    id_dobavljaca = rs1.getInt(1);
                }
                updateProductDobavljacUpit.setInt(1,id_dobavljaca);
                updateProductDobavljacUpit.setInt(2,currentProduct.get().getId());

                updateProductDobavljacUpit.executeUpdate();

                products.set(products.indexOf(currentProduct.get()), proizvod);
                currentProduct.set(proizvod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateCurrentCategory(Kategorija k){
        try {
            if(k != null) {
                updateCategoryUpit.setString(1, k.getNaziv());
                ResultSet rs1 = getCategoriesUpit.executeQuery();
                int nad = 0;
                int br = 0;

                boolean imaNadKat = false;
                while (rs1.next()) {
                    if (rs1.getString(2).equals(k.getNadKategorija())) {
                        nad = br + 1;
                        imaNadKat = true;
                        break;
                    }
                    br++;
                }
                if(imaNadKat == false){
                    updateCategoryUpit.setString(2,null);
                }
                else
                    updateCategoryUpit.setInt(2,nad);

                /*
                getNadCategory.setInt(1,k.getId());
                getNadCategory.executeQuery();
                ResultSet rs = getNadCategory.executeQuery();

                if(rs.next()){
                    System.out.println(rs.getInt(1));
                    if(rs.getInt(1) != 0)
                    updateCategoryUpit.setInt(2,rs.getInt(1));
                    else updateCategoryUpit.setString(2,null);
                }*/
                updateCategoryUpit.setInt(3, currentCategory.get().getId());
                updateCategoryUpit.executeUpdate();

                //categories.set(categories.indexOf(currentCategory.get()), k);
                currentCategory.set(k);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentEmployee(Osobe employee){
        try {
            if(employee != null){
                updateCurrentEmployeeUpit.setString(1,employee.getIme());
                updateCurrentEmployeeUpit.setString(2,employee.getPrezime());
                updateCurrentEmployeeUpit.setString(3,employee.getTelefon());
                updateCurrentEmployeeUpit.setDate(4,Date.valueOf(employee.getDatum_zaposljavanja()));
                updateCurrentEmployeeUpit.setString(5,String.valueOf(employee.getJMBG()));
                updateCurrentEmployeeUpit.setString(6,employee.getNaziv_lokacije());
                updateCurrentEmployeeUpit.setInt(7,currentEmployee.get().getId());

                updateCurrentEmployeeUpit.executeUpdate();

                getKorisnickiIdUpit.setString(1,currentEmployee.get().getPassword());
                getKorisnickiIdUpit.setString(2,currentEmployee.get().getEmail());
                ResultSet rs = getKorisnickiIdUpit.executeQuery();

                int id=1;
                if(rs.next()){
                    id = rs.getInt(1);
                }

                updateCurrentKor.setString(1,employee.getPassword());
                updateCurrentKor.setString(2,employee.getEmail());
                updateCurrentKor.setInt(3,id);

                updateCurrentKor.executeUpdate();
                currentEmployee.set(employee);

            }
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
    public ObservableList<Osobe> getEmployees(){
        ObservableList<Osobe> osobe = FXCollections.observableArrayList();
        try {
            ResultSet rs = getOsobeUpit.executeQuery();

            while(rs.next()) {
                Osobe o = new Osobe(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDate(5).toLocalDate(),
                        rs.getString(6), rs.getString(7),rs.getString(8),rs.getString(9));

                osobe.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return osobe;
    }

    public Osobe getCurrentEmployee() {
        if(currentEmployee == null) return null;
        return currentEmployee.get();
    }

    public SimpleObjectProperty<Osobe> currentEmployeeProperty() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Osobe currentEmployee) {
        if (this.currentEmployee == null) {
            this.currentEmployee = new SimpleObjectProperty<>(currentEmployee);
        } else {
            this.currentEmployee.set(currentEmployee);
        }
    }

    public void addEmployee(Osobe e){
        try {
            int id = 1;
            ResultSet rs = odrediIDEmployee.executeQuery();
            if(rs.next()){
                id = rs.getInt(1);
            }

            addEmployeeUpit.setInt(1,id);
            addEmployeeUpit.setString(2,e.getIme());
            addEmployeeUpit.setString(3,e.getPrezime());
            addEmployeeUpit.setString(4,e.getTelefon());
            addEmployeeUpit.setDate(5,Date.valueOf(e.getDatum_zaposljavanja()));
            addEmployeeUpit.setString(6,String.valueOf(e.getJMBG()));
            addEmployeeUpit.setString(7,e.getNaziv_lokacije());

            addEmployeeUpit.executeUpdate();

            ResultSet rs1 = odrediIdKorisnicki.executeQuery();
            int id1=1;
            if(rs1.next()){
                id1=rs1.getInt(1);
            }
            addEmployeeKorisnicki.setInt(1,id1);
            addEmployeeKorisnicki.setInt(2,id);      // osoba_id
            addEmployeeKorisnicki.setInt(3,2);    // Uposlenik
            addEmployeeKorisnicki.setString(4,e.getPassword());
            addEmployeeKorisnicki.setString(5,e.getEmail());

            addEmployeeKorisnicki.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteEmployee(){
        try {
            if(currentEmployee != null) {
                deleteEmployeeKor.setInt(1, currentEmployee.get().getId());
                deleteEmployeeKor.executeUpdate();

                deleteEmployee.setInt(1, currentEmployee.get().getId());
                deleteEmployee.executeUpdate();

                currentEmployee = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}