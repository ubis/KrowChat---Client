package krowchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import krowchat.Manager.GlobalManager;

public class KrowChat 
{    
    public static void main(String[] args) throws IOException
    {     
        long startTime = System.currentTimeMillis(); 
        
        // perform update and/or clean old file
        performUpdate(args);
        
        // let's start up GlobalManager
        GlobalManager globMan = new GlobalManager();

        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime-startTime) + "ms.");          
    }
    
    private static void performUpdate(String[] args)
    {
        if (args.length > 0)
        {
            if (args[0].equals("-perform"))
            {
                System.out.println("Performing update...");
                try 
                {
                    Thread.sleep(2000);
                    String currentDir = new File("").getAbsolutePath();
                    
                    File old = new File(currentDir + "/KrowChat.jar");
                    old.delete();
                    
                    File src = new File(currentDir + "/update.jar");
                    Path srcPath = src.toPath();
                    Files.copy(srcPath, new File(currentDir + "/KrowChat.jar").toPath());
                    
                    Process proc = Runtime.getRuntime().exec("java -jar KrowChat.jar");
                    InputStream in = proc.getInputStream();
                    InputStream err = proc.getErrorStream();
                }
                catch (Exception e)
                {
                    System.out.println("Update error: " + e.getMessage());
                    
                }
                System.exit(0);
            }
        } 
        else
        {
            String currentDir = new File("").getAbsolutePath();
            File old = new File(currentDir + "/update.jar");
            
            if (old.exists())
                old.delete();    
        }        
    }
}
