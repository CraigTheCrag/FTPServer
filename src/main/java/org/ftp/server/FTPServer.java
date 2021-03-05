package org.ftp.server;

import org.ftp.data.FTPFileData;
import org.ftp.exception.FTPException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class FTPServer {

    private static final Integer DEFAULT_PORT = 6666;

    private static Integer port;

    private static Socket s;
    private static ServerSocket ss;

    public static void main(String[] args) throws FTPException {

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = DEFAULT_PORT;
        }

        s = openSocket();
        ObjectInputStream ois;

        try {
            ois = new ObjectInputStream(s.getInputStream());
        } catch (IOException e) {
            throw new FTPException(String.format("Error creating object input stream on port: %d", port));
        }

        FTPFileData data;
        try {
            data = (FTPFileData) ois.readObject();
        } catch (IOException e) {
            throw new FTPException(String.format("Error reading data from connection over port %d", port));
        } catch (ClassNotFoundException e) {
            throw new FTPException(String.format("Class not found"));
        }

        saveData(data);

    }

    private static Socket openSocket() throws FTPException {
        try {
            ss = new ServerSocket(port);
            return ss.accept();
        } catch (IOException e) {
            throw new FTPException(String.format("Error opening server socket on port: %d", port));
        }
    }

    private static void closeSocket() throws FTPException {
        try {
            ss.close();
        } catch (IOException e) {
            throw new FTPException(String.format("Error closing server socket on port %d", port));
        }
    }

    private static void saveData(FTPFileData data) throws FTPException {
        File file = new File(data.getFilename());
        FileWriter fw;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            throw new FTPException(String.format("Error opening file writer for %s", file.getName()));
        }

        List<String> strings = data.getData();
        for (int i=0;i<strings.size();i++) {
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
