package krowchat.UI;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.WindowEvent;

import krowchat.Manager.AuthManager;
import krowchat.Util.AuthStatus;

public class AuthFX extends Application 
{    
    private Stage           auth;
    private TextField       userField;
    private PasswordField   passField;
    private Button          loginBtn;
    private Label           updLabel;
       
    /**
     * AuthFX constructor
     */
    public AuthFX() 
    {
        /* */
    }
                
    /**
     * Starts to render AuthFX stage
     * @param stage AuthFX's stage to render
     */
    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Login Authentication - KrowChat");
        stage.setResizable(false);
        stage.centerOnScreen();
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label userLabel = new Label("Username");
        grid.add(userLabel, 0, 1);

        userField = new TextField();
        grid.add(userField, 1, 1);

        Label passLabel = new Label("Password");
        grid.add(passLabel, 0, 2);

        passField = new PasswordField();
        grid.add(passField, 1, 2);
        
        updLabel = new Label("");
        updLabel.setMinWidth(100);
        HBox _hbBtn = new HBox(10);
        _hbBtn.setAlignment(Pos.BOTTOM_LEFT);
        _hbBtn.getChildren().add(updLabel);
        grid.add(_hbBtn, 0, 4);
        
        loginBtn = new Button("Log in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginBtn);
        grid.add(hbBtn, 1, 4);
        
        loginBtn.setOnAction((ActionEvent e) -> { handleLogin(); });
          
        userField.setOnKeyPressed((KeyEvent e) -> { if (e.getCode() == KeyCode.ENTER) handleLogin(); });
        passField.setOnKeyPressed((KeyEvent e) -> { if (e.getCode() == KeyCode.ENTER) handleLogin(); });
        loginBtn.setOnKeyPressed((KeyEvent e) -> { if (e.getCode() == KeyCode.ENTER) handleLogin(); });

        stage.setOnCloseRequest((WindowEvent e) ->
        {
            Platform.exit();
            System.exit(0);
        });
        stage.setScene(new Scene(grid, 350, 170));
        stage.show();
    }
    
    /**
     * Handles username and password text fields for initial authentication
     */
    private void handleLogin() 
    {
        String userid = userField.getText();
        String passid = passField.getText();
            
        userField.setEditable(false);
        passField.setEditable(false);
        loginBtn.setDisable(true);
            
        if (userid.length() == 0 || passid.length() == 0)
        {
            error(AuthStatus.EMPTY_FIELDS);
            userField.setEditable(true);
            passField.setEditable(true);
            loginBtn.setDisable(false);
            return;
        }
          
        AuthManager.getInstance().attemptConnect(userid, passid);
    }
    
    /**
     * Stops rendering AuthFX stage
     */
    public void close()
    {
        Platform.runLater(() -> { auth.close(); });
    }
    
    /**
     * Initialization for AuthFX rendering
     */
    public void render() 
    {
        new JFXPanel();
        Platform.runLater(() -> 
        {
            auth = new Stage();
            start(auth);
        });       
    }
    
    /**
     * Shows an authentication error with alert box
     * @param err AuthStatus enumeration for error code
     */
    public void error(AuthStatus err)
    {
        String error = "Unknown error!";
        
        switch (err)
        {
            case EMPTY_FIELDS:
                error = "Username or password is empty!";
                break;
            case INCORRECT_DETAILS:
                error = "Username or password is incorrect!";
                break;
            case SERVER_ERROR:
                error = "Server side error! Please, try later.";             
                break;
            case DISCONNECTED:
                error = "You have been disconnected from the server.\nTry logging in again.";
                break;
        }
        
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Authentication error");
        alert.setHeaderText("Ooops, there was an authentication error!");
        alert.setContentText(error);
        alert.showAndWait();
        
        userField.setEditable(true);
        passField.setEditable(true);
        loginBtn.setDisable(false);
    }
        
    /**
     * Setting respond messages from Updater thread 
     * @param msg 
     */
    public void setUpdateLabel(String msg)
    {
        Platform.runLater(() -> 
        {
            updLabel.setText(msg);
        }); 
    }
    
    /**
     * Control AuthFX field's control's
     * @param control enable or disable
     */
    public void setAuthControl(boolean control)
    {
        Platform.runLater(() -> 
        {
            userField.setEditable(control);
            passField.setEditable(control);
            loginBtn.setDisable(!control);
        }); 
    }
}
