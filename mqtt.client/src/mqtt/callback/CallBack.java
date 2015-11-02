package mqtt.callback;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;
import com.ibm.mqtt.MqttSimpleCallback;

public class CallBack implements MqttSimpleCallback
{
  private IMqttClient client;
  private Object connLostWait = new Object();
  private String brokerIP;
  private int broker_port;
  private boolean isCleanSession = true;
  private short keepAlive = 30;
  private boolean isConnected = false;
  private String subscribeTopic;
  private String payload;
  private String publishTopic;
  private int QoS;
  private String CLIENT_ID;
  private boolean isRetained;

  public static class Builder
  {
    // DEFAULT VARS
    private String brokerIP = "10.155.19.78";
    private int broker_port = 1883;
    private boolean isCleanSession = false;
    private boolean isRetained = false;
    private short keepAlive = 30;
    private String subscribeTopic = "";
    private String payload = "";
    private String publishTopic = "";
    private int QoS = 0;
    private String CLIENT_ID = Double.toString(Math.random() * 1234);

    public Builder(String brokerIP, int broker_port)
    {
      this.brokerIP = brokerIP;
      this.broker_port = broker_port;
    }

    public Builder isCleanSession(boolean flag)
    {
      this.isCleanSession = flag;
      return this;
    }

    public Builder isRetained(boolean flag)
    {
      this.isRetained = flag;
      return this;
    }

    public Builder keepAlive(short value)
    {
      this.keepAlive = value;
      return this;
    }

    public Builder subscribeTopic(String topic)
    {
      this.subscribeTopic = topic;
      return this;
    }

    public Builder payload(String payload)
    {
      this.payload = payload;
      return this;
    }

    public Builder publishTopic(String topic)
    {
      this.publishTopic = topic;
      return this;
    }

    public Builder clientID(String clientID)
    {
      this.CLIENT_ID = clientID;
      return this;
    }

    public Builder QoS(int qos)
    {
      this.QoS = qos;
      return this;
    }

    public CallBack build()
    {
      return new CallBack(this);
    }

  }

  private CallBack(Builder builder)
  {
    this.brokerIP = builder.brokerIP;
    this.broker_port = builder.broker_port;
    this.CLIENT_ID = builder.CLIENT_ID;
    this.isCleanSession = builder.isCleanSession;
    this.keepAlive = builder.keepAlive;
    this.payload = builder.payload;
    this.publishTopic = builder.publishTopic;
    this.subscribeTopic = builder.subscribeTopic;
    this.QoS = builder.QoS;
    this.isRetained = builder.isRetained;
  }

  public void connectionLost() throws Exception
  {
    writeLog("  --> CONNECTION LOST");
  }

  public void publishArrived(String topic, byte[] data, int qos,
      boolean retained) throws Exception
  {
    updateReceivedData(topic, data, qos, retained);
  }

  private boolean connect(String connectionString, boolean persistence)
  {
    try
    {
      client = MqttClient.createMqttClient(connectionString, null);
      client.connect(CLIENT_ID, isCleanSession, keepAlive);
      isConnected = true;
      client.registerSimpleHandler(this);
      return isConnected;
    }
    catch (MqttException e)
    {
      e.printStackTrace();
    }
    return isConnected;
  }

  public void disconnect()
  {
    isConnected = false;

    synchronized (connLostWait)
    {
      connLostWait.notify();
    }

    if (client != null)
    {
      try
      {
        client.disconnect();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        System.exit(1);
      }
    }
  }

  public void publish(String topic, byte[] message, int qos, boolean retained)
      throws Exception
  {
    if (isConnected)
    {
      try
      {
        client.publish(topic, message, qos, retained);
        writeLog("  --> PUBLISH,        TOPIC:" + topic + ", Requested QoS:"
            + qos);
      }
      catch (MqttException ex)
      {
        throw ex;
      }
    }
    else
    {
      throw new Exception("MQTT client not connected");
    }
  }

  public void subscribe(String topic, int qos, boolean sub)
  {

    if (isConnected)
    {
      try
      {
        String[] theseTopics = new String[1];
        int[] theseQoS = new int[1];

        theseTopics[0] = topic;
        theseQoS[0] = qos;

        synchronized (this)
        {
          if (sub)
          {
            writeLog("  --> SUBSCRIBE,        TOPIC:" + topic
                + ", Requested QoS:" + qos);
          }
          else
          {
            writeLog("  --> UNSUBSCRIBE,      TOPIC:" + topic);
          }
        }

        if (sub)
        {
          client.subscribe(theseTopics, theseQoS);
        }
        else
        {
          client.unsubscribe(theseTopics);
        }

      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
    else
    {
      writeLog("MQTT client not connected !");
    }
  }

  public void updateReceivedData(String topic, byte[] data, int QoS,
      boolean retained)
  {
    synchronized (this)
    {
      writeLog("  --> PUBLISH received, TOPIC:" + topic + ", QoS:" + QoS
          + ", Retained:" + retained);
      writeLog("   DATA: " + new String(data));
    }

  }

  private void writeLog(String string)
  {
    System.out.println(string);
  }

  public void run()
  {
    synchronized (this)
    {
      connect("tcp://" + brokerIP + ":" + broker_port, false);
    }
    try
    {
      publish(publishTopic, payload.getBytes(), QoS, isRetained);
      subscribe(subscribeTopic, QoS, isRetained);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}
