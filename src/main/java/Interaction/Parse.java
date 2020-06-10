package Interaction;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import Objects.Project;
import Objects.Student;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Parse {

    private static Pattern validPref = Pattern.compile("[1-7]"); // Modify if students are allowed more project preferences

    /* ParseProjectInputFile() is a function which parses input from a CSV file containing project data.
     * Project data is stored in the static variable Project.projectList.  The function utilizes the
     * Apache Commons CSV Parser.
     *
     * @param csvFile - the CSV file containing project and student data
     * @return void -  data is populated into static variables (Project.projectList & Student.studentList)
     */
    public static void parseProjectInputFile(File csvFile) {
        try {
            CSVParser projParser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.EXCEL.withFirstRecordAsHeader());

            for (CSVRecord record : projParser) {
                // Parse project data
                String fullListing = record.get("Full Listing");
                String name = record.get("Project Name");
                String type = record.get("Type");
                String sponsor = record.get("Sponsor");
                int minTeamSize = Integer.parseInt(record.get("Min Size"));
                int maxTeamSize = Integer.parseInt(record.get("Max Size"));
                String preassignedStudentsString = record.get("Preassigned Student IDs").trim();

                // Create Project object
                Project proj = new Project(fullListing, name, type, sponsor, preassignedStudentsString, minTeamSize, maxTeamSize, (int)record.getRecordNumber());
                Project.projectList.add(proj);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    /* ParseStudentInputFile() is a function which parses input from a CSV file containing student data.
     * Student data is stored in the static variable Student.studentList.  The function utilizes the
     * Apache Commons CSV Parser.
     *
     * @param csvFile - the CSV file containing project and student data
     * @return void -  data is populated into static variables (Project.projectList & Student.studentList)
     */
    public static void parseStudentInputFile(File csvFile) {
        final int NUM_PREFERENCES = 7;  // Modify if students are allowed more project preferences
        try {
            CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.EXCEL.withFirstRecordAsHeader());
            for (String header : parser.getHeaderNames()) {
                if(header.toLowerCase().contains("skills")) {
                    Student.skillsList.add(header);
                }
            }

            // For each CSV record, capture relevant student data
            for (CSVRecord csvRecord : parser) {
                boolean validStudentInput = true;
                String id = csvRecord.get("Student ID");
                String first = csvRecord.get("First Name");
                String last = csvRecord.get("Last Name");
                String fullName = first + " " + last;
                String major = csvRecord.get("Major");
                String gender = csvRecord.get("Gender");
                ArrayList<Project> prefs = new ArrayList<Project>();
                ArrayList<Project> remainingToApply = new ArrayList<Project>();
                ArrayList<Project> avoid = new ArrayList<Project>();
                HashMap<String, String> skills = new HashMap<>();

                for (int i = 0; i < NUM_PREFERENCES; i++) {
                    prefs.add(new Project());
                    remainingToApply.add(new Project());
                }

                for (int i = 0; i < Project.projectList.size(); i++) {
                    String projHeader = Project.projectList.get(i).getFullListing();
                    String value = csvRecord.get(projHeader);
                    if (validPref.matcher(value).matches()) {
                        int pref = Integer.parseInt(value) - 1;
                        prefs.set(pref, Project.projectList.get(i));
                        remainingToApply.set(pref, Project.projectList.get(i));
                    } else if (value.equalsIgnoreCase("Avoid")) {
                        avoid.add(Project.projectList.get(i));
                    } else if (value.equalsIgnoreCase("")) {
                        continue;
                    } else { // entry is invalid
                        validStudentInput = false;
                    }
                }

                for (int i = 0; i < Student.skillsList.size(); i++) {
                    String value = csvRecord.get(Student.skillsList.get(i));
                    skills.put(Student.skillsList.get(i), value);
                }
                Student student = new Student((int) csvRecord.getRecordNumber(), fullName, id, major, gender, prefs, remainingToApply, skills);
                if (validStudentInput && validateStudentEntries(student)) {
                    Student.studentList.add(student); 
                }
                else {
                    System.out.println("Invalid input entry by student: " + fullName + ".  Please modify input entry and retry.");
                    Student.invalidInputStudentList.add(student);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Function to validate that a parsed student object is able to be run through the algorithm
    private static boolean validateStudentEntries(Student student) {
        if (student.studentName == null || student.studentName.equals("")) {
            return false;
        }
        else if (student.studentId == null) {
            return false;
        }
        else if (student.major == null || student.major.equals("")) {
            return false;
        }
        else if (student.preferredProjects == null || student.preferredProjects.isEmpty() || !validateStudentPreferences(student.preferredProjects)) {
            return false;
        }
        else if (student.remainingProjectsToApply == null || student.remainingProjectsToApply.isEmpty() || !validateStudentPreferences(student.remainingProjectsToApply)) {
            return false;
        }
        else if (student.parseId == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    // Function to validate that a student has entered all of their project preferences
    private static boolean validateStudentPreferences(ArrayList<Project> prefs) {
        for (Project p : prefs) {
            if (p.maxTeamSize == -1) {
                return false; // if they did not indicate one of their project preferences, return false
            }
        }
        return true;
    }
}
