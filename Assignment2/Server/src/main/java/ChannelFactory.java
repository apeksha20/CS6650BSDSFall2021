import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ChannelFactory extends BasePooledObjectFactory<Channel> {
  private final Connection connection;

  /**
   * Creating rabbitmq connection
   * @throws IOException throws I/O exception
   * @throws TimeoutException throws timeout exception
   */
  public ChannelFactory() throws IOException, TimeoutException {
    ConnectionFactory connectionFactory  =new ConnectionFactory();
    //TODO change the fields or read from a property file
    connectionFactory.setHost("52.207.253.81");
    connectionFactory.setUsername("guest1");
    connectionFactory.setPassword("guest1");
    connection = connectionFactory.newConnection();
  }

  /**
   * create a channel
   * @return the created channel
   * @throws Exception throws exception when channel is does not get created
   */
  @Override
  public Channel create() throws Exception {
    return connection.createChannel();
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }
}
