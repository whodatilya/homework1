package server;

import main.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Server
{
    
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket serverSocket;

    
    public Server()
    {
        try
        {
            serverSocket = new ServerSocket(Const.Port);

            while (true)
            {
                Socket socket = serverSocket.accept();

                Connection connection = new Connection(socket);
                connections.add(connection);

                connection.start();

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeAll();
        }
    }

    private void closeAll()
    {
        try
        {
            serverSocket.close();

            synchronized(connections)
            {
                Iterator<Connection> iter = connections.iterator();
                while(iter.hasNext())
                {
                    ((Connection) iter.next()).close();
                }
            }
        }
        catch (Exception e)
        {
            System.err.println((char) 27 + "[31mThreads are not closed! " + (char)27 + "[0m");
        }
    }


    private class Connection extends Thread
    {
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;
        private Socket socket;

        private String name = "";


        public Connection(Socket socket)
        {
            this.socket = socket;

            try
            {
                bufferedReader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                printWriter = new PrintWriter(socket.getOutputStream(), true);

            }
            catch (IOException e)
            {
                e.printStackTrace();
                close();
            }
        }


        @Override
        public void run()
        {
            try
            {
                name = bufferedReader.readLine();
                // Отправляем всем клиентам сообщение о том, что зашёл новый пользователь
                synchronized(connections)
                {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext())
                    {
                        ((Connection) iter.next()).printWriter.println(name + " " +(char) 27 + "[36mcames now " + (char)27 + "[0m");
                    }
                }

                String str = "";
                while (true)
                {
                    str = bufferedReader.readLine();
                    if(str.equals("exit")) break;

                    // Отправляем всем клиентам очередное сообщение
                    synchronized(connections)
                    {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext())
                        {
                            ((Connection) iter.next()).printWriter.println(name + ": " + str);
                        }
                    }
                }

                synchronized(connections)
                {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext())
                    {
                        ((Connection) iter.next()).printWriter.println(name + " " +(char) 27 + "[31mhas left " + (char)27 + "[0m");
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                close();
            }
        }

        public void close()
        {
            try
            {
                bufferedReader.close();
                printWriter.close();
                socket.close();

                connections.remove(this);
                if (connections.size() == 0)
                {
                    Server.this.closeAll();
                    System.exit(0);
                }
            }
            catch (Exception e)
            {
                System.err.println((char) 27 + "[31mThreads are not closed! " + (char)27 + "[0m");
            }
        }
    }
}