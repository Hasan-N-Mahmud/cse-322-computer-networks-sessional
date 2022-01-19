import java.io.*;
import java.net.Socket;

public class utility {
    BufferedReader br;
    PrintWriter pr;
    Socket socket;
    DataOutputStream dos;
    DataInputStream dis;

    public utility(BufferedReader br, PrintWriter pr,DataOutputStream d,Socket socket) {
        this.br = br;
        this.pr = pr;
        this.socket = socket;

        this.dos=d;
    }

    public utility(BufferedReader br, PrintWriter pr, Socket socket, DataOutputStream dos, DataInputStream dis) throws IOException {
        this.br = br;
        this.pr = pr;
        this.socket = socket;

        this.dos = dos;
        this.dis = dis;

    }

    public utility(PrintWriter pr, Socket socket, DataOutputStream dos) throws IOException {
        this.pr = pr;
        this.socket = socket;

        this.dos = dos;
        br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dis=new DataInputStream(socket.getInputStream());
    }

    public void closeConnection() throws IOException {
        pr.close();
        br.close();
       dos.close();
       dis.close();
        socket.close();

    }
}
