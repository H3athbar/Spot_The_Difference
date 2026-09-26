// Fig. 28.8: ServerTest.java
// Class that tests the Server.

import javax.swing.*;

public class ServerDriver
{
    public static void main(String[] args)
    {
        Server application = new Server(); // create server
        application.waitForPlayers(); // run server application
    }
}