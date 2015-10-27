package krowchat.UI;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import krowchat.Manager.UIManager;

public class MainFX extends Application
{
    class Contact
    {
        private int         id;    
        private String      name;
        private Label       label;
        private FlowPane    messages;
        
        public Contact(int id, String name, Label label)
        {
            this.id = id;
            this.name = name;
            this.label = label;
            messages = new FlowPane();
            messages.heightProperty().addListener(new ChangeListener() 
            {
                @Override
                public void changed(ObservableValue observable, Object oldvalue, Object newValue) 
                {
                    chatScrollPane.setVvalue((Double)newValue );  
                }
            });
        }
        
        public int getId()
        {
            return id;
        }
        
        public Label getLabel()
        {
            return label;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
                
        public void setMessages(FlowPane messages)
        {
            this.messages = messages;
        }
        
        public FlowPane getMessages()
        {
            return messages;
        }
    }
        
    private static boolean  _KDEBUG;
    private Stage           main;
    private int             selectedId = -1;
    private Label           localName;
    private Label           remoteName;
    private VBox            sidebarVBox;
    private VBox            listbarVBox;
    private FlowPane        chatFlowPane;
    private ScrollPane      chatScrollPane;
    private TextField       writeField;
    private Stage           dummyPopup;
    private List<Contact>   contacts = new ArrayList<>();
   
    /**
     * MainFX constructor
     */
    public MainFX() 
    {
        /* */
    }
    
    /**
     * Starts to render MainFX stage
     * @param stage MainFX's stage to render
     */
    @Override
    public void start(Stage stage)
    {           
        long startTime = System.currentTimeMillis();    
        main = stage;
        /*scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> 
        {
            topbar.ResizeImage(scene.getWidth(), false);
        });
        
        scene.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeigth, Number newSceneHeigth) -> 
        {
            sidebar.ResizeImage(false, scene.getHeight());
        });*/
        
        TrayIconWorkAround();
        
        Group root = new Group();
        Scene scene = new Scene(root);
        
        Pane rootPane = new Pane();
        
        prepareTopPane(rootPane);
        prepareSidePane(rootPane);
        prepareListPane(rootPane);
        prepareChatPane(rootPane);
        prepareWritePane(rootPane);
               
        root.getChildren().add(rootPane);
        
        main.setOnCloseRequest((WindowEvent e) ->
        {
            if (_KDEBUG)
                System.exit(0);
            
            dummyPopup.show();
            main.hide();
        });
        
        main.setScene(scene);
        main.setTitle("KrowChat");
        main.show();
        
        addSidebarButton("Contacts", "contact-icon");
        addSidebarButton("History", "history-icon");
        addSidebarButton("Add Contact", "addcontact-icon");
        addSidebarButton("Settings", "settings-icon");
        addSidebarButton("Logout", "logout-icon");       
        
        long endTime = System.currentTimeMillis();
        System.out.println("MainFX total execution time: " + (endTime-startTime) + "ms."); 
    }
    
    /**
     * Creates topbar pane
     * MainFX thread
     * @param rootPane root pane to add topbar
     */
    private void prepareTopPane(Pane rootPane)
    {
        kImageView topbar = new kImageView("/krowchat/Style/backgrounds/topbar.png");
        Pane topPane = new Pane();
        topPane.setLayoutX(65.0);
        topPane.getChildren().add(topbar);
        
        kImageView avatar = new kImageView("/krowchat/Style/avatar.png");
        avatar.setLayoutX(10.0);
        avatar.setLayoutY(10.0);
        topPane.getChildren().add(avatar);
        
        localName = new Label();
        localName.setFont(Font.font("Lato Light", 24));
        localName.setLayoutX(65.0);
        localName.setLayoutY(8.0);
        localName.setText("");
        localName.setTextFill(Color.WHITE);
        topPane.getChildren().add(localName);
        
        remoteName = new Label();
        remoteName.setFont(Font.font("Lato Light", 24));
        remoteName.setLayoutX(290.0);
        remoteName.setLayoutY(8.0);
        remoteName.setText("");
        remoteName.setTextFill(Color.WHITE);
        
        topPane.getChildren().add(remoteName);
        
        Label exitBtn = new Label();
        Font exitFont = new Font("Comfortaa", 15);
        exitBtn.setLayoutX(1065.0);
        exitBtn.prefWidth(3.0);
        exitBtn.prefHeight(17.0);
        exitBtn.setText("x");
        exitBtn.setTextFill(Color.valueOf("#3e2d1b"));
        exitBtn.setFont(exitFont);
        //topPane.getChildren().add(exitBtn);
        
        rootPane.getChildren().add(topPane);        
    }

