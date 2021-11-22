package main;

import client.Client;
import server.Server;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);

        System.out.println("Open programm in client or server style? (S(erver) / C(lient))");
        while (true)
        {
            char answer = Character.toLowerCase(in.nextLine().charAt(0));
            if (answer == 's')
            {
                new Server();
                break;
            }
            else if (answer == 'c')
            {
                new Client();
                break;
            }
            else
            {
                System.out.println((char) 27 + "[31mIncorrect input. Please, try again... " + (char)27 + "[0m");
            }
        }
    }
}
