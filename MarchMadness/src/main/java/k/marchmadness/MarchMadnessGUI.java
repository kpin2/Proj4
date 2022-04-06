package k.marchmadness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * MarchMadnessGUI
 * <p>
 * this class contains the buttons the user interacts
 * with and controls the actions of other objects
 *
 * @author Grant Osborn
 */
public class MarchMadnessGUI extends Application {


    //all the gui el21  qaements
    private BorderPane root;
    private ToolBar toolBar;
    private ToolBar btoolBar;
    private Button simulate;
    private Button login;
    private Button scoreBoardButton;
    private Button viewBracketButton;
    private Button clearButton;
    private Button resetButton;
    private Button finalizeButton;
    private Tooltip tooltip;


    /**
     * Edit 4/1/2022 by Kevin Pinto - adding new buttons to give the user additional options after clicking Simulate
     */
    private Button exitGameButton;
    private Button logoutButton;
    private Button newBracketButton;

    //EDIT: by Kevin Pinto New bracket when user clicks the button
    private Bracket newBracket;


    //allows you to navigate back to division selection screen
    private Button back;


    private Bracket startingBracket;
    //reference to currently logged in bracket
    private Bracket selectedBracket;
    private Bracket simResultBracket;


    private ArrayList<Bracket> playerBrackets;
    private HashMap<String, Bracket> playerMap;


    private ScoreBoardTable scoreBoard;
    private TableView table;
    private BracketPane bracketPane;
    private GridPane loginP;
    private TournamentInfo teamInfo;


    @Override
    public void start(Stage primaryStage) {
        //try to load all the files, if there is an error display it
        try {
            teamInfo = new TournamentInfo();
            startingBracket = new Bracket(TournamentInfo.loadStartingBracket());
            simResultBracket = new Bracket(TournamentInfo.loadStartingBracket());
            newBracket = new Bracket(TournamentInfo.loadStartingBracket());
        } catch (IOException ex) {
            showError(new Exception("Can't find " + ex.getMessage(), ex), true);
        }
        //deserialize stored brackets
        playerBrackets = loadBrackets();

        playerMap = new HashMap<>();
        addAllToMap();

        //the main layout container
        root = new BorderPane();
        scoreBoard = new ScoreBoardTable();
        table = scoreBoard.start();
        loginP = createLogin();
        CreateToolBars();

        //display login screen
        login();

        setActions();
        root.setTop(toolBar);
        root.setBottom(btoolBar);
        Scene scene = new Scene(root);
        primaryStage.setMaximized(true);

        primaryStage.setTitle("March Madness Bracket Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * simulates the tournament
     * simulation happens only once
     * <p>
     * Edit by Kevin Pinto to add logout, exit, new bracket button availability
     */
    private void simulate() {
        //cant login and restart prog after simulate
        login.setDisable(true);

        //Yuliia: Tooltip added for sign button button
        tooltip = new Tooltip("Click to Login");
        login.setTooltip(tooltip);
        simulate.setDisable(true);

        //Yuliia: Tooltip added for simulate button button
        tooltip = new Tooltip("Click to Simulate");
        simulate.setTooltip(tooltip);

        //Yuliia: Tooltip added for sign button button
        tooltip = new Tooltip("Click to Login");
        login.setTooltip(tooltip);

        //Yuliia: Tooltip added for simulate button button
        tooltip = new Tooltip("Click to Simulate");
        simulate.setTooltip(tooltip);

        //Yuliia: Tooltip added for score board button button
        tooltip = new Tooltip("Move to Score Board");
        scoreBoardButton.setTooltip(tooltip);

        //Yuliia: Tooltip added for view bracket button button
        tooltip = new Tooltip("Click to See Bracket");
        viewBracketButton.setTooltip(tooltip);


        scoreBoardButton.setDisable(false);
        viewBracketButton.setDisable(false);
        logoutButton.setDisable(false);
        newBracketButton.setDisable(false);
        exitGameButton.setDisable(false);

        teamInfo.simulate(simResultBracket);
        for (Bracket b : playerBrackets) {
            scoreBoard.addPlayer(b, b.scoreBracket(simResultBracket));
        }


        scoreBoardButton.setDisable(false);
        //Yuliia: Tooltip added for score board button button
        tooltip = new Tooltip("Move to Score Board");
        scoreBoardButton.setTooltip(tooltip);
        viewBracketButton.setDisable(false);
        //Yuliia: Tooltip added for view bracket button button
        tooltip = new Tooltip("Click to See Bracket");
        viewBracketButton.setTooltip(tooltip);

        teamInfo.simulate(simResultBracket);
        for (Bracket b : playerBrackets) {
            scoreBoard.addPlayer(b, b.scoreBracket(simResultBracket));
        }

        displayPane(table);
    }

    /**
     * Displays the login screen
     */
    private void login() {
        login.setDisable(true);
        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);
        viewBracketButton.setDisable(true);
        btoolBar.setDisable(true);
        displayPane(loginP);
    }

    /**
     * Displays the score board
     */
    private void scoreBoard() {
        displayPane(table);
    }

    /**
     * Displays Simulated Bracket
     */
    private void viewBracket() {
        selectedBracket = simResultBracket;
        bracketPane = new BracketPane(selectedBracket);
        GridPane full = bracketPane.getFullPane();
        full.setAlignment(Pos.CENTER);
        full.setDisable(true);
        displayPane(new ScrollPane(full));
    }

    /**
     * allows user to choose bracket
     */
    private void chooseBracket() {
        login.setDisable(true);
        btoolBar.setDisable(false);
        bracketPane = new BracketPane(selectedBracket);
        displayPane(bracketPane);

    }

    /**
     * resets current selected sub tree
     * for final4 reset Ro2 and winner
     */
    private void clear() {
        bracketPane.clear();
        bracketPane = new BracketPane(selectedBracket);
        displayPane(bracketPane);

    }

    /**
     * resets entire bracket
     */
    private void reset() {
        if (confirmReset()) {
            //horrible hack to reset
            selectedBracket = new Bracket(startingBracket);
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        }
    }

    /**
     * Allows the user to return to the login screen.
     *
     * @author Kevin Pinto
     */
    private void logout() {

        login.setDisable(true);
        simulate.setDisable(true);
        scoreBoardButton.setDisable(true);
        viewBracketButton.setDisable(true);
        newBracketButton.setDisable(true);
        btoolBar.setDisable(true);
        displayPane(loginP);
        logoutButton.setDisable(true);

    }

    /**
     * Allows user to create a new bracket
     *
     * @author Kevin Pinto
     */
    private void newBracket() {
        if (confirmNewBracket()) {
            selectedBracket = new Bracket(newBracket);
            bracketPane = new BracketPane(selectedBracket);


            toolBar.setDisable(true);
            btoolBar.setDisable(false);
//            seralizeBracket(selectedBracket);

            displayPane(bracketPane);
        }
    }

    /**
     * Prompt user for confirmation before creating new bracket
     *
     * @author Kevin Pinto
     */
    private boolean confirmNewBracket() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "This will create a new BLANK bracket, are you sure?",
                ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("New Bracket Confirmation");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    /**
     * Allow user to exit the game with confirmation dialog
     *
     * @author Kevin Pinto
     */
    private void exitGame() {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you wish to exit the game?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit Game?");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            System.exit(0);
        } else if (alert.getResult() == ButtonType.NO) {
            alert.close();
        }
    }


