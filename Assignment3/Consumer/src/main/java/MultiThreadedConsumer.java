import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;

public class MultiThreadedConsumer {

  private static final String QUEUE_NAME = "skier_queue";
  //exchange name
  private static final String EXCHANGE_NAME = "exchange";
  private static final String TABLE_NAME = "skiers_info";
  private static final int NO_OF_MSG_PER_RECEIVER = 1;
  private static LiftRideDAO liftRideDAO  = new LiftRideDAO();
  private static ConcurrentMap<Integer, List<LiftRide>> skierDataMap = new ConcurrentHashMap<>();

  public static void main(String args[]) throws IOException, TimeoutException {
    ConsumerParameters parameters = ParameterProcessor.processParameters();
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(parameters.getHostName());
    factory.setUsername(parameters.getUserName());
    factory.setPassword(parameters.getPassword());
    Connection connection = factory.newConnection();

    //Deleting and creating a new table
    liftRideDAO.deleteTable(TABLE_NAME);
    liftRideDAO.createTable(TABLE_NAME);
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        try {
          Channel channel = connection.createChannel();

          channel.queueDeclare(QUEUE_NAME, true, false, false, null);
          channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
          channel.basicQos(NO_OF_MSG_PER_RECEIVER);
          System.out.println(" [*] Waiting for messages: Skier Microservice. To exit press CTRL+C");
          DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            //true for acknowledging multiple deliveries false otherwise Message gets deleted
            //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            //System.out.println("Message received:" + message);
            //inserting skier data in the database
            insertSkierDataInDatabase(message);
            //updateSkierDataInMap(message);
          };
          //make true for auto acknowledge
          channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
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



  private static void insertSkierDataInDatabase(String message){
    Gson gson = new Gson();
    LiftRide liftRide = gson.fromJson(message.toString(), LiftRide.class);
//    System.out.println(
//        "SkierInfo: skierId:" + liftRide.getSkierId() + " time:" + liftRide.getTime() + " liftId:"
//            + liftRide.getLiftId() + " resortId:" + liftRide.getResortId() + " seasonId:" +
//        liftRide.getSeasonId() + " dayId:" + liftRide.getDayId());
    System.out.println("Inserting record in the table");
    liftRideDAO.createLiftRide(liftRide);
  }

//  private synchronized static void updateSkierDataInMap(String message) {
//    Gson gson = new Gson();
//    SkierInfo skierInfo = gson.fromJson(message.toString(), SkierInfo.class);
//    System.out.println(
//        "SkierInfo: skierId:" + skierInfo.getSkierId() + " time:" + skierInfo.getTime() + " liftId:"
//            + skierInfo.getLiftId());
//    LiftRide liftRide = new LiftRide(skierInfo.getTime(), skierInfo.getLiftId());
//    if (skierDataMap.containsKey(skierInfo.getSkierId())) {
//      List<LiftRide> liftRideList = skierDataMap.get(skierInfo.getSkierId());
//      liftRideList.add(new LiftRide(skierInfo.getTime(), skierInfo.getLiftId()));
//    } else {
//      List<LiftRide> newLiftRideList = new ArrayList<>();
//      newLiftRideList.add(liftRide);
//      skierDataMap.put(skierInfo.getSkierId(), newLiftRideList);
//      liftRideDAO.createLiftRide(new LiftRide(skierInfo.getTime(), skierInfo.getLiftId(),
//          skierInfo.getSkierId(), 12, 15, 18 ));
//    }
//    System.out.println("Map size: " + skierDataMap.size());
//  }
}