    /**
     * Creates sidebar pane
     * MainFX thread
     * @param rootPane root pane to add sidebar
     */
    private void prepareSidePane(Pane rootPane)
    {
        kImageView sidebar = new kImageView("/krowchat/Style/backgrounds/sidebar.png");
        Pane sidePane = new Pane();
        sidePane.getChildren().add(sidebar);
                
        sidebarVBox = new VBox();
        sidebarVBox.setPrefWidth(65.0);
        sidebarVBox.setAlignment(Pos.CENTER);
        sidePane.getChildren().add(sidebarVBox);
               
        rootPane.getChildren().add(sidePane);        
    }
    
    /**
     * Creates listbar pane
     * MainFX thread
     * @param rootPane root pane to add listbar
     */
    private void prepareListPane(Pane rootPane)
    {
        kImageView listbar = new kImageView("/krowchat/Style/backgrounds/searchbar.png");
        Pane listPane = new Pane();
        listPane.setLayoutX(65.0);
        listPane.setLayoutY(69.0);
        listPane.getChildren().add(listbar);
        listPane.setStyle("-fx-border-color: #cdd2d5; -fx-border-width: 0 1px 0 0");
        
        TextField searchField = new TextField();
        searchField.setLayoutX(40.0);
        searchField.setLayoutY(11.5);
        searchField.setPrefSize(226.0, 25.0);
        searchField.setStyle("-fx-background-color: transparent;");
        listPane.getChildren().add(searchField);
        
        listbarVBox = new VBox();
        listbarVBox.setLayoutY(49.0);
        listbarVBox.setPrefSize(280.0, 582.0);
        listbarVBox.setPadding(new Insets(0, 0, 0, 0));    
        listPane.getChildren().add(listbarVBox);
        
        rootPane.getChildren().add(listPane);   
    }
    
    /**
     * Creates chatbar pane
     * MainFX thread
     * @param rootPane root pane to add chatbar
     */
    private void prepareChatPane(Pane rootPane)
    {
        Pane chatPane = new Pane();
        chatPane.setLayoutX(345.0);
        chatPane.setLayoutY(66.0);
        
        chatScrollPane = new ScrollPane();
        chatScrollPane.setPrefSize(799.0, 570.0);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        chatScrollPane.setStyle("-fx-background-color: transparent;");
        chatFlowPane = new FlowPane();
        chatFlowPane.setPrefSize(775.0, 568.0);    
        
        chatScrollPane.setContent(chatFlowPane);
        
        chatFlowPane.heightProperty().addListener(new ChangeListener() 
        {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) 
            {
                chatScrollPane.setVvalue((Double)newValue );  
            }
        });
        
        chatPane.getChildren().add(chatScrollPane);
        rootPane.getChildren().add(chatPane);   
    }
    
    /**
     * Creates writebar pane
     * MainFX thread
     * @param rootPane root pane to add writebar
     */
    private void prepareWritePane(Pane rootPane)
    {
        Pane writePane = new Pane();
        writePane.setLayoutX(345.0);
        writePane.setLayoutY(642.0);    
        writePane.setPrefSize(799.0, 58.0);
        writePane.setStyle("-fx-border-color: #cdd2d5; -fx-border-width: 1px 0 0 0");
        
        writeField = new TextField();
        writeField.setLayoutX(15.0);
        writeField.setLayoutY(11.0);
        writeField.setPrefSize(672.0, 35.0);
        writeField.setDisable(true);
        writePane.getChildren().add(writeField);
        
        kImageView sendBtn = new kImageView("/krowchat/Style/icons/send-icon.png");
        sendBtn.setLayoutX(700.0);
        sendBtn.setLayoutY(8.5);
        sendBtn.setFitWidth(90.0);
        sendBtn.setFitHeight(40.0);
        writePane.getChildren().add(sendBtn);
        
        sendBtn.setOnMouseClicked((MouseEvent e) -> { writeMessage(); });
        writeField.setOnAction((ActionEvent e) -> { writeMessage(); });
        
        rootPane.getChildren().add(writePane);  
    }
    
