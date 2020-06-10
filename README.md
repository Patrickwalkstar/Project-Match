# Project Match
Project Match is a software application that creates Senior Design teams by matching a collection of students to a set of available projects.  The algorithm used to produce these teams is based on an algorithm outlined in the paper *Two Algorithms for the Student-Project Allocation Problem* by Abraham, Irving, and Manlove (https://www.cs.cmu.edu/~dabraham/papers/aim04.pdf).  

The goal of this application is to match students to one of their top three project preferences.


## Getting Started
Begin by cloning the repository to your local machine.  Here is a tutorial to do so: https://help.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository.

If you do not already have an IDE that you enjoy working with, the original Project Match Team all used **IntelliJ IDEA**.  It can be downloaded here (https://www.jetbrains.com/idea/download/#section=mac) and free student licenses can be acquired here (https://www.jetbrains.com/community/education/#students).


## Installation
   You may need to install the following tools if you have not already:

   * **Java JDK (14.0.1)**
      Installation Guide: https://www.oracle.com/java/technologies/javase-downloads.html

   * **JavaFx (14.0.1)**
      Installation Guide for Mac: https://openjfx.io/openjfx-docs/   
      Download Page: https://gluonhq.com/products/javafx/

   * **Maven (3.6.3)**     
      Installation Guide for Mac: https://www.journaldev.com/2348/install-maven-mac-os#1-installing-java-on-mac
      Download Page: https://maven.apache.org/download.cgi


## IDE Setup
Open up the Project Match folder in your IDE. ***The following setup tutorial will be using IntelliJ IDEA**.*

**1. Configure your Project Structure**
While in IntelliJ IDEA, at the top of your screen select 'File' -> 'Project Structure'.

* **Platform Settings**

    Under SDKs, add the Java JDK 14.0.1.  Here is a helpful guide: https://www.jetbrains.com/help/idea/sdk.html

    Under Global Libraries, add the JavaFx 14.0.1 library.  Here is a helpful tutorial: https://www.youtube.com/watch?v=82QcFSstJs0

* **Project Settings**

    Under Project, ensure that you have selected JDK 14.0.1 as your Project SDK.  Additionally, it may help to specify Language Level 12 (though I don't know that it makes a difference).

    Under Libraries, ensure that you have added the Apache Commons CSV 1.8 (should be added by Maven once Apache Commons CSV has been installed) and ancillary JavaFx files (see JavaFx tutorial used to install JavaFx in Global Libraries just above).   

---
**2. Add Run/Debug Configurations**
While in IntelliJ IDEA, at the top of your screen select 'Run' -> 'Edit Configurations'.

If you do not already have a configuration called ProjectMatchFx under the Applications section on the left sidebar, select the '+' -> 'Application'.  Then, fill in the following information:

  * Name: ProjectMatchFx
  * Main Class: Interaction.ProjectMatchFx
  * VM Options: Enter the following line without enclosing quotes:
  
  "--module-path "***PATH_TO_JAVAFX_SDK***/javafx-sdk-14.0.1/lib" --add-modules=javafx.controls,javafx.fxml" 
  
  Note: Instead of ***PATH_TO_JAVAFX_SDK***, copy and paste the actual path to your JavaFx SDK.  For example, my path looks like this: "/Users/zachfukuhara/Tools/javafx-sdk-14.0.1/lib"
  
  If you are having trouble locating it, you should be able to find it in the *Global Libraries* section of your *Project Structure* that we set up above.
  
---
**3. Run the application**
At this stage, you should be able to run the Project Match application by pressing the green arrow towards the top right of your screen or by selecting 'Run' -> 'Run 'ProjectMatchFx''.  Alternatively, you can create a **JAR file** using Maven and run the application from that file.


## Creating and Using a JAR File
Using Maven, you can create a JAR file by completing the following steps:

   1. In Terminal, navigate into the directory containing the clone of this repository using *cd* commands.  At this point, you should be in the "Project-Match" directory.
   
   2. Execute the following commands:
   ```
   $ mvn clean
   $ mvn compile
   $ mvn package
   ```
   This set of commands will create two JAR files in the "target" directory within "Project-Match".  You will need to re-run these commands each time you add a new feature to the application, as the JAR file does not automatically update itself.  The two generated JAR files should look something like:
   
   * "Project-Match-1.0-SNAPSHOT-jar-with-dependencies.jar"  // Run this one
   
   * "Project-Match-1.0-SNAPSHOT.jar" // Don't run this one
   
   If you're having trouble finding them, make sure you navigate to the directory named "target" using *cd* commands.  
   
   **To run the Project Match application via a JAR file, double click the "Project-Match-1.0-SNAPSHOT-jar-with-dependencies.jar" file.**  *Do NOT use the "Project-Match-1.0-SNAPSHOT.jar" as it does not have the proper dependencies and will not run."*


## Project Match Application Workflow
Once the application opens (either via an IDE or an executable), follow these steps:

   1. Click on the "Import Project File" button and select a CSV file containing information about each of the Senior Design *Projects*.

   2. Click on the "Import Student File" button and select a CSV file containing information about each of the Senior Design *Students*.

   3. Click on the "Run Algorithm" button to create the teams and view a preview page of the results.

   4. View the teams.  Feel free to adjust the width of each of the columns.

   5. Click on the "View Statistics" button to view the Match Statistics.

   6. Enter a file name of your choice, and then click the "Export File" button to export the teams to a CSV file.  This file can be view in such applications as Microsoft Excel, Google Sheets, or Apple Numbers.

Note: To run another iteration of the application, you must exit out of all existing instances and start over.  We suggest future teams implement functionality to be able to rerun the algorithm without having to exit out of the application.


## Some Things to Keep in Mind

* Both Student and Project input files must match a specific templated format that will be provided in this repository.  If these files do not match this format, the application will not parse the data correctly and there will likely be errors.

* Students that do not fill out their input surveys correctly will not be matched to projects by the algorithm.  This was a design choice made by request of our sponsor, and these students will need to contact the Senior Design administrator to be matched.  A list of all students that are unmatched due to errors in input will be displayed at the bottom of the exported CSV file.
