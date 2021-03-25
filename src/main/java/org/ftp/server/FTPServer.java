package org.ftp.server;

import org.ftp.data.FTPFileData;
import org.ftp.exception.FTPException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class FTPServer {

    private static final Integer DEFAULT_PORT = 6666;

    private static Integer port;

    public static void main(String[] args) {
        if (args.length != 1) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }
        try {
            ServerSocket listening = new ServerSocket(port);
            while (true) {
                Socket connection = listening.accept();
                Thread instance = new Thread(new ServerInstance(connection));
                instance.start();
            }
        } catch (Exception e) {
            System.out.println("Doh " + e);
        }
    }

    private static class ServerInstance implements Runnable {

        Socket connection;

        public ServerInstance(Socket connection) {
            this.connection = connection;
        }

        public void run() {
            ObjectInputStream inputStream;
            try {
                inputStream = new ObjectInputStream(connection.getInputStream());
                FTPFileData data = (FTPFileData) inputStream.readObject();
                saveFile(data);
            } catch (IOException e) {
                System.out.println("Doh: " + e);
            } catch (ClassNotFoundException e) {
                System.out.println("Doh: " + e);
            }
            try {
                connection.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e);
            }
        }

        private static void saveFile(FTPFileData data) {
            File file = new File(data.getFilename());
            FileWriter fw;
            try {
                fw = new FileWriter(file);
            } catch (IOException e) {
                throw new FTPException(String.format("Error opening file writer for %s", file.getName()));
            }

            List<String> strings = data.getData();
            for (int i = 0; i < strings.size(); i++) {
                try {
                    fw.write(strings.get(i) + ((i != strings.size() - 1) ? "\n" : ""));
                } catch (IOException e) {
                    throw new FTPException(String.format("Error writing line %d to file %s", i, file.getName()));
                }
            }
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                throw new FTPException(String.format("Error flushing/closing file %s", file.getName()));
            }
        }

    }

}
