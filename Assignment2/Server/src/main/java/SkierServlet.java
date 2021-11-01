import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {

  private static final String QUEUE_NAME = "skier_queue";
  private ObjectPool<Channel> pool;

  //initializing the object pool through the constructor
  @Override
  public void init() throws ServletException {
    super.init();
    try {
      pool = new GenericObjectPool<>(new ChannelFactory());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();
    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters : " + urlPath);
      return;
    }
    // Validate url path and return the response status code
    // Returning value if url path is valid
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().write("The get works for Skiers! : " + urlPath);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();
    System.out.println("Inside doPost: " + urlPath);
    ;

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters : " + urlPath);
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      try {
        String data = request.getReader().lines().collect(Collectors.joining());
        JsonObject obj = new JsonParser().parse(data).getAsJsonObject();
        obj.addProperty("skierId", urlParts[7]);
        sendSkierDataToQueue(obj);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Post Successful! " + obj.toString());
      } catch (Exception ex) {
        ex.printStackTrace();
        response.getWriter().write("Not a valid request body");
      }
    }
  }

  private int sendSkierDataToQueue(JsonObject data) {
    try {
      Channel channel = pool.borrowObject();

      // setting durable to true so that queue survives server restart
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.basicPublish("", QUEUE_NAME, null, data.toString().getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] Sent '" + data.toString() + "'");
      pool.returnObject(channel);
      return 1;
    } catch (Exception e) {
      System.err.println("Failed to send data to RabbitMQ");
      return 0;
    }
  }

  private boolean isUrlValid(String[] urlParts) {
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    if (urlParts == null || urlParts.length == 0 || urlParts.length > 8) {
      return false;
    }
    for (int i = 1; i < urlParts.length; ++i) {
      if (urlParts[i].isEmpty()) {
        return false;
      }
    }
    try {
      //checking if parameters are valid
      Integer.parseInt(urlParts[1]);
      Integer.parseInt(urlParts[3]);
      Integer.parseInt(urlParts[5]);
      Integer.parseInt(urlParts[7]);
    } catch (NumberFormatException ex) {
      return false;
    }
    if (!urlParts[2].equalsIgnoreCase("seasons") ||
        !urlParts[4].equalsIgnoreCase("days") ||
        !urlParts[6].equalsIgnoreCase("skiers")) {
      return false;
    }
    return true;
  }

}
