package paket;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditEmployeeController {

    public TextField fldFirstName;
    public TextField fldLastName;
    public TextField fldPhone;
    public TextField fldDate;
    public TextField fldJMBG;
    public TextField fldLocation;
    public Osobe employee;
    public Button btnOk;
    public TextField fldEmail;
    public TextField fldPassword;

    public EditEmployeeController(Osobe employee){
        this.employee = employee;
    }

    public void initialize(){
        if(employee == null){
            fldFirstName.getStyleClass().add("poljeNijeIspravno");
            fldLastName.getStyleClass().add("poljeNijeIspravno");
            fldPhone.getStyleClass().add("poljeNijeIspravno");
            fldDate.getStyleClass().add("poljeNijeIspravno");
            fldJMBG.getStyleClass().add("poljeNijeIspravno");
            fldLocation.getStyleClass().add("poljeNijeIspravno");
            fldEmail.getStyleClass().add("poljeNijeIspravno");
            fldPassword.getStyleClass().add("poljeNijeIspravno");
        }
        else {
            fldFirstName.getStyleClass().add("poljeIspravno");
            fldLastName.getStyleClass().add("poljeIspravno");
            fldPhone.getStyleClass().add("poljeIspravno");
            fldDate.getStyleClass().add("poljeIspravno");
            fldJMBG.getStyleClass().add("poljeIspravno");
            fldLocation.getStyleClass().add("poljeIspravno");
            fldEmail.getStyleClass().add("poljeIspravno");
            fldPassword.getStyleClass().add("poljeIspravno");

            fldFirstName.setText(employee.getIme());
            fldLastName.setText(employee.getPrezime());
            fldPhone.setText(employee.getTelefon());
            fldDate.setText(employee.getDatum_zaposljavanja().toString());
            fldJMBG.setText(String.valueOf(employee.getJMBG()));
            fldLocation.setText(employee.getNaziv_lokacije());
            fldEmail.setText(employee.getEmail());
            fldPassword.setText(employee.getPassword());
        }

        fldFirstName.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && newIme.length() > 2) {
                fldFirstName.getStyleClass().removeAll("poljeNijeIspravno");
                fldFirstName.getStyleClass().add("poljeIspravno");
            } else {
                fldFirstName.getStyleClass().removeAll("poljeIspravno");
                fldFirstName.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldLastName.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && newIme.length() > 2) {
                fldLastName.getStyleClass().removeAll("poljeNijeIspravno");
                fldLastName.getStyleClass().add("poljeIspravno");
            } else {
                fldLastName.getStyleClass().removeAll("poljeIspravno");
                fldLastName.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldPhone.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty() && newIme.length() > 5) {
                fldPhone.getStyleClass().removeAll("poljeNijeIspravno");
                fldPhone.getStyleClass().add("poljeIspravno");
            } else {
                fldPhone.getStyleClass().removeAll("poljeIspravno");
                fldPhone.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldDate.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty()) {
                fldDate.getStyleClass().removeAll("poljeNijeIspravno");
                fldDate.getStyleClass().add("poljeIspravno");
            } else {
                fldDate.getStyleClass().removeAll("poljeIspravno");
                fldDate.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldJMBG.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && EditController.DaLiJeBroj(newIme) && newIme.length() == 13) {
                fldJMBG.getStyleClass().removeAll("poljeNijeIspravno");
                fldJMBG.getStyleClass().add("poljeIspravno");
            } else {
                fldJMBG.getStyleClass().removeAll("poljeIspravno");
                fldJMBG.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldEmail.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && validacijaEmaila(newIme)) {
                fldEmail.getStyleClass().removeAll("poljeNijeIspravno");
                fldEmail.getStyleClass().add("poljeIspravno");
            } else {
                fldEmail.getStyleClass().removeAll("poljeIspravno");
                fldEmail.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldPassword.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && checkPassword(newIme)) {
                fldPassword.getStyleClass().removeAll("poljeNijeIspravno");
                fldPassword.getStyleClass().add("poljeIspravno");
            } else {
                fldPassword.getStyleClass().removeAll("poljeIspravno");
                fldPassword.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldLocation.textProperty().addListener((obs, oldIme, newIme) -> {               // Kategorija koja se unosi mora biti jednaka nekoj iz baze
            if (!newIme.isEmpty()) {
                fldLocation.getStyleClass().removeAll("poljeNijeIspravno");
                fldLocation.getStyleClass().add("poljeIspravno");
            } else {
                fldLocation.getStyleClass().removeAll("poljeIspravno");
                fldLocation.getStyleClass().add("poljeNijeIspravno");
            }
        });


    }

    public boolean validacijaEmaila(String email){
        if(email.length() < 3) return false;
        String pattern = "[A-Za-z0-9]+@[A-Za-z]+.*";
        char c;
        int index = 0;

        if(email.matches(pattern)) return true;

        return false;
    }

    public boolean checkPassword(String pass){
        // String password = this.getPassword();

        boolean prviUslov = false;
        boolean drugiUslov = false;
        boolean treciUslov = false;
        //      System.out.println(pass);

        // System.out.println("Sifra je "+pass);

        for(int i=0 ; i<pass.length() ; i++){
            if(pass.charAt(i) >= 'A' && pass.charAt(i) <='Z'){
                prviUslov = true;
            }
            if(pass.charAt(i) >= 'a' && pass.charAt(i) <='z'){
                drugiUslov = true;
            }
            if(pass.charAt(i) >= '0' && pass.charAt(i) <='9'){
                treciUslov = true;
            }
        }

        //  System.out.println("Prvi "+ prviUslov + "Drgi "+ drugiUslov + "Treci " + treciUslov);

        if(prviUslov == false || drugiUslov == false || treciUslov == false) return false;

        return true;

    }

    public void actOk(ActionEvent actionEvent) {
        if(!(fldFirstName.getText().isEmpty()) && !(fldLastName.getText().isEmpty()) && !(fldPhone.getText().isEmpty()) && (EditController.DaLiJeBroj(fldJMBG.getText())) && fldJMBG.getText().length() == 13
        && validacijaEmaila(fldEmail.getText()) && checkPassword(fldPassword.getText())) {
            employee = new Osobe(fldFirstName.getText(), fldLastName.getText(), fldPhone.getText(), LocalDate.parse(fldDate.getText()),
                    fldJMBG.getText(), fldLocation.getText(), fldPassword.getText(), fldEmail.getText());

            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
    }

    public void actCancel(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public Osobe getEmployee() {
        return employee;
    }

    public void setEmployee(Osobe employee) {
        this.employee = employee;
    }
}
