package com.mex.bidder.engine.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mex.bidder.engine.kafka.config.ConfigConstants;
import com.mex.bidder.util.JsonHelper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Module to listen for messages from vertx event bus and send them to Kafka defaultTopic.
 */
public class MessageProducer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    public static String EVENTBUS_DEFAULT_ADDRESS = "kafka.message.publisher";
    private KafkaProducer<String, String> producer;
    private String busAddress;
    private String defaultTopic;
    private JSONObject producerConfig;
    private ExecutorService sender;

    @Override
    public void start(final Future<Void> startedResult) {
        try {
//            String conf = JsonHelper.readFile("kafka.cnf.json");
            String conf = JsonHelper.readFile("main.cnf.json");
            JSONObject mainJsonObj = JSON.parseObject(conf);
            producerConfig = mainJsonObj.getJSONObject("kafka-cnf");

//            producerConfig = JSON.parseObject(conf);

            Properties properties = populateKafkaConfig();

            busAddress = (String) producerConfig.getOrDefault(ConfigConstants.EVENTBUS_ADDRESS, EVENTBUS_DEFAULT_ADDRESS);
            defaultTopic = producerConfig.getString(ConfigConstants.DEFAULT_TOPIC);
            sender = Executors.newSingleThreadExecutor();

            producer = new KafkaProducer<>(properties);

            vertx.eventBus().consumer(busAddress, this::sendMessage);

            // try to disconnect from ZK as gracefully as possible
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            startedResult.complete();
            logger.info("install MessageProducer ok");

        } catch (Exception ex) {
            logger.error("Message producer initialization failed with ex: {}", ex);
            startedResult.fail(ex);
        }
    }

    /**
     * Send a message on a pre-configured defaultTopic.
     *
     * @param message the message to send
     */
    public void sendMessage(Message<JsonObject> message) {
        JsonObject payload = message.body();
        ProducerRecord<String, String> record;

        if (!payload.containsKey(KafkaPublisher.TYPE_FIELD)) {
            logger.error("Invalid message sent missing {} field, msg: {}", KafkaPublisher.TYPE_FIELD, message);
            return;
        }

        KafkaPublisher.MessageType type = KafkaPublisher.MessageType.fromInt(payload.getInteger(KafkaPublisher.TYPE_FIELD));
        String value = payload.getString(ConfigConstants.VALUE_FIELD);
        switch (type) {
            case SIMPLE:
                record = new ProducerRecord<>(defaultTopic, value);
                break;
            case CUSTOM_TOPIC:
                record = new ProducerRecord<String, String>(payload.getString(ConfigConstants.TOPIC_FIELD), value);
                break;
            case CUSTOM_KEY:
                record = new ProducerRecord<>(payload.getString(ConfigConstants.TOPIC_FIELD),
                        payload.getString(ConfigConstants.KEY_FIELD),
                        value);
                break;
            case CUSTOM_PARTITION:
                record = new ProducerRecord<>(payload.getString(ConfigConstants.TOPIC_FIELD),
                        payload.getInteger(ConfigConstants.PARTITION_FIELD),
                        payload.getString(ConfigConstants.KEY_FIELD),
                        value);
                break;
            default:
                String error = String.format("Invalid type submitted: {%s} message being thrown away: %s",
                        type.toString(), value);
                logger.error(error);
                message.fail(-1, error);
                return;
        }

        //logger.info(value);
        sender.submit(() ->
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        vertx.runOnContext(aVoid -> message.fail(-1, exception.getMessage()));
                        exception.printStackTrace();
                        logger.error("Failed to send message to kafka ex: ", exception);
                        // reply with nothing for now
                    } else {
                        vertx.runOnContext(aVoid -> message.reply(new JsonObject()));
                    }
                })
        );
    }

    private Properties populateKafkaConfig() {
        Properties properties = new Properties();

        properties.put(ConfigConstants.BOOTSTRAP_SERVERS, getRequiredConfig(ConfigConstants.BOOTSTRAP_SERVERS));

        // default serializer to the String one
        String defaultSerializer = (String) producerConfig.getOrDefault(ConfigConstants.SERIALIZER_CLASS,
                ConfigConstants.DEFAULT_SERIALIZER_CLASS);

        properties.put(ConfigConstants.SERIALIZER_CLASS, defaultSerializer);
        properties.put(ConfigConstants.KEY_SERIALIZER_CLASS,
                producerConfig.getOrDefault(ConfigConstants.KEY_SERIALIZER_CLASS, defaultSerializer));
        properties.put(ConfigConstants.VALUE_SERIALIZER_CLASS,
                producerConfig.getOrDefault(ConfigConstants.VALUE_SERIALIZER_CLASS, defaultSerializer));
        properties.put(ConfigConstants.PRODUCER_TYPE, producerConfig.getOrDefault(ConfigConstants.PRODUCER_TYPE, "async"));
//        properties.put(ConfigConstants.COMPRESSION_CODEC,"gzip");
        long maxBlockMs = producerConfig.getLongValue(ConfigConstants.MAX_BLOCK_MS);
        properties.put(ConfigConstants.MAX_BLOCK_MS, maxBlockMs > 0 ? maxBlockMs : 6000);
        return properties;
    }

    private String getRequiredConfig(String key) {
        String value = producerConfig.getString(key);

        if (null == value) {
            throw new IllegalArgumentException(String.format("Required config value not found key: %s", key));
        }
        return value;
    }

    private void shutdown() {
        try {
            if (producer != null) {
                producer.close();
                producer = null;
            }

            if (sender != null) {
                sender.shutdown();
                sender = null;
            }
        } catch (Exception ex) {
            logger.error("Failed to close producer", ex);
        }
    }
}
