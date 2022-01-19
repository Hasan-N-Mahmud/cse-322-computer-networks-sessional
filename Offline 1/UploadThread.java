import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class UploadThread implements Runnable {
    utility util;
    String input;
    Thread t;

    public UploadThread(utility util, String input) {
        this.util = util;
        this.input = input;
        t=new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Upload thread 1");
        File file=new File(input);
        if(file.exists())
        {
            FileInputStream fis=null;
            try {

                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

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


            util.pr.write("UPLOAD "+file.getName()+" "+fileSize+"\n");
            util.pr.flush();
            int length;
            byte[] array=new byte[HTTPServerSkeleton.packet];
            while(true)
            {

                try {
                    if (!((length = fis.read(array)) > 0)) break;
                    util.dos.write(array);
                    util.dos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
        else
            System.out.println("File doesnot exist");
        util.pr.write("exit\n");
        util.pr.flush();
        System.out.println("Upload thread exiting");
    }
}
