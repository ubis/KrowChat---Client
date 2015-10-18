package krowchat.Manager;

import java.io.InputStream;

import krowchat.UI.AuthFX;
import krowchat.Util.AuthStatus;
import krowchat.Updater.Updater;

public class AuthManager
{
    private static AuthManager  authMan;
    private AuthFX              auth;
    private Updater             upd;
    private String[]            auth_details = new String[2];
    
    /**
     * AuthManager constructor
     */
    public AuthManager()
    {
        authMan = this;
        
        // let's create AuthFX window and render it
        auth = new AuthFX();
        auth.render();
        
        // let's disable control at start
        auth.setUpdateLabel("Checking for update...");
        auth.setAuthControl(false);
        
        // let's start up updater thread
        upd = new Updater();
        upd.start();
    }
    
    /**
     * Method for getting AuthManager instance
     * @return AuthManager
     */
    public static AuthManager getInstance()
    {
        return authMan;
    }
        
    /**
     * Method for trying connect to the Authentication server
     * @param uid username
     * @param pwd password
     */
    public void attemptConnect(String uid, String pwd)
    {
        System.out.println("Attempt connect: " + uid + ".");
        auth_details[0] = uid;
        auth_details[1] = uid;
        NetworkManager.getInstance().connect();
    }
    
    /**
     * Showing network response errors with alert box
     * @param err AuthStatus enumeration for error code
     */
    public void errorOccurred(AuthStatus err)
    {
        if (err == AuthStatus.AUTH_OK)
        {
            auth.close();
            return;
        }
        
        if (err == AuthStatus.DISCONNECTED)
        {
            auth.render();
        }
        
        auth.error(err);
    }
    
    /**
     * Returns user login name
     * @return String
     */
    public String getName()
    {
        return auth_details[0];
    }
    
    /**
     * Returns user login password
     * @return String
     */
    public String getPassword()
    {
        return auth_details[1];
    }
    
    /**
     * Return status from Updater thread
     * @param status status number
     */
    public void updaterStatus(byte status)
    {
        switch (status)
        {
            case 0:
                upd.quit();
                auth.setUpdateLabel("No update available.");
                auth.setAuthControl(true);
                break;
            case 1:
                auth.setUpdateLabel("Downloading update...");
                break; 
            case 3:
                upd.quit();
                auth.setUpdateLabel("Program will close now.");
                try 
                {
                    Thread.sleep(2000);            
                    Process proc = Runtime.getRuntime().exec("java -jar update.jar -perform");
                    InputStream in = proc.getInputStream();
                    InputStream err = proc.getErrorStream();
                }
                catch (Exception e)
                {
                    System.out.println("Update error: " + e.getLocalizedMessage());
                }

                System.exit(0);                
                break;
        }
    }
}
