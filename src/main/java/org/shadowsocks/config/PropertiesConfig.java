package org.shadowsocks.config;

public class PropertiesConfig extends BaseConfig{
    public PropertiesConfig(String path){
        super(path);
    }
    @Override
    public RealConfig loadConfig(Object path) {
        return new RealConfig();
    }
}
