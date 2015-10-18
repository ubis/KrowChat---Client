package krowchat.Manager;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.DataInputStream;

public class GlobalManager
{
    private static GlobalManager    globMan;
    
    /**
     * GlobalManager constructor
     */
    public GlobalManager()
    {
        globMan = this;
        
        // let's start up AuthManager
        AuthManager authMan = new AuthManager();
        
        // let's start up NetworkManager
        NetworkManager netMan = new NetworkManager();
        
        // let's start up UIManager
        UIManager uiMan = new UIManager();
        
        // create tray icon
        initTray();
    }
    
    /**
     * Method for getting GlobalManager instance
     * @return GlobalManager
     */
    public static GlobalManager getInstance()
    {
        return globMan;
    }
       
    /**
     * Create system tray icon
     */
    private void initTray()
    {
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) 
        {
            SystemTray tray = SystemTray.getSystemTray();            
            Image image = null; 
            byte bytes[] = null;
            DataInputStream datainputstream = new DataInputStream(getClass().getResourceAsStream("/krowchat/Style/icons/tray-icon.png"));
            
            try
            {
                bytes = new byte[datainputstream.available()];
                datainputstream.readFully(bytes);
                datainputstream.close();
                image = Toolkit.getDefaultToolkit().createImage(bytes);
            } 
            catch (Exception ex)
            {
                System.out.println("Tray icon error: " + ex.getMessage());
            }
                      
            PopupMenu popup = new PopupMenu();
            
            MenuItem openItem = new MenuItem("Open");
            openItem.addActionListener((ActionEvent e) -> { UIManager.getInstance().showHideUI(true); });
            popup.add(openItem);
            
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener((ActionEvent e) -> { System.exit(0); });
            popup.add(exitItem);
            
            trayIcon = new TrayIcon(image, "KrowChat", popup);
            trayIcon.addActionListener((ActionEvent e) -> 
            { 
                if (UIManager.getInstance().isShowing())
                    UIManager.getInstance().showHideUI(false);
                else
                    UIManager.getInstance().showHideUI(true);
            });
            
            try
            {
                tray.add(trayIcon);
            } 
            catch (Exception ex)
            {
                System.out.println("Tray icon error: " + ex.getMessage());
            }
        } 
    }
}
