import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class UploadServerThread implements Runnable {
    Thread t;
    utility util;
    String input;

    public UploadServerThread(utility util,String input) {
        this.util = util;
        this.input=input;
        t=new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Stage 1");
        byte[] arr=new byte[HTTPServerSkeleton.packet];
        String[] str=input.split(" ");
        System.out.println(str[2]);
        File f=new File("upload");
        f.mkdir();
        String name="upload/";
        name=name.concat(str[1]);
        File file=new File(name);
        int file_len=Integer.parseInt(str[2]);
        FileOutputStream fo=null;
        int length;
        try {
            fo = new FileOutputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Stage 2");
        int total=0;
        while(true)
        {
            try {
                if(total >= file_len) break;
                if (!((length = util.dis.read(arr)) > 0)) break;
                //System.out.println(length);
                total=total+length;
                fo.write(arr);
                fo.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //System.out.println("Stage 3");
        try {
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("exiting");
    }
}
