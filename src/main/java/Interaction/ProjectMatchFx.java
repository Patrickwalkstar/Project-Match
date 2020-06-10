package Interaction;

import Algorithm.MatchAlgorithm;
import Algorithm.MatchStatistics;

import Objects.Project;
import Objects.Student;
import Objects.PreviewTableRow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



/*This is the main JavaFx program for "Project Match".
  The format of the stages and processes therein directly
  correlate with the order of the code in this this file. */

public class ProjectMatchFx extends Application {
    //Used for the Alter Teams stage when moving rows and columns
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private IntegerProperty index = new SimpleIntegerProperty();

    //The input project and student files are stored in these variables
    public File selectedProjectFile;
    public File selectedStudentFile;

    //The preview window's table, a list and array of the columns for the table are declared here
    public TableView<PreviewTableRow> tableView = new TableView<>();
    public List<String> colList;
    public TableColumn<PreviewTableRow, String> [] colArray;

    /*This checks to see if the table has already been written
    (the table is written only once when the user runs the algorithm).*/
    public int tableWrittenav = 0;

    //Used to move a window with no stage heading. (no top bar)
    private double xOffset = 0;
    private double yOffset = 0;

    //These functions are used for table (column and row) manipulation
    public final Integer getIndex() {
        return index.get();
    }
    public final void setIndex(Integer value) {
        index.set(value);
    }
    public IntegerProperty indexProperty() {
        return index;
    }

