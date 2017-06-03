package server;

import java.io.IOException;

public class Main {


    public static void main(String[] args) {
       try{
            new Server();
        } catch (IOException e) {
            throw new Error("Cannot instantiate server.Server!");
        }

    }
}
