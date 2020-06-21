package paket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginController {

    public TextField fldUsername;
    public TextField fldPassword;
    private SkladisteDAO skladisteDAO;
    private Button btnCancel;


    @FXML
    public void initialize(){
        fldPassword.setText("");
        fldUsername.setText("");
        this.skladisteDAO = new SkladisteDAO();

    }


    public void actLogin(ActionEvent actionEvent) {
        String email = fldUsername.getText();
        String pass = fldPassword.getText();

        ArrayList<Administrator> admini = skladisteDAO.getAdmins();

        for (int i = 0; i < admini.size(); i++) {
            if (admini.get(i).getEmail().equals(email) && admini.get(i).getPassword().equals(pass)) {
                fldUsername.getStyleClass().removeAll("poljeNijeIspravno");
                fldUsername.getStyleClass().add("poljeIspravno");
                fldPassword.getStyleClass().removeAll("poljeNijeIspravno");
                fldPassword.getStyleClass().add("poljeIspravno");
                SkladisteDAO skladisteDAO = SkladisteDAO.getInstance();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
                MainController mainController = new MainController(skladisteDAO);
                Stage editBookWindow = new Stage();
                loader.setController(mainController);
                Parent root = null;
                try {
                    root = loader.load();
                    editBookWindow.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                    editBookWindow.setResizable(false);
                    editBookWindow.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {
                fldUsername.getStyleClass().removeAll("poljeIspravno");
                fldUsername.getStyleClass().add("poljeNijeIspravno");
                fldPassword.getStyleClass().removeAll("poljeIspravno");
                fldPassword.getStyleClass().add("poljeNijeIspravno");
            }
        }
    }

    @FXML
    public void actCancel(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }
}
