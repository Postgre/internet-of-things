package mqtt.client;

import mqtt.callback.CallBack;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

  private static final String BROKER_IP = "10.155.19.78";
  private static final int BROKER_PORT = 1883;
  private static final String DEVICE_IP = System.getProperty("gw.ip");
  private static final String DEVICE_NAME = "WISER HC";
  private static final short KEEP_ALIVE = 50;
  private static final String MAC_ADDR = System.getProperty("ehc.version.macaddress");
  private static final String CLIENT_ID = "WISER";

  public void start(BundleContext context) throws Exception
  {
    String payload = "IP:" + DEVICE_IP + ":NAME:" + DEVICE_NAME;
    String publish_topic = "d2d/device/" + MAC_ADDR;
    String subscribe_topic = "d2d/device/fired/" + MAC_ADDR;

    CallBack callBack = new CallBack.Builder(BROKER_IP, BROKER_PORT)
                        .clientID(CLIENT_ID)
                        .isCleanSession(true)
                        .keepAlive(KEEP_ALIVE)
                        .publishTopic(publish_topic)
                        .payload(payload)
                        .QoS(0)
                        .isRetained(true)
                        .subscribeTopic(subscribe_topic)
                        .build();

    callBack.run();

  }

  public void stop(BundleContext context) throws Exception
  {
  }
}
