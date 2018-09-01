package org.shadowsocks.config;

public interface Config {
    String getServerAddress();
    int getServerPort();
    int getLocalPort();
    String getLocalAddress();
    String getPassword();
    int getTimeout();
    String getMethod();
    RealConfig loadConfig(String path);

}
