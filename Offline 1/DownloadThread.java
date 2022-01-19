import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;


public class DownloadThread implements Runnable {

    private utility uc;
    private ReadThread rt;
    public Thread mine;
    public File file;


    public DownloadThread(utility uc, ReadThread r, File f) {

        this.uc = uc;
        this.rt = r;
        this.file = f;
        mine = new Thread(this);
        mine.start();
    }

    public String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }



    @Override
    public void run() {
        String out = null;
        long fileSize = 0;
        FileChannel fileChannel;
        try {
            fileChannel = FileChannel.open(Paths.get(file.getAbsolutePath()));
            fileSize = fileChannel.size();
            System.out.println(fileSize + " bytes");
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileSize = (int) fileSize;


        //double num =  (fileSize/HTTPServerSkeleton.packet);
        //num=Math.ceil(num);
        byte[] data = new byte[HTTPServerSkeleton.packet];
        int i = 0, dec = 0;

        String data_packet = null;
        uc.pr.write("HTTP/1.1 200 OK\r\n");
        uc.pr.write("Server: Java HTTP Server: 1.0\r\n");
        uc.pr.write("Date: " + new Date() + "\r\n");
        uc.pr.write("Content-Disposition: attachment");
        try {
            uc.pr.write("Content-Type:" + Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        uc.pr.write("Content-Length: " + fileSize + "\r\n");
        uc.pr.write("\r\n");
        uc.pr.flush();
        String ex = getExtension(file.toString());

        try {
            data = HTTPServerSkeleton.readFileData(file, (int) fileSize, 0);

            // dos = new ObjectOutputStream(uc.socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

            if (uc.dos != null) {

                try {
                    FileInputStream fr = new FileInputStream(file);
                    int length;
                    byte[] array = new byte[HTTPServerSkeleton.packet];
                    ;
                    while ((length = fr.read(array)) > 0) {
                        uc.dos.write(array, 0, length);
                        uc.dos.flush();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Download thread exiting..");

            }

    }
}