    /**
     * Creates sidebar button
     * MainFX thread
     * @param name name of the button
     * @param image image of the button, must be in Style/icons folder!
     */
    private void addSidebarButton(String name, String image)
    {
        kImageView icon = new kImageView("/krowchat/Style/icons/" + image + ".png"); 
        Label tmp = new Label();
        tmp.setAlignment(Pos.CENTER);
        tmp.setContentDisplay(ContentDisplay.TOP);
        tmp.setTextFill(Color.WHITE);
        tmp.setGraphic(icon);
        tmp.setPrefSize(65.0, 67.0);
        tmp.setFont(new Font(10));
                
        tmp.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>()
        {

            boolean pressed = false;
            @Override
            public void handle(MouseEvent e)
            {
                if (e.getEventType() == MouseEvent.MOUSE_ENTERED)
                {
                    tmp.setText(name);
                    tmp.setStyle("-fx-background-color: #182333;");
                }
                else if (e.getEventType() == MouseEvent.MOUSE_EXITED)
                {
                    pressed = false;
                    tmp.setText("");
                    tmp.setStyle("-fx-background-color: transparent;");
                }
                else if (e.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    pressed = true;
                    tmp.setStyle("-fx-background-color: black;");
                }
                else if (e.getEventType() == MouseEvent.MOUSE_RELEASED)
                {
                    if (!pressed) return;
                    tmp.setStyle("-fx-background-color: #182333;");
                }
            }
        });
        
