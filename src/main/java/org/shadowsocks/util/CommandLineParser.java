package org.shadowsocks.util;
import org.shadowsocks.config.BaseConfig;
import org.shadowsocks.config.Config;
import org.shadowsocks.config.JsonConfig;
import org.shadowsocks.config.RealConfig;

import java.util.Arrays;

public class CommandLineParser {

    public static Config parse(String[] args)
    {
        RealConfig realConfig = new RealConfig();
        for(int i=0;i<args.length;i++)
        {
            System.out.println(Arrays.toString(args));
            String[] parts = args[i].split("=");
            String key = parts[0];
            String value = parts[1];

            if(key.compareTo("config")==0)
                return new JsonConfig(value);

            if(key.compareTo("server")==0)
                realConfig.server = value;

            if(key.compareTo("server_port")==0)
                realConfig.server_port = Integer.parseInt(value);

            if(key.compareTo("local_address")==0)
                realConfig.local_address = value;
            if(key.compareTo("local_port")==0)
                realConfig.local_port = Integer.parseInt(value);
            if(key.compareTo("method")==0)
                realConfig.method = value;
            if(key.compareTo("password")==0)
                realConfig.password = value;
            if(key.compareTo("time_out")==0)
                realConfig.timeout = Integer.parseInt(value);
        }

        return new BaseConfig(realConfig) {
            @Override
            public RealConfig loadConfig(Object source) {
                return (RealConfig) source;
            }
        };
    }
}
