package org.shadowsocks.config;

public abstract class BaseConfig implements Config{

    private RealConfig config;
    BaseConfig(String path){
        this.config = loadConfig(path);
    }


    @Override
    public String getServerAddress() {
        return config.server;
    }

    @Override
    public int getServerPort() {
        return config.server_port;
    }

    @Override
    public String getLocalAddress() {
        return config.local_address;
    }

    @Override
    public int getLocalPort() {
        return config.local_port;
    }

    @Override
    public int getTimeout() {
        return config.timeout;
    }

    @Override
    public String getMethod() {
        return config.method;
    }

    @Override
    public String getPassword() {
        return config.password;
    }

    public RealConfig getConfig(){
        return this.config;
    }

}
