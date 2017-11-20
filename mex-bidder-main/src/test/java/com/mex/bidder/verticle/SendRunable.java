package com.mex.bidder.verticle;

import javax.net.ssl.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * xuchuanao
 * on 2017/3/16.
 */
public class SendRunable implements Runnable{

    private static final String HTTPS_URL = "https://dsptrack.ad-mex.com/adClick?requestid=1111111111&adgroupid=120&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=ios&connectiontype=WIFI&material_id=487&adid=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&android_id=NA&android_id_md5=NA&android_id_sha1=e8c47bd5b9df9d539d31b336627fdd50d7f63269&imei=NA&imei_md5=NA&imei_sha1=NA&deviceID=FB9340D8-49CD-4DFF-8149-B3A5B8043CEC&mac=02%3A00%3A00%3A00%3A00%3A00&mac_md5=NA&mac_sha1=c1976429369bfe063ed8b3409db7c7e7d87196d9&remote_addr=180.111.70.106&cur_adv=RMB&cur_adx=RMB&adver_id=17&campaign_id=22&resptimestamp=20170316165009474&height=50&width=320&make=Apple&model=iPhone8%2C1&bundle=com.xunxin.popstarv&ip=180.111.70.106&app_name=%E6%8D%95%E9%B1%BC%E8%BE%BE%E4%BA%BAiOS&material_type=banner";

    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    @Override
    public void run() {
        InputStream in = null;
        OutputStream out = null;
        byte[] buffer = new byte[4096];
        String str_return = "";
        try {
            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("SSL");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                        new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            URL console = new URL(HTTPS_URL);
            HttpsURLConnection conn = (HttpsURLConnection) console
                    .openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new SendRunable.TrustAnyHostnameVerifier());
            conn.connect();
            InputStream is = conn.getInputStream();
            DataInputStream indata = new DataInputStream(is);
            String ret = "";
            while (ret != null) {
                ret = indata.readLine();
                if (ret != null && !ret.trim().equals("")) {
                    str_return = str_return
                            + new String(ret.getBytes("ISO-8859-1"), "GBK");
                }
            }
            conn.disconnect();
        } catch (ConnectException e) {
            System.out.println("ConnectException");
            System.out.println(e);

        } catch (IOException e) {
            System.out.println("IOException");
            System.out.println(e);

        } finally {
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
        }
        System.out.println(str_return);
    }





}
