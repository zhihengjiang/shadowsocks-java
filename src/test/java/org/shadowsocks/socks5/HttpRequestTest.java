package org.shadowsocks.socks5;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;


public class HttpRequestTest {
    public static void main(String[] args) throws Exception {
        final String user = "t";
        final String password = "test";

        Proxy proxyTest = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10000));

//        java.net.Authenticator.setDefault(new java.net.Authenticator()
//        {
//            private PasswordAuthentication authentication = new PasswordAuthentication(user, password.toCharArray());
//
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication()
//            {
//                return authentication;
//            }
//        });


        OkHttpClient client = new OkHttpClient.Builder().proxy(proxyTest).build();
        Request request = new Request.Builder().url("http://www.baidu.com").build();
        Response response = client.newCall(request).execute();
        System.out.println(response.code());
        System.out.println(response.body());

        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    public void socks5PortTest() throws Exception{
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10000));
        HttpGet get = new HttpGet("http://www.baidu.com/search?hl=en&q=httpclient&btnG=Google+Search&aq=f&oq=");
        HttpClient httpClient = HttpClients.createDefault();
        httpClient.execute(get);
    }
}
