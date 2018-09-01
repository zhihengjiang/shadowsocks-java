package org.shadowsocks;

import org.shadowsocks.config.Config;
import org.shadowsocks.config.ConfigFactory;
import org.shadowsocks.config.JsonConfig;

import java.io.File;

public class LocalMain {
    public static void main(String[] args) throws InterruptedException{
        Config config = ConfigFactory.getConfig("/home/thales/config.json");
        new ShadowsocksLocal(config).start();
    }
}
