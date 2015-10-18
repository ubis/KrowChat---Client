package krowchat.Manager;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import krowchat.Network.Authentication;
import krowchat.Util.AuthStatus;

public class NetworkManager
{
    private static NetworkManager   netMan;
    private Client                  netClient;
    private final String            serverIp    = "78.57.170.249";
    private final int               serverPort  = 2550;
    
    /**
     * NetworkManager constructor
     */
    public NetworkManager()
    {
        netMan = this;
        
        // start up client
        netClient = new Client();
	netClient.start();

        // register packets
	Authentication.register(netClient);

        // start listener
	netClient.addListener(new Listener() 
        {
            @Override
            public void connected(Connection connection) 
            {
                System.out.println("Connection successful. Sending authentication request.");
		Authentication.C2S_AuthRequest request = new Authentication.C2S_AuthRequest();
		request.username = AuthManager.getInstance().getName();
                request.password = AuthManager.getInstance().getPassword();
		netClient.sendTCP(request);
            }
            
            @Override
            public void disconnected(Connection connection) 
            {
                System.out.println("Server closed connection.");
                AuthManager.getInstance().errorOccurred(AuthStatus.DISCONNECTED);
            }

            @Override
            public void received (Connection connection, Object object) 
            {
                System.out.println("Received packet: " + object + ".");
                                
                if (object instanceof Authentication.S2C_AuthRespond) 
                {
                    Authentication.S2C_AuthRespond respond = (Authentication.S2C_AuthRespond)object;
                    
                    if (respond.status == 1)
                    {
                        // init uiman
                        UIManager.getInstance().startUI();
                        AuthManager.getInstance().errorOccurred(AuthStatus.AUTH_OK);
                    } 
                    else 
                        AuthManager.getInstance().errorOccurred(AuthStatus.INCORRECT_DETAILS);
                    
                    System.out.println("Respond status: " + respond.status);
                    return;
                }
                
                if (object instanceof Authentication.LoadRespond) 
                {
                    Authentication.LoadRespond respond = (Authentication.LoadRespond)object;
                    
                    if (respond.name == null)
                        return;
                    
                    System.out.println("NEW CONTACT: #" + respond.idx + ". Name: " + respond.name + ".");
                    
                    UIManager.getInstance().addContact(respond.idx, respond.name);
                    return;
                }
                
                if (object instanceof Authentication.MessageReceive) 
                {
                    Authentication.MessageReceive respond = (Authentication.MessageReceive)object;
                    System.out.println("Received message from #" + respond.idx + ". Message: " + respond.msg + ".");
                    UIManager.getInstance().receiveMessage(respond.idx, respond.msg);
                    return;
                }
                
                if (object instanceof Authentication.RemoveContact) 
                {
                    Authentication.RemoveContact respond = (Authentication.RemoveContact)object;
                    UIManager.getInstance().removeContact(respond.idx);
                }
               
            }
	});
    }
    
    /**
     * Method for getting NetworkManager instance
     * @return NetworkManager
     */
    public static NetworkManager getInstance()
    {
        return netMan;
    }
    
    /**
     * Connection to authentication server
     */
    public void connect()
    {
        try
        {
            netClient.connect(5000, serverIp, serverPort);
        } 
        catch (Exception e)
        {
            AuthManager.getInstance().errorOccurred(AuthStatus.SERVER_ERROR);
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Sends message to user via server
     * @param idx user's id
     * @param msg sending message
     */
    public void writeMessage(int idx, String msg)
    {        
        System.out.println("Sending message to #" + idx + ". Message: " + msg + ".");
        Authentication.MessageRespond respond = new Authentication.MessageRespond();
        respond.idx = idx;
        respond.msg = msg;
        netClient.sendTCP(respond);
    }
}
