package com.mex.bidder.vertxHttp;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

import java.util.Random;

/**
 * xuchuanao
 * on 2016/12/29.
 */
public class VertxHttpDemo {


    public static void main(String[] args) throws InterruptedException {
        Random random = new Random(System.currentTimeMillis());
        int num = random.nextInt(3);

        System.out.println(num);
        //FutureDemo();
        httpClient();
//        compositionDemo();

    }

    private static void compositionDemo(){
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();
        Future<Void> startFuture = Future.future();

        Future<Void> fut1 = Future.future();
        fs.createFile("/foo", fut1.completer());

        fut1.compose(v -> {
            // When the file is created (fut1), execute this:
            Future<Void> fut2 = Future.future();
            fs.writeFile("/foo", Buffer.buffer(), fut2.completer());
            return fut2;
        }).compose(v -> {
                    // When the file is written (fut2), execute this:
                    Future<Void> fut3 = Future.future();
                    fs.move("/foo", "/bar", fut3.completer());
                },
                // mark the start future as completed when all the chain has been completed,
                // or mark it as failed if any step fails.
                startFuture);
    }

    private static void httpClient() throws InterruptedException {
        Vertx vertx = Vertx.vertx();

        Random random = new Random(System.currentTimeMillis());
        int num = random.nextInt(2);
        System.out.println("num = " + num);
        String uri1 = buildUri()[num];

//        while (true) {
            HttpClient httpClient = vertx.createHttpClient();
            httpClient.getNow("http://testdsptrack.ad-mex.com", uri1, resp -> {
                resp.bodyHandler(body -> {
                    System.out.println(body.toString());
                });
            });
            Thread.sleep(100L);
            //httpClient.close();
//        }

    }

    private static String[] buildUri() {
        String uri1 = "/winnotice?requestid=0bymWA1CtM642gVnCb2me6i84pF6nB&adgroupid=27&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=android&connectiontype=WIFI&material_id=121&adid=NA&idfa=NA&android_id=c88d3d8dfe4fa8bf&android_id_md5=NA&android_id_sha1=b1d769b590f86bc0de0e36ce2e7b5d60ebcb4fdd&imei=861374035481271&imei_md5=NA&imei_sha1=32463111bed6fef17f257fb3a9f0832564c6a993&deviceID=861374035481271&remote_addr=60.255.77.63&cur_adv=RMB&cur_adx=RMB\n";
        String uri2 = "/adImp?requestid=0bymWA1CtM642gVnCb2me6i84pF6nB&adgroupid=27&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=android&connectiontype=WIFI&material_id=121&adid=NA&idfa=NA&android_id=c88d3d8dfe4fa8bf&android_id_md5=NA&android_id_sha1=b1d769b590f86bc0de0e36ce2e7b5d60ebcb4fdd&imei=861374035481271&imei_md5=NA&imei_sha1=32463111bed6fef17f257fb3a9f0832564c6a993&deviceID=861374035481271&remote_addr=60.255.77.63&cur_adv=RMB&cur_adx=RMB&price={AUCTION_BID_PRICE}\n";
        String uri3 = "/adClick?requestid=0bymWA1CtM642gVnCb2me6i84pF6nB&adgroupid=27&netid=018&netname=adszp&devicetype=HIGHEND_PHONE&os=android&connectiontype=WIFI&material_id=121&adid=NA&idfa=NA&android_id=c88d3d8dfe4fa8bf&android_id_md5=NA&android_id_sha1=b1d769b590f86bc0de0e36ce2e7b5d60ebcb4fdd&imei=861374035481271&imei_md5=N\n" +
                "A&imei_sha1=32463111bed6fef17f257fb3a9f0832564c6a993&deviceID=861374035481271&remote_addr=60.255.77.63&cur_adv=RMB&cur_adx=RMB\n";
        String[] uriArr = new String[3];
        uriArr[0] = uri1;
        uriArr[1] = uri2;
        uriArr[2] = uri3;

        return uriArr;
    }

    private static void FutureDemo() {
        Vertx vertx = Vertx.vertx();
        FileSystem fs = vertx.fileSystem();
        Future<Void> startFuture = Future.future();

        Future<Void> fut1 = Future.future();
        fs.createFile("/foo", fut1.completer());

        fut1.compose(v -> {
            // When the file is created (fut1), execute this:
            Future<Void> fut2 = Future.future();
            fs.writeFile("/foo", Buffer.buffer(), fut2.completer());
            return fut2;
        }).compose(v -> {
                    // When the file is written (fut2), execute this:
                    Future<Void> fut3 = Future.future();
                    fs.move("/foo", "/bar", fut3.completer());
                },
                // mark the start future as completed when all the chain has been completed,
                // or mark it as failed if any step fails.
                startFuture);
    }

    private static void BlockingDemo() {
        Vertx vertx = Vertx.vertx();


    }

    private static void periodicDemo() {
        Vertx vertx = Vertx.vertx();
        vertx.setPeriodic(1000, id -> {
            // This handler will get called every second
            System.out.println("timer fired!");
        });
    }

    public static void vertxServerDemo() {
        VertxOptions vertxOptions = new VertxOptions().setWorkerPoolSize(20);
        Vertx vertx = Vertx.vertx(vertxOptions);
        HttpServer server = vertx.createHttpServer();

        server.requestHandler(request -> {

            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World!");
        });

        server.listen(8090);
    }
}
