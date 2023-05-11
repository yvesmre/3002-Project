package main.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;

import main.HtmlRenderer;
import main.QuizManager;
import main.UserManager;
import main.quiz.Quiz;

public class ProfilePage extends AbstractPageHandler {

  private final String htmlPage;

  public ProfilePage() {
    htmlPage = HtmlRenderer.readHTML("profile.html");
  }

  @Override
  public void handleGet(HttpExchange t) throws IOException {
    String user = "";
    String password = "";
    ArrayList<String> redirect = new ArrayList<>();
    if (t.getRequestHeaders().get("Cookie") != null) {
      for (String str : t.getRequestHeaders().get("Cookie")) {
        user = str.split("=")[1].split(":")[0];
        password = str.split("=")[1].split(":")[1];
      }
    }

    if (!UserManager.INSTANCE.validate(user, password)) {
      redirect.add("logout");
      t.getResponseHeaders().put("Location", redirect);
      t.sendResponseHeaders(302, -1);
      t.close();
    }

    if (user.isEmpty()) {
      redirect.add("login");
      t.getResponseHeaders().put("Location", redirect);
      t.sendResponseHeaders(302, -1);
      t.close();
    }

    HashMap<String, Object> dataToHTML = new HashMap<String, Object>();
    dataToHTML.put("username", user);

    String pastQuizzes = "";
    ArrayList<Quiz> past = QuizManager.INSTANCE.getPastQuizzes(UserManager.INSTANCE.getUser(user));
    for (Quiz q : past)
      pastQuizzes = pastQuizzes + "<p>" + "<b>" + q.getType().toUpperCase() + "</b> " + q.getPath().replace("T", " ") + " <b> Marks: " + q.totalMarks() + "/30 </b>" + "</p>";

    dataToHTML.put("pastquizzes", pastQuizzes);
    String htmlPage = HtmlRenderer.render(this.htmlPage, dataToHTML);
    t.sendResponseHeaders(200, htmlPage.length());
    t.getResponseBody().write(htmlPage.getBytes());
    t.getResponseBody().close();
  }

  @Override
  public void handlePost(HttpExchange t) throws IOException {
    String user = "";
    if (t.getRequestHeaders().get("Cookie") != null) {
      for (String str : t.getRequestHeaders().get("Cookie")) {
        user = str.split("=")[1].split(":")[0];
      }
    }
    QuizManager.INSTANCE.createNewQuiz(UserManager.INSTANCE.getUser(user));

    ArrayList<String> redirect = new ArrayList<>();

    redirect.add("quiz");
    t.getResponseHeaders().put("Location", redirect);
    t.sendResponseHeaders(302, -1);

  }

}
