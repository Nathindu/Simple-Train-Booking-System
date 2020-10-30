//Importing mongodb classes for database operations.
import com.mongodb.*;

//Importing java util logger classes for disable logger information.
import java.util.logging.Level;
import java.util.logging.Logger;

//Importing Java FX Classes.
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//importing java classes
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class TrainStation extends Application
{
    //array lists for get customer details from course work one
    private ArrayList<Passenger> storedCustomerObjectsToBadulla = new ArrayList();
    private ArrayList<Passenger> storedCustomerObjectsToColombo = new ArrayList();

    //array list for waiting room passengers
    private List<Passenger> waitingRoomToBadulla = new ArrayList<>();
    private List<Passenger> waitingRoomToColombo = new ArrayList<>();

    //array list for boarded passengers
    private ArrayList <Passenger> boardedPassengersToBadulla = new ArrayList<>();
    private ArrayList <Passenger> boardedPassengersToColombo = new ArrayList<>();

    //Passenger Queue object implementation
    PassengerQueue trainQueue = new PassengerQueue();


    //main method.launching the primary stage
    public static void main(String[] args)
    {
        launch();
    }

    //primary stage
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //welcome message
        System.out.println("\n===== Welcome to the Passenger Management System ===== \n\n");

        //disabling the Mongodb log info
        Logger mongodbLogger = Logger.getLogger("org.mongodb.driver");
        mongodbLogger.setLevel(Level.SEVERE);

        //connecting to the database,collection
        MongoClient mongoClient = new MongoClient("localhost", 27017);//connecting to mongodb
        DB database = mongoClient.getDB("TrainSeatsBookingSystem");//connecting to the database

        //route 01 cmb-badulla
        DBCollection dataBaseToBadulla = database.getCollection("ReservationToBadulla");//connecting to the collection
        DBCursor cursor1 = dataBaseToBadulla.find();
        List<DBObject> dataBaseCollection1 = cursor1.toArray();//getting objects to a list

        //by this for loop implementing the passenger objects from the course work one and adding them into an array list
        for (DBObject objectsInArray : dataBaseCollection1)
        {
            Passenger customer = new Passenger();
            customer.setCustomerName(objectsInArray.get("customerName").toString());
            customer.setCustomerId(objectsInArray.get("NIC").toString());
            customer.setBookedSeat(objectsInArray.get("seat").toString());
            storedCustomerObjectsToBadulla.add(customer);
        }


        //route 02 badulla-cmb
        DBCollection dataBaseToColombo = database.getCollection("ReservationToColombo");//connecting to the collection
        DBCursor cursor2 = dataBaseToColombo.find();//taking the database data into object
        List<DBObject> dataBaseCollection2 = cursor2.toArray();//converting object to a list

        //by this for loop implementing the passenger objects from the course work one and adding them into an array list
        for (DBObject objectsInArray : dataBaseCollection2)
        {
            Passenger customer = new Passenger();
            customer.setCustomerName(objectsInArray.get("customerName").toString());
            customer.setCustomerId(objectsInArray.get("NIC").toString());
            customer.setBookedSeat(objectsInArray.get("seat").toString());
            storedCustomerObjectsToColombo.add(customer);
        }

        //checking if there are stored data files available.if it is deletes them before starting the methods.
        //implemented this method because when running the whole project again to deletes the previous time data
        File fileToColombo = new File("SimulationReportColomboRoute.txt");
        File fileToBadulla = new File("SimulationReportBadullaRoute.txt");

        if(fileToColombo.exists())
        {
            fileToColombo.delete();
        }
        if(fileToBadulla.exists())
        {
            fileToBadulla.delete();
        }

        //menu options
        while (true)
        {
            System.out.println("| Select the Option and Enter the Letter |\n");
            System.out.println("E - To Mark the Entrance to the Station");
            System.out.println("A - Add Passengers to the Train Queue");
            System.out.println("V - To View the Train Queue");
            System.out.println("D - Delete Passenger from the Train Queue");
            System.out.println("S - Store Train Queue Data");
            System.out.println("L - Load Train Queue Data");
            System.out.println("R - Run the Simulation and Produce a Report");
            System.out.println("Q - To Quit the Program\n");

            //user choice
            System.out.print("Enter the Letter: ");
            Scanner scanner = new Scanner(System.in);
            String userChoice = scanner.next();
            userChoice = userChoice.toUpperCase();
            System.out.println();

            //checking the user input and calling the methods
            switch (userChoice)
            {
                case "E":
                    markEntrance();
                    break;

                case "A":
                    addPassengerToQueue();
                    break;

                case "V":
                    viewTrainQueue();
                    break;

                case "D":
                    deletePassengerFromQueue();
                    break;

                case "S":
                    storeData();
                    break;

                case "L":
                    loadData();
                    break;

                case "R":
                    simulationAndReport();
                    break;

                case "Q":
                    //exits the system
                    System.out.println("Program End");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Input. Select and Re-Enter the Option\n\n");
            }
        }
    }

    //this method of get the user attendance to the station by the booked seat details in the coursework one
    //in here marked customers will be added to the waiting room lists according to the route
    private void markEntrance()
    {
        //---Route Selection Window--//
        //window select the route
        Stage entranceRouteStage = new Stage();//stage,pane,scene,stage title,grid pane implementing
        entranceRouteStage.setTitle("Station Select Window");
        BorderPane borderPaneInRouteSelection = new BorderPane();
        Scene routeSelectionScene = new Scene(borderPaneInRouteSelection, 400, 200);
        GridPane gridPaneInRouteSelection = new GridPane();
        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

        //Display Labels
        Label labelInRouteSelection = new Label("Select the Station: ");
        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
        gridPaneInRouteSelection.add(labelInRouteSelection, 0, 3);

        //radio buttons for Route selection
        ToggleGroup toggleGroupInRouteSelection= new ToggleGroup();

        RadioButton colomboBtn = new RadioButton("Colombo");
        colomboBtn.setStyle("-fx-padding:20 0 10 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(colomboBtn, 0, 5);

        RadioButton badullaBtn = new RadioButton("Badulla");
        badullaBtn.setStyle("-fx-padding:10 0 0 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(badullaBtn, 0, 6);

        badullaBtn.setToggleGroup(toggleGroupInRouteSelection);
        colomboBtn.setToggleGroup(toggleGroupInRouteSelection);

        //confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:70px;-fx-border-color:#808080");

        //back button
        Button cancelButton = new Button("Close");
        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

        //button aligning.
        HBox hBoxInRoteSelection = new HBox(5);
        hBoxInRoteSelection .setStyle("-fx-padding: 0 0 10 0");
        borderPaneInRouteSelection.setBottom(hBoxInRoteSelection );
        hBoxInRoteSelection .setAlignment(Pos.CENTER);
        hBoxInRoteSelection .getChildren().addAll(cancelButton,confirmButton);

        //route selection confirm button action
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //checking weather the one of the button is selected. if it is executing these codes
                if (colomboBtn.isSelected() || badullaBtn.isSelected())
                {
                    //window to mark the attendance by user
                    Stage entranceStage = new Stage();
                    entranceStage.setTitle("Station Entrance Window");
                    BorderPane borderPaneInEntrance = new BorderPane();
                    Scene entranceScene = new Scene(borderPaneInEntrance, 660, 700);
                    entranceScene.getStylesheets().add("styles.css");//style sheet link

                    GridPane gridPane = new GridPane();
                    gridPane.setId("gridPaneInWaitingRoom");
                    borderPaneInEntrance.setLeft(gridPane);
                    gridPane.setPadding(new Insets(10, 60, 10, 20));
                    gridPane.setVgap(15);
                    gridPane.setHgap(20);

                    //vertical box for user instructions
                    VBox vBoxToInstructions = new VBox(10);
                    borderPaneInEntrance.setTop(vBoxToInstructions);
                    vBoxToInstructions.setAlignment(Pos.TOP_LEFT);
                    vBoxToInstructions.setPadding(new Insets(10));

                    //user instructions in top of the window
                    Label userInstructions = new Label("Welcome To the Station\n\n");
                    Label userInstructionsOne = new Label("- In Here Displays Seats Booked Passengers .");
                    Label userInstructionsTwo = new Label("- Move the Mouse Pointer to the Seat Number to View the Customer Details.");
                    Label userInstructionsThree = new Label("- Click On the Booked Seat Number to Mark the Attendance");
                    userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                    userInstructionsThree.setStyle("-fx-padding: 0 0 10 0;");
                    vBoxToInstructions.getChildren().addAll(userInstructions, userInstructionsOne, userInstructionsTwo, userInstructionsThree);

                    //array list for hold the passengers buttons which is implementing in the code
                    ArrayList<Button> bookedCustomers = new ArrayList<>();

                    //if colombo-badulla radio button is selected execute these codes
                    if (colomboBtn.isSelected())
                    {
                        //count for the button arrangements
                        int countForCol = 0;
                        int countForRow = 0;
                        //loop for button implementing and button actions. loop size is Passenger object list size
                        //which was implemented at the beginning of the code
                        for (int i = 0; i < storedCustomerObjectsToBadulla.size(); i++)
                        {
                            //changing the row count
                            if (countForCol >= 6)
                            {
                                countForRow++;
                                countForCol = 0;
                            }

                            //buttons implementing according to the count.seat number getting from accessing the object array list
                            //(by the index of thr for loop) which was implemented at the beginning of the code
                            Button entranceButton = new Button(storedCustomerObjectsToBadulla.get(i).getBookedSeat());
                            entranceButton.setId("waitingRoomLabel");//setting id for the button for set styles
                            bookedCustomers.add(entranceButton);//adding the implemented button to bookedCustomers array list for actions

                            //tooltip using to display the customer details when hover the buttons.
                            //customer details are getting by the passenger object array list which was implemented at the beginning of the code.
                            Tooltip tooltip = new Tooltip("Name: " + storedCustomerObjectsToBadulla.get(i).getCustomerName() + "\nN.I.C: " + storedCustomerObjectsToBadulla.get(i).getCustomerId());
                            entranceButton.setTooltip(tooltip);

                            gridPane.add(entranceButton, countForCol, countForRow); // adding the buttons to gridPane
                            countForCol++;//column count increment
                        }

                        //window button(seat) on click actions.
                        // getting the button from the array list which was created above to store implemented buttons
                        for (Button buttonInList : bookedCustomers)
                        {
                            //button on click
                            buttonInList.setOnMouseClicked(new EventHandler<MouseEvent>()
                            {
                                @Override
                                public void handle(MouseEvent event)
                                {
                                    //loop for check the selected button from the passenger object list
                                    for(int i=0;i<storedCustomerObjectsToBadulla.size();i++)
                                    {
                                        //comparing the selected button text(seat no) and the passenger object seat numbers
                                        if(buttonInList.getText().equals(storedCustomerObjectsToBadulla.get(i).getBookedSeat()))
                                        {
                                            //when matched the passenger object seat number taking the index of it
                                            int indexOfPassengerObject = storedCustomerObjectsToBadulla.indexOf(storedCustomerObjectsToBadulla.get(i));
                                            //adding the matched passenger object to waiting room list
                                            waitingRoomToBadulla.add(storedCustomerObjectsToBadulla.get(indexOfPassengerObject));
                                            //removing the matched passenger object from the passenger array which was at the top
                                            storedCustomerObjectsToBadulla.remove(indexOfPassengerObject);
                                            buttonInList.setDisable(true);//disabling the button
                                        }
                                    }
                                }
                            });
                        }
                    }
                    //if badulla-colombo radio button is selected execute these codes
                    else
                    {
                        //count for the button arrangements
                        int countForCol = 0;
                        int countForRow = 0;
                        //loop for button implementing and button actions. loop size is Passenger object list size
                        //which was implemented at the beginning of the code
                        for (int i = 0; i < storedCustomerObjectsToColombo.size(); i++)
                        {
                            //changing the row count
                            if (countForCol >= 6)
                            {
                                countForRow++;
                                countForCol = 0;
                            }

                            //buttons implementing according to the count.seat number getting from accessing the object array list
                            //(by the index of thr for loop) which was implemented at the beginning of the code
                            Button entranceButton = new Button(storedCustomerObjectsToColombo.get(i).getBookedSeat());
                            entranceButton.setId("waitingRoomLabel");//setting id for the button for set styles
                            bookedCustomers.add(entranceButton);//adding the implemented button to bookedCustomers array list for actions

                            //tooltip using to display the customer details when hover the buttons.
                            //customer details are getting by the passenger object array list which was implemented at the beginning of the code.
                            Tooltip tooltip = new Tooltip("Name: " + storedCustomerObjectsToColombo.get(i).getCustomerName() + "\nN.I.C: " + storedCustomerObjectsToColombo.get(i).getCustomerId());
                            entranceButton.setTooltip(tooltip);

                            gridPane.add(entranceButton, countForCol, countForRow); // adding the buttons to gridPane
                            countForCol++; //column count increment
                        }

                        //window button(seat) on click actions.
                        // getting the button from the array list which was created above to store implemented buttons
                        for (Button buttonInList : bookedCustomers)
                        {
                            //button on click
                            buttonInList.setOnMouseClicked(new EventHandler<MouseEvent>()
                            {
                                @Override
                                public void handle(MouseEvent event)
                                {
                                    //loop for check the selected button from the passenger object list
                                    for(int i=0;i<storedCustomerObjectsToColombo.size();i++)
                                    {
                                        //comparing the selected button text(seat no) and the passenger object seat numbers
                                        if(buttonInList.getText().equals(storedCustomerObjectsToColombo.get(i).getBookedSeat()))
                                        {
                                            //when matched the passenger object seat number taking the index of it
                                            int indexOfPassengerObject = storedCustomerObjectsToColombo.indexOf(storedCustomerObjectsToColombo.get(i));
                                            //adding the matched passenger object to waiting room list
                                            waitingRoomToColombo.add(storedCustomerObjectsToColombo.get(indexOfPassengerObject));
                                            //removing the matched passenger object from the passenger array which was at the top
                                            storedCustomerObjectsToColombo.remove(indexOfPassengerObject);
                                            buttonInList.setDisable(true);//disabling the button
                                        }
                                    }
                                }
                            });
                        }
                    }

                    //Implementing the close button
                    HBox hBox = new HBox(10);
                    borderPaneInEntrance.setBottom(hBox);
                    hBox.setAlignment(Pos.BOTTOM_CENTER);
                    hBox.setPadding(new Insets(10));

                    Button closeButton = new Button("Close"); // button implementation
                    closeButton.setId("closeButton");//set id for styles
                    hBox.getChildren().addAll(closeButton);

                    //close button on action
                    closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            //closing the opened windows
                            entranceRouteStage.close();
                            entranceStage.close();
                        }


                    });
                    //entrance marking window scene set and show
                    entranceStage.setScene(entranceScene);
                    entranceStage.showAndWait();
                }

                //if any radio button is not selected displays it the route selection window
                else
                {
                    Label labelInRouteSelection = new Label("Please Select a Station");
                    labelInRouteSelection.setStyle("-fx-padding: 15 0 5 70;-fx-font-size:12px;-fx-text-fill:red;");
                    gridPaneInRouteSelection.add(labelInRouteSelection, 0, 7);
                }
            }
        });

        //route selection window close button action
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
       {
            @Override
            public void handle(MouseEvent event)
            {
                //--Route Selection Closed Alert popup Window--//
                //opens a window(alert) and confirming to close the window
                Stage bookingWindowClosePopupStage = new Stage();
                bookingWindowClosePopupStage.setTitle("Alert");
                BorderPane bookingWindowClosePopupPane = new BorderPane();
                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                VBox vBox = new VBox(10);
                bookingWindowClosePopupPane.setCenter(vBox);
                vBox.setAlignment(Pos.CENTER);

                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                noSeatAlert.setStyle("-fx-font-size:15px");

                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                //alert popup close button.In here closed the opened windows
                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        entranceRouteStage.close();//closing the windows
                        bookingWindowClosePopupStage.close();
                    }
                });

                //alert cancel button action
                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        //closing the alert window
                         bookingWindowClosePopupStage.close();
                     }
                });

                //set the scene and show the alert window
                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                bookingWindowClosePopupStage.showAndWait();
             }
        });

        //route selection window scene set and show
        entranceRouteStage.setScene(routeSelectionScene);
        entranceRouteStage.showAndWait();
    }

    //this method for add customers from the waiting to the train queue according to the route.
    //in here previews the waiting room list and after adding this method will previewed the train queue too
    private void addPassengerToQueue()
    {
        //---Route Selection Window--//
        //window select the route
        Stage addPassengerRouteStage = new Stage();//stage,pane,scene,stage title,grid pane implementing
        addPassengerRouteStage.setTitle("Station Selection Window");
        BorderPane borderPaneInRouteSelection = new BorderPane();
        Scene routeSelectionScene = new Scene(borderPaneInRouteSelection, 400, 200);
        GridPane gridPaneInRouteSelection = new GridPane();
        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

        //Display Labels
        Label labelInRouteSelection = new Label("Select the Station: ");
        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
        gridPaneInRouteSelection.add(labelInRouteSelection, 0, 3);

        //radio buttons for Route selection
        ToggleGroup toggleGroupInRouteSelection= new ToggleGroup();

        RadioButton colomboBtn = new RadioButton("Colombo");
        colomboBtn.setStyle("-fx-padding:20 0 10 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(colomboBtn, 0, 5);

        RadioButton badullaBtn = new RadioButton("Badulla");
        badullaBtn.setStyle("-fx-padding:10 0 0 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(badullaBtn, 0, 6);

        badullaBtn.setToggleGroup(toggleGroupInRouteSelection);
        colomboBtn.setToggleGroup(toggleGroupInRouteSelection);

        //confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:70px;-fx-border-color:#808080");

        //back button
        Button cancelButton = new Button("Close");
        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

        //button aligning.
        HBox hBoxInRoteSelection = new HBox(5);
        hBoxInRoteSelection .setStyle("-fx-padding: 0 0 10 0");
        borderPaneInRouteSelection.setBottom(hBoxInRoteSelection );
        hBoxInRoteSelection .setAlignment(Pos.CENTER);
        hBoxInRoteSelection .getChildren().addAll(cancelButton,confirmButton);

        //route selection window confirm button action
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //checking weather is one of the route button is selected. if it is this code will execute
                if (colomboBtn.isSelected() || badullaBtn.isSelected())
                {
                    //checking weather the colombo-badulla route is selected and queue size is full or not.
                    //if queue size is full displays queue full alert
                    if ((colomboBtn.isSelected()) && trainQueue.isFullToBadulla())
                    {
                        Stage addPassengerFullStage = new Stage();
                        addPassengerFullStage.setTitle("Waiting Room");
                        BorderPane waitingWindowClosePopupPane = new BorderPane();
                        Scene scene = new Scene(waitingWindowClosePopupPane, 250, 150);
                        VBox vBox = new VBox(10);
                        waitingWindowClosePopupPane.setCenter(vBox);
                        vBox.setAlignment(Pos.CENTER);

                        Label noSeatAlert = new Label("Train Queue is Full");//label
                        noSeatAlert.setStyle("-fx-font-size:15px");

                        Button waitingWindowClosePopupButton = new Button("Close");//yes button
                        waitingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                        vBox.getChildren().addAll(noSeatAlert, waitingWindowClosePopupButton);

                        //popup close button.In here closed the pop up window
                        waitingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                addPassengerFullStage.close();

                            }
                        });

                        //popup window show and setting the scene
                        addPassengerFullStage.setScene(scene);
                        addPassengerFullStage.showAndWait();
                    }
                    //checking weather the badulla-colombo route is selected and queue size is full or not.
                    //if queue size is full displays queue full alert
                    else if ((badullaBtn.isSelected()) && trainQueue.isFullToColombo())
                    {
                        Stage addPassengerFullLStage = new Stage();
                        addPassengerFullLStage.setTitle("Alert");
                        BorderPane waitingWindowClosePopupPane = new BorderPane();
                        Scene scene = new Scene(waitingWindowClosePopupPane, 250, 150);
                        VBox vBox = new VBox(10);
                        waitingWindowClosePopupPane.setCenter(vBox);
                        vBox.setAlignment(Pos.CENTER);

                        Label noSeatAlert = new Label("Train Queue is Full");//label
                        noSeatAlert.setStyle("-fx-font-size:15px");

                        Button waitingWindowClosePopupButton = new Button("Close");//yes button
                        waitingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                        vBox.getChildren().addAll(noSeatAlert, waitingWindowClosePopupButton);

                        //popup close button.In here closed the popup window
                        waitingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                addPassengerFullLStage.close();
                            }
                        });
                        //popup window show and setting the scene
                        addPassengerFullLStage.setScene(scene);
                        addPassengerFullLStage.showAndWait();
                    }
                    //if queue is not full will execute this code
                    else
                    {
                        //stage implementing to preview the waiting room passengers
                        Stage addPassengerStage = new Stage();
                        addPassengerStage.setTitle("Waiting Room Window");
                        BorderPane borderPaneInWaitingRoom = new BorderPane();
                        Scene waitingRoomScene = new Scene(borderPaneInWaitingRoom, 690, 700);
                        waitingRoomScene.getStylesheets().add("styles.css");

                        GridPane gridPane = new GridPane();
                        gridPane.setId("gridPaneInWaitingRoom");
                        borderPaneInWaitingRoom.setLeft(gridPane);
                        gridPane.setPadding(new Insets(10, 60, 10, 20));
                        gridPane.setVgap(15);
                        gridPane.setHgap(20);

                        // vertical box for instructions to user
                        VBox vBoxToInstructions = new VBox(10);
                        borderPaneInWaitingRoom.setTop(vBoxToInstructions);
                        vBoxToInstructions.setAlignment(Pos.TOP_LEFT);
                        vBoxToInstructions.setPadding(new Insets(10));

                        //user instructions
                        Label userInstructions = new Label("Passenger Waiting Room\n\n");
                        Label userInstructionsOne = new Label("- In Here Displays Passengers in the Waiting Room.");
                        Label userInstructionsTwo = new Label("- Move the Mouse Pointer to the Seat Number to View the Customer Details.");
                        Label userInstructionsThree = new Label("- Click 'Add Passengers to Queue Button' to Add Passengers to the Train Queue.");
                        userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                        userInstructionsThree.setStyle("-fx-padding: 0 0 10 0;");

                        vBoxToInstructions.getChildren().addAll(userInstructions, userInstructionsOne, userInstructionsTwo, userInstructionsThree);

                        //if cololombo-badulla route is selected this code will execute and preview the passengers by the labels
                        if (colomboBtn.isSelected())
                        {
                            //count for rows and columns
                            int countForCol = 0;
                            int countForRow = 0;
                            //this loop will loops to the size of the waiting room
                            for (int i = 0; i < waitingRoomToBadulla.size(); i++)
                            {
                                //row changing
                                if (countForCol >= 6)
                                {
                                    countForRow++;
                                    countForCol = 0;
                                }

                                //implementing the labels for represent the passengers and previewing the customer data by accessing the
                                //passenger objects from the waiting room list
                                Label waitingRoomLabel = new Label(waitingRoomToBadulla.get(i).getBookedSeat());
                                waitingRoomLabel.setId("waitingRoomLabel");//setting id for the styles

                                //tool tip for preview customer information
                                Tooltip tooltip = new Tooltip("Name: " + waitingRoomToBadulla.get(i).getCustomerName() + "\nN.I.C: " + waitingRoomToBadulla.get(i).getCustomerId());
                                waitingRoomLabel.setTooltip(tooltip);

                                gridPane.add(waitingRoomLabel, countForCol, countForRow); // adding to the gridPane
                                countForCol++;//column count

                            }
                        }
                        //if badulla-colombo route is selected this code will execute and preview the passengers by the labels
                        else
                        {
                            //count for rows and columns
                            int countForCol = 0;
                            int countForRow = 0;
                            //this loop will loops to the size of the waiting room
                            for (int i = 0; i < waitingRoomToColombo.size(); i++)
                            {
                                //row changing
                                if (countForCol >= 6)
                                {
                                    countForRow++;
                                    countForCol = 0;
                                }

                                //implementing the labels for represent the passengers and previewing the customer data by accessing the
                                //passenger objects from the waiting room list
                                Label waitingRoomLabel = new Label(waitingRoomToColombo.get(i).getBookedSeat());
                                waitingRoomLabel.setId("waitingRoomLabel");//setting id foe styles

                                //tool tip for preview customer information
                                Tooltip tooltip = new Tooltip("Name: " + waitingRoomToColombo.get(i).getCustomerName() + "\nN.I.C: " + waitingRoomToColombo.get(i).getCustomerId());
                                waitingRoomLabel.setTooltip(tooltip);

                                gridPane.add(waitingRoomLabel, countForCol, countForRow); // adding to the gridPane
                                countForCol++;//column count increment
                            }
                        }

                        //Implementing the done and close button
                        HBox hBox = new HBox(10);
                        borderPaneInWaitingRoom.setBottom(hBox);
                        hBox.setAlignment(Pos.BOTTOM_CENTER);
                        hBox.setPadding(new Insets(10));

                        Button addButton = new Button("Add Passengers to Queue"); // button implementation
                        addButton.setId("closeButton");

                        Button closeButton = new Button("Close"); // button implementation
                        closeButton.setId("closeButton");
                        hBox.getChildren().addAll(closeButton, addButton);

                        //this condition will check the sizes of the waiting room to disable the add passengers button
                        if((colomboBtn.isSelected() && waitingRoomToBadulla.size()==0) || (badullaBtn.isSelected() && waitingRoomToColombo.size()==0))
                        {
                            addButton.setDisable(true);
                        }

                        //waiting room close button action.
                        closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                //closing the windows
                                addPassengerStage.close();
                                addPassengerRouteStage.close();

                            }
                        });

                        //waiting room 'add passengers to queue' button action
                        addButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                //adding to train queue if colombo button is selected
                                if (colomboBtn.isSelected())
                                {
                                    //condition for random number re generate if generated number is larger than the availability
                                    while (true)
                                    {
                                        //generating a random number(1-6)
                                        int randomNumber = ((int) (Math.random() * 6)) + 1;

                                        //checking the generated random is <= to the waiting room passengers count
                                        //and is <= available space in the train queue. if not re generating it
                                        if (randomNumber <= waitingRoomToBadulla.size() && (randomNumber<=(21-trainQueue.getMaxLengthToBadulla())))
                                        {
                                            //adding the waiting room passengers to the train queue
                                            for (int i = 0; i < randomNumber; i++)
                                            {
                                                //always getting the 0 index value because after removing its re indexing
                                                trainQueue.addToBadulla(waitingRoomToBadulla.get(0));
                                                waitingRoomToBadulla.remove(0);
                                            }
                                            break;//breaks the while loop
                                        }

                                    }
                                }
                                //adding to train queue if badulla button is selected
                                else
                                {
                                    //condition for random number re generate if generated number is larger than the availability
                                    while (true)
                                    {
                                        //generating a random number(1-6)
                                        int randomNumber = ((int) (Math.random() * 6)) + 1;

                                        //checking the generated random is <= to the waiting room passengers count
                                        //and is <= available space in the train queue. if not re generating it
                                        if (randomNumber <= waitingRoomToColombo.size() && (randomNumber<=(21-trainQueue.getMaxLengthToColombo())))
                                        {
                                            //adding the waiting room passengers to the train queue
                                            for (int i = 0; i < randomNumber; i++)
                                            {
                                                //always getting the 0 index value because after removing its re indexing
                                                trainQueue.addToColombo(waitingRoomToColombo.get(0));
                                                waitingRoomToColombo.remove(0);
                                            }
                                            break;//breaks the while loop
                                        }
                                    }
                                }

                                //train queue preview stage implementing
                                addPassengerStage.setTitle("Train Queue Window");
                                BorderPane borderPaneInTrainQueue = new BorderPane();
                                Scene trainQueueScene = new Scene(borderPaneInTrainQueue, 690, 700);
                                trainQueueScene.getStylesheets().add("styles.css");

                                //vertical box to user instuctions
                                VBox vBoxToInstructions = new VBox(10);
                                borderPaneInTrainQueue.setTop(vBoxToInstructions);
                                vBoxToInstructions.setAlignment(Pos.TOP_LEFT);
                                vBoxToInstructions.setPadding(new Insets(10));

                                //user instructions
                                Label userInstructions = new Label("Train Queue\n\n");
                                Label userInstructions1 = new Label("- In Here Displays Passengers in the Train Queue.");
                                Label userInstructions2 = new Label("- Move the Mouse Pointer to the Seat Number to View the Customer Details.");
                                userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                                userInstructions2.setStyle("-fx-padding: 0 0 10 0;");

                                vBoxToInstructions.getChildren().addAll(userInstructions, userInstructions1, userInstructions2);

                                GridPane gridPaneInQueue = new GridPane();
                                gridPaneInQueue.setId("gridPaneInQueue");
                                borderPaneInTrainQueue.setCenter(gridPaneInQueue);
                                gridPaneInQueue.setPadding(new Insets(10, 20, 10, 20));
                                gridPaneInQueue.setVgap(40);
                                gridPaneInQueue.setHgap(20);

                                //if colombo-badulla button is selected this code will execute
                                if (colomboBtn.isSelected())
                                {
                                    //getting the size of the train queue to badulla
                                    int sizeOfQueue = trainQueue.getMaxLengthToBadulla();
                                    //copying train queue objects to a temporary list
                                    ArrayList<Passenger> passengerQueueObject = trainQueue.getObjectAvailableToBadulla();

                                    //loop for the buttons implementation
                                    for (int columnIndex = 0; columnIndex < 6; columnIndex++)
                                    {
                                        for (int rowIndex = 0; rowIndex < 7; rowIndex++)
                                        {
                                            //executes until the size of the queue to display the name
                                            if (sizeOfQueue > 0)
                                            {
                                                //if the selected value is null displays an empty label
                                                if (passengerQueueObject.get(0) == null)
                                                {
                                                    sizeOfQueue--;
                                                    Label labelForCustomerDisplay = new Label("Empty");
                                                    labelForCustomerDisplay.setId("waitingRoomLabel");
                                                    labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");

                                                    gridPaneInQueue.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                                    passengerQueueObject.remove(0);

                                                }
                                                //if not null displays the passenger name on the label
                                                else
                                                {
                                                    sizeOfQueue--;//decrease the size of the count variable
                                                    //implementing the lable and displaying the customer seat by accessing the object data
                                                    Label labelForCustomerDisplay = new Label(passengerQueueObject.get(0).getBookedSeat());
                                                    labelForCustomerDisplay.setId("waitingRoomLabel");

                                                    //tooltip for display customer information
                                                    Tooltip tooltip = new Tooltip("Name: " + passengerQueueObject.get(0).getCustomerName() + "\nN.I.C: " + passengerQueueObject.get(0).getCustomerId());
                                                    labelForCustomerDisplay.setTooltip(tooltip);

                                                    gridPaneInQueue.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                                    passengerQueueObject.remove(0);//removing from the array
                                                }
                                            }
                                            //when count ==0 means waiting room is empty. then displays empty labels by this else part
                                            else
                                            {
                                                Label labelForCustomerDisplay = new Label("Empty");
                                                labelForCustomerDisplay.setId("waitingRoomLabel");
                                                labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");
                                                gridPaneInQueue.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                            }
                                        }
                                    }
                                }
                                //if badulla-colombo button is selected this code will execute
                                else
                                {
                                    //getting the size of the train queue to badulla
                                    int sizeOfQueue = trainQueue.getMaxLengthToColombo();
                                    //copying train queue objects to a temporary list
                                    ArrayList<Passenger> passengerQueueObject = trainQueue.getObjectAvailableToColombo();

                                    //loop for the buttons implementation
                                    for (int columnIndex = 0; columnIndex < 6; columnIndex++)
                                    {
                                        for (int rowIndex = 0; rowIndex < 7; rowIndex++)
                                        {
                                            //executes until the size of the queue to display the name
                                            if (sizeOfQueue > 0)
                                            {
                                                sizeOfQueue--;//decrease the size of the count variable
                                                //implementing the lable and displaying the customer seat by accessing the object data
                                                Label labelForCustomerDisplay = new Label(passengerQueueObject.get(0).getBookedSeat());
                                                labelForCustomerDisplay.setId("waitingRoomLabel");

                                                //tooltip for display customer information
                                                Tooltip tooltip = new Tooltip("Name: " + passengerQueueObject.get(0).getCustomerName() + "\nN.I.C: " + passengerQueueObject.get(0).getCustomerId());
                                                labelForCustomerDisplay.setTooltip(tooltip);

                                                gridPaneInQueue.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                                passengerQueueObject.remove(0);//remove from the list

                                            }
                                            //when count ==0 means waiting room is empty. then displays empty labels by this else part
                                            else
                                            {
                                                Label labelForCustomerDisplay = new Label("Empty");
                                                labelForCustomerDisplay.setId("waitingRoomLabel");
                                                labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");
                                                gridPaneInQueue.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                            }
                                        }
                                    }
                                }

                                //Implementing the done and close button
                                HBox hBox = new HBox(10);
                                borderPaneInTrainQueue.setBottom(hBox);
                                hBox.setAlignment(Pos.BOTTOM_CENTER);
                                hBox.setPadding(new Insets(10));

                                Button closeButton = new Button("Close"); // button implementation
                                closeButton.setId("closeButton");
                                hBox.getChildren().addAll(closeButton);

                                //train queue close button action
                                closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                                {
                                    @Override
                                    public void handle(MouseEvent event)
                                    {
                                        //closing the opened windows
                                        addPassengerStage.close();
                                        addPassengerRouteStage.close();

                                    }
                                });

                                //setting the train queue scene to the waiting room stage
                                addPassengerStage.setScene(trainQueueScene);
                            }
                        });
                        //waiting room stage show and setting the scene
                        addPassengerStage.setScene(waitingRoomScene);
                        addPassengerStage.showAndWait();
                    }
                }
                //if route is not selected displays an alert
                else
                {
                    Label labelInRouteSelection = new Label("Please Select a Station");
                    labelInRouteSelection.setStyle("-fx-padding: 15 0 5 70;-fx-font-size:12px;-fx-text-fill:red;");
                    gridPaneInRouteSelection.add(labelInRouteSelection, 0, 7);
                }
            }
        });

        //route selection window cancel button action.
        //in here displays and alert and close the window
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //--Route Selection Closed Alert popup Window--//
                Stage bookingWindowClosePopupStage = new Stage();
                bookingWindowClosePopupStage.setTitle("Alert");
                BorderPane bookingWindowClosePopupPane = new BorderPane();
                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                VBox vBox = new VBox(10);
                bookingWindowClosePopupPane.setCenter(vBox);
                vBox.setAlignment(Pos.CENTER);

                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                noSeatAlert.setStyle("-fx-font-size:15px");

                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                //popup close button.In here closed the opened windows
                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                       addPassengerRouteStage.close();
                        bookingWindowClosePopupStage.close();
                    }
                });

                //alert cancel button action. this will close the alert popup
                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        bookingWindowClosePopupStage.close();
                    }
                });

                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                bookingWindowClosePopupStage.showAndWait();
            }
        });

        //route selection window stage show and setting the scene
        addPassengerRouteStage.setScene(routeSelectionScene);
        addPassengerRouteStage.showAndWait();
    }


