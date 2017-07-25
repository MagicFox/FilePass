package com.kidyone.filepass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 文件服务器
 */
public class FileServer implements Runnable{

    public static String filePath = null;

    /**服务器初始化自检程序*/
    static{
        try{
            String userDir = System.getProperty("user.dir");
            filePath = userDir + "/文件";
            File videoFile = new File(filePath);
            if(!videoFile.exists()){
                videoFile.mkdirs();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**主程序入口*/
    public void run() {
        try{
            ServerSocket serverSoc = new ServerSocket(8088);
            while(serverSoc != null){
                try {
                    //侦听客户端连接
                    Socket socket = serverSoc.accept();
                    socket.setSoTimeout(10000); //10秒没响应拒绝传输
                    //客户端连接  获取客户端输入流
                    InputStream is = socket.getInputStream();
                    OutputStream os = socket.getOutputStream();

                    //获取文件名
                    byte[] bys = new byte[1024 * 8];
                    int len = is.read(bys);
                    String fileName = new String(bys, 0, len);
                    //创建目录
                    InetAddress inetAddress = socket.getInetAddress();
                    String videoPathTemp = filePath + inetAddress.toString();
                    File videoFile = new File(videoPathTemp);
                    if(!videoFile.exists()){
                        videoFile.mkdirs();
                    }

                    //创建输出流
                    File downloadFile = new File(videoPathTemp + File.separator + System.currentTimeMillis() + fileName);
                    FileOutputStream fos = new FileOutputStream(downloadFile);

                    //输出标志
                    os.write("1".getBytes());
                    os.flush();

                    while((len = is.read(bys)) != -1){
                        fos.write(bys, 0, len);
                        fos.flush();
                    }
                    fos.close();
                    is.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{

                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
