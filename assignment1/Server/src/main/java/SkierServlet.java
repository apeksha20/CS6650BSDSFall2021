import com.google.gson.Gson;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SkierServlet", value = "/skiers/*")
public class SkierServlet extends HttpServlet {

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
    System.out.println("Inside doPost: " + urlPath);;

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters : " + urlPath);
      return;
    }
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    else {
      try {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = request.getReader().readLine()) != null) {
          sb.append(s);
        }
        Gson gson = new Gson();
        LiftRide liftRide = gson.fromJson(sb.toString(), LiftRide.class);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Post Successful! Received liftId: " + liftRide.getLiftId() +
            " time: " + liftRide.getTime());
      }
      catch (Exception ex){
        ex.printStackTrace();
        response.getWriter().write("Not a valid request body");
      }
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
