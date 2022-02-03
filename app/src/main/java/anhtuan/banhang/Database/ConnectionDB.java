package anhtuan.banhang.Database;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Created by Anh Tuan on 27/11/2018.
 */

public class ConnectionDB {
    String ip = "192.168.1.27";//171.247.194.72
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    //String db = "BanHangTest";
    String db = "BanHang";
    String un = "tuan";
    String password = "1";

    public static void main(String []arg){
        try {
            InetAddress i_a = InetAddress.getLocalHost();
            System.out.println("==== "+i_a.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
            while (eni.hasMoreElements()) {
                NetworkInterface ni = eni.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ia = inetAddresses.nextElement();
                    System.out.println("-IP-"+ia.getHostAddress());
                    if(ia.isSiteLocalAddress() && ni.getName().startsWith("wlan"))
                        System.out.println("\n______"+ia.getHostAddress());
                    //strIp = ia.getHostAddress();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        //ip = getIP();
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }

    private String getIP() {
     String strIp = "";
        try {
            Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
            while (eni.hasMoreElements()) {
                NetworkInterface ni = eni.nextElement();
                Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress ia = inetAddresses.nextElement();
                    if(ia.isSiteLocalAddress() && ni.getName().startsWith("wlan"))
                        strIp = ia.getHostAddress();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strIp;
    }

    public boolean checkConnecDatabase(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
            return  true;
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
            return false;
        }
    }

}