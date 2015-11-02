package com.thoughtconcepts.d2d.mqtt.services.listener;

import com.thoughtconcepts.d2d.event.constants.EventConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author AMIT KUMAR MONDAL <admin@amitinside.com>
 */
public class MQTTListener implements Runnable {

    private static final long DEFAULT_SLEEP_BEFORE_RE_ATTEMPT_IN_SECONDS = 5000;
    private static final long DEFAULT_MAX_RE_ATTEMPT_DURATION_IN_SECONDS = 3600 * 3;
    private long listenerSleepBeforeReAttemptInSeconds;
    private long listenerMaxReAttemptDurationInSeconds;
    private MQTT mqtt;
    private ArrayList<Topic> topics;
    private boolean listenerDebug;
    private String listenerHostURI;
    private String listenerTopic;
    private String listenerLogFile;
    private EventAdmin eventAdmin;
    private long listenerLastSuccessfulSubscription;
    private Logger fLogger;
    private String NEW_LINE = System.getProperty("line.separator");

    public MQTTListener(String listenerHostURI, String listenerTopic, String logFile, boolean debug, EventAdmin eventAdmin) {
        this(listenerHostURI, listenerTopic, logFile, DEFAULT_SLEEP_BEFORE_RE_ATTEMPT_IN_SECONDS, DEFAULT_MAX_RE_ATTEMPT_DURATION_IN_SECONDS, debug, eventAdmin);
    }

    public MQTTListener(String listenerHostURI, String listenerTopic, String logFile, long listenerSleepBeforeReAttemptInSeconds, long listenerMaxReAttemptDurationInSeconds, boolean debug, EventAdmin eventAdmin) {
        init(listenerHostURI, listenerTopic, logFile, eventAdmin, listenerSleepBeforeReAttemptInSeconds, listenerMaxReAttemptDurationInSeconds, debug);
    }

    private void init(String listenerHostURI, String listenerTopic, String logFile, EventAdmin eventAdmin, long listenerSleepBeforeReAttemptInSeconds, long listenerMaxReAttemptDurationInSeconds, boolean debug) {
        this.listenerHostURI = listenerHostURI;
        this.listenerTopic = listenerTopic;
        this.listenerLogFile = logFile;
        this.listenerSleepBeforeReAttemptInSeconds = listenerSleepBeforeReAttemptInSeconds;
        this.listenerMaxReAttemptDurationInSeconds = listenerMaxReAttemptDurationInSeconds;
        this.listenerDebug = debug;
        this.eventAdmin = eventAdmin;
        initMQTT();
    }

    private void initMQTT() {
        mqtt = new MQTT();
        listenerLastSuccessfulSubscription = System.currentTimeMillis();

        try {
            fLogger = Logger.getLogger(getClass().getName());
            FileHandler handler = new FileHandler(listenerLogFile);
            fLogger.addHandler(handler);
        } catch (IOException e) {
            System.out.println("Logger - Failed");
        }

        try {
            mqtt.setHost(listenerHostURI);
        } catch (URISyntaxException e) {
            stderr("setHost failed: " + e);
            stderr(e);
        }
        QoS qos = QoS.AT_MOST_ONCE;
        topics = new ArrayList<Topic>();
        topics.add(new Topic(listenerTopic, qos));
    }

    private void stdout(String x) {
        if (listenerDebug) {
            fLogger.log(Level.INFO, x + NEW_LINE);
        }
    }

    private void stderr(String x) {
        if (listenerDebug) {
            fLogger.log(Level.SEVERE, x + NEW_LINE);
        }
    }

    private void stderr(Throwable e) {
        if (listenerDebug) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            fLogger.log(Level.SEVERE, sw.toString() + NEW_LINE);
        }
    }

    private void subscriptionSuccessful() {
        listenerLastSuccessfulSubscription = System.currentTimeMillis();
    }

    private void postEventOnReceiveMQTTTopic(String topic, String payload) {

        Dictionary properties = new Hashtable();
        properties.put("MQTT_TOPIC", topic);
        properties.put("payload", payload);
        properties.put("time", System.currentTimeMillis());

        Event reportGeneratedEvent = new Event(EventConstants.NEW_DEVICE_DETECTED, properties);

        eventAdmin.sendEvent(reportGeneratedEvent);
    }

    private boolean tryToListen() {
        return ((System.currentTimeMillis() - listenerLastSuccessfulSubscription) < listenerMaxReAttemptDurationInSeconds * 1000);
    }

    private void sleepBeforeReAttempt() throws InterruptedException {
        stdout(String.format(("Listener stopped, re-attempt in %s seconds."), listenerSleepBeforeReAttemptInSeconds));
        Thread.sleep(listenerSleepBeforeReAttemptInSeconds);
    }

    private void listenerReAttemptsOver() {
        stdout(String.format(("Listener stopped since reattempts have failed for %s seconds."), listenerMaxReAttemptDurationInSeconds));
    }

    private void listen() {
        final CallbackConnection connection = mqtt.callbackConnection();
        final CountDownLatch done = new CountDownLatch(1);

        connection.listener(new Listener() {
            public void onConnected() {
                stdout("Listener onConnected");
            }

            public void onDisconnected() {
                stdout("Listener onDisconnected");
            }

            public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
                stdout(topic + " --> " + body.toString());
                ack.run();
                postEventOnReceiveMQTTTopic(topic.toString(), filterPayload(body.toString()));
            }

            private String filterPayload(String input) {
                String output = "";
                if (input.startsWith("ascii: ")) {
                    output += input.replace("ascii: ", "");
                }
                return output;
            }

            public void onFailure(Throwable value) {
                stdout("Listener onFailure: " + value);
                stderr(value);
                done.countDown();
            }
        });

        connection.resume();

        connection.connect(new Callback<Void>() {
            public void onFailure(Throwable value) {
                stderr("Connect onFailure...: " + value);
                stderr(value);
                done.countDown();
            }

            @Override
            public void onSuccess(Void value) {
                final Topic[] ta = topics.toArray(new Topic[topics.size()]);
                connection.subscribe(ta, new Callback<byte[]>() {
                    public void onSuccess(byte[] value) {
                        for (int i = 0; i < value.length; i++) {
                            stdout("Subscribed to Topic: " + ta[i].name() + " with QoS: " + QoS.values()[value[i]]);
                        }
                        subscriptionSuccessful();
                    }

                    public void onFailure(Throwable value) {
                        stderr("Subscribe failed: " + value);
                        stderr(value);
                        done.countDown();
                    }
                });
            }
        });

        try {
            done.await();
        } catch (Exception e) {
            stderr(e);
        }
    }

    @Override
    public void run() {
        while (tryToListen()) {
            initMQTT();
            listen();
            try {
                sleepBeforeReAttempt();
            } catch (InterruptedException e) {
                stderr("Sleep failed:" + e);
                stderr(e);
            }
        }

        listenerReAttemptsOver();
    }
}
