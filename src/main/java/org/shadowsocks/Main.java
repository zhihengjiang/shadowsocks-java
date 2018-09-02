package org.shadowsocks;
import org.shadowsocks.config.Config;
import org.shadowsocks.util.CommandLineParser;

import java.util.Arrays;

public class Main {
    public static void main( String[] args) throws Exception{
        String main = args[0];
        Config config = CommandLineParser.parse(Arrays.copyOfRange(args,1,args.length));
        switch (main){
            case "LocalMain":
                new ShadowsocksLocal(config).start();break;
            case "ServerMain":
                new ShadowSocksServer(config).start();break;

            default:System.out.println("please set running mode");break;
        }


    }
}