//this method previews the waiting room, train queue and the boarded passengers
    private void viewTrainQueue()
    {
        //---Route Selection Window--//
        //window to select the route
        Stage viewRouteStage = new Stage();//stage,pane,scene,stage title,grid pane implementing
        viewRouteStage.setTitle("Station Select Window");
        BorderPane borderPaneInRouteSelection = new BorderPane();
        Scene routeSelectionScene = new Scene(borderPaneInRouteSelection, 400, 200);
        GridPane gridPaneInRouteSelection = new GridPane();
        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

        //Display Labels
        Label labelInRouteSelection = new Label("Select the Station: ");
        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
        gridPaneInRouteSelection.add(labelInRouteSelection, 0, 3);

        //radio buttons for Route selection
        ToggleGroup toggleGroupInRouteSelection = new ToggleGroup();

        RadioButton colomboBtn = new RadioButton("Colombo");
        colomboBtn.setStyle("-fx-padding:20 0 10 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(colomboBtn, 0, 5);

        RadioButton badullaBtn = new RadioButton("Badulla");
        badullaBtn.setStyle("-fx-padding:10 0 0 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(badullaBtn, 0, 6);

        badullaBtn.setToggleGroup(toggleGroupInRouteSelection);
        colomboBtn.setToggleGroup(toggleGroupInRouteSelection);

        //confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:70px;-fx-border-color:#808080");

        //back button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

        //button aligning.
        HBox hBoxInRoteSelection = new HBox(5);
        hBoxInRoteSelection.setStyle("-fx-padding: 0 0 10 0");
        borderPaneInRouteSelection.setBottom(hBoxInRoteSelection);
        hBoxInRoteSelection.setAlignment(Pos.CENTER);
        hBoxInRoteSelection.getChildren().addAll(cancelButton, confirmButton);

        //route selection window confirm button action
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //checking weather the buttons is selected. if it is this code will execute
                if (colomboBtn.isSelected() || badullaBtn.isSelected())
                {
                    //stage to view option select(waiting\queue\boarded)
                    Stage optionStage = new Stage();//stage,pane,scene,stage title,grid pane implementing
                    optionStage.setTitle("Option Selection Window");
                    BorderPane borderPane = new BorderPane();
                    Scene scene = new Scene(borderPane, 750, 300);
                    GridPane gridPane = new GridPane();
                    gridPane.setAlignment(Pos.CENTER);
                    gridPane.setHgap(5);
                    borderPane.setCenter(gridPane);
                    scene.getStylesheets().add("styles.css");

                    //vertical box for user instructions
                    VBox vBox = new VBox(10);
                    borderPane.setTop(vBox);
                    vBox.setAlignment(Pos.TOP_CENTER);
                    vBox.setPadding(new Insets(10));

                    //user instructions
                    Label userInstructions = new Label("Waiting Room | Train Queue |  Boarded Passengers\n\n");
                    Label userInstructionsOne = new Label("- To Display the Relevant Section Click the Relevant Buttons -");
                    userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                    userInstructionsOne.setStyle("-fx-padding: 0 0 10 0;");

                    vBox.getChildren().addAll(userInstructions, userInstructionsOne);

                    //buttons for the selection
                    Button waitingRoomBtn = new Button("Waiting Room");
                    waitingRoomBtn.setStyle("-fx-pref-width: 150px;-fx-font-size:11px;");
                    waitingRoomBtn.setId("closeButton");

                    Button trainQueueBtn = new Button("Train Queue");
                    trainQueueBtn.setStyle("-fx-pref-width: 150px;-fx-font-size:11px;");
                    trainQueueBtn.setId("closeButton");

                    Button boardedBtn = new Button("Boarded Passengers");
                    boardedBtn.setStyle("-fx-pref-width: 150px;-fx-font-size:11px;");
                    boardedBtn.setId("closeButton");

                    //buttons adding
                    gridPane.add(waitingRoomBtn, 5, 5);
                    gridPane.add(trainQueueBtn, 7, 5);
                    gridPane.add(boardedBtn, 9, 5);

                    //back button
                    Button cancelButton = new Button("Close");
                    cancelButton.setId("closeButton");

                    //button aligning.
                    HBox hBox = new HBox(5);
                    hBox.setStyle("-fx-padding: 0 0 10 0");
                    borderPane.setBottom(hBox);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(cancelButton);

                    //waiting room button action
                    waitingRoomBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            //stage for waiting room
                            Stage viewStage = new Stage();
                            viewStage.setTitle("Waiting Room");
                            BorderPane borderPaneInWaitingRoom = new BorderPane();
                            Scene waitingRoomScene = new Scene(borderPaneInWaitingRoom, 550, 600);
                            waitingRoomScene.getStylesheets().add("styles.css");

                            VBox vBoxToInstructions = new VBox(5);
                            borderPaneInWaitingRoom.setTop(vBoxToInstructions);
                            vBoxToInstructions.setAlignment(Pos.TOP_CENTER);
                            vBoxToInstructions.setPadding(new Insets(10));

                            //user instructions
                            Label userInstructions = new Label("| Passenger Waiting Room |\n\n");
                            Label userInstructionsOne = new Label("- This Section Previews the Passengers in the Waiting Room-");
                            Label userInstructionsThree = new Label("- Move the Mouse Pointer to the Seat Number to View the Customer Details-");

                            userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                            userInstructionsThree.setStyle("-fx-padding: 0 0 10 0;");

                            vBoxToInstructions.getChildren().addAll(userInstructions, userInstructionsOne, userInstructionsThree);
                            //grid pane for label adding
                            GridPane gridPane = new GridPane();
                            gridPane.setId("gridPaneInWaitingRoom");
                            gridPane.setStyle("-fx-pref-width: 560px;");
                            borderPaneInWaitingRoom.setLeft(gridPane);
                            gridPane.setPadding(new Insets(10, 20, 10, 20));
                            gridPane.setVgap(20);
                            gridPane.setHgap(10);

                            //if colombo button is implemented will execute this code
                            if (colomboBtn.isSelected())
                            {
                                //count for rows and coloumns
                                int countForCol = 0;
                                int countForRow = 0;
                                //this loop will loops to the size of the waiting room
                                for (int i = 0; i < waitingRoomToBadulla.size(); i++)
                                {
                                    //row changing
                                    if (countForCol >= 6)
                                    {
                                        countForRow++;
                                        countForCol = 0;
                                    }

                                    //implementing the labels for represent the passengers and previewing the customer data by accessing the
                                    //passenger objects from the waiting room list
                                    Label waitingRoomLabel = new Label(waitingRoomToBadulla.get(i).getBookedSeat());
                                    waitingRoomLabel.setId("waitingRoomLabel");

                                    //tool tip for preview customer information
                                    Tooltip tooltip = new Tooltip("Name: " + waitingRoomToBadulla.get(i).getCustomerName() + "\nN.I.C: " + waitingRoomToBadulla.get(i).getCustomerId());
                                    waitingRoomLabel.setTooltip(tooltip);

                                    gridPane.add(waitingRoomLabel, countForCol, countForRow); // adding to the gridPane
                                    countForCol++;//column count
                                }
                            }
                            //if badulla-colombo route is selected this code will execute and preview the passengers by the labels
                            else
                            {
                                //count for rows and columns
                                int countForCol = 0;
                                int countForRow = 0;
                                //this loop will loops to the size of the waiting room
                                for (int i = 0; i < waitingRoomToColombo.size(); i++)
                                {
                                    //row changing
                                    if (countForCol >= 6)
                                    {
                                        countForRow++;
                                        countForCol = 0;
                                    }

                                    //implementing the labels for represent the passengers and previewing the customer data by accessing the
                                    //passenger objects from the waiting room list
                                    Label waitingRoomLabel = new Label(waitingRoomToColombo.get(i).getBookedSeat());
                                    waitingRoomLabel.setId("waitingRoomLabel");

                                    //tool tip for preview customer information
                                    Tooltip tooltip = new Tooltip("Name: " + waitingRoomToColombo.get(i).getCustomerName() + "\nN.I.C: " + waitingRoomToColombo.get(i).getCustomerId());
                                    waitingRoomLabel.setTooltip(tooltip);

                                    gridPane.add(waitingRoomLabel, countForCol, countForRow); // adding to the gridPane
                                    countForCol++;//column count increment
                                }
                            }

                            //waiting room close button
                            Button cancelButton = new Button("Close");
                            cancelButton.setId("closeButton");

                            //button aligning.
                            HBox hBox = new HBox(5);
                            hBox.setStyle("-fx-padding: 0 0 10 0");
                            borderPaneInWaitingRoom.setBottom(hBox);
                            hBox.setAlignment(Pos.CENTER);
                            hBox.getChildren().addAll(cancelButton);

                            //close button action
                            cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    viewStage.close();//close the waiting room
                                }
                            });

                            //setting the scene and show thw waiting room
                            viewStage.setScene(waitingRoomScene);
                            viewStage.showAndWait();
                        }
                    });

                    //train queue button action
                    trainQueueBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            //train queue preview stage implementing
                            Stage queueStage = new Stage();
                            queueStage.setTitle("Train Queue");
                            BorderPane borderPaneInQueue = new BorderPane();
                            Scene waitingRoomScene = new Scene(borderPaneInQueue, 570, 600);
                            waitingRoomScene.getStylesheets().add("styles.css");

                            GridPane gridPane = new GridPane();
                            gridPane.setId("gridPaneInQueue");
                            borderPaneInQueue.setRight(gridPane);
                            gridPane.setPadding(new Insets(10, 20, 10, 20));
                            gridPane.setVgap(20);
                            gridPane.setHgap(10);

                            //vertical box to user instuctions
                            VBox vBoxToInstructions = new VBox(5);
                            borderPaneInQueue.setTop(vBoxToInstructions);
                            vBoxToInstructions.setAlignment(Pos.TOP_CENTER);
                            vBoxToInstructions.setPadding(new Insets(10));

                            //user instructions
                            Label userInstructions = new Label("| Train Queue |\n\n");
                            Label userInstructionsOne = new Label("- This Section Previews the Passengers in the Train Queue -");
                            Label userInstructionsThree = new Label("- Move the Mouse Pointer to the Seat Number to View the Customer Details -");

                            userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                            userInstructionsThree.setStyle("-fx-padding: 0 0 10 0;");

                            vBoxToInstructions.getChildren().addAll(userInstructions, userInstructionsOne, userInstructionsThree);

                            //if colombo-badulla button is selected this code will execute
                            if (colomboBtn.isSelected())
                            {
                                //getting the size of the train queue to badulla
                                int sizeOfQueue = trainQueue.getMaxLengthToBadulla();
                                //copying train queue objects to a temporary list
                                ArrayList<Passenger> passengerQueueObject = trainQueue.getObjectAvailableToBadulla();

                                //loop for the buttons implementation
                                for (int columnIndex = 0; columnIndex < 6; columnIndex++)
                                {
                                    for (int rowIndex = 0; rowIndex < 7; rowIndex++)
                                    {
                                        //executes until the size of the queue to display the name
                                        if (sizeOfQueue > 0)
                                        {
                                            //if the selected value is null displays an empty label
                                            if (passengerQueueObject.get(0) == null)
                                            {
                                                sizeOfQueue--;
                                                Label labelForCustomerDisplay = new Label("Empty");
                                                labelForCustomerDisplay.setId("waitingRoomLabel");
                                                labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");

                                                gridPane.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                                passengerQueueObject.remove(0);

                                            }
                                            //if not null displays the passenger name on the label
                                            else
                                            {
                                                sizeOfQueue--;//decrease the size of the count variable
                                                //implementing the lable and displaying the customer seat by accessing the object data
                                                Label labelForCustomerDisplay = new Label(passengerQueueObject.get(0).getBookedSeat());
                                                labelForCustomerDisplay.setId("waitingRoomLabel");

                                                //tooltip for display customer information
                                                Tooltip tooltip = new Tooltip("Name: " + passengerQueueObject.get(0).getCustomerName() + "\nN.I.C: " + passengerQueueObject.get(0).getCustomerId());
                                                labelForCustomerDisplay.setTooltip(tooltip);

                                                gridPane.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                                passengerQueueObject.remove(0);//removing from the array
                                            }
                                        }
                                        //when count ==0 means waiting room is empty. then displays empty labels by this else part
                                        else
                                        {
                                            Label labelForCustomerDisplay = new Label("Empty");
                                            labelForCustomerDisplay.setId("waitingRoomLabel");
                                            labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");
                                            gridPane.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                        }
                                    }
                                }
                            }
                            //if badulla-colombo button is selected this code will execute
                            else
                            {
                                //getting the size of the train queue to badulla
                                int sizeOfQueue = trainQueue.getMaxLengthToColombo();
                                //copying train queue objects to a temporary list
                                ArrayList<Passenger> passengerQueueObject = trainQueue.getObjectAvailableToColombo();

                                //loop for the buttons implementation
                                for (int columnIndex = 0; columnIndex < 6; columnIndex++)
                                {
                                    for (int rowIndex = 0; rowIndex < 7; rowIndex++)
                                    {
                                        //executes until the size of the queue to display the name
                                        if (sizeOfQueue > 0)
                                        {
                                            sizeOfQueue--;//decrease the size of the count variable
                                            //implementing the lable and displaying the customer seat by accessing the object data
                                            Label labelForCustomerDisplay = new Label(passengerQueueObject.get(0).getBookedSeat());
                                            labelForCustomerDisplay.setId("waitingRoomLabel");

                                            //tooltip for display customer information
                                            Tooltip tooltip = new Tooltip("Name: " + passengerQueueObject.get(0).getCustomerName() + "\nN.I.C: " + passengerQueueObject.get(0).getCustomerId());
                                            labelForCustomerDisplay.setTooltip(tooltip);

                                            gridPane.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                            passengerQueueObject.remove(0);//remove from the array
                                        }
                                        //when count ==0 means waiting room is empty. then displays empty labels by this else part
                                        else
                                        {
                                            Label labelForCustomerDisplay = new Label("Empty");
                                            labelForCustomerDisplay.setId("waitingRoomLabel");
                                            labelForCustomerDisplay.setStyle("-fx-background-color: #F0F0F0;-fx-font-size:14px;");
                                            gridPane.add(labelForCustomerDisplay, columnIndex, rowIndex); // adding to gridPane
                                        }
                                    }
                                }
                            }

                            //Implementing the done and close button
                            HBox hBox = new HBox(10);
                            borderPaneInQueue.setBottom(hBox);
                            hBox.setAlignment(Pos.BOTTOM_CENTER);
                            hBox.setPadding(new Insets(10));

                            Button closeButton = new Button("Close"); // button implementation
                            closeButton.setId("closeButton");
                            hBox.getChildren().addAll(closeButton);

                            //train queue close button action
                            closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                            {
                                @Override
                                public void handle(MouseEvent event)
                                {
                                    queueStage.close();//queue stage close
                                }
                            });

                            //setting the scene and show the train queue
                            queueStage.setScene(waitingRoomScene);
                            queueStage.showAndWait();
                        }
                    });

                    //boarded passengers button action
                    boardedBtn.setOnMouseClicked(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            //array list for hold the implemented buttons
                            ArrayList<Button> seats = new ArrayList<>();
                            //array lists for get the boarded passengers
                            ArrayList<Passenger> boardedCustomers = new ArrayList<>();

                            //according to the route selection copying the boarded passengers to the temp list
                            if (colomboBtn.isSelected())
                            {
                                boardedCustomers = (ArrayList<Passenger>) boardedPassengersToBadulla.clone();
                            }
                            else if (badullaBtn.isSelected())
                            {
                                boardedCustomers = (ArrayList<Passenger>) boardedPassengersToColombo.clone();
                            }

                            //--Boarded passengers preview window--//
                            //stage,pane,scene,stage title,grid pane implementing
                            Stage boardedStage = new Stage();
                            boardedStage.setTitle("Boarded Passengers Window");
                            BorderPane borderPane = new BorderPane();
                            borderPane.setId("boardingPane");
                            Scene scene = new Scene(borderPane, 550, 750);
                            scene.getStylesheets().add("styles.css");//styles adding

                            GridPane gridPaneLeft = new GridPane();
                            borderPane.setLeft(gridPaneLeft);
                            gridPaneLeft.setPadding(new Insets(10, 60, 10, 20));
                            gridPaneLeft.setVgap(15);
                            gridPaneLeft.setHgap(15);

                            GridPane gridPaneRight = new GridPane();
                            borderPane.setRight(gridPaneRight);
                            gridPaneRight.setPadding(new Insets(10, 20, 10, 60));
                            gridPaneRight.setVgap(15);
                            gridPaneRight.setHgap(15);

                            VBox vBoxToInstructions = new VBox(5);
                            borderPane.setTop(vBoxToInstructions);
                            vBoxToInstructions.setAlignment(Pos.TOP_CENTER);
                            vBoxToInstructions.setPadding(new Insets(10));

                            //user instructions
                            Label userInstructions = new Label("| Boarded Passengers |\n\n");
                            Label userInstructionsOne = new Label("- This Section Previews the Passengers in the Train -");
                            Label userInstructionsThree = new Label("- Move the Mouse Pointer to the Seat Number When Boarded to View Details -");

                            userInstructions.setStyle("-fx-font-size:18px;-fx-font-weight:900;");
                            userInstructionsThree.setStyle("-fx-padding: 0 0 10 0;");

                            vBoxToInstructions.getChildren().addAll(userInstructions, userInstructionsOne, userInstructionsThree);

                            //loop for column A button implementation
                            int countA = 0;
                            for (int i = 1; i <= 10; i++) //row count
                            {
                                countA++; // count for button number
                                String countToString = Integer.toString(countA); // count to string for button text
                                Button button = new Button(countToString + "A"); // button implementation
                                button.setId("waitingRoomLabel");
                                seats.add(button); // adding to a button list
                                gridPaneLeft.add(button, 0, i); // adding to gridPane
                            }

                            //loop for column B button implementation
                            int countB = 0;
                            for (int i = 1; i <= 11; i++) {
                                countB++; // count for button number
                                String countToString = Integer.toString(countB); // count to string for button text
                                Button button = new Button(countToString + "B"); // button implementation
                                button.setId("waitingRoomLabel");
                                seats.add(button);// adding to a button list
                                gridPaneLeft.add(button, 1, i); // adding to gridPane
                            }

                            //loop for column C button implementation
                            int countC = 0;
                            for (int i = 1; i <= 11; i++) {
                                countC++; // count for button number
                                String countToString = Integer.toString(countC); // count to string for button text
                                Button button = new Button(countToString + "C"); // button implementation
                                button.setId("waitingRoomLabel");
                                seats.add(button); // adding to a button list
                                gridPaneRight.add(button, 0, i); // adding to gridPane
                            }

                            //loop for column D button implementation
                            int countD = 0;
                            for (int i = 1; i <= 10; i++)//row count
                            {
                                countD++; // count for button number
                                String countToString = Integer.toString(countD); // count to string for button text
                                Button button = new Button(countToString + "D"); // button implementation
                                button.setId("waitingRoomLabel");
                                seats.add(button); // adding to a button list
                                gridPaneRight.add(button, 1, i); // adding to gridPane
                            }

                            //loop for take the button actions
                            for (Button seat : seats)// taking the button name from to list as a Button type
                            {
                                //getting the boarded passengers
                                for (Passenger customer : boardedCustomers)
                                {
                                    //checking if the seat numbers equals to the boarded passengers seat
                                    boolean results = seat.getText().equals(customer.getBookedSeat());
                                    //if equal setting the color and tooltip for customer details
                                    if (results)
                                    {
                                        Tooltip tooltip = new Tooltip("Name: " + customer.getCustomerName() + "\nN.I.C: " +customer.getCustomerId());
                                        seat.setTooltip(tooltip);
                                        seat.setStyle("-fx-background-color:#B00000;-fx-border-color:black;-fx-text-fill:white");
                                    }
                                }
                            }

                            //Implementing the close button
                            HBox hBox = new HBox(10);
                            borderPane.setBottom(hBox);
                            hBox.setAlignment(Pos.BOTTOM_CENTER);
                            hBox.setPadding(new Insets(10));

                            Button closeButton = new Button("Close"); // button implementation
                            closeButton.setId("closeButton");
                            hBox.getChildren().addAll(closeButton);

                            //close button action
                            closeButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                            {
                                @Override
                                public void handle(MouseEvent event)
                                {
                                    boardedStage.close();//closing boarded stage
                                }
                            });

                            //scene,stage display
                            boardedStage.setScene(scene);
                            boardedStage.showAndWait();
                        }
                    });

                    //main option selection window cancel button
                    cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent event)
                        {
                            viewRouteStage.close();
                            optionStage.close();
                        }
                    });

                    //open stage show and scene set
                    optionStage.setScene(scene);
                    optionStage.showAndWait();
                }
                //if route is not selected display an alert
                else
                    {
                    Label labelInRouteSelection = new Label("Please Select a Station");
                    labelInRouteSelection.setStyle("-fx-padding: 15 0 5 70;-fx-font-size:12px;-fx-text-fill:red;");
                    gridPaneInRouteSelection.add(labelInRouteSelection, 0, 7);
                }
            }
        });

        //route window cancel button action
        //here show an alert to the user before closing.
        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //--Route Selection Closed Alert popup Window--//
                Stage bookingWindowClosePopupStage = new Stage();
                bookingWindowClosePopupStage.setTitle("Alert");
                BorderPane bookingWindowClosePopupPane = new BorderPane();
                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                VBox vBox = new VBox(10);
                bookingWindowClosePopupPane.setCenter(vBox);
                vBox.setAlignment(Pos.CENTER);

                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                noSeatAlert.setStyle("-fx-font-size:15px");

                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                //popup close button.In here closed the opened windows
                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        viewRouteStage.close();
                        bookingWindowClosePopupStage.close();
                    }
                });

                //alert popup cancel button action
                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        bookingWindowClosePopupStage.close();//alert popup close
                    }
                });

                //popup stage show and setting the scene
                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                bookingWindowClosePopupStage.showAndWait();
            }
        });

        //view stage show and scene setting
        viewRouteStage.setScene(routeSelectionScene);
        viewRouteStage.showAndWait();
    }

    //this method will delete the passenger object from the queue according to the user input
    private void deletePassengerFromQueue()
    {
        System.out.println("=== Delete Passenger From The Train Queue Option ===\n");
        //route selection
        options:
        while (true)
        {
            System.out.println("Select an Option:-");
            System.out.println("To Select the Colombo Station Enter 1");
            System.out.println("To Select the Badulla Station Enter  2");
            System.out.println("To Quit Enter Q\n");

            //this loop for get the user input for route/quit. checks weather the input correct or not
            String userSelectedRoute;
            while (true)
            {
                System.out.print("Option: ");
                Scanner scanner = new Scanner(System.in);
                userSelectedRoute = scanner.next();
                userSelectedRoute = userSelectedRoute.toUpperCase();
                System.out.println();

                //by the if condition is the entered input is correct or not
                if (userSelectedRoute.equals("1"))
                {
                    //checking weather the queue is empty
                    if (trainQueue.isEmptyToBadulla())
                    {
                        System.out.println("The Queue is Empty\n");
                        break options;//breaks the main loop
                    }
                    break;
                }
                else if (userSelectedRoute.equals("2"))
                {
                    //checking weather the queue is empty
                    if (trainQueue.isEmptyToColombo())
                    {
                        System.out.println("The Queue is Empty\n");
                        break options;//breaks the main loop
                    }
                    break;
                }
                else if (userSelectedRoute.equals("Q"))
                {
                    break options;//breaks the main loop
                }
                else
                {
                    System.out.println("Invalid Selection. Select Again \n");
                }
            }

            //this loop for checks the entered NIC no correct or not.
            //in here passing the entered NIC no the method in passenger queue class.
            //validating and deleting the object in there
            while (true)
            {
                System.out.print("Enter the Customers N.I.C Number Which Needs to Delete: ");
                Scanner scannerForId = new Scanner(System.in);
                String customerId = scannerForId.next();
                System.out.println();

                if (userSelectedRoute.equals("1"))
                {
                    //sending the object to the method and re order the queue
                    trainQueue.deleteToBadulla(customerId);
                    trainQueue.reOrderToBadulla();
                }
                else
                {
                    //sending the object to the method and re order the queue
                    trainQueue.deleteToColombo(customerId);
                    trainQueue.reOrderToColombo();
                }

                //this loop for ask weather wants to delete more or not by the user
                while (true)
                {
                    System.out.print("To Delete More In this Station Enter Y or to Quit Enter Q: ");
                    Scanner sc = new Scanner(System.in);
                    String choice = sc.next();
                    choice = choice.toUpperCase();
                    System.out.println();

                    if(userSelectedRoute.equals("1") && trainQueue.isEmptyToBadulla()){
                        System.out.println("Train Queue is Empty\n");
                        break options;
                    }
                    else if(userSelectedRoute.equals("2") && trainQueue.isEmptyToColombo()){
                        System.out.println("Train Queue is Empty\n");
                        break options;
                    }
                    else if (choice.equals("Y"))
                    {
                        break;
                    }
                    else if (choice.equals("Q"))
                    {
                        break options;//breaks the main loop
                    }
                    else
                    {
                        System.out.println("Invalid Input. Please Enter Again\n");
                    }
                }
            }
        }
    }

    //this method for store the train queue.
    //in this method stores the train queue to a text file according to the route
    //and also save to the Mongo database
    private void storeData()
    {
        //copying the Passenger Queue data for temporary lists according to the route
        ArrayList<Passenger> passengerQueueObjectToBadulla = trainQueue.getObjectAvailableToBadulla();
        ArrayList<Passenger> passengerQueueObjectToColombo = trainQueue.getObjectAvailableToColombo();

        //saving data to mongo db
        try
        {
            //disable the Mongodb log info
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.SEVERE);

            //connecting to the database,collection
            MongoClient mongoClient = new MongoClient("localhost",27017);//connecting to mongodb
            DB database = mongoClient.getDB("TrainSeatsBookingSystem");//connecting to the database

            //checking the route and connecting
            DBCollection dataCollectionToBadulla = database.getCollection("TrainQueueToBadulla");//connecting to the collection
            dataCollectionToBadulla.drop();//drop the database before store the new data set

            //store route 01 cmb-badulla
            for(Passenger passengerObject : passengerQueueObjectToBadulla)
            {
                //adding the data to the database
                BasicDBObject objectForCustomerNameAndSeat = new BasicDBObject();//object creating

                objectForCustomerNameAndSeat.put("customerName", passengerObject.getCustomerName());//adding the data to the object
                objectForCustomerNameAndSeat.put("customerId", passengerObject.getCustomerId());
                objectForCustomerNameAndSeat.put("seat", passengerObject.getBookedSeat());//adding the data to the object

                dataCollectionToBadulla.insert(objectForCustomerNameAndSeat);//adding the object to the database
            }

           //checking the route and connecting
            DBCollection dataCollectionToColombo = database.getCollection("TrainQueueToColombo");//connecting to the collection
            dataCollectionToColombo.drop();//drop the database before store the new data set

            //store route 02 badulla-cmb
            for(Passenger passengerObject : passengerQueueObjectToColombo)
            {
                //adding the data to the database
                BasicDBObject objectForCustomerNameAndSeat = new BasicDBObject();//object creating

                objectForCustomerNameAndSeat.put("customerName", passengerObject.getCustomerName());//adding the data to the object
                objectForCustomerNameAndSeat.put("customerId", passengerObject.getCustomerId());
                objectForCustomerNameAndSeat.put("seat", passengerObject.getBookedSeat());//adding the data to the object

                dataCollectionToColombo.insert(objectForCustomerNameAndSeat);//adding the object to the database
            }
        }
        catch (Exception e)
        {
            System.out.println("Something went wrong. \n\n");
        }

        //saving data to text file
        try
        {
            //store route 01 cmb-badulla
            File fileToBadulla = new File("TrainQueueToBadulla.txt");
            FileOutputStream fosToBadulla = new FileOutputStream(fileToBadulla);
            ObjectOutputStream oosToBadulla = new ObjectOutputStream(fosToBadulla);

            for (int i = 0; i < passengerQueueObjectToBadulla.size(); i++)
            {
                oosToBadulla.writeObject(passengerQueueObjectToBadulla.get(i));

            }

            //store route 02 badulla-cmb
            File fileToColombo = new File("TrainQueueToColombo.txt");
            FileOutputStream fosToColombo = new FileOutputStream(fileToColombo);
            ObjectOutputStream oosToColombo = new ObjectOutputStream(fosToColombo);

            for (int i = 0; i < passengerQueueObjectToColombo.size(); i++)
            {
                oosToColombo.writeObject(passengerQueueObjectToColombo.get(i));
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File Implementing Error\n");
        }
        catch (IOException e)
        {
            System.out.println("Something Went Wrong\n");
        }
        finally
        {
            System.out.println("Data Saved Successfully\n");
        }
    }


    private void loadData()
    {
        //copying the loaded data into this array lists
        ArrayList<Passenger> passengerQueueObjectToBadulla = trainQueue.getObjectAvailableToBadulla();
        ArrayList<Passenger> passengerQueueObjectToColombo = trainQueue.getObjectAvailableToColombo();

        //lists for get the queue data
        Passenger[] storedObjectsToBadulla = new Passenger[21];
        Passenger[] storedObjectsToColombo = new Passenger[21];

        //loading from mongo db
        /*
        try
        {
            //disable the Mongodb log info
            Logger mongodbLogger = Logger.getLogger("org.mongodb.driver");
            mongodbLogger.setLevel(Level.SEVERE);

            //connecting to the database,collection
            MongoClient mongoClient = new MongoClient("localhost", 27017);//connecting to mongodb
            DB database = mongoClient.getDB("TrainSeatsBookingSystem");//connecting to the database

            try
            {
                //route 01 cmb-badulla
                DBCollection dataBaseToBadulla = database.getCollection("TrainQueueToBadulla");//connecting to the collection
                DBCursor cursorToBadulla = dataBaseToBadulla.find();//taking the database data into object
                List<DBObject> dataBaseDataToBadulla = cursorToBadulla.toArray();//converting object to a list

                //implementing passenger objects and inserting loaded data
                for (int i = 0; i < dataBaseDataToBadulla.size(); i++)
                {
                    Passenger customer = new Passenger();
                    customer.setCustomerName(dataBaseDataToBadulla.get(i).get("customerName").toString());
                    customer.setCustomerId(dataBaseDataToBadulla.get(i).get("customerId").toString());
                    customer.setBookedSeat(dataBaseDataToBadulla.get(i).get("seat").toString());
                    storedObjectsToBadulla[i] = customer;//adding the passenger object into temporary array list
                }

                //in the code under this loop gets the passengers which is added after the stores.
                //after loading data checks the lists and adding them back into the waiting room.
                //getting the stored passenger objects
                for (Passenger passengerObject : storedObjectsToBadulla)
                {
                    if (passengerObject == null)
                    {
                        continue;
                    }

                    //getting the passenger objects which in the queue
                    for (Passenger queueObject : passengerQueueObjectToBadulla)
                    {
                        if (queueObject == null)
                        {
                            continue;
                        }

                        //if the both objects equal its remove from the temporary list which was created to load the data
                        if (passengerObject.getCustomerId().equals(queueObject.getCustomerId()))
                        {
                            passengerQueueObjectToBadulla.remove(queueObject);
                            break;
                        }

                        //if temporary list is empty breaks the loop
                        if (passengerQueueObjectToBadulla.size() == 0)
                        {
                            break;
                        }
                    }

                    //if temporary list is empty breaks the loop
                    if (passengerQueueObjectToBadulla.size() == 0)
                    {
                        break;
                    }
                }

                //adding the the filtered passengers and the current passengers to this
                //array list to maintain the added order(entrance to waiting room)
                ArrayList<Passenger> passengersToBadulla = new ArrayList<>();

                //adding the filtered passengers to the waiting room back
                for (Passenger object : passengerQueueObjectToBadulla)
                {
                    passengersToBadulla .add(object);
                }

                //adding the waiting room passengers(at that time) to the temp list
                for (Passenger waitingRoomPassengers : waitingRoomToBadulla)
                {
                   passengersToBadulla.add(waitingRoomPassengers);
                }

                //clear the waiting room
                waitingRoomToBadulla.clear();

                //adding the temporary list passengers to the waiting room
                for (Passenger passengers : passengersToBadulla)
                {
                   waitingRoomToBadulla.add(passengers);
                }

                //setting all values null in the passenger queue
                trainQueue.setNullToBadulla();
                //setting the first last and size ints to zero before adding data
                trainQueue.setFirstLastMaxToBadulla();

                //adding the loaded passengers into the queue
                for (Passenger object : storedObjectsToBadulla)
                {
                    if (object == null)
                    {
                        continue;
                    }
                    trainQueue.addToBadulla(object);
                }
            }
            catch (Exception e)
            {
                System.out.println("Something Went Wrong in Colombo to Badulla Data Loading\n");
            }

            try
            {
                //route 02 badulla-cmb
                DBCollection dataBaseToColombo = database.getCollection("TrainQueueToColombo");//connecting to the collection
                DBCursor cursorToColombo = dataBaseToColombo.find();//taking the database data into object
                List<DBObject> dataBaseDataToColombo = cursorToColombo.toArray();//converting object to a list

                //implementing passenger objects and inserting loaded data
                for (int i = 0; i < dataBaseDataToColombo.size(); i++)
                {
                    Passenger customer = new Passenger();
                    customer.setCustomerName(dataBaseDataToColombo.get(i).get("customerName").toString());
                    customer.setCustomerId(dataBaseDataToColombo.get(i).get("customerId").toString());
                    customer.setBookedSeat(dataBaseDataToColombo.get(i).get("seat").toString());
                    storedObjectsToColombo[i] = customer;//adding the passenger object into temporary array list
                }

                //in the code under this loop gets the passengers which is added after the stores.
                //after loading data checks the lists and adding them back into the waiting room.

                //getting the stored passenger objects
                for (Passenger passengerObject : storedObjectsToColombo)
                {
                    if (passengerObject == null)
                    {
                        continue;
                    }

                    //getting the passenger objects which in the queue
                    for (Passenger queueObject : passengerQueueObjectToColombo)
                    {
                        if (queueObject == null)
                        {
                            continue;
                        }

                        //if the both objects equal its remove from the temporary list which was created to load the data
                        if (passengerObject.getCustomerId().equals(queueObject.getCustomerId()))
                        {
                            passengerQueueObjectToColombo.remove(queueObject);
                            break;
                        }

                        //if temporary list is empty breaks the loop
                        if (passengerQueueObjectToColombo.size() == 0)
                        {
                            break;
                        }
                    }

                    //if temporary list is empty breaks the loop
                    if (passengerQueueObjectToColombo.size() == 0)
                    {
                        break;
                    }
                }

                //adding the the filtered passengers and the current passengers to this
                //array list to maintain the added order(entrance to waiting room)
                ArrayList<Passenger> passengersToColombo = new ArrayList<>();

                //adding the filtered passengers to the waiting room back
                for (Passenger object : passengerQueueObjectToColombo)
                {
                    passengersToColombo.add(object);
                }

                //adding the waiting room passengers(at that time) to the temp list
                for (Passenger waitingRoomPassengers : waitingRoomToColombo)
                {
                   passengersToColombo.add(waitingRoomPassengers);
                }

                //clear the waiting room
                waitingRoomToColombo.clear();

                //adding the temporary list passengers to the waiting room
                for (Passenger passengers : passengersToColombo)
                {
                   waitingRoomToColombo.add(passengers);
                }

                //setting all values null in the passenger queue
                trainQueue.setNullToColombo();
                //setting the first last and size ints to zero before adding data
                trainQueue.setFirstLastMaxToColombo();

                //adding the loaded passengers into the queue
                for (Passenger object : storedObjectsToColombo)
                {
                    if (object == null)
                    {
                        continue;
                    }
                    trainQueue.addToColombo(object);
                }
            } catch (Exception e)
            {
                System.out.println("Something Went Wrong in Badulla to Colombo Data Loading\n");
            }
        }
        catch(Exception e)
        {
            System.out.println("Something Went Wrong Data Loading\n");
        }
        finally
        {
            System.out.println("Data Loaded Successfully\n");
        }*/



        //loading the data from the text files
        try {
            //colombo-badulla route
            try {
                //temporary array list to load the data from the files
                ArrayList<Passenger> passengerQueueObjectToBadullaTxt = new ArrayList<>();

                File fileToBadulla = new File("TrainQueueToBadulla.txt");
                FileInputStream fisToBadulla = new FileInputStream(fileToBadulla);
                ObjectInputStream oisToBadulla = new ObjectInputStream(fisToBadulla);

                //loop for read the objects from the file
                while (true)
                {
                    try
                    {
                        Passenger objToBadulla = (Passenger) oisToBadulla.readObject();
                        passengerQueueObjectToBadullaTxt.add(objToBadulla);//adding to the temp array
                    }
                    //this catch will works at the end the reading data from the file.
                    catch (EOFException e)
                    {
                        //adding the loaded data into passenger list
                        for (int i = 0; i < passengerQueueObjectToBadullaTxt.size(); i++)
                        {
                            storedObjectsToBadulla[i] = passengerQueueObjectToBadullaTxt.get(i);
                        }

                        //in the code under this loop gets the passengers which is added after the stores.
                        //after loading data checks the lists and adding them back into the waiting room.
                        //getting the stored passenger objects
                        for (Passenger passengerObject : storedObjectsToBadulla)
                        {
                            if (passengerObject == null)
                            {
                                continue;
                            }

                            //getting the passenger objects which in the queue
                            for (Passenger queueObject : passengerQueueObjectToBadulla)
                            {
                                if (queueObject == null)
                                {
                                    continue;
                                }

                                //if the both objects equal its remove from the temporary list which was created to load the data
                                if (passengerObject.getCustomerId().equals(queueObject.getCustomerId()))
                                {
                                    passengerQueueObjectToBadulla.remove(queueObject);
                                    break;
                                }

                                //if temporary list is empty breaks the loop
                                if (passengerQueueObjectToBadulla.size() == 0)
                                {
                                    break;
                                }
                            }

                            //if temporary list is empty breaks the loop
                            if (passengerQueueObjectToBadulla.size() == 0)
                            {
                                break;
                            }
                        }

                        //adding the the filtered passengers and the current passengers to this
                        //array list to maintain the added order(entrance to waiting room)
                        ArrayList<Passenger> passengersToBadulla = new ArrayList<>();

                        //adding the filtered passengers to the temporary list
                        for (Passenger object : passengerQueueObjectToBadulla)
                        {
                            passengersToBadulla.add(object);
                        }

                        //adding the waiting room passengers(at that time) to the temp list
                        for (Passenger waitingRoomPassengers : waitingRoomToBadulla)
                        {
                            passengersToBadulla.add(waitingRoomPassengers);
                        }

                        //clear the waiting room array list
                        waitingRoomToBadulla.clear();

                        //adding the temporary list passengers to the waiting room
                        for (Passenger passengers : passengersToBadulla)
                        {
                            waitingRoomToBadulla.add(passengers);
                        }

                        //setting all values null in the passenger queue
                        trainQueue.setNullToBadulla();
                        //setting the first last and size ints to zero before adding data
                        trainQueue.setFirstLastMaxToBadulla();

                        //adding the loaded passengers into the queue
                        for (Passenger object : storedObjectsToBadulla)
                        {
                            if (object == null)
                            {
                                continue;
                            }
                            trainQueue.addToBadulla(object);
                        }
                        break;
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                System.out.println("File Not Found In Badulla Route\n");
            }
            catch (IOException e)
            {
                System.out.println("Something Went Wrong Data in Loading - IO Exceptions In Badulla Route\n");
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("Something Went Wrong in Data Loading - ClassNotFoundException In Badulla Route\n");
            }

            //badulla-colombo route
            try
            {
                //temporary array list to load the data from the files
                ArrayList<Passenger> passengerQueueObjectToColomboTxt = new ArrayList<>();

                File fileToColombo = new File("TrainQueueToColombo.txt");
                FileInputStream fisToColombo = new FileInputStream(fileToColombo);
                ObjectInputStream oisToColombo = new ObjectInputStream(fisToColombo);

                //loop for read the objects from the file
                while (true)
                {
                    try
                    {
                        Passenger objToColombo= (Passenger) oisToColombo.readObject();
                        passengerQueueObjectToColomboTxt.add(objToColombo);//adding to the temp array
                    }
                    //this catch will works at the end the reading data from the file.
                    catch (EOFException e)
                    {
                        //adding the loaded data into passenger list
                        for (int i = 0; i < passengerQueueObjectToColomboTxt.size(); i++)
                        {
                            storedObjectsToColombo[i] = passengerQueueObjectToColomboTxt.get(i);
                        }

                        //in the code under this loop gets the passengers which is added after the stores.
                        //after loading data checks the lists and adding them back into the waiting room.

                        //getting the stored passenger objects

                        for (Passenger passengerObject : storedObjectsToColombo)
                        {
                            if (passengerObject == null)
                            {
                                continue;
                            }

                            //getting the passenger objects which in the queue
                            for (Passenger queueObject : passengerQueueObjectToColombo)
                            {
                                if (queueObject == null)
                                {
                                    continue;
                                }

                                //if the both objects equal its remove from the temporary list which was created to load the data
                                if (passengerObject.getCustomerId().equals(queueObject.getCustomerId()))
                                {
                                    passengerQueueObjectToColombo.remove(queueObject);
                                    break;
                                }

                                //if temporary list is empty breaks the loop
                                if (passengerQueueObjectToColombo.size() == 0)
                                {
                                    break;
                                }
                            }

                            //if temporary list is empty breaks the loop
                            if (passengerQueueObjectToColombo.size() == 0)
                            {
                                break;
                            }
                        }

                        //adding the the filtered passengers and the current passengers to this
                        //array list to maintain the added order(entrance to waiting room)
                        ArrayList<Passenger> passengersToColombo = new ArrayList<>();

                        //adding the filtered passengers to the temporary list
                        for (Passenger object : passengerQueueObjectToColombo)
                        {
                            passengersToColombo.add(object);
                        }

                        //adding the waiting room passengers(at that time) to the temp list
                        for (Passenger waitingRoomPassengers : waitingRoomToColombo)
                        {
                            passengersToColombo.add(waitingRoomPassengers);
                        }

                        //clear the waiting room array list
                        waitingRoomToColombo.clear();

                        //adding the temporary list passengers to the waiting room
                        for (Passenger passengers : passengersToColombo)
                        {
                            waitingRoomToColombo.add(passengers);
                        }

                        //setting all values null in the passenger queue
                        trainQueue.setNullToColombo();
                        //setting the first last and size ints to zero before adding data
                        trainQueue.setFirstLastMaxToColombo();

                        //adding the loaded passengers into the queue
                        for (Passenger object : storedObjectsToColombo)
                        {
                            if (object == null)
                            {
                                continue;
                            }
                            trainQueue.addToColombo(object);
                        }
                        break;
                    }
                }

            }
            catch (FileNotFoundException e)
            {
                System.out.println("File Not Found In Colombo Route\n");
            }
            catch (IOException e)
            {
                System.out.println("Something Went Wrong Data in Loading - IO Exceptions In Colombo Route\n");
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("Something Went Wrong in Data Loading - ClassNotFoundException In Colombo Route\n");
            }
        }
        catch(Exception e)
        {
            System.out.println("Something Went Wrong in Data Loading\n");
        }
        finally
        {
            System.out.println("Data Loaded Successfully\n");
        }
    }

    //this method will boards the passengers and previews a gui report with statistics of time
    //and also save a text file of the statistics of time
    private void simulationAndReport()
    {
        //---Route Selection Window--//
        //window select the route
        Stage routeStage = new Stage();//stage,pane,scene,stage title,grid pane implementing
        routeStage.setTitle("Station Select Window");
        BorderPane borderPaneInRouteSelection = new BorderPane();
        Scene routeSelectionScene = new Scene(borderPaneInRouteSelection, 400, 200);
        GridPane gridPaneInRouteSelection = new GridPane();
        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

        //Display Labels
        Label labelInRouteSelection = new Label("Select the Station: ");
        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
        gridPaneInRouteSelection.add(labelInRouteSelection, 0, 4);

        //radio buttons for Route selection
        ToggleGroup toggleGroupInRouteSelection= new ToggleGroup();

        RadioButton colomboBtn = new RadioButton("Colombo");
        colomboBtn.setStyle("-fx-padding:20 0 10 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(colomboBtn, 0, 5);

        RadioButton badullaBtn = new RadioButton("Badulla");
        badullaBtn.setStyle("-fx-padding:10 0 0 60;-fx-font-size:14px;");
        gridPaneInRouteSelection.add(badullaBtn, 0, 6);

        badullaBtn.setToggleGroup(toggleGroupInRouteSelection);
        colomboBtn.setToggleGroup(toggleGroupInRouteSelection);

        //confirm button
        Button confirmButton = new Button("Confirm");
        confirmButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:70px;-fx-border-color:#808080");

        //back button
        Button cancelButton = new Button("Close");
        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

        //button aligning.
        HBox hBoxInRoteSelection = new HBox(5);
        hBoxInRoteSelection .setStyle("-fx-padding: 0 0 10 0");
        borderPaneInRouteSelection.setBottom(hBoxInRoteSelection );
        hBoxInRoteSelection .setAlignment(Pos.CENTER);
        hBoxInRoteSelection .getChildren().addAll(cancelButton,confirmButton);

        //route selection window confirm button
        confirmButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //checking weather the routes are selected.if it is wil execute this code
                if(badullaBtn.isSelected() || colomboBtn.isSelected())
                {
                    //checking weather the train queue is empty.if it is displays an alert
                    if ((colomboBtn.isSelected() && trainQueue.isEmptyToBadulla()) || (badullaBtn.isSelected() && trainQueue.isEmptyToColombo()))
                    {
                        Stage stage = new Stage();
                        stage.setTitle("Alert");
                        BorderPane waitingWindowClosePopupPane = new BorderPane();
                        Scene scene = new Scene(waitingWindowClosePopupPane, 250, 150);
                        VBox vBox = new VBox(10);
                        waitingWindowClosePopupPane.setCenter(vBox);
                        vBox.setAlignment(Pos.CENTER);

                        Label noSeatAlert = new Label("Train Queue is Empty");//label
                        noSeatAlert.setStyle("-fx-font-size:15px");

                        Button waitingWindowClosePopupButton = new Button("Close");//yes button
                        waitingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                        vBox.getChildren().addAll(noSeatAlert, waitingWindowClosePopupButton);

                        //popup close button.In here closed the opened windows
                        waitingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                stage.close();
                            }
                        });

                        stage.setScene(scene);
                        stage.showAndWait();
                    }

                    //if colombo button is selected this code will execute
                    else if (colomboBtn.isSelected())
                    {
                        //variables for calculations
                        int timeForPassenger;
                        int totalTime = 0;
                        //setting the min and max time to default before calculating
                        trainQueue.setTimeToBadulla();

                        //looping to the size of the queue
                        for (int i = 0; i < trainQueue.getMaxLengthToBadulla(); i++)
                        {
                            //getting the queue object by the getters
                            Passenger object = trainQueue.getQueueValueToBadulla();

                            if (object == null)
                            {
                                break;
                            }
                            //if object not null it will added to the boarded array and do the calculations of the time
                            else
                            {
                                //getting the processing time by 3 dices
                                int firstRandomNumber = ((int) (Math.random() * 6)) + 1;
                                int secondRandomNumber = ((int) (Math.random() * 6)) + 1;
                                int thirdRandomNumber = ((int) (Math.random() * 6)) + 1;

                                //calculating the total time for the passenger
                                timeForPassenger = (firstRandomNumber + secondRandomNumber + thirdRandomNumber);
                                //calculating the whole time for the boarding process
                                totalTime = totalTime + timeForPassenger;

                                //to sleep the program for process time
                                //TimeUnit.SECONDS.sleep(timeForPassenger);

                                //setting the min and max time stay in the queue
                                trainQueue.setMaxStayInQueueToBadulla(totalTime);
                                trainQueue.setMinStayInQueueToBadulla(totalTime);

                                //setting the time for the passenger
                                object.setSecondsInQueue(totalTime);

                                //adding to the boarding array list
                                boardedPassengersToBadulla.add(object);
                            }
                        }

                        //calculating the average time for the boarding process
                        int averageTime = totalTime/trainQueue.getMaxLengthToBadulla();

                        //stage implementing for the GUI report
                        routeStage.setTitle("Simulation Report");
                        BorderPane borderPaneInRouteSelection = new BorderPane();
                        Scene reportScene = new Scene(borderPaneInRouteSelection, 450, 400);
                        GridPane gridPaneInRouteSelection = new GridPane();
                        gridPaneInRouteSelection.setAlignment(Pos.CENTER);
                        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

                        //Display Labels
                        Label labelInRouteSelection = new Label("Simulation Report");
                        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
                        borderPaneInRouteSelection.setTop(labelInRouteSelection);

                        //implementing a FX list
                        ListView listView = new ListView();
                        listView.setStyle("-fx-border: 10 10 10 10;-fx-font-size:13px;");
                        listView.setPrefSize(440, 300);

                        for(Passenger passenger : boardedPassengersToBadulla)
                        {
                            listView.getItems().add("");
                            listView.getItems().add("Name: "+passenger.getCustomerName());
                            listView.getItems().add("N.I.C: "+passenger.getCustomerId());
                            listView.getItems().add("Seat: "+passenger.getBookedSeat());
                            listView.getItems().add("Time Stayed in the Queue: "+passenger.getSecondsInQueue() +" seconds");
                        }

                        listView.getItems().add("");
                        listView.getItems().add("");
                        listView.getItems().add("Maximum Length of the Queue: " + "\t\t\t\t" + trainQueue.getMaxLengthToBadulla() +"   Passenger(s).");
                        listView.getItems().add("Maximum Waiting Time of the Passengers: " + "\t" + trainQueue.getMaxStayInQueueToBadulla() +"   Seconds.");
                        listView.getItems().add("Minimum Waiting Time of the Passengers: " + "\t\t" + trainQueue.getMinStayInQueueToBadulla() +"   Seconds.");
                        listView.getItems().add("Average Waiting Time of the Passengers: " + "\t\t" + averageTime +"   Seconds.");

                        //adding the list to the grid pane
                        gridPaneInRouteSelection.add(listView, 0, 0);

                        //close button
                        Button cancelButton = new Button("Close");
                        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

                        //button aligning.
                        HBox hBox = new HBox(5);
                        hBox.setStyle("-fx-padding: 0 0 10 0");
                        borderPaneInRouteSelection.setBottom(hBox);
                        hBox.setAlignment(Pos.CENTER);
                        hBox.getChildren().addAll(cancelButton);

                        //close button action
                        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event)
                            {
                                //--Route Selection Closed Alert popup Window--//
                                //opens a window(alert) and confirming to close the window
                                Stage bookingWindowClosePopupStage = new Stage();
                                bookingWindowClosePopupStage.setTitle("Alert");
                                BorderPane bookingWindowClosePopupPane = new BorderPane();
                                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                                VBox vBox = new VBox(10);
                                bookingWindowClosePopupPane.setCenter(vBox);
                                vBox.setAlignment(Pos.CENTER);

                                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                                noSeatAlert.setStyle("-fx-font-size:15px");

                                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                                //alert popup close button.In here closed the opened windows
                                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                                {
                                    @Override
                                    public void handle(MouseEvent event)
                                    {
                                        routeStage.close();//close the stage
                                        bookingWindowClosePopupStage.close();
                                    }
                                });

                                //alert cancel button action
                                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                                {
                                    @Override
                                    public void handle(MouseEvent event)
                                    {
                                        //closing the alert window
                                        bookingWindowClosePopupStage.close();
                                    }
                                });

                                //set the scene and show the alert window
                                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                                bookingWindowClosePopupStage.showAndWait();


                            }
                        });


                        routeStage.setScene(reportScene);

                        //writing the statistic data into a file
                        File file = new File("SimulationReportBadullaRoute.txt");

                        PrintWriter pw;
                        FileWriter fw;

                        try
                        {
                            fw = new FileWriter(file,true);
                            pw = new PrintWriter(fw,true);

                            for(Passenger passenger : boardedPassengersToBadulla)
                            {
                                pw.println("");
                                pw.println("Name: "+passenger.getCustomerName());
                                pw.println("N.I.C: "+passenger.getCustomerId());
                                pw.println("Seat: "+passenger.getBookedSeat());
                                pw.println("Time Stayed in the Queue: "+passenger.getSecondsInQueue() +" seconds");
                            }

                            pw.println("");
                            pw.println("Maximum Length of the Queue: " + "\t\t\t\t" + trainQueue.getMaxLengthToBadulla() +"   Passenger(s).");
                            pw.println("Maximum Waiting Time of the Passengers: " + "\t" + trainQueue.getMaxStayInQueueToBadulla() +"   Seconds.");
                            pw.println("Minimum Waiting Time of the Passengers: " + "\t\t" + trainQueue.getMinStayInQueueToBadulla()+"   Seconds.");
                            pw.println("Average Waiting Time of the Passengers: " + "\t\t" + averageTime +"   Seconds.");
                            pw.println("");
                        }
                        catch(FileNotFoundException e)
                        {
                            System.out.println("File is not Found\n");

                        }

                        catch(IOException e)
                        {
                            System.out.println("Something went wrong in File Permissions");
                        }

                        //set first/last/length 0 in train queue
                        trainQueue.setFirstLastMaxToBadulla();
                    }
                    //if badulla button is selected this code will execute
                    else
                    {
                        //variables for calculations
                        int timeForPassenger;
                        int totalTime = 0;
                        //setting the min and max time to default before calculating
                        trainQueue.setTimeToColombo();

                        //looping to the size of the queue
                        for (int i = 0; i < trainQueue.getMaxLengthToColombo(); i++)
                        {
                            //getting the queue object by the getters
                            Passenger object = trainQueue.getQueueValueToColombo();

                            if (object == null)
                            {
                                break;
                            }
                            //if object not null it will added to the boarded array and do the calculations of the time
                            else
                            {
                                //getting the processing time by 3 dices
                                int firstRandomNumber = ((int) (Math.random() * 6)) + 1;
                                int secondRandomNumber = ((int) (Math.random() * 6)) + 1;
                                int thirdRandomNumber = ((int) (Math.random() * 6)) + 1;

                                //calculating the total time for the passenger
                                timeForPassenger = (firstRandomNumber + secondRandomNumber + thirdRandomNumber);
                                //calculating the whole time for the boarding process
                                totalTime = totalTime + timeForPassenger;

                                //to sleep the program for process time
                                //TimeUnit.SECONDS.sleep(timeForPassenger);

                                //setting the min and max time stay in the queue
                                trainQueue.setMaxStayInQueueToColombo(totalTime);
                                trainQueue.setMinStayInQueueToColombo(totalTime);

                                //setting the time for the passenger
                                object.setSecondsInQueue(totalTime);

                                //adding to the boarding array list
                                boardedPassengersToColombo.add(object);
                            }
                        }

                        //calculating the average time for the boarding process
                        int averageTime = totalTime/trainQueue.getMaxLengthToColombo();

                        //stage implementing for the GUI report
                        routeStage.setTitle("Simulation Report");
                        BorderPane borderPaneInRouteSelection = new BorderPane();
                        Scene reportScene = new Scene(borderPaneInRouteSelection, 450, 400);
                        GridPane gridPaneInRouteSelection = new GridPane();
                        gridPaneInRouteSelection.setAlignment(Pos.CENTER);
                        borderPaneInRouteSelection.setCenter(gridPaneInRouteSelection);

                        //Display Labels
                        Label labelInRouteSelection = new Label("Simulation Report");
                        labelInRouteSelection.setStyle("-fx-padding: 15 0 0 20;-fx-font-size:16px;");
                        borderPaneInRouteSelection.setTop(labelInRouteSelection);

                        //implementing a FX list
                        ListView listView = new ListView();
                        listView.setStyle("-fx-border: 10 10 10 10;-fx-font-size:13px;");
                        listView.setPrefSize(440, 300);

                        for(Passenger passenger : boardedPassengersToColombo)
                        {
                            listView.getItems().add("");
                            listView.getItems().add("Name: "+passenger.getCustomerName());
                            listView.getItems().add("N.I.C: "+passenger.getCustomerId());
                            listView.getItems().add("Seat: "+passenger.getBookedSeat());
                            listView.getItems().add("Time Stayed in the Queue: "+passenger.getSecondsInQueue() +" seconds");
                        }

                        listView.getItems().add("");
                        listView.getItems().add("");
                        listView.getItems().add("Maximum Length of the Queue: " + "\t\t\t\t" + trainQueue.getMaxLengthToColombo() +"   Passenger(s).");
                        listView.getItems().add("Maximum Waiting Time of the Passengers: " + "\t" + trainQueue.getMaxStayInQueueToColombo() +"   Seconds.");
                        listView.getItems().add("Minimum Waiting Time of the Passengers: " + "\t\t" + trainQueue.getMinStayInQueueToColombo()+"   Seconds.");
                        listView.getItems().add("Average Waiting Time of the Passengers: " + "\t\t" + averageTime +"   Seconds.");

                        //adding the list to the grid pane
                        gridPaneInRouteSelection.add(listView, 0, 0);

                        //back button
                        Button cancelButton = new Button("Close");
                        cancelButton.setStyle("-fx-padding:5 5 5 5;-fx-font-size:15px;-fx-pref-width:80px;-fx-border-color:#808080");

                        //button aligning.
                        HBox hBox = new HBox(5);
                        hBox.setStyle("-fx-padding: 0 0 10 0");
                        borderPaneInRouteSelection.setBottom(hBox);
                        hBox.setAlignment(Pos.CENTER);
                        hBox.getChildren().addAll(cancelButton);

                        //close button action
                        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                        {
                            @Override
                            public void handle(MouseEvent event) {
                                //--Route Selection Closed Alert popup Window--//
                                //opens a window(alert) and confirming to close the window
                                Stage bookingWindowClosePopupStage = new Stage();
                                bookingWindowClosePopupStage.setTitle("Alert");
                                BorderPane bookingWindowClosePopupPane = new BorderPane();
                                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                                VBox vBox = new VBox(10);
                                bookingWindowClosePopupPane.setCenter(vBox);
                                vBox.setAlignment(Pos.CENTER);

                                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                                noSeatAlert.setStyle("-fx-font-size:15px");

                                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                                //alert popup close button.In here closed the opened windows
                                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                                {
                                    @Override
                                    public void handle(MouseEvent event)
                                    {
                                        routeStage.close();//close the stage
                                        bookingWindowClosePopupStage.close();
                                    }
                                });

                                //alert cancel button action
                                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                                {
                                    @Override
                                    public void handle(MouseEvent event)
                                    {
                                        //closing the alert window
                                        bookingWindowClosePopupStage.close();
                                    }
                                });

                                //set the scene and show the alert window
                                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                                bookingWindowClosePopupStage.showAndWait();
                            }
                        });

                        routeStage.setScene(reportScene);


                        //writing the statistic data into a file
                        File file = new File("SimulationReportColomboRoute.txt");

                        PrintWriter pw;
                        FileWriter fw;

                        try
                        {
                            fw = new FileWriter(file,true);
                            pw = new PrintWriter(fw,true);

                            for(Passenger passenger : boardedPassengersToColombo)
                            {
                                pw.println("");
                                pw.println("Name: "+passenger.getCustomerName());
                                pw.println("N.I.C: "+passenger.getCustomerId());
                                pw.println("Seat: "+passenger.getBookedSeat());
                                pw.println("Time Stayed in the Queue: "+passenger.getSecondsInQueue() +" seconds");
                            }

                            pw.println("");
                            pw.println("Maximum Length of the Queue: " + "\t\t\t\t" + trainQueue.getMaxLengthToColombo() +"   Passenger(s).");
                            pw.println("Maximum Waiting Time of the Passengers: " + "\t" + trainQueue.getMaxStayInQueueToColombo() +"   Seconds.");
                            pw.println("Minimum Waiting Time of the Passengers: " + "\t\t" + trainQueue.getMinStayInQueueToColombo()+"   Seconds.");
                            pw.println("Average Waiting Time of the Passengers: " + "\t\t" + averageTime +"   Seconds.");
                            pw.println("");
                        }
                        catch(FileNotFoundException e)
                        {
                            System.out.println("File is not Found\n");
                        }

                        catch(IOException e)
                        {
                            System.out.println("Something went wrong in File Permissions");
                        }

                        //set first/last/length 0 in train queue
                        trainQueue.setFirstLastMaxToBadulla();
                    }
                }
                //if route is not selected displays an alert
                else
                {
                    Label labelAlert = new Label("Please Select a Station");
                    labelAlert.setStyle("-fx-padding: 15 0 5 70;-fx-font-size:12px;-fx-text-fill:red;");
                    gridPaneInRouteSelection.add(labelAlert, 0, 7);
                }
            }
        });

        cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event) {
                //--Route Selection Closed Alert popup Window--//
                //opens a window(alert) and confirming to close the window
                Stage bookingWindowClosePopupStage = new Stage();
                bookingWindowClosePopupStage.setTitle("Alert");
                BorderPane bookingWindowClosePopupPane = new BorderPane();
                Scene noSeatsAlertScene = new Scene(bookingWindowClosePopupPane, 250, 150);
                VBox vBox = new VBox(10);
                bookingWindowClosePopupPane.setCenter(vBox);
                vBox.setAlignment(Pos.CENTER);

                Label noSeatAlert = new Label("Are You Sure Want to Close ?");//label
                noSeatAlert.setStyle("-fx-font-size:15px");

                Button bookingWindowClosePopupButton = new Button("YES");//yes button
                bookingWindowClosePopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                Button bookingWindowCancelPopupButton = new Button("CANCEL");//cancel button
                bookingWindowCancelPopupButton.setStyle("-fx-border-color:#808080;-fx-font-size:13px;-fx-pref-width:100px;");

                vBox.getChildren().addAll(noSeatAlert, bookingWindowClosePopupButton, bookingWindowCancelPopupButton);

                //alert popup close button.In here closed the opened windows
                bookingWindowClosePopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        routeStage.close();//close the stage
                        bookingWindowClosePopupStage.close();
                    }
                });

                //alert cancel button action
                bookingWindowCancelPopupButton.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        //closing the alert window
                        bookingWindowClosePopupStage.close();
                    }
                });

                //set the scene and show the alert window
                bookingWindowClosePopupStage.setScene(noSeatsAlertScene);
                bookingWindowClosePopupStage.showAndWait();
            }
        });

        //route selection window show and set the scene
        routeStage.setScene(routeSelectionScene);
        routeStage.showAndWait();
    }
}

