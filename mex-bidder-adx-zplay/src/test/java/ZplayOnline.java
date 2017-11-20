import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ExtensionRegistry;
import com.mex.bidder.adx.zplay.ZadxExt;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;

/**
 * Created by Administrator on 2016/12/21.
 */
public class ZplayOnline {
    private static final ExtensionRegistry registry = ExtensionRegistry.newInstance();

    public static void main(String[] args) {

        ZadxExt.registerAllExtensions(registry);

        String jsonReqData = JsonHelper.readFile("zplay.req.json");
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost("101.200.34.111").setDefaultPort(9901);
        HttpClient client = vertx.createHttpClient(options);

        Runnable send1000 = () -> {
            for (int i = 0; i < 1; i++) {

                HttpClientRequest localhost = client.post("/adszp", resp -> {
                    System.out.println("Got response " + resp.statusCode());
                    resp.bodyHandler(body -> {
                        if (resp.statusCode() == 200) {
                            try {
                                //OpenRtb.BidResponse bidRes = OpenRtb.BidResponse.parseFrom(body.getBytes(), registry);
                                JSONObject jsonObject = JSON.parseObject(body.toString());
                                System.out.println("responseId = " + jsonObject.getString("id"));
                                System.out.println(body.toString());

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("Got data " + body.toString("utf-8"));
                        }
                    });

                });
                //OpenRtb.BidRequest request = bidRequest.toBuilder().setId(MexUtil.uuid()).build();
                localhost.end(Buffer.buffer(jsonReqData));
            }
        };

        new Thread(send1000).start();
        // new Thread(send1000).start();
        // new Thread(send1000).start();
    }
}
