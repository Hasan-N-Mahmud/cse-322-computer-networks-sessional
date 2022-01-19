import java.io.*;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;

public class ReadThread implements Runnable {
    private Thread thr;
    private utility uc;
    int port=HTTPServerSkeleton.PORT;
    public int id;
    public ReadThread(int id,utility nc) {
        this.id=id;
        this.uc = nc;
        this.thr = new Thread(this);
        thr.start();
    }
    public String find_Dir (String st){

        System.out.println(st);
        String root=System.getProperty("user.dir");
        File F_root=new File(root);
        String[] pathsd;
        String relPath=null;
        ArrayList<String> files=new ArrayList<>();
        ArrayList<String> directories=new ArrayList<>();
        boolean is_Dir=true;
        //st.replaceFirst("/","");
        String current = System.getProperty("user.dir");
        current = current.concat(st);
        //System.out.println("Current:"+current);
        File f = new File(current);
        File f1;
        // Populates the array with names of files and directories

        pathsd = f.list();
        for (String pathname : pathsd) {

            File file = new File(f,pathname);

            boolean exists =      file.exists();      // Check if the file exists
            boolean isDirectory = file.isDirectory(); // Check if it's a directory
            boolean isFile =      file.isFile();      // Check if it's a regular file
            if(isDirectory){
                directories.add(pathname);
                 }
            else
            {
                files.add(pathname);
            }


        }
        //System.out.println(" ");
        String script="<!DOCTYPE html>\n";
        script=script.concat("<html>\n");

        script=script.concat("<head>\n");
     //   if(is_Dir)
            script=script.concat("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

        script=script.concat("</head>\n");

        script=script.concat("<body>\n");
        for(String s:directories)
        {

            script=script.concat("<h2>");
            String s1=current.concat("/");
            s1=s1.concat(s);
            f1=new File(s1);
            int rootLength = F_root.getAbsolutePath().length();

            String absFileName = f1.getAbsolutePath();
            // System.out.println(absFileName);
            String relFileName = absFileName.substring(rootLength +1);
            relFileName=relFileName.replace('\\','/');

            relFileName=relFileName.replaceAll(" ","%20");
            //System.out.println(relFileName);
            script=script.concat("<a href=\"http://localhost:"+port+"/"+relFileName+"\">"+s+"</a>");

            script=script.concat("</h2>\n");
        }
        for(String s:files)
        {
            script=script.concat("<h3>");
            String s1=current.concat("/");
            s1=s1.concat(s);
            f1=new File(s1);
            int rootLength = F_root.getAbsolutePath().length();

            String absFileName = f1.getAbsolutePath();
            // System.out.println(absFileName);
            String relFileName = absFileName.substring(rootLength +1);
            relFileName=relFileName.replace('\\','/');
            //System.out.println(relFileName);
            relFileName=relFileName.replaceAll(" ", "%20");
            script=script.concat("<a href=\"http://localhost:"+port+"/"+relFileName+"\" download>"+s+"</a>");
           // script=script.concat(relFileName);
            // script=script.concat("\">"+s+"</a>");
            script=script.concat("</h3>\n");

        }
        script=script.concat("<h4><a href=http://localhost:"+port+"/>"+ "Back to Home</a></h4>");
        script=script.concat("</body>\n");

        script=script.concat("</html>");
       // System.out.println(script);
        // For each pathname in the pathnames array
        return script;
    }



    public void run() {
        try {


                String cur_dir=System.getProperty("user.dir");
                String checking=null;
                String input =uc.br.readLine();
                System.out.println(input);
                HTTPServerSkeleton.log_writer.write("Browser id: "+this.id+"  Request:"+input+" ");

                // String content = "<html>Hello</html>";
            System.out.println("Read thread 1");
                if ( input!=null && input.length() > 0) {
                    if (input.startsWith("GET")) {
                        String[] str = input.split("\\s+");

                        str[1].replaceFirst("/", "");
                        str[1]=str[1].replaceAll("%20"," ");

                        // System.out.println(str[1]);
                        checking = cur_dir.concat(str[1]);
                        File f = new File(checking);
                        //System.out.println(checking);
                        String response=null;
                        if(f.exists()){
                        if (f.isDirectory()) {

                            String content = find_Dir(str[1]);
                            //System.out.println(content);
                            response="\nHTTP/1.1 200 OK\n";
                            response=response.concat("Server: Java HTTP Server: 1.0\nContent-Type: text/html\nContent-Length: " + content.length() + "\n\n");
                            uc.pr.write("HTTP/1.1 200 OK\r\n");
                            uc.pr.write("Server: Java HTTP Server: 1.0\r\n");
                            uc.pr.write("Date: " + new Date() + "\r\n");
                            uc.pr.write("Content-Type: text/html\r\n");
                            uc.pr.write("Content-Length: " + content.length() + "\r\n");
                            uc.pr.write("\r\n");
                            uc.pr.write(content);
                            uc.pr.flush();
                            HTTPServerSkeleton.log_writer.write(response);
                            HTTPServerSkeleton.log_writer.flush();

                        } else if (f.isFile()) {

                            String ost ="\nResponse:HTTP/1.1 200 OK\n";
                            //ost=HTTPServerSkeleton.readFileData(f,HTTPServerSkeleton.size);

                            ost=ost.concat("Server: Java HTTP Server: 1.0\nContent-Type: " + Files.probeContentType(f.toPath())+ "\n\n");
                            HTTPServerSkeleton.log_writer.write(ost);
                            HTTPServerSkeleton.log_writer.flush();
                            DownloadThread dt=new DownloadThread(this.uc, this,f);
                            dt.mine.join();
                        }
                    }
                        else
                        {
                            //System.out.println("entered");
                            String script = "<html>\n";

                            script=script.concat("<head>\n");

                            script=script.concat("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

                            script=script.concat("</head>\n");

                            script=script.concat("<body>\n");

                            script=script.concat("<h1><b>ERROR 404: ITEM NOT FOUND </b></h1>\n</body>\n</html>");
//
                            uc.pr.write("HTTP/1.1 404 ERROR\r\n");
                            uc.pr.write("Server: Java HTTP Server: 1.0\r\n");
                            uc.pr.write("Date: " + new Date() + "\r\n");
                            uc.pr.write("Content-Type: text/html\r\n");
                            uc.pr.write("Content-Length: " + script.length() + "\r\n");
                            uc.pr.write("\r\n");
                            uc.pr.write(script);
                            uc.pr.flush();

                            String eroro_response="HTTP/1.1 404 ERROR\n\n";
                            HTTPServerSkeleton.log_writer.write("\nResponse:");
                            HTTPServerSkeleton.log_writer.write(eroro_response);
                            HTTPServerSkeleton.log_writer.flush();
                        }
                    }
                    else if(input.contains("UPLOAD"))
                    {
                        UploadServerThread tf=new UploadServerThread(this.uc,input);
                        tf.t.join();

                    }
                //uc.closeConnection();
                    System.out.println("exiting");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                uc.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
