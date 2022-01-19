import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class CLient {
    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("localhost",6789);
        Scanner scn=new Scanner(System.in);

        DataOutputStream dos=new DataOutputStream(socket.getOutputStream());
        DataInputStream dis=new DataInputStream(socket.getInputStream());
        PrintWriter pr=new PrintWriter(socket.getOutputStream());
        BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        utility util=new utility(br,pr,socket,dos,dis);
        while(true)
        {
            System.out.println("Enter filepath:");
            String path=scn.nextLine();
            System.out.println(path);
            if(path.equalsIgnoreCase("x"))
            break;
            new UploadThread(util,path);

        }
    }
}