    //The primary stage is drawn and displayed to the user.
    @Override
    public void start(Stage primaryStage) throws Exception, FileNotFoundException {
        //Set the primaryStage with specific sizes, title and resizability values.
        primaryStage.setWidth(650);
        primaryStage.setHeight(270);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Project Match");

        try{//Necessary try-catch block

            /* Create the main pane for the Project Match stage
            This will allow us to add elements on the top, bottom, left, right. */
            BorderPane main = new BorderPane();
            //Set the size of the main pane to be equivalent to the size of the stage.
            main.setMinSize(primaryStage.getX(), primaryStage.getY());

            /* Create a grid pane that will take on the many rows of the primary stage.
            Set the required, minimum height and width of the grid pane. Set the grid
            pane to the center of the border pane already initialized. so far, this grid
            pane takes up the entirety of the border pane and primary stage. */
            GridPane gridPane = new GridPane();
            gridPane.minHeight(250);
            gridPane.minWidth(450);
            main.setCenter(gridPane);

            //Allows developers to see the grid lines of the grid pane
            gridPane.setGridLinesVisible(false);
            gridPane.setAlignment(Pos.TOP_CENTER);

            //Set the spacing between the rows and columns of the grid pane (and the main stage).
            gridPane.setVgap(10);
            gridPane.setHgap(10);

            //Create artificial (customizable) spacing for some of the rows in the grid pane.
            Label spacingLabel = new Label("                                    ");


            // ---------------------- Setup the title screen ----------------------

            //Configure custom text of the title name
            Text title = new Text("Project Match");
            InputStream input = ProjectMatchFx.class.getResourceAsStream("/resources/roguehero3d.ttf");
            if (input == null) { // if running the application in an IDE, need to change the way the .ttf file is accessed
                input = ProjectMatchFx.class.getClassLoader().getResourceAsStream("roguehero3d.ttf");
            }
            Font titleFont = Font.loadFont(input, 55);
            title.setFont(titleFont);
            title.setTextAlignment(TextAlignment.CENTER);

            /* Create a horizontal box that will be the primary storage for
            the contents of primary stage/title name. */
            HBox titlename = new HBox();

            //until another object implicitly requests focus, the focus will be on the title, doing nothing.
            titlename.requestFocus();

            //Position and place the title in the correct position in the gridPane
            titlename.setAlignment(Pos.BASELINE_CENTER);
            titlename.getChildren().add(title);
            gridPane.add(titlename, 0,0);
            HBox space = new HBox();
            gridPane.add(space, 0,1);

            //The close button is created and placed here. The button is placed in a new horizontal
            //box that will take the top part of the main border pane.
            HBox plain = new HBox(10);

            //An example of setting the style for an element of a window
            //The styling is set with typical CSS attributes, each starting with -fx-
            plain.setStyle("-fx-border-width: 1;");
            Button closeApplicationButton = new Button("ⓧ");
            closeApplicationButton.setStyle("-fx-background-color: transparent;" +
                    "-fx-font: 20px arial;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 100px;"
            );

            plain.getChildren().add(closeApplicationButton);
            plain.setAlignment(Pos.TOP_LEFT);
            closeApplicationButton.setAlignment(Pos.TOP_LEFT);
            main.setTop(plain);

            //In the case that one element was the issue in terms of sizing, configure the width and height of every
            //element relating to the closeApplicationButton.
            plain.maxWidth(20);
            plain.minWidth(20);
            closeApplicationButton.maxWidth(20);
            closeApplicationButton.minWidth(20);
            main.getTop().maxWidth(20);
            main.getTop().minWidth(20);
            main.getTop().maxHeight(20);
            main.setStyle("-fx-border-width: 3;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 6px;" +
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 9px;" +
                    "-fx-background-style: solid outside;");

            //Wen the user clicks the closeApplicationButton, the primary stage closes
            closeApplicationButton.setOnMouseClicked(MouseEvent -> {
                primaryStage.close();
            });

            //Choose the stage by holding click on window.
            main.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            //On drag, move the window with the move of the mouse.
            main.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    primaryStage.setX(event.getScreenX() - xOffset);
                    primaryStage.setY(event.getScreenY() - yOffset);
                }
            });

            //set up the scene
            Scene scene = new Scene(main, 500, 500);
            //have the scene fill be transparent so the we can have rounded borders for the primary stage
            scene.setFill(Color.TRANSPARENT);
            //set the stage with the scene
            primaryStage.setScene(scene);
            //have the primary stage appear at the center of the user's screen
            primaryStage.centerOnScreen();
            //set the style of the stage to be transparent, no background and no decorations (top bar)
            primaryStage.initStyle(StageStyle.TRANSPARENT);

            primaryStage.show();


            // ---------------------- Create Buttons ----------------------

            //Create and style buttons to import student & project CSV files
            Button importProjectFileButton = new Button("Import Project File"); // First step for user
            importProjectFileButton.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 3;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-color: rgb(225, 225, 225);" +
                    "-fx-transition: 0.2s ease;");
            Button importStudentFileButton = new Button("Import Student File");
            importStudentFileButton.setDisable(true); // Disable the student CSV import button until a project CSV has been imported
            importStudentFileButton.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 3;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-color: rgb(225, 225, 225);" +
                    "-fx-transition: 0.2s ease;");

            // Create and style button to run the Matching Algorithm
            Button runAlgorithmButton = new Button("       Run Algorithm       ");
            runAlgorithmButton.setDisable(true);  // Disable the Run Algorithm button until both CSV files have been imported
            Button showTableButton = new Button("⚙");
            showTableButton.setStyle("-fx-background-color: transparent;" +
                    "-fx-font: 20px arial;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 1px;" +
                    "-fx-border-radius: 6;");

            runAlgorithmButton.setStyle("-fx-font: 15px Roboto;"+
                    "-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-color: rgb(225, 225, 225);" +
                    "-fx-transition: 0.2s ease;");
            showTableButton.setAlignment(Pos.CENTER);
            runAlgorithmButton.setAlignment(Pos.CENTER);

            // ---------------------- Import CSV functionality ----------------------

            /* Create a horizontal box that will be the primary storage for
            the contents of the file upload functionality*/
            HBox projectFileUpload = new HBox(6);
            HBox studentFileupload = new HBox(6);

            //Add the artificial spacing to its own horizontal box.
            HBox spacingBox = new HBox();
            spacingBox.getChildren().add(spacingLabel);

            /* Create a custom label for the import file, whose name changes based
            on the name of the imported file. */

            //Create and style a horizontal box that will take only the value of the file name.
            HBox projectFileNameBox = new HBox();
            HBox studentFileNameBox = new HBox();

            projectFileNameBox.setStyle("-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 6;");
            studentFileNameBox.setStyle("-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 6;");

            //Create and style an initial label for the name of the input file.
            Label projectInputFileLabel = new Label("    <- Select Project CSV File    ");
            Label studentInputFileLabel = new Label("    <- Select Student CSV File    ");

            projectInputFileLabel.setStyle("-fx-padding: 2.3;" +
                    "-fx-font: 15px Roboto;");
            studentInputFileLabel.setStyle("-fx-padding: 2.3;" +
                    "-fx-font: 15px Roboto;");

            //Align elements and set up the rest of the import csv row
            projectInputFileLabel.setAlignment(Pos.BASELINE_CENTER);
            projectFileNameBox.setAlignment(Pos.BASELINE_CENTER);
            projectFileNameBox.getChildren().add(projectInputFileLabel);
            studentInputFileLabel.setAlignment(Pos.BASELINE_CENTER);
            studentFileNameBox.setAlignment(Pos.BASELINE_CENTER);
            studentFileNameBox.getChildren().add(studentInputFileLabel);
            projectFileUpload.setAlignment(Pos.BASELINE_CENTER);
            projectFileUpload.getChildren().addAll(importProjectFileButton, projectFileNameBox);
            studentFileupload.setAlignment(Pos.BASELINE_CENTER);
            studentFileupload.getChildren().addAll(importStudentFileButton, studentFileNameBox);
            gridPane.add(projectFileUpload, 0, 2);
            gridPane.add(studentFileupload, 0, 3);

            //Setup a file chooser system that allows the user to choose a file to import
            FileChooser fileChooser = new FileChooser();

            /* When the user clicks the button to import file, the file chooser asks the
            user for a file and sets the label text to the name of the file selected. */
            importProjectFileButton.setOnAction(e -> {
                selectedProjectFile = fileChooser.showOpenDialog(primaryStage);
                projectInputFileLabel.setText((selectedProjectFile.getName()));
                Parse.parseProjectInputFile(selectedProjectFile);
                importStudentFileButton.setDisable(false); // Re-enable student CSV import button (next step for user)
            });

            importStudentFileButton.setOnAction(e -> {
                selectedStudentFile = fileChooser.showOpenDialog(primaryStage);
                studentInputFileLabel.setText((selectedStudentFile.getName()));
                Parse.parseStudentInputFile(selectedStudentFile);
                runAlgorithmButton.setDisable(false); // Re-enable Run Algorithm button after both CSV's are imported
            });


            // ---------------------- Run Algorithm ----------------------
            {
                HBox algBox = new HBox(2);
                algBox.setAlignment(Pos.CENTER);
                algBox.getChildren().addAll(runAlgorithmButton, showTableButton);
                gridPane.add(algBox, 0, 5);
                Stage modify = new Stage();

                /*When the algorithm button is pressed, the algorithm calls the MatchAlgorithm runAlgorithm
                function on the list of students and the list of projects. The user is immediately presented with the
                preview table window */
                //If this action event or the next is performed, then the table will be written (filled in with rows/values) and will not be written again
                ArrayList<Student> students = Student.studentList;
                ArrayList<Student> invalidStudents = Student.invalidInputStudentList;
                runAlgorithmButton.setOnMouseClicked(MouseEvent -> {
                    MatchAlgorithm.runAlgorithm(students, Project.projectList);
                    startmodify(modify, tableWrittenav, students, invalidStudents);
                    tableWrittenav++;
                });

                //This button allows the user to open the preview table window without having to run the algorithm again
                showTableButton.setOnMouseClicked(MouseEvent -> {
                    startmodify(modify, tableWrittenav, students, invalidStudents);
                    tableWrittenav++;
                });
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //The stats stage is drawn and displayed to the user.
    public void statsStage(Stage stats, Boolean statsStageav, Stage modify, ArrayList<Student> students) {
        //As long as the stats stage is not open, then begin to draw the stats stage.
        //This is to make sure that only one stats page can be displayed at a time
        if(statsStageav == true) {
            //set the stats stage
            stats.setTitle("Statistics");
            stats.setWidth(450);
            stats.setHeight(600);
            BorderPane statsPane = new BorderPane();

            //Calculate and store the match statistics using the MatchStatistics calculateMatchStats function on the list of students
            HashMap<String, Double[]> matchStats = MatchStatistics.calculateMatchStats(students);

            //Set up the pieces of the pie chart
            ObservableList<PieChart.Data> statsPieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("First Choice: ", matchStats.get("First")[1]),
                    new PieChart.Data("Second Choice: ", matchStats.get("Second")[1]),
                    new PieChart.Data("Third Choice: ", matchStats.get("Third")[1]),
                    new PieChart.Data("Fourth Choice: ", matchStats.get("Fourth")[1]),
                    new PieChart.Data("Fifth+ Choice: ", matchStats.get("Fifth+")[1])
            );

            //Create and attach the data to this PieChart
            PieChart statsPieChart = new PieChart(statsPieChartData);

            //If future developers want to apply custom colors to the pie chart
            //String [] pieColors = {"rgb(0, 55, 111)", "rgb(120, 192, 233)", "rgb(100, 100, 100)", "rgb(1, 164, 153)", "rgb(50, 100, 150)"};
            //applyCustomColorSequence(statsPieChartData, pieColors);

            //These are some PieChart statistics-related characteristics that are ancillary to the chart itself
            statsPieChart.setLegendVisible(false);
            statsPieChart.setClockwise(false);
            statsPieChart.setStyle("-fx-border-width: 1;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 6px;" +
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 9px;" +
                    "-fx-background-style: solid outside;");
            statsPieChart.setMaxHeight(250);

            //In the case that future developers would like to store more than one chart in the stats window, this
            //horizontal box (or vertical box) stores all current charts
            HBox pieCharts = new HBox();

            //Customize and set the title of the piechart
            Font pieChartTitleFont = new Font("Roboto", 24);
            Font pieChartStatsFont = new Font("Roboto", 16);
            Label pieChartTitle = new Label("Student Project Placement (by %)");
            pieChartTitle.setFont(pieChartTitleFont);
            Label caption = new Label("");
            caption.setVisible(false);
            caption.setFont(pieChartStatsFont);
            caption.setTextAlignment(TextAlignment.CENTER);

            //Stores the running total of the piechart data
            DoubleBinding total = Bindings.createDoubleBinding(() ->
                    statsPieChartData.stream().collect(Collectors.summingDouble(PieChart.Data::getPieValue)), statsPieChartData);

            VBox additionalData = new VBox(5);
            additionalData.setAlignment(Pos.TOP_CENTER);
            additionalData.setMaxHeight(40);
            additionalData.setStyle("-fx-border-width: 1;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 6px;" +
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 9px;" +
                    "-fx-background-style: solid outside;" +
                    "-fx-padding: 4px;");

            // Set data to be displayed below pie chart  // NOTE: POOR CODE, NEEDS TO BE REFACTORED
            int i = 0;
            //Store the values of the
            int[] rawNumbers = new int[] {
                    matchStats.get("First")[0].intValue(),
                    matchStats.get("Second")[0].intValue(),
                    matchStats.get("Third")[0].intValue(),
                    matchStats.get("Fourth")[0].intValue(),
                    matchStats.get("Fifth+")[0].intValue() };

            //For each of the pieces of the pie, get the percentage of students associated with that piece
            //and display it and the respective number of students
            for(PieChart.Data data: statsPieChartData) {
                Text name = new Text(data.getName());
                Text percentage = new Text(String.format("%.1f%%", 100 * data.getPieValue() / total.get()));
                Text rawNumber = new Text("" + rawNumbers[i++]);
                Text space = new Text("    ");
                Text spaceSlash = new Text("    /    ");
                Text studentsText = new Text("  students");
                name.setFont(pieChartStatsFont);
                percentage.setFont(pieChartStatsFont);
                rawNumber.setFont(pieChartStatsFont);
                space.setFont(pieChartStatsFont);
                spaceSlash.setFont(pieChartStatsFont);
                studentsText.setFont(pieChartStatsFont);
                HBox singleValue = new HBox();
                singleValue.setMaxHeight(30);
                singleValue.getChildren().addAll(name, space, percentage, spaceSlash, rawNumber, studentsText);
                singleValue.setAlignment(Pos.CENTER);
                additionalData.getChildren().add(singleValue);
            }

            //Create a button to close the stats window.
            Button btnClose = new Button("ⓧ");
            btnClose.setStyle("-fx-background-color: white;" +
                    "-fx-font: 20px Roboto;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-radius: 100px;"
            );

            HBox plain = new HBox(5);
            btnClose.setAlignment(Pos.BASELINE_LEFT);
            pieChartTitle.setAlignment(Pos.BASELINE_CENTER);
            plain.setAlignment((Pos.CENTER));

            /*the plain horizontal box sits at the top of the stats page, and contains the close button and
            the title of the piechart. The title of the piechart is not with the chart so that the chart takes up
            less space on the page and is customizable*/
            plain.getChildren().add(btnClose);
            plain.getChildren().add(pieChartTitle);

            //When the user clicks the close button, the stats window closes but the table preview window stays open
            btnClose.setOnMouseClicked(MouseEvent -> {
                stats.close();
            });

            //Put the stats piechart in the piecharts box
            pieCharts.getChildren().add(statsPieChart);
            VBox statsElements = new VBox(10);

            //Put all the elements of the stats window into the center of the stats border pane
            statsElements.getChildren().addAll(plain, statsPieChart, additionalData);
            statsPane.setCenter(statsElements);
            statsPane.setStyle("-fx-border-width: 3;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 6px;" +
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 9px;" +
                    "-fx-background-style: solid outside;" +
                    "-fx-padding: 5px;");



            //Choose the stage by holding click on window.
            statsPane.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });

            //On drag, move the window with the move of the mouse.
            statsPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    stats.setX(event.getScreenX() - xOffset);
                    stats.setY(event.getScreenY() - yOffset);
                }
            });

            Scene statsScene = new Scene(statsPane, 400, 600);
            stats.setScene(statsScene);

            //when the user opens the stats window, it will
            //appear at the same height vertically, but in the middle horizontally of the preview table window
            stats.setX((modify.getWidth() / 2));
            stats.setY(modify.getY());
            statsScene.setFill(Color.TRANSPARENT);
            stats.initStyle(StageStyle.TRANSPARENT);

            stats.show();
            statsStageav = false;
        }
    }


    public void writeTable(ArrayList<Student> students, ArrayList<Student> invalidStudents, ArrayList<ArrayList<String>> exportData) {
        //create a TableView and allow edits on the table
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Used for Export Data and Preview Table
        ObservableList<PreviewTableRow> previewData = FXCollections.observableArrayList();
        String assignedProjectPreference = null, assignedProjectName = null, assignedProjectString = null, previousProject = students.get(0).assignedProject.projectName;

        // Export all students that were included in the matching
        for (Student student : students) {
            if (student.assignedProject == null) {
                System.out.println("" + student.studentName + " was not assigned to any project.");
            }
            // Allow students that are not assigned to a project to still be displayed in output
            assignedProjectName = student.assignedProject != null ? student.assignedProject.projectName : "-- Unassigned --";
            assignedProjectPreference = student.preferredProjects.indexOf(student.assignedProject) >= 0 ? "" + (student.preferredProjects.indexOf(student.assignedProject) + 1) : "-";
            assignedProjectString = assignedProjectName + "\t(" + assignedProjectPreference + ")";
            previewData.add(new PreviewTableRow(student.studentName, student.studentId, student.major, assignedProjectString, student.gender));

            if (assignedProjectName != previousProject) {
                exportData.add(new ArrayList<String>(Arrays.asList("","","","",""))); // Add empty row after each team
                previousProject = assignedProjectName;
            }
            exportData.add(new ArrayList<String>(Arrays.asList(student.studentName, student.studentId, student.major, assignedProjectString, student.gender)));
        }

        // Add empty rows between teams and students that were not included in the match
        exportData.add(new ArrayList<String>(Arrays.asList("","","","","")));


        // Preview and Export all students that were not included in the matching
        for (Student invalidStudent : invalidStudents) {
            previewData.add(new PreviewTableRow(invalidStudent.studentName, invalidStudent.studentId, invalidStudent.major, "-- INVALID STUDENT INPUT --", invalidStudent.gender));
            exportData.add(new ArrayList<String>(Arrays.asList(invalidStudent.studentName, invalidStudent.studentId, invalidStudent.major, "-- INVALID STUDENT INPUT --", invalidStudent.gender)));
        }



        // Add Match Statistics to export file
        exportData.add(new ArrayList<String>(Arrays.asList("","","","",""))); // Add empty row between teams and match stats
        HashMap<String, Double[]> matchStats = MatchStatistics.calculateMatchStats(students);
        exportData.add(new ArrayList<String>(Arrays.asList("Student Preference", "% Students", "# Students","","")));
        exportData.add(new ArrayList<String>(Arrays.asList("First Choice: ", (Math.round(matchStats.get("First")[1] * 100.0) / 100.0) + "%", "" + matchStats.get("First")[0].intValue(),"","")));
        exportData.add(new ArrayList<String>(Arrays.asList("Second Choice: ", (Math.round(matchStats.get("Second")[1] * 100.0) / 100.0) + "%", "" + matchStats.get("Second")[0].intValue(),"","")));
        exportData.add(new ArrayList<String>(Arrays.asList("Third Choice: ", (Math.round(matchStats.get("Third")[1] * 100.0) / 100.0) + "%", "" + matchStats.get("Third")[0].intValue(),"","")));
        exportData.add(new ArrayList<String>(Arrays.asList("Fourth Choice: ", (Math.round(matchStats.get("Fourth")[1] * 100.0) / 100.0) + "%", "" + matchStats.get("Fourth")[0].intValue(),"","")));
        exportData.add(new ArrayList<String>(Arrays.asList("Fifth+ Choice: ", (Math.round(matchStats.get("Fifth+")[1] * 100.0) / 100.0) + "%", "" + matchStats.get("Fifth+")[0].intValue(),"","")));


        //add columns with titles and different properties
        TableColumn<PreviewTableRow, String> nameCol =  createCol("Name", PreviewTableRow::nameProperty, 150);
        TableColumn<PreviewTableRow, String> idCol =  createCol("ID", PreviewTableRow::idProperty, 150);
        TableColumn<PreviewTableRow, String> majorCol =  createCol("Major", PreviewTableRow::majorProperty, 150);
        TableColumn<PreviewTableRow, String> gpaCol =  createCol("GPA", PreviewTableRow::gpaProperty, 150);
        TableColumn<PreviewTableRow, String> projectCol =  createCol("Project", PreviewTableRow::projProperty, 150);
        TableColumn<PreviewTableRow, String> genderCol =  createCol("Gender", PreviewTableRow::genderProperty, 150);
        /*TableColumn<PreviewTableRow, String> identificationCol = createCol("Identification", PreviewTableRow::identificationProperty, 150);
        TableColumn<PreviewTableRow, String> placementCol = createCol("Placement", PreviewTableRow::placementProperty, 150);*/

        // TODO: Test whether or not allows rows to be moved
        nameCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("Name"));
        idCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("ID"));
        majorCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("Major"));
        gpaCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("GPA"));
        projectCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("Project"));
        genderCol.setCellValueFactory(new PropertyValueFactory<PreviewTableRow, String>("Gender"));

        /*identificationCol.setCellValueFactory((new PropertyValueFactory<PreviewTableRow, String>("Identification")));
        placementCol.setCellValueFactory((new PropertyValueFactory<PreviewTableRow, String>("Placement")));*/

        nameCol.setStyle("-fx-border-width: 1px;" +
                "-fx-border-color: grey;" +
                "-fx-border-style: none none solid none;" +
                "-fx-padding: 4;" +
                "-fx-font: 13px Roboto;");
        idCol.setStyle("-fx-alignment: center;" + "-fx-font: 13px Roboto;");
        majorCol.setStyle("-fx-border-width: 1px;" +
                "-fx-border-color: grey;" +
                "-fx-border-style: none none solid none;" +
                "-fx-padding: 4;" +
                "-fx-font: 13px Roboto;");
        gpaCol.setStyle("-fx-font: 13px Roboto;" + "-fx-alignment: center;");
        projectCol.setStyle("-fx-font: 13px Roboto;" + "-fx-border-width: 1px;" +
                "-fx-border-color: grey;" +
                "-fx-border-style: none none solid none;" +
                "-fx-padding: 4;");
        genderCol.setStyle(
                "-fx-alignment: center;" +
                        "-fx-font: 13px Roboto;");
        /*identificationCol.setStyle("-fx-font: 13px Roboto;" + "-fx-border-width: 1px;" +
                "-fx-border-color: grey;" +
                "-fx-border-style: none none solid none;" +
                "-fx-padding: 4;");
        placementCol.setStyle(
                "-fx-alignment: center;" +
                        "-fx-font: 13px Roboto;");*/


        tableView.getColumns().addAll(nameCol, idCol, majorCol, projectCol, genderCol);
        //tableView.getColumns().addAll(nameCol, idCol, majorCol, gpaCol, projectCol, genderCol, identificationCol, placementCol);

        //set each of the columns to editable and store the columns in an array
        colArray = new TableColumn[tableView.getColumns().size()];
        colList = new ArrayList<>();
        for (int i = 0; i < tableView.getColumns().size(); i++) {
            tableView.getColumns().get(i).setEditable(true);
            colArray[i] = (TableColumn<PreviewTableRow, String>) tableView.getColumns().get(i);
            colList.add(tableView.getColumns().get(i).getText());
        }

        //assign the previewTable data to the table
        tableView.setItems(previewData);

        //The next three functions build the table and add extra functionality: static highlight-able rows, scrollbar capability
        indexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tableView.getSelectionModel().select(index.get());
            }
        });
        tableView.onScrollProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldvalue, Object newValue) {
                System.out.println("scroll bar " + oldvalue);
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PreviewTableRow>() {
            @Override
            public void changed(ObservableValue<? extends PreviewTableRow> observable, PreviewTableRow oldValue, PreviewTableRow newValue) {
                for (TablePosition t : (ObservableList<TablePosition>)
                        tableView.getSelectionModel().getSelectedCells()) {
                }
            }
        });

        //Funcitonality for selecting a row.
        tableView.setRowFactory(tv -> {
            TableRow<PreviewTableRow> row = new TableRow<>();

            //Detecting if the row selected is being dragged.
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });

            /* If a row is being dragged over a different row, set the transfer mode of the hovered-over row to
            COPY_OR_MOVE. This allows the row to be altered in the next event. */
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            /* If a row is being dragged over a different row, handle the different cases
            of dropping it in a new location */
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    PreviewTableRow draggedStudent = tableView.getItems().remove(draggedIndex);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    tableView.getItems().add(dropIndex, draggedStudent);
                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();
                }
            });
            return row;
        });
    }

    //The preview table stage is drawn and displayed to the user.
    public void startmodify(Stage modify, int tableWrittenav, ArrayList<Student> students, ArrayList<Student> invalidStudents) {
        //set the second stage
        modify.setTitle("View Teams");
        modify.setWidth(1500);
        modify.setMinWidth(750);
        modify.setMinHeight(700);
        modify.setHeight(1001);
        modify.setMaxHeight(1500);
        modify.setMaxHeight(1001);
        modify.setX(200);
        modify.setY(150);
        //allow the second stage to be resizeable
        modify.setResizable(false);

        tableView.setStyle("-fx-selection-bar: rgb(25, 59, 104);" +
                "-fx-selection-bar-non-focused: white;" +
                "-fx-padding: 5px;" +
                "-fx-background-color: transparent;" +
                "-fx-base: rgba(255, 255, 255, 0.65);");

        ArrayList<ArrayList<String>> exportData = new ArrayList<ArrayList<String>>();

        //As long as the table has not been written yet, the table is then filled in with data
        if(tableWrittenav == 0) {
            writeTable(students, invalidStudents, exportData);
        }
        tableWrittenav++;

        //Set the Alter Teams stage's pane to a border pane with the table as the center of the border pane.
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(tableView);

        {//View Statistics and Export CSV functionality

            //Create artificial spacing for the top of the Alter Teams stage. Give the spacing its own box.
            Label spacingLabel = new Label("                                    ");
            HBox box2 = new HBox();
            box2.getChildren().add(spacingLabel);

            /* Create a horizontal box that will be the primary storage for
            the contents of the top of the Alter Teams stage. */
            HBox filestatsanddownload = new HBox(6);

            //Create and style a Statistics button
            Button viewStatsButton = new Button("View Statistics");
            viewStatsButton.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 4;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-background-color: rgb(225, 225, 225);" +
                    "-fx-background-radius: 1px;" +
                    "-fx-border-radius: 6;");

            //Create and style a Export File button
            Button exportButton = new Button("Export File");
            exportButton.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 4;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 1;" +
                    "-fx-background-color: rgb(225, 225, 225);" +
                    "-fx-background-radius: 1px;" +
                    "-fx-border-radius: 6;");

            //Provide an area for the user to input a file name.
            TextField exportFileName = new TextField();

            /* Create, style and set the prompt text for the text field. This text will
            disappear once the user inputs text in to the text box provided. */
            exportFileName.setPromptText("Enter file name");

            //Set the style of the text field itself.
            exportFileName.setStyle("-fx-border-style: solid outside;" +
                    "-fx-border-width: 2;" + "-fx-border-insets: 1;" +
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 1px;" +
                    "-fx-border-radius: 6;"
            );

            //Handling the key typed event
            EventHandler<KeyEvent> eventHandlerTextField = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    //Playing the animation
                    exportButton.setDisable(false);
                }
            };

            //Adding an event handler to the text field
            exportFileName.addEventHandler(KeyEvent.KEY_TYPED, eventHandlerTextField);

            Stage stats = new Stage();
            Boolean statsStageav = true;

            //when the stats page button is pressed, the stats window is presented to the user
            viewStatsButton.setOnMousePressed(MouseEvent -> {
                statsStage(stats, statsStageav, modify, students);
            });

            //when the user presses the export Button, and the textbox is not empty, then the file is exported with the extension .csv
            exportButton.setOnMousePressed(f ->
            {
                if (exportFileName.getText().trim().isEmpty()) {
                    exportButton.setDisable(true);
                } else{
                    try {
                        exportcsv(tableView, exportFileName, exportData, colArray, colList);
                    } catch (Exception d) {
                        d.printStackTrace();
                    }
                    //Download the file from the computer with the name input in the textfield.
                    //The export file is the file created after the matching algorithm finishes and the document is formed.
                }
            });

            Label label2 = new Label(" .csv ");

            //a toolbar is a special type of JavaFx construction, instead of multiple buttons and labels
            ToolBar tb = new ToolBar(viewStatsButton, exportButton, exportFileName, label2);
            tb.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 4;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-color: white;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-color: white");

            filestatsanddownload.getChildren().add(tb);
            filestatsanddownload.setMinHeight(45);
            filestatsanddownload.setPrefHeight(45);

            filestatsanddownload.setStyle("-fx-font: 15px Roboto;" +
                    "-fx-padding: 4;" +
                    "-fx-border-style: solid outside;" +
                    "-fx-border-color: white;" +
                    "-fx-border-width: 2;" +
                    "-fx-background-color: white");

            box2.setAlignment(Pos.CENTER);
            exportButton.setAlignment(Pos.CENTER);
            exportFileName.setAlignment(Pos.CENTER);
            label2.setAlignment(Pos.CENTER);
            filestatsanddownload.setAlignment(Pos.CENTER);
            borderPane.setTop(filestatsanddownload);
            Scene scene = new Scene(borderPane, 600, 400);
            modify.setScene(scene);
            modify.show();

        }
    }

    private TableColumn<PreviewTableRow, String> createCol(String title, Function<PreviewTableRow, ObservableValue<String>> mapper, double size) {
        TableColumn<PreviewTableRow, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> mapper.apply(cellData.getValue()));
        col.setPrefWidth(size);
        return col ;
    }

    /* This function uses the procured exportData and writes to an exported .csv file. The data included in the exported
    * .csv document: all the data from the preview table with additional ancillary information about students. Statistics
    * as well as students not matched to teams are included in the export document.  */
    public void exportcsv(TableView tableView, TextField exportFileName,  ArrayList<ArrayList<String>> exportData, TableColumn<PreviewTableRow, String> [] colArray, List<String> colList) throws Exception{

        try {
            File exportFile = new File(System.getProperty("user.home") + "/Desktop");
            File exportFile2 = new File(exportFileName.getCharacters() + ".csv");
            exportFile.renameTo(exportFile2);
            FileWriter csvWriter = new FileWriter(exportFile2);

            csvWriter.append(String.join(", ", colList));
            csvWriter.flush();
            csvWriter.append("\n");

            for (List<String> rowData : exportData) {
                csvWriter.append(String.join(", ", rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    //Launch the application
    public static void main(String[] args) {
        launch(args);
    }
}