package cn.zzwei.scnet.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ForwardMapping {
    String name;
    String type;
    String localAddress;
    String remoteAddress;
    Integer localPort;
    Integer remotePort;

    @Override
    public String toString() {
        return "[%s]    %s  :   %s  =>  %s  :   %s   [%s]"
                .formatted(type, localAddress, localPort, remoteAddress, remotePort, name);
    }
}
