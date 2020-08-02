package paket;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EditEmployeeController {

    public TextField fldFirstName;
    public TextField fldLastName;
    public TextField fldPhone;
    public TextField fldJMBG;
    public TextField fldLocation;
    public Osobe employee;
    public Button btnOk;
    public TextField fldEmail;
    public TextField fldPassword;
    public DatePicker datePicker;
    private boolean validno;

    public EditEmployeeController(Osobe employee){
        this.employee = employee;
    }

    public void initialize(){
        if(employee == null){
            fldFirstName.getStyleClass().add("poljeNijeIspravno");
            fldLastName.getStyleClass().add("poljeNijeIspravno");
            fldPhone.getStyleClass().add("poljeNijeIspravno");
            datePicker.getStyleClass().add("poljeNijeIspravno");
            fldJMBG.getStyleClass().add("poljeNijeIspravno");
            fldLocation.getStyleClass().add("poljeNijeIspravno");
            fldEmail.getStyleClass().add("poljeNijeIspravno");
            fldPassword.getStyleClass().add("poljeNijeIspravno");
            validno = false;
        }
        else {
            fldFirstName.getStyleClass().add("poljeIspravno");
            fldLastName.getStyleClass().add("poljeIspravno");
            fldPhone.getStyleClass().add("poljeIspravno");
            datePicker.getStyleClass().add("poljeIspravno");
            fldJMBG.getStyleClass().add("poljeIspravno");
            fldLocation.getStyleClass().add("poljeIspravno");
            fldEmail.getStyleClass().add("poljeIspravno");
            fldPassword.getStyleClass().add("poljeIspravno");

            fldFirstName.setText(employee.getIme());
            fldLastName.setText(employee.getPrezime());
            fldPhone.setText(employee.getTelefon());
            datePicker.setValue(employee.getDatum_zaposljavanja());
            fldJMBG.setText(String.valueOf(employee.getJMBG()));
            fldLocation.setText(employee.getNaziv_lokacije());
            fldEmail.setText(employee.getEmail());
            fldPassword.setText(employee.getPassword());
            validno = true;
        }

        fldFirstName.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && newIme.length() > 2) {
                fldFirstName.getStyleClass().removeAll("poljeNijeIspravno");
                fldFirstName.getStyleClass().add("poljeIspravno");
            } else {
                fldFirstName.getStyleClass().removeAll("poljeIspravno");
                fldFirstName.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldLastName.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && newIme.length() > 2) {
                fldLastName.getStyleClass().removeAll("poljeNijeIspravno");
                fldLastName.getStyleClass().add("poljeIspravno");
            } else {
                fldLastName.getStyleClass().removeAll("poljeIspravno");
                fldLastName.getStyleClass().add("poljeNijeIspravno");
            }
        });
        fldPhone.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty() && newIme.length() > 5) {
                fldPhone.getStyleClass().removeAll("poljeNijeIspravno");
                fldPhone.getStyleClass().add("poljeIspravno");
            } else {
                fldPhone.getStyleClass().removeAll("poljeIspravno");
                fldPhone.getStyleClass().add("poljeNijeIspravno");
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
        fldLocation.textProperty().addListener((obs, oldIme, newIme) -> {
            if (!newIme.isEmpty()) {
                fldLocation.getStyleClass().removeAll("poljeNijeIspravno");
                fldLocation.getStyleClass().add("poljeIspravno");
            } else {
                fldLocation.getStyleClass().removeAll("poljeIspravno");
                fldLocation.getStyleClass().add("poljeNijeIspravno");
            }
        });
        datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                if(t1.isAfter(LocalDate.now())){
                    datePicker.getStyleClass().removeAll("poljeIspravno");
                    datePicker.getStyleClass().add("poljeNijeIspravno");
                    validno = false;
                }
                else {
                    datePicker.getStyleClass().removeAll("poljeNijeIspravno");
                    datePicker.getStyleClass().add("poljeIspravno");
                    validno = true;
                }
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
        if(!(fldFirstName.getText().isEmpty()) && fldFirstName.getText().length() > 2 && !(fldLastName.getText().isEmpty()) && fldLastName.getText().length() > 2 && ((fldPhone.getText().length() > 5 )) && (EditController.DaLiJeBroj(fldJMBG.getText())) && fldJMBG.getText().length() == 13
        && validacijaEmaila(fldEmail.getText()) && checkPassword(fldPassword.getText()) && validno == true && fldLocation.getText().length() != 0) {
            if(employee == null) employee = new Osobe();
            employee.setIme(fldFirstName.getText());
            employee.setPrezime(fldLastName.getText());
            employee.setTelefon(fldPhone.getText());
            employee.setDatum_zaposljavanja(datePicker.getValue());
            employee.setJMBG(fldJMBG.getText());
            employee.setNaziv_lokacije(fldLocation.getText());
            employee.setEmail(fldEmail.getText());
            employee.setPassword(fldPassword.getText());

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