    //    EDIT: by Kevin Pinto change which buttons are disabled
    private void finalizeBracket() {
        if (bracketPane.isComplete()) {
//            btoolBar.setDisable(true);
            clearButton.setDisable(true);
            resetButton.setDisable(true);
            finalizeButton.setDisable(true);
            back.setDisable(true);

            bracketPane.setDisable(true);

            simulate.setDisable(false);
            login.setDisable(false);
            exitGameButton.setDisable(false);

            //save the bracket along with account info
            seralizeBracket(selectedBracket);

//            addAllToMap();

        } else {
            infoAlert("You can only finalize a bracket once it has been completed.");
            //go back to bracket section selection screen
            // bracketPane=new BracketPane(selectedBracket);
            displayPane(bracketPane);

        }
        //bracketPane=new BracketPane(selectedBracket);


    }


    /**
     * displays element in the center of the screen
     *
     * @param p must use a subclass of Pane for layout.
     *          to be properly center aligned in  the parent node
     */
    private void displayPane(Node p) {
        root.setCenter(p);
        BorderPane.setAlignment(p, Pos.CENTER);
    }

    /**
     * Creates toolBar and buttons.
     * adds buttons to the toolbar and saves global references to them
     */
    private void CreateToolBars() {
        toolBar = new ToolBar();
        btoolBar = new ToolBar();
        login = new Button("Login");
        simulate = new Button("Simulate");

        //Yuliia: Tooltip added for simulate button button
        tooltip = new Tooltip("Start Simulation");
        simulate.setTooltip(tooltip);

        scoreBoardButton = new Button("ScoreBoard");
        viewBracketButton = new Button("View Simulated Bracket");
        clearButton = new Button("Clear");

        //Yuliia: Tooltip added for clear bracket button button
        tooltip = new Tooltip("Clear Bracket");
        clearButton.setTooltip(tooltip);
        resetButton = new Button("Reset");

        //Yuliaa: Tooltip added for reset  button
        tooltip = new Tooltip("Reset All Brackets");
        resetButton.setTooltip(tooltip);

        finalizeButton = new Button("Finalize");
        simulate = new Button("Simulate");
        scoreBoardButton = new Button("ScoreBoard");
        viewBracketButton = new Button("View Simulated Bracket");
        clearButton = new Button("Clear");
        resetButton = new Button("Reset");
        finalizeButton = new Button("Finalize");

        //EDIT: added by Kevin Pinto
        newBracketButton = new Button("New Bracket");
        exitGameButton = new Button("Exit Game");
        logoutButton = new Button("Logout");


        //Yuliia: Tooltip added for simulate button button
        tooltip = new Tooltip("Start Simulation");
        simulate.setTooltip(tooltip);

        //Yuliia: Tooltip added for clear bracket button button
        tooltip = new Tooltip("Clear Bracket");
        clearButton.setTooltip(tooltip);

        //Yuliaa: Tooltip added for reset  button
        tooltip = new Tooltip("Reset All Brackets");
        resetButton.setTooltip(tooltip);

        //Yuliia: Tooltip added for finalize  button
        tooltip = new Tooltip("Finalise the Bracket");
        finalizeButton.setTooltip(tooltip);
        tooltip = new Tooltip("Actions");
        toolBar.setTooltip(tooltip);


        toolBar.getItems().addAll(
                createSpacer(),
                login,
                logoutButton,
                simulate,
                scoreBoardButton,
                viewBracketButton,
                newBracketButton,
                createSpacer()
        );
        tooltip = new Tooltip("Reset Buttons");
        btoolBar.setTooltip(tooltip);


        btoolBar.getItems().addAll(
                createSpacer(),
                clearButton,
                resetButton,
                finalizeButton,
                back = new Button("Choose Division"),
                exitGameButton,
                createSpacer()
        );
    }

