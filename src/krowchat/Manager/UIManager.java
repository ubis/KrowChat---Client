package krowchat.Manager;

import krowchat.UI.MainFX;

public class UIManager
{
    private static UIManager    uiMan;
    private MainFX              ui;
    
    /**
     * UIManager contstructor
     */
    public UIManager()
    {
        uiMan = this;
    }
    
    /**
     * Method for getting UIManager instance
     * @return UIManager
     */
    public static UIManager getInstance()
    {
        return uiMan;
    }
       
    /**
     * Adds new contact to the list
     * @param id contact id
     * @param name contact name
     */
    public void addContact(int id, String name)
    {
        ui.addContact(id, name);
    }
    
    /**
     * Removes contact from the list
     * @param id contact's id
     */
    public void removeContact(int id)
    {
        ui.removeContact(id);
    }
    
    /**
     * Sends message to user
     * @param id user's id
     * @param message sending message
     */
    public void sendMessage(int id, String message)
    {        
        NetworkManager.getInstance().writeMessage(id, message);
    }
    
    /**
     * Notices and writes received message from user
     * @param id user's id that sent message
     * @param message message was received
     */
    public void receiveMessage(int id, String message)
    {
        ui.addChatMessage("00:00", message, id);
    }
    
    /**
     * Starts to render MainFX window
     */
    public void startUI()
    {
        // it's alive!
        // render main application window
        ui = new MainFX();
        ui.render();
        ui.setLocalName(AuthManager.getInstance().getName());
    }
    
    /**
     * Show or hide MainFX ui
     * @param show show or hide
     */
    public void showHideUI(boolean show)
    {
        if (ui == null)
            return;
        
        if (show)
            ui.show();
        else
            ui.hide();
    }
    
    /**
     * Check if MainFX window is showing
     * @return boolean
     */
    public boolean isShowing()
    {
        return ui.isShowing();
    }
}
