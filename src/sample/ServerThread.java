package sample;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    protected Socket socket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;

    public ServerThread(Socket socket){
        super();
        this.socket = socket;
        try{
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch(IOException e){
            System.err.println("IOEXception while opening a read/write connection");
        }
    }


    public void run() {
        String message;
        try {

            message = checkMessage();
            //Message sent from client to server
            System.out.println("Message = " + message);
            if(message.equals("CONNECT")) {
                out = new PrintWriter(socket.getOutputStream(), true);

                folderFiles();
            }
            if(message.equals("UPLOAD")) receiveFile();
            if(message.equals("DOWNLOAD")) sendFile();
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void folderFiles() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        File remoteDirectory = new File("./RemoteFolder");
        File[] content = remoteDirectory.listFiles();
        String message = "";
        if (remoteDirectory.isDirectory()) {
            for (File current : content) {
                //System.out.println(current.getName());
                message += current.getName() + ",";
            }
        }
        out.println(message);
        out.flush();
    }

    private void sendFile() throws IOException {
        String fileName = checkMessage();
        InputStream in = new FileInputStream("./RemoteFolder/" + fileName);
        OutputStream out = socket.getOutputStream();
        byte[] buffer = new byte[8192];
        int len = 0;
        while((len = in.read(buffer)) != -1){
            out.write(buffer,0,len);
        }
        out.flush();
        in.reset();
        System.out.println("file sent");
    }

    public void receiveFile() throws IOException {
        try {
            socket.getOutputStream().flush();
            InputStream input = socket.getInputStream();
            OutputStream out = new FileOutputStream("ServerReceive.txt");
            byte[] buffer = new byte[8192];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            input.reset();
            System.out.println("Received file on server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String checkMessage() throws IOException {
        InputStream inputStream = socket.getInputStream();

        // create a DataInputStream so we can read data from it.
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        // read the message from the socket
        String message = dataInputStream.readUTF();
        return message;
    }
    }

