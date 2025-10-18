package cn.zzwei.scnet.thread;

import cn.zzwei.scnet.Main;
import cn.zzwei.scnet.mapping.ConfigMapping;
import cn.zzwei.scnet.mapping.StateMapping;
import cn.zzwei.scnet.utils.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class CheckIpThread extends Thread {

    @Override
    public void run() {
        for(Map.Entry<String, StateMapping>entry: Main.clientAddressList.entrySet() ){
            String k=entry.getKey();
            StateMapping v=entry.getValue();
            if (v==StateMapping.DEFAULT) {
                try{
                    Map<String, Object> data = new HashMap<>();
                    data.put("ip",k);
                    JsonNode r= HttpUtils.HttpPost(ConfigMapping.HttpUrl,data);
                    int code= r.at("code").intValue();
                    if (code != 0 ) {
                        log.warn("Check {} failed, the server returned an unexpected value",k);
                        return;
                    }
                    int state = r.at("data/state").intValue();
                    if(state == 0) entry.setValue(StateMapping.ACCEPT);
                    if(state == 1) entry.setValue(StateMapping.DEFAULT);
                }catch (Exception e){
                    log.error("failed {}",k,e);
                }
            }
        }
    }

}
