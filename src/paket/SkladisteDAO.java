package paket;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class SkladisteDAO {
    private static SkladisteDAO instance;
    private static Connection connection;
    private ArrayList<Proizvodjac> manufacturers = new ArrayList<>();
    private ArrayList<Kategorija> categories = new ArrayList<>();
    private ObservableList<Proizvod> products = FXCollections.observableArrayList();
    private SimpleObjectProperty<Proizvod> currentProduct = null;
    private SimpleObjectProperty<Kategorija> currentCategory = null;
    private SimpleObjectProperty<Osobe> currentEmployee = null;
    private SimpleObjectProperty<Skladiste> currentWarehouse = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

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
           // connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/skladista", "root", "");
            connection = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-05.cleardb.net/heroku_9bfc25ce30cf06d", "b222f6200b7fb5", "d2c2f06e");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private String dajJson(String url) {
        HttpURLConnection con = null;
        try {
            URL u = new URL(url);
            con = (HttpURLConnection) u.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
         //   System.out.println(sb.toString());
            br.close();
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    public ArrayList<Administrator> getAdmins() {
        ArrayList<Administrator> admini = new ArrayList<>();
        JSONArray jsonArray  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/users"));
        if (jsonArray == null) return null;

        for(int i=0 ; i<jsonArray.length() ; i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            int pravo = jo.getInt("pravo_pristupa");
            String password = jo.getString("password");
            String email = jo.getString("email");

            if (pravo == 1) {
                admini.add(new Administrator(email, password));
            }
        }
        return admini;
    }

    public int MaxIdWarehouse(){
        JSONArray warehouses = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/warehouses"));

        JSONObject warehouse = warehouses.getJSONObject(warehouses.length()-1);

        return warehouse.getInt("id");
    }

    public void addSkladiste(Skladiste sk) throws SQLException {
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        int max = MaxIdWarehouse();
        max++;
        String json = "{ \"naziv\": \"" + sk.getNaziv() + "\"," +
                "\"naziv_lokacije\":  \"" + sk.getNaziv_lokacije() + "\"}";

        addUsingHttp(json, url);
    }

    private void addViaHttp (JSONObject jsonObject, URL url) {
        HttpURLConnection con = null;
        JSONObject jsonObject1 = null;
        try {
            byte[] data = jsonObject.toString().getBytes();
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            BufferedReader entry = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json = "{", line = "";
            while ((line = entry.readLine()) != null) {
                json = json + line;
            }
            json = json + "}";
            entry.close();

        } finally {
            return;
        }
    }

    private void deleteViaHttp (URL url) {
        HttpURLConnection httpCon = null;
        try {
            httpCon = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            httpCon.setRequestMethod("DELETE");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            httpCon.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(httpCon.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
    }

    public void updateSkladiste(Skladiste sk) throws SQLException {
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses/" + sk.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String json = "{ \"naziv\": \"" + sk.getNaziv() + "\"," +
                "\"naziv_lokacije\":  \"" + sk.getNaziv_lokacije() + "\"}";


        updateUsingHttp(json,url);
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

    public void deleteWarehouse(Skladiste sk){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses/" + sk.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deleteViaHttp( url);
    }

    public ObservableList<Dobavljac> getDobavljaci(){
        ObservableList<Dobavljac> dobavljaci = FXCollections.observableArrayList();
        JSONArray jsonArray  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/suppliers"));
        if (jsonArray == null) return null;

        for(int i=0 ; i<jsonArray.length() ; i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            int id = jo.getInt("id");
            String dobavljac = jo.getString("naziv");

            dobavljaci.add(new Dobavljac(id,dobavljac));
        }
        return dobavljaci;
    }

    public ArrayList<Proizvodjac> getManufacturers() {
        JSONArray jsonManufacturers  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/manufacturers"));
        if (jsonManufacturers == null) return null;

        for(int i=0 ; i<jsonManufacturers.length() ; i++) {
            JSONObject jo = jsonManufacturers.getJSONObject(i);

            int id = jo.getInt("id");
            String naziv = jo.getString("naziv");

            Proizvodjac p = new Proizvodjac(id,naziv);

            manufacturers.add(p);
        }
        return manufacturers;
    }

    public ArrayList<Kategorija> getCategories() {
        JSONArray jsonCategories  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/categories"));
        if (jsonCategories == null) return null;

        for(int i=0 ; i<jsonCategories.length() ; i++) {
            JSONObject jo = jsonCategories.getJSONObject(i);

            int id = jo.getInt("id");
            String naziv = jo.getString("naziv");

            String nadkat = String.valueOf(jo.get("nadkategorija"));
            Integer nadKategorija = null;

            if(!(nadkat.equals("null"))) {
                nadKategorija = Integer.parseInt(nadkat);
            }
            String nad = "";

            if(!(nadKategorija == null)) {
                for (int j = 0; j < categories.size(); j++) {
                    if (nadKategorija == categories.get(j).getId()) {
                        nad = categories.get(j).getNaziv();
                    }
                }
            }
            Kategorija k = new Kategorija(id,naziv,nad);

            categories.add(k);
        }
        return categories;
    }

    public void addCategory(Kategorija k){
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/categories");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Integer nad = null;

        for(int i=0 ; i<categories.size() ; i++){
            if(categories.get(i).getNaziv().equals(k.getNadKategorija())){
                nad = i;
                break;
            }
        }
        String json = "{ \"naziv\": \"" + k.getNaziv() + "\"," +
                "\"nadkategorija\":  \"" + nad + "\"}";

        addUsingHttp(json, url);
    }

    public void addUsingHttp(String json,URL url){
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        //    System.out.println(response.toString());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addProduct(Proizvod proizvod,Skladiste skladiste,Dobavljac dobavljac) {
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/items");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String json = "{ \"naziv\": \""+proizvod.getNaziv()+"\"," +
                "\"proizvodjac\":  \""+proizvod.getProizvodjac().getId()+"\",\"kategorija\":  \""+proizvod.getKategorija().getId()+"\",\"cijena\":  \""+proizvod.getCijena()+"\"}";


        addUsingHttp(json,url);

            JSONArray jsonArray = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/items"));
            JSONObject jo = jsonArray.getJSONObject(jsonArray.length()-1);
            int id = jo.getInt("id");
            proizvod.setId(id);

         url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses/" + skladiste.getId() + "/items/"+proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        json = "{ \"quantity\":  \""+proizvod.getKolicina()+"\"}";

        addUsingHttp(json,url);


        url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/suppliers/" + dobavljac.getId() + "/items/"+proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        json = "{ \"dobavljac_id\":  \""+dobavljac.getId()+"\"}";

        addUsingHttp(json,url);
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

    public void deleteCategory(Kategorija k){
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/categories/" + k.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deleteViaHttp(url);
    }

    public void deleteProduct(Proizvod product,Skladiste sk) {
        URL url = null;
        HttpURLConnection con = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/suppliers/" + product.getDobavljac().getId()+"/items/"+product.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deleteViaHttp(url);

         url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses/" + sk.getId()+"/items/"+product.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deleteViaHttp(url);

        url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/items/" + product.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        deleteViaHttp(url);
    }

    public void updateCurrentProduct(Proizvod proizvod,Skladiste sk,Dobavljac dobavljac) {
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/items/" + proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String json = "{ \"naziv\": \"" + proizvod.getNaziv() + "\"," +
                "\"proizvodjac\":  \"" + proizvod.getProizvodjac().getId() + "\",\"kategorija\":  \"" + proizvod.getKategorija().getId() + "\",\"cijena\":  \"" + proizvod.getCijena() + "\"}";


        updateUsingHttp(json,url);

        url = null;

        try {
            url = new URL("https://nrs-backend.herokuapp.com/warehouses/" + sk.getId() + "/items/"+proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JSONObject jo = new JSONObject();
        jo.put("quantity",proizvod.getKolicina());

        json = "{ \"quantity\":  \""+proizvod.getKolicina()+"\"}";

        updateUsingHttp(json,url);

        url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/suppliers/" + findDobavljac(proizvod.getId()).getId() +"/items/"+proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        deleteViaHttp(url);

        url = null;

        try {
            url = new URL("https://nrs-backend.herokuapp.com/suppliers/" + dobavljac.getId() + "/items/"+proizvod.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        json = "{ \"dobavljac_id\":  \""+dobavljac.getId()+"\"}";

        addUsingHttp(json,url);

    }

    public void updateUsingHttp(String json,URL url){
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("PUT");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void updateCurrentCategory(Kategorija k){
        try {
            URL url = null;
            try {
                url = new URL("https://nrs-backend.herokuapp.com/categories/" + k.getId());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            Integer nad = null;

            for(int i=0 ; i<categories.size() ; i++){
                if(categories.get(i).getNaziv().equals(k.getNadKategorija())){
                    nad = categories.get(i).getId();
                    break;
                }
            }

            String json = "{ \"naziv\": \"" + k.getNaziv() + "\"," +
                    "\"nadkategorija\":  \"" + nad + "\"}";


            updateUsingHttp(json,url);
            currentCategory.set(k);
            } catch (Exception e) {
            e.printStackTrace();
        }

        }

    public void updateCurrentEmployee(Osobe employee){
            URL url = null;
            try {
                url = new URL("https://nrs-backend.herokuapp.com/people/" + employee.getId());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            String json = "{ \"ime\": \""+employee.getIme()+"\"," +
                    "\"prezime\":  \""+employee.getPrezime()+"\",\"telefon\":  \""+employee.getTelefon()+"\",\"datum_zaposljavanja\":  \""+employee.getDatum_zaposljavanja()+"\",\"lokacija\":  \""+employee.getNaziv_lokacije()+"\"}";

            currentEmployee.set(employee);

            updateUsingHttp(json,url);

            url = null;
        int user_id = findUser(employee.getId());

        try {
            url = new URL("https://nrs-backend.herokuapp.com/users/" + user_id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        json = "{ \"ime\": \""+employee.getIme()+"\"," +
                "\"prezime\":  \""+employee.getPrezime()+"\",\"telefon\":  \""+employee.getTelefon()+"\",\"datum_zaposljavanja\":  \""+employee.getDatum_zaposljavanja()+"\",\"jmbg\":  \""+employee.getJMBG()+"\",\"lokacija\":  \""+employee.getNaziv_lokacije()+"\",\"pravo_pristupa\": \""+2+"\", \"email\":  \""+employee.getEmail()+"\",\"password\": \""+employee.getPassword()+"\"}";

        updateUsingHttp(json,url);

    }

    int findUser(int id){
        JSONArray users = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/users"));
        int trazeni_id = -1;
        for(int i=0 ; i<users.length() ; i++){
            if(users.getJSONObject(i).getInt("osoba_id") == id){
                trazeni_id = users.getJSONObject(i).getInt("id");
            }
        }
        return trazeni_id;
    }

    public ArrayList<Skladiste> getSkladista() {
        ArrayList<Skladiste> skladista = new ArrayList<>();
        JSONArray jsonManufacturers  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/warehouses"));
        if (jsonManufacturers == null) return null;

        for(int i=0 ; i<jsonManufacturers.length() ; i++) {
            JSONObject jo = jsonManufacturers.getJSONObject(i);

            int id = jo.getInt("id");
            String naziv = jo.getString("naziv");
            String naziv_lokacije = jo.getString("naziv_lokacije");

            Skladiste s = new Skladiste(id,naziv,naziv_lokacije);

            skladista.add(s);
        }
        return skladista;
    }

    public int getProductCijena(int id){
        JSONObject jo = new JSONObject(dajJson("https://nrs-backend.herokuapp.com/items/"+id));

        return jo.getInt("cijena");
    }

    public Kategorija findKategorija(String kategorija){
        JSONArray array = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/categories"));
        Kategorija k = new Kategorija();

        for(int i=0 ; i<array.length() ; i++){
            JSONObject jo = array.getJSONObject(i);

            if(jo.getString("naziv").equals(kategorija)) {
                int id = jo.getInt("id");
                String naziv = jo.getString("naziv");
                int nadKategorija = Integer.parseInt(String.valueOf(jo.get("id")));

                k.setId(id);
                k.setNaziv(naziv);
                String nad = "";
                for(int j=0 ; j<array.length() ; j++){
                    if(Integer.parseInt(String.valueOf(array.getJSONObject(j).get("id"))) == nadKategorija){
                        nad = array.getJSONObject(j).getString("naziv");
                    }
                }
                k.setNadKategorija(nad);

            }
        }
        return k;
    }

    public ArrayList<Proizvod> getProizvodiSkladista(Skladiste skl) {
        ArrayList<Proizvod> p_skladista = new ArrayList<>();
        try {
            ArrayList<Proizvodjac> proizvodjaci = getManufacturers();
            ArrayList<Kategorija> kategorije = getCategories();
            //   ObservableList<Proizvod> proizvodi = getProducts();
            ArrayList<Skladiste> skladista = getSkladista();
            JSONArray proizvodi_skladista = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/warehouses/" + skl.getId() + "/items"));

            for (int i = 0; i < proizvodi_skladista.length(); i++) {
                JSONObject jo1 = proizvodi_skladista.getJSONObject(i);
                int id = jo1.getInt("proizvod_id");
                String naziv = jo1.getString("naziv_proizvoda");
                Integer proizvodjac = jo1.getInt("proizvodjac");
                Proizvodjac manufa = new Proizvodjac();

                for (int k = 0; k < proizvodjaci.size(); k++) {
                    if (proizvodjaci.get(k).getId() == proizvodjac) {
                        manufa = manufacturers.get(k);
                        break;
                    }
                }

                int cijena = getProductCijena(id);
                String kategorija = jo1.getString("naziv_kategorije");

               // String kolicina = String.valueOf(jo1.get("kolicina"));

            //    Integer kolicina = jo1.getInt("kolicina");
              //  String kol = jo1.getString("kolicina");
//                 int kolicina = Integer.parseInt(kol);

                int kolicina = jo1.getInt("kolicina");
                Dobavljac dobavljac = findDobavljac(id);
   /*             JSONArray proizvodi_dobavljaca = new JSONArray(getJSON("https://nrs-backend.herokuapp.com/suppliers/" + i + 1 + "/items"));
                String dobavljac = "";

                for (int k = 0; k < proizvodi_dobavljaca.length(); k++) {
                    JSONObject jo2 = proizvodi_dobavljaca.getJSONObject(k);

                    if (jo2.getInt("proizvod_id") == id) {
                        dobavljac = jo2.getString("naziv_dobavljaca");
                    }
                } */
                Proizvod p = new Proizvod(id, naziv, manufa, findKategorija(kategorija), cijena, kolicina);
                p.setDobavljac(dobavljac);
                p_skladista.add(p);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return p_skladista;
    }

    private Dobavljac findDobavljac(int id) {
        JSONArray skladista = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/suppliers/"));
        int vel = skladista.length();
        Dobavljac dobavljac = new Dobavljac();

        for (int i = 0; i < vel; i++) {
            JSONObject jo = skladista.getJSONObject(i);
            int dobavljac_id = jo.getInt("id");
            String naziv = jo.getString("naziv");
            JSONArray proizvodi_dobavljaca = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/suppliers/" + dobavljac_id + "/items"));

            for(int j=0 ; j<proizvodi_dobavljaca.length() ; j++){
                if(proizvodi_dobavljaca.getJSONObject(j).getInt("proizvod_id") == id){
                    dobavljac.setId(dobavljac_id);
                    dobavljac.setNaziv(naziv);
                    break;
                }
            }
            if(dobavljac.getNaziv() != null) break;
        }
        return dobavljac;
    }

    public Osobe findOsobu(int id){
        JSONObject jo = new JSONObject(dajJson("https://nrs-backend.herokuapp.com/people/"+id));
        String datum_zaposljavanja = jo.getString("datum_zaposljavanja");
        LocalDate date = LocalDate.parse(datum_zaposljavanja,formatter);
        Osobe o = new Osobe(jo.getInt("id"),jo.getString("Ime"),jo.getString("Prezime"),jo.getString("Telefon"),date,jo.getString("JMBG"),jo.getString("naziv_lokacije"));

        return o;
    }

    public ObservableList<Osobe> getEmployees(){
        ObservableList<Osobe> osobe = FXCollections.observableArrayList();

        JSONArray jsonEmployees  = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/users"));

        if (jsonEmployees == null) return null;

        for(int i=0 ; i<jsonEmployees.length() ; i++) {
            JSONObject jo = jsonEmployees.getJSONObject(i);

            if(jo.getInt("pravo_pristupa") == 2){
                Osobe o = findOsobu(jo.getInt("osoba_id"));
                String email = jo.getString("email");
                String password = jo.getString("password");
                o.setEmail(email);
                o.setPassword(password);
                osobe.add(o);
            }
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

    public int MaxIntEmployee() {
        JSONArray jsonArray = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/people"));
        return jsonArray.getJSONObject(jsonArray.length()-1).getInt("id");
    }

    public void addEmployee(Osobe e){
        URL url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/people");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        //max++;
        String json = "{ \"ime\": \""+e.getIme()+"\"," +
                "\"prezime\":  \""+e.getPrezime()+"\",\"telefon\":  \""+e.getTelefon()+"\",\"datum_zaposljavanja\":  \""+e.getDatum_zaposljavanja()+"\",\"lokacija\":  \""+e.getNaziv_lokacije()+"\"}";


     //       String json = "{ \"id\":  \""+max+"\", \"ime\": \""+e.getIme()+"\"," +
    //              "\"prezime\":  \""+e.getPrezime()+"\",\"telefon\":  \""+e.getTelefon()+"\",\"datum_zaposljavanja\":  \""+e.getDatum_zaposljavanja()+"\",\"jmbg\":  \""+e.getJMBG()+"\",\"lokacija\":  \""+e.getNaziv_lokacije()+"\",\"email\":  \""+e.getEmail()+"\",\"password\": \""+e.getPassword()+"\"}";


            addUsingHttp(json,url);
            JSONArray jsonArray = new JSONArray(dajJson("https://nrs-backend.herokuapp.com/people"));
            JSONObject jo = jsonArray.getJSONObject(jsonArray.length()-1);
            int id = jo.getInt("id");
            e.setId(id);

         url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/users");
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

         json = "{ \"ime\": \""+e.getIme()+"\"," +
                "\"prezime\":  \""+e.getPrezime()+"\",\"telefon\":  \""+e.getTelefon()+"\",\"datum_zaposljavanja\":  \""+e.getDatum_zaposljavanja()+"\",\"jmbg\":  \""+e.getJMBG()+"\",\"lokacija\":  \""+e.getNaziv_lokacije()+"\",\"pravo_pristupa\": \""+2+"\", \"email\":  \""+e.getEmail()+"\",\"password\": \""+e.getPassword()+"\"}";

        addUsingHttp(json,url);
    }

    public void deleteEmployee(Osobe e){
        URL url = null;
        int id = findUser(e.getId());
        try {
            url = new URL("https://nrs-backend.herokuapp.com/users/" + id);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        deleteViaHttp(url);

         url = null;
        try {
            url = new URL("https://nrs-backend.herokuapp.com/people/" + e.getId());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        deleteViaHttp(url);
    }

}