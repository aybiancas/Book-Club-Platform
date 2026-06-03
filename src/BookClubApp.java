import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import config.ConnectionProvider;
import gui.*;
import model.*;
import services.*;

public class BookClubApp extends Application {

    private static BookClubService service;
    private static User currentUser;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        ConnectionProvider.getInstance();
        service = new BookClubService();
        showLogin();
    }

    static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    BookClubApp.class.getResource("/gui/resources/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setService(service, user -> {
                currentUser = user;
                showMain();
            });

            Scene scene = new Scene(root, 760, 460);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Book Club — Sign In");
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void logout() {
        currentUser = null;
        primaryStage.setResizable(false);
        showLogin();
    }

    static void showMain() {
        try {
            TabPane tabs = new TabPane();
            tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            tabs.getTabs().add(loadTab("Dashboard", "/gui/resources/dashboard.fxml",
                    DashboardController.class, ctrl -> ctrl.setService(service, currentUser, BookClubApp::logout)));
            tabs.getTabs().add(loadTab("Books", "/gui/resources/books.fxml",
                    BookController.class, ctrl -> ctrl.setService(service, currentUser)));
            tabs.getTabs().add(loadTab("Meetings", "/gui/resources/meetings.fxml",
                    MeetingController.class, ctrl -> ctrl.setService(service, currentUser)));
            tabs.getTabs().add(loadTab("Posts", "/gui/resources/posts.fxml",
                    PostController.class, ctrl -> ctrl.setService(service, currentUser)));
            tabs.getTabs().add(loadTab("Venues", "/gui/resources/venues.fxml",
                    VenueController.class, ctrl -> ctrl.setService(service, currentUser)));
            if (currentUser instanceof Manager) {
                tabs.getTabs().add(loadTab("Users", "/gui/resources/users.fxml",
                        UserManagementController.class, ctrl -> ctrl.setService(service)));
            }

            Scene scene = new Scene(tabs, 980, 680);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Book Club  -  " + currentUser.getName() + "  [" + currentUser.getRole() + "]");
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <C> Tab loadTab(String title, String fxmlPath, Class<C> controllerClass, ControllerSetup<C> setup) throws Exception {
        FXMLLoader loader = new FXMLLoader(BookClubApp.class.getResource(fxmlPath));
        Parent root = loader.load();
        C controller = loader.getController();
        setup.init(controller);
        return new Tab(title, root);
    }

    @FunctionalInterface
    interface ControllerSetup<C> {
        void init(C controller) throws Exception;
    }

    @Override
    public void stop() {
        ConnectionProvider.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
