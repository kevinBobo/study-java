import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author bobo
 * @Description:
 * @date 2019-03-06 12:11
 */
public class XPackDemo {

    public static void main(String[] args) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch") //设置ES实例的名称
                .put("xpack.security.transport.ssl.enabled", false) //设置xpack权限用户
                .put("xpack.security.user","elastic:Ptb123456")
                .put("client.transport.sniff", true) //自动嗅探整个集群的状态，把集群中其他ES节点的ip添加到本地的客户端列表中
                .build();
        PreBuiltXPackTransportClient preBuiltTransportClient  = new PreBuiltXPackTransportClient(settings);
        TransportClient client = preBuiltTransportClient.addTransportAddress(new TransportAddress(InetAddress.getByName("39.106.157.165"), 9300));
        System.out.println(client);
    }
}
