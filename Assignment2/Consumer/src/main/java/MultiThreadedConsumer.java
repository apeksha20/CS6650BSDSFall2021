import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {

  private static final String QUEUE_NAME = "skier_queue";
  private static final int NO_OF_MSG_PER_RECEIVER = 1;
  private static ConcurrentMap<Integer, List<LiftRide>> skierDataMap = new ConcurrentHashMap<>();

  public static void main(String args[]) throws IOException, TimeoutException {
    ConsumerParameters parameters = ParameterProcessor.processParameters();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(parameters.getHostName());
    factory.setUsername(parameters.getUserName());
    factory.setPassword(parameters.getPassword());
    Connection connection = factory.newConnection();
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          Channel channel = connection.createChannel();
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);
          channel.basicQos(NO_OF_MSG_PER_RECEIVER);
          System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //true for acknowledging multiple deliveries false otherwise Message gets deleted
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            System.out.println("Message received:" + message);
            updateSkierDataInMap(message);

          };
          channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
          });
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    };
    for (int i = 0; i < parameters.getMaxThreads(); ++i) {
      Thread newThread = new Thread(runnable);
      newThread.start();
    }
  }

  private synchronized static void updateSkierDataInMap(String message) {
    Gson gson = new Gson();
    SkierInfo skierInfo = gson.fromJson(message.toString(), SkierInfo.class);
    System.out.println(
        "SkierInfo: skierId:" + skierInfo.getSkierId() + " time:" + skierInfo.getTime() + " liftId:"
            + skierInfo.getLiftId());
    LiftRide liftRide = new LiftRide(skierInfo.getTime(), skierInfo.getLiftId());
    if (skierDataMap.containsKey(skierInfo.getSkierId())) {
      List<LiftRide> liftRideList = skierDataMap.get(skierInfo.getSkierId());
      liftRideList.add(new LiftRide(skierInfo.getTime(), skierInfo.getLiftId()));
    } else {
      List<LiftRide> newLiftRideList = new ArrayList<>();
      newLiftRideList.add(liftRide);
      skierDataMap.put(skierInfo.getSkierId(), newLiftRideList);
    }
    System.out.println("Map size: " + skierDataMap.size());
  }
}