    /**
     * sets the actions for each button
     */
    private void setActions() {
        login.setOnAction(e -> login());
        simulate.setOnAction(e -> simulate());
        scoreBoardButton.setOnAction(e -> scoreBoard());
        viewBracketButton.setOnAction(e -> viewBracket());
        clearButton.setOnAction(e -> clear());
        resetButton.setOnAction(e -> reset());
        finalizeButton.setOnAction(e -> finalizeBracket());
        back.setOnAction(e -> {
            bracketPane = new BracketPane(selectedBracket);
            displayPane(bracketPane);
        });

        //EDIT: Added by Kevin Pinto
        logoutButton.setOnAction(e -> logout());
        newBracketButton.setOnAction(e -> newBracket());
        exitGameButton.setOnAction(e -> exitGame());
    }

    /**
     * Creates a spacer for centering buttons in a ToolBar
     */
    private Pane createSpacer() {
        Pane spacer = new Pane();
        HBox.setHgrow(
                spacer,
                Priority.SOMETIMES
        );
        return spacer;
    }


    private GridPane createLogin() {
        
        
        /*
        LoginPane
        Sergio and Joao
         */

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(5, 5, 5, 5));

        //Yuliia: Greetings for the user
        Text welcomeMessage = new Text("Welcome to March Madness");
        welcomeMessage.setFont(new Font("SansSerif", 35));
        loginPane.add(welcomeMessage, 0, 0, 2, 1);

        Label userName = new Label("User Name: ");
        loginPane.add(userName, 0, 1);

        TextField enterUser = new TextField();
        loginPane.add(enterUser, 1, 1);


        //Yuliia: Tooltip added for user name text field
        tooltip = new Tooltip("Enter username here");
        enterUser.setTooltip(tooltip);


