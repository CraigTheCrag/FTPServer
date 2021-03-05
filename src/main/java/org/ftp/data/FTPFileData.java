package org.ftp.data;

import org.ftp.exception.FTPException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FTPFileData {

    private List<String> data;
    private String filename;

    public FTPFileData(File file) {
        this.filename = file.getName();
        this.data = this.readData(file);
    }

    private List<String> readData(File file) throws FTPException {
        List<String> data = new ArrayList<>();
        Scanner scanner;
        try {
             scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new FTPException(String.format("File not found: %s", file.getName()));
        }
        return data;
    }

    public String getFilename() {
        return this.filename;
    }

    public List<String> getData() {
        return this.data;
    }
}
