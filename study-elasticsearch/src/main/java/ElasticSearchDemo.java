import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.security.CreateTokenRequest;
import org.elasticsearch.client.security.CreateTokenResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonMap;

/**
 * @author bobo
 * @Description:
 * @date 2019-02-28 17:40
 */
public class ElasticSearchDemo {

    private RestHighLevelClient client;

    public static boolean flag = true;

    @Before
    public void getHighClient() throws IOException {
        //高级请求客户端构建,高级客户端需要一个低级客户端,低级客户端负责维护连接池和启动线程
        client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("39.106.157.165", 9200, "http")
        ));
        final char [] password = "Ptb123456".toCharArray();
        CreateTokenRequest createTokenRequest = CreateTokenRequest.passwordGrant("elastic",password);
        CreateTokenResponse createTokenResponse = client.security().createToken(createTokenRequest, RequestOptions.DEFAULT);
        String accessToken = createTokenResponse.getAccessToken();

    }

    /**
     * 创建索引
     *
     * @throws IOException
     */
    @Test
    public void createIndex() throws IOException {
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "2");

        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);
        //同步执行
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            System.out.println("执行了创建");
        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            System.out.println("执行了更新");
        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
            System.out.println("分片数不一致");
        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure :
                    shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }
        //异步执行
//        client.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
//            public void onResponse(IndexResponse indexResponse) {
//                System.out.println("异步成功执行:" + indexResponse.toString());
//                ElasticSearchDemo.flag = false;
//            }
//
//            public void onFailure(Exception e) {
//                System.out.println("执行失败");
//                e.printStackTrace();
//            }
//        });
//        while (flag) {
//            System.out.println("111");
//        }
        //2.关闭连接
        client.close();
    }

    /**
     * 是否存在 api
     *
     * @throws IOException
     */
    @Test
    public void exists() throws IOException {
        GetRequest getRequest = new GetRequest(
                "posts",
                "doc",
                "1"
        );
        //判断是否存在
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println("是否存在:" + exists);
        client.close();
    }

    /**
     * 是否存在异步api
     *
     * @throws IOException
     */
    @Test
    public void existsAsync() throws IOException, InterruptedException {
        GetRequest getRequest = new GetRequest(
                "posts",
                "doc",
                "1"
        );
        //判断是否存在
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        client.existsAsync(getRequest, RequestOptions.DEFAULT, new ActionListener<Boolean>() {
            @Override
            public void onResponse(Boolean Boolean) {
                System.out.println("是否存在:" + Boolean);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        TimeUnit.SECONDS.sleep(5);
        client.close();

    }

    /**
     * 查询api
     *
     * @throws IOException
     */
    @Test
    public void get() throws IOException {
        GetRequest getRequest = new GetRequest(
                "posts",
                "doc",
                "1"
        );
//        getRequest.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
//        String[] includes = new String[]{"message", "*Date"};
//        String[] excludes = Strings.EMPTY_ARRAY;
//        FetchSourceContext fetchSourceContext =
//                new FetchSourceContext(true, includes, excludes);
//        getRequest.fetchSourceContext(fetchSourceContext);
//        String[] includes = Strings.EMPTY_ARRAY;
//        String[] excludes = new String[]{"message"};
//        FetchSourceContext fetchSourceContext =
//                new FetchSourceContext(true, includes, excludes);
//        getRequest.fetchSourceContext(fetchSourceContext);
//        getRequest.storedFields("_none_");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.toString());
//        String message = getResponse.getField("message").getValue();
//        System.out.println(message);
//        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        client.close();
    }

    @Test
    public void update() throws IOException {
        UpdateRequest request = new UpdateRequest(
                "posts",
                "doc",
                "1");
        Map<String, Object> parameters = singletonMap("count", 4);

        Script inline = new Script(ScriptType.INLINE, "painless",
                "ctx._source.field += params.count", parameters);
        request.script(inline);
        UpdateResponse update = client.update(request, RequestOptions.DEFAULT);
        System.out.println(update.toString());
        client.close();
    }


}