        Label password = new Label("Password: ");
        loginPane.add(password, 0, 2);

        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordField, 1, 2);
        //Yuliia: Tooltip added for password text field
        tooltip = new Tooltip("Enter password here");
        passwordField.setTooltip(tooltip);


        //Yuliia: Tooltip added for password text field
        tooltip = new Tooltip("Enter password here");
        passwordField.setTooltip(tooltip);


        Button signButton = new Button("Sign in");

        tooltip = new Tooltip("Click to Login");
        signButton.setTooltip(tooltip);


        loginPane.add(signButton, 1, 4);
        signButton.setDefaultButton(true);//added by matt 5/7, lets you use sign in button by pressing enter

        Label message = new Label();
        loginPane.add(message, 1, 5);

        signButton.setOnAction(event -> {

            // the name user enter
            String name = enterUser.getText();
            // the password user enter
            String playerPass = passwordField.getText();


            if (playerMap.get(name) != null) {
                //check password of user

                Bracket tmpBracket = this.playerMap.get(name);

                String password1 = tmpBracket.getPassword();

                if (Objects.equals(password1, playerPass)) {
                    // load bracket
                    selectedBracket = playerMap.get(name);
                    chooseBracket();
                } else {
                    infoAlert("The password you have entered is incorrect!");
                }

            } else {
                //Yuliia: check for empty username and password
                if (!name.equals("") && !playerPass.equals("")) {
                    /**
                     Yuliia: Special characters are not allowed in user name
                     Yuliia: But they are allowed in passwords
                     Yuliia: Password length must be 8 characters at least
                     */
                    boolean special = false;
                    for (int a = 0; a < name.length(); a++) {
                        if (Character.isDigit(name.charAt(a)) || Character.isAlphabetic(name.charAt(a))) {
                            //all good if we have alphabets and digits
                        } else {
                            special = true;
                            break;
                        }
                    }
                    if (special) {
                        infoAlert("Special Characters in Username are not allowed!");
                    } else {
                        if (playerPass.length() < 8) {
                            infoAlert("Password should be at least 8 characters long!");
                        } else {
                            //create new bracket
                            Bracket tmpPlayerBracket = new Bracket(startingBracket, name);
                            playerBrackets.add(tmpPlayerBracket);
                            tmpPlayerBracket.setPassword(playerPass);

                            playerMap.put(name, tmpPlayerBracket);
                            selectedBracket = tmpPlayerBracket;
                            //alert user that an account has been created
                            infoAlert("No user with the Username \"" + name + "\" exists. A new account has been created.");
                            chooseBracket();
                        }
                    }
                } else {
                    infoAlert("Username & Password cannot be empty!");
                }
            }
        });
        return loginPane;
    }

    /**
     * addAllToMap
     * adds all the brackets to the map for login
     */
    private void addAllToMap() {
        for (Bracket b : playerBrackets) {
            playerMap.put(b.getPlayerName(), b);
        }
    }

    /**
     * The Exception handler
     * Displays a error message to the user
     * and if the error is bad enough closes the program
     *
     * @param msg   message to be displayed to the user
     * @param fatal true if the program should exit. false otherwise
     */
    private void showError(Exception e, boolean fatal) {
        String msg = e.getMessage();
        if (fatal) {
            msg = msg + " \n\nthe program will now close";
            //e.printStackTrace();
        }
        Alert alert = new Alert(AlertType.ERROR, msg);
        alert.setResizable(true);
        alert.getDialogPane().setMinWidth(420);
        alert.setTitle("Error");
        alert.setHeaderText("something went wrong");
        alert.showAndWait();
        if (fatal) {
            System.exit(666);
        }
    }

    /**
     * alerts user to the result of their actions in the login pane
     *
     * @param msg the message to be displayed to the user
     */
    private void infoAlert(String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    /**
     * Prompts the user to confirm that they want
     * to clear all predictions from their bracket
     *
     * @return true if the yes button clicked, false otherwise
     */
    private boolean confirmReset() {
        Alert alert = new Alert(AlertType.CONFIRMATION,
                "Are you sure you want to reset the ENTIRE bracket?",
                ButtonType.YES, ButtonType.CANCEL);
        alert.setTitle("March Madness Bracket Simulator");
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }


    /**
     * Tayon Watson 5/5
     * seralizedBracket
     *
     * @param B The bracket the is going to be seralized
     */
    private void seralizeBracket(Bracket B) {
        FileOutputStream outStream = null;
        ObjectOutputStream out = null;
        try {
            outStream = new FileOutputStream(B.getPlayerName() + ".ser");
            out = new ObjectOutputStream(outStream);
            out.writeObject(B);
            out.close();
        } catch (IOException e) {
            // Grant osborn 5/6 hopefully this never happens
            showError(new Exception("Error saving bracket \n" + e.getMessage(), e), false);
        }
    }

    /**
     * Tayon Watson 5/5
     * deseralizedBracket
     *
     * @param filename of the seralized bracket file
     * @return deserialized bracket
     */
    private Bracket deseralizeBracket(String filename) {
        Bracket bracket = null;
        FileInputStream inStream = null;
        ObjectInputStream in = null;
        try {
            inStream = new FileInputStream(filename);
            in = new ObjectInputStream(inStream);
            bracket = (Bracket) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            // Grant osborn 5/6 hopefully this never happens either
            showError(new Exception("Error loading bracket \n" + e.getMessage(), e), false);
        }
        return bracket;
    }

    /**
     * Tayon Watson 5/5
     * deseralizedBracket
     *
     * @param filename of the seralized bracket file
     * @return deserialized bracket
     */
    private ArrayList<Bracket> loadBrackets() {
        ArrayList<Bracket> list = new ArrayList<Bracket>();
        File dir = new File(".");
        for (final File fileEntry : dir.listFiles()) {
            String fileName = fileEntry.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

            if (extension.equals("ser")) {
                list.add(deseralizeBracket(fileName));
            }
        }
        return list;
    }

}
