import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class HTTPServerSkeleton {
    static final int PORT = 6789;
    static int packet=200;

    static  File log_file=new File("log.txt");

    static FileWriter log_writer;

    static {
        try {
            log_writer = new FileWriter(log_file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] readFileData(File file, int fileLength,int start) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
           fileIn.read(fileData);

        } finally {
            if (fileIn != null)
                fileIn.close();
        }
        byte[] data=new byte[HTTPServerSkeleton.packet];
        if((start+HTTPServerSkeleton.packet-1)<fileData.length)
            data = Arrays.copyOfRange(fileData,start,(start+HTTPServerSkeleton.packet-1));
        else {
            int var= (int) (fileData.length-1-start);
            //System.out.println("else"+i + " " + var);
            data = Arrays.copyOfRange(fileData,start,(var));

        }
        return data;
    }
    
    public static void main(String[] args) throws IOException {
        
        ServerSocket serverConnect = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        int i=1;

        while(true)
        {

            Socket s = serverConnect.accept();
            HTTPServerSkeleton.log_file.createNewFile();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter pr = new PrintWriter(s.getOutputStream());
            DataOutputStream dos=new DataOutputStream(s.getOutputStream());
            DataInputStream dis=new DataInputStream(s.getInputStream());
            utility util=new utility(in,pr, s, dos, dis);
            System.out.println("Browser id:"+i);
            new ReadThread(i,util);
            i++;

            }
        }
        
    }
    

