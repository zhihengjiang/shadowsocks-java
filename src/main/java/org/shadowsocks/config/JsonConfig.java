package org.shadowsocks.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.*;



public class JsonConfig extends BaseConfig {

    public JsonConfig(String path){
        super(path);
    }

    @Override
    public RealConfig loadConfig(Object path) {
        Gson gson = new Gson();
        RealConfig config = new RealConfig();
        try{

            JsonReader reader = new JsonReader(new FileReader((String)path));
            config = gson.fromJson(reader,RealConfig.class);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return  config;
    }



    public static void main(String[] args){
        String path = "/home/thales/config.json";
        Config config = new JsonConfig(path);
        System.out.println(new Gson().toJsonTree(((JsonConfig) config).getConfig()));
    }
}
