package krowchat.Updater;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import krowchat.Manager.AuthManager;

public class Updater extends Thread
{
    boolean running     = true;
    int     version     = 1;
    String  updateUrl   = "http://draco.us.lt/krowupdate/";
    
    /**
     * Updater thread constructor
     */
    public Updater()
    {
        System.out.println("Client version: " + version);
    }
          
    /**
     * Makes connection to update server and checks if any update is available
     * @return boolean
     */
    public boolean checkUpdate()
    {
        URL url;
        try
        {
            url = new URL(updateUrl + "check.php?v=" + version);
            Scanner s = new Scanner(url.openStream());
            String output = s.next();
            String[] status = output.split("-");
            
            switch (status[0])
            {
                case "0x01":
                    AuthManager.getInstance().updaterStatus((byte)0);
                    return false;
                    
                case "0x02":
                    AuthManager.getInstance().updaterStatus((byte)1);
                    return true; 
            }
            
        }
        catch (Exception ex)
        {
            System.out.println("Updater error: " + ex.getLocalizedMessage());
        }
        
        return false;
    }
    
    /**
     * Updater thread startup method
     */
    @Override
    public void run() 
    {
        while (running)
        {
            try
            {
                Thread.sleep(1000);
                if (checkUpdate())
                    downloadUpdate();
            } 
            catch (InterruptedException e)
            {
                System.out.println("Update error: " + e.getLocalizedMessage());
            }
        }
    }
    
    /**
     * Updater thread stop method
     */
    public void quit()
    {
        running = false;
    }
    
    /**
     * Download's available update and responds to AuthManager when it's done
     */
    private void downloadUpdate()
    {        
        try 
        {
            downloadUsingNIO(updateUrl + "updates/update.jar", "update.jar");
        } 
        catch (IOException e) 
        {
            System.out.println("Update error: " + e.getLocalizedMessage());
        }
        
        AuthManager.getInstance().updaterStatus((byte)3);
    }
    
    /**
     * Download's file from url and saves it
     * @param urlStr url to download from
     * @param file save location
     * @throws IOException 
     */
    private void downloadUsingNIO(String urlStr, String file) throws IOException 
    {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }
}
