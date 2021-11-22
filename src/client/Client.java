package client;

import main.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private Socket socket;

    public Client()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println((char) 27 + "[32mPlease, enter IP adress of server: " + (char)27 + "[0m");


        String ip = scanner.nextLine();
        try
        {
            socket = new Socket(ip, Const.Port);
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outputWriter = new PrintWriter(socket.getOutputStream(), true);

            System.out.println((char) 27 + "[32mEnter your nickname: " + (char)27 + "[0m");
            outputWriter.println(scanner.nextLine());

            Resend resend = new Resend();
            resend.start();

            String str = "";
            while (!str.equals("exit"))
            {
                str = scanner.nextLine();
                outputWriter.println(str);
            }
            resend.setStop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            close();
        }

        }
    private void close()
    {
        try
        {
            inputReader.close();
            outputWriter.close();
            socket.close();
        } catch (Exception e)
        {
            System.err.println((char) 27 + "[31mSomething wrong! " + (char)27 + "[0m");
        }
    }
    private class Resend extends Thread
    {
        private boolean isStopped;

        public void setStop()
        {
            isStopped = true;
        }
        @Override
        public void run()
        {
            try
            {
                while (!isStopped)
                {
                    String str = inputReader.readLine();
                    System.out.println(str);
                }
            }
            catch (IOException e)
            {
                System.err.println((char) 27 + "[31mError receiving message... " + (char)27 + "[0m");
                e.printStackTrace();
            }
        }
    }
}