        sidebarVBox.getChildren().add(tmp);   
    }
     
    /**
     * Writes a new message in chatbar
     * MainFX thread
     * @param name message source contact name
     * @param time time when message was written
     * @param message message
     */
    private void newChatMessage(String name, String time, String message)
    {
        newChatMessage(name, time, message, true, -1);
    }
    
    /**
     * Writes a new message in chatbar
     * MainFX thread
     * @param name message source contact name
     * @param time time when message was written
     * @param message message
     * @param local if it's local message, name tag is ignored
     * @param id if it's not equal to -1, message is from remote contact
     */
    private void newChatMessage(String name, String time, String message, boolean local, int id)
    {
        FlowPane tmp = null;
        int l_id = -1;
        
        if (id > -1)
        {
            l_id = getLocalID(id);
            name = getContactName(l_id);
            tmp = getContactPane(l_id);
        }
            
        Pane msgPane = new Pane();
        msgPane.setPrefWidth(793.0);
        msgPane.setPadding(new Insets(10, 0, 0, 0));
        msgPane.setStyle("-fx-border-color: #cdd2d5; -fx-border-width: 1px 0 0 0");
        
        Label msgNameLabel = new Label();
        msgNameLabel.setLayoutX(14.0);
        msgNameLabel.setLayoutY(14.0);
        msgNameLabel.setText(name);
        if (local)
            msgNameLabel.setTextFill(Color.valueOf("#a2acb1"));
        else
            msgNameLabel.setTextFill(Color.valueOf("#358858"));
        Font font = new Font("Comfortaa", 17.0);
        msgNameLabel.setFont(font);
        msgPane.getChildren().add(msgNameLabel);

        Label msgTimeLabel = new Label();
        msgTimeLabel.setLayoutX(747.0);
        msgTimeLabel.setLayoutY(16.0);
        msgTimeLabel.setText(time);
        msgTimeLabel.setTextFill(Color.valueOf("#a2acb1"));
        Font font2 = new Font("Comfortaa", 14.0);
        msgTimeLabel.setFont(font2);
        msgPane.getChildren().add(msgTimeLabel);

        Text msgText = new Text();
        msgText.setLayoutX(15.0);
        msgText.setLayoutY(55.0);
        msgText.setText(message);
        msgText.setWrappingWidth(730.0);
        msgText.setFill(Color.valueOf("#727a7e"));
        msgPane.getChildren().add(msgText);
                    
        if (tmp != null)
        {
            tmp.getChildren().add(msgPane);
            setContactPane(l_id, tmp);
        }
        else
        {
            chatFlowPane = getContactPane(selectedId);
            chatFlowPane.getChildren().add(msgPane);
            setContactPane(selectedId, chatFlowPane);
            //chatFlowPane.getChildren().add(msgPane); 
        }
    }  
    
    /**
     * Creates a new contact in listbar
     * MainFX thread
     * @param id contact's id
     * @param name contact's name
     */
    private void newContact(int id, String name)
    {
        Label tmp = new Label();
        tmp.setPrefSize(312.0, 60.0);
        tmp.setText(name);
        Font listFont = new Font("Comfortaa", 17);
        tmp.setFont(listFont);
        kImageView profile_img = new kImageView("/krowchat/Style/avatar.png");
        profile_img.setFitWidth(43.0);
        profile_img.setFitHeight(44.0);
        tmp.setGraphic(profile_img);
        tmp.setPadding(new Insets(0, 5, 0, 5));
            
        tmp.addEventHandler(MouseEvent.ANY, (MouseEvent e) ->
        {           
            if (e.getEventType() == MouseEvent.MOUSE_RELEASED)
            {
                resetSelection();
                tmp.setStyle("-fx-background-color: #dce6e8;"); 
                tmp.setFont(Font.font("Comfortaa", FontWeight.BOLD, 17));
            }
            else if(e.getEventType() == MouseEvent.MOUSE_CLICKED)
            {
                if (e.getClickCount() == 2)
                {
                    setRemoteName(name);
                    selectedId = getLocalIDByLabel(tmp);
                    chatFlowPane = contacts.get(selectedId).getMessages();
                    chatScrollPane.setContent(chatFlowPane);
                    writeField.setDisable(false);
                }
            }
        }); 
        listbarVBox.getChildren().add(tmp); 
        contacts.add(new Contact(id, name, tmp));
        
    }

    /**
     * Removing previous selections in listbar
     * MainFX thread
     */
    private void resetSelection()
    {
        for (int i = 0; i < listbarVBox.getChildren().size(); i ++)
        {
            Label tmp = (Label)listbarVBox.getChildren().get(i);
            if (tmp.getStyle().equals("-fx-background-color: #dce6e8;"))
            {
                tmp.setStyle("-fx-background-color: transparent;");
                tmp.setFont(Font.font("Comfortaa", FontWeight.NORMAL, 17));
                break;
            }
        }
    } 
    
    /**
     * Writes a new message in chatbar and/or to contact
     * MainFX thread
     */
    private void writeMessage()
    {
        if (selectedId == -1)
            return;
            
        String message = writeField.getText();
      
        if (message.length() == 0 || message.length() > 18000)
            return;

        newChatMessage(getLocalName(), "00:00", message);
        writeField.setText("");
        writeField.requestFocus();   
        
        UIManager.getInstance().sendMessage(getIdByLocal(selectedId), message);
    }
    
    /**
     * Gets contact's global(local) id by his real id
     * MainFX thread
     * @param id contact's real id
     * @return int
     */
    private int getLocalID(int id)
    {
        for (int i = 0; i < contacts.size(); i ++)
        {
            if (contacts.get(i).getId() == id)
                return i;
        }
        
        return -1;
    }
    
    /**
     * Gets contact's real id by it's global(local) id
     * MainFX thread
     * @param id contact's local id
     * @return int
     */
    private int getIdByLocal(int id)
    {
        if (id == -1)
            return -1;
        
        return contacts.get(id).getId();
    }
    
    /**
     * Gets contact's global(local) id by it's label in listbar
     * MainFX thread
     * @param label contact's label in listbar
     * @return int
     */
    private int getLocalIDByLabel(Label label)
    {
        for (int i = 0; i < contacts.size(); i ++)
        {
            if (contacts.get(i).getLabel() == label)
                return i;
        }
        
        return -1;
    }
    
    /**
     * Gets contact's name by global(local) id
     * MainFX thread
     * @param id local id
     * @return String
     */
    private String getContactName(int id)
    {
        if (id == -1)
            return null;
        
        return contacts.get(id).getName();
    }
    
    /**
     * Gets contact's label by global(local) id
     * MainFX thread
     * @param id local id
     * @return Label
     */
    private Label getContactLabel(int id)
    {
        if (id == -1)
            return null;
        
        return contacts.get(id).getLabel();       
    }
    
    /**
     * Get's contact's FlowPane from global(local) id
     * MainFX thread
     * @param id local id
     * @return FlowPane
     */
    private FlowPane getContactPane(int id)
    {
        if (id == -1)
            return null;
        
        return contacts.get(id).getMessages();       
    }
    
    /**
     * Sets contact's FlowPane by global(local)id
     * MainFX thread
     * @param id local id
     * @param messages FlowPane to set
     */
    private void setContactPane(int id, FlowPane messages)
    {
        if (id == -1)
            return;
        
        contacts.get(id).setMessages(messages);       
    }    
    
    /**
     * TrayIcon temporary workaround
     * MainFX thread
     */
    private void TrayIconWorkAround()
    {
        dummyPopup = new Stage();
        dummyPopup.initModality(Modality.NONE);
        dummyPopup.initStyle(StageStyle.UTILITY);
        dummyPopup.setOpacity(0d);
        final Screen screen = Screen.getPrimary();
        final Rectangle2D bounds = screen.getVisualBounds();
        dummyPopup.setX(bounds.getMaxX());
        dummyPopup.setY(bounds.getMaxY());
        final Group root = new Group();
        dummyPopup.setScene(new Scene(root, 1d, 1d, Color.TRANSPARENT));    
    } 
    
    /**
     * Sets local name in MainFX
     * @param name name to set
     */
    public void setLocalName(String name)
    {
        Platform.runLater(() -> { localName.setText(name); });
    }
    
    /**
     * Sets contact name in MainFX
     * @param name name to set
     */
    public void setRemoteName(String name)
    {
        Platform.runLater(() -> { remoteName.setText(name); });
    }
    
    /**
     * Gets local name in MainFX
     * @return String
     */
    public String getLocalName()
    {
        return localName.getText();
    }
    
    /**
     * Gets contact name in MainFX
     * @return String
     */
    public String getRemoteName()
    {
        return remoteName.getText();
    }
    
    /**
     * Creates a new contact in listbar
     * @param id contact's id
     * @param name contact's name
     */
    public void addContact(int id, String name)
    {
        Platform.runLater(() -> { newContact(id, name); });
    }
        
    /**
     * Writes a new message in chatbar
     * @param time time when message was written
     * @param message message
     * @param id message source contact's id
     */
    public void addChatMessage(String time, String message, int id)
    {
        Platform.runLater(() -> { newChatMessage("", time, message, false, id); });
    }
    
    /**
     * Removes contact from listbar and from chatbar
     * @param id contact's id to remove
     */
    public void removeContact(int id)
    {
        Platform.runLater(() -> 
        { 
            int l_id = getLocalID(id);
            
            if (selectedId == id)
                selectedId = -1;
            
            if (l_id == -1)
                return;
            
            Label tmp = contacts.get(l_id).getLabel();
            listbarVBox.getChildren().remove(tmp);
            chatScrollPane.setContent(new FlowPane());
            writeField.setDisable(true);
            contacts.remove(l_id);
            setRemoteName("");
        });
    }
        
    /**
     * Shows MainFX window
     */
    public void show() 
    {
        Platform.runLater(() -> 
        {
            main.show();
            dummyPopup.hide();
        });      
    }
    
    /**
     * Hides MainFX window
     */
    public void hide()
    {
        Platform.runLater(() -> 
        {
            dummyPopup.show();
            main.hide();
        });         
    }
    
    /**
     * Returns if MainFX window is showing
     * @return boolean
     */
    public boolean isShowing()
    {
        return main.isShowing();
    }
       
    /**
     * Initialization for MainFX rendering
     */
    public void render() 
    {
        new JFXPanel();
        Platform.runLater(() -> 
        {
            start(new Stage());
        });       
    }
    
    /**
     * Main static method for test and/or debug purposes
     * @param args program args to launch
     */
    public static void main(String[] args)
    {
        _KDEBUG = true;
        launch(args);
    }
}