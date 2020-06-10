package Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Project {
    // --------------------- Member Variables --------------------- //
    public int csvIndex;
    public int projectId;
    public int maxTeamSize;
    public int minTeamSize;
    public int popularity; // Integer representing how many students wanted a project and to what degree (larger int => more desired)
    public String fullListing;
    public String projectName;
    public String projectType; // i.e. Industry, Entrepreneurial, etc.
    public String sponsorName;
    public String preassignedStudentIds;
    public HashMap<String, Integer> majorPreferences; // Maps Major (String) to number of students required for each major (int)
    public CopyOnWriteArrayList<Student> preferredStudents; // A ranking of every student who has this project on their preference list (and therefore each student who could potentially be assigned to this project)
    public ArrayList<Student> assignedStudents;

    public static ArrayList<Project> projectList = new ArrayList<Project>();

    // --------------------- Constructors --------------------- //
    public Project() {
        this.projectName = null;
        this.projectId = -1;
        this.projectType = null;
        this.sponsorName = null;
        this.preferredStudents = null;
        this.assignedStudents = null;
        this.majorPreferences = null;
        this.maxTeamSize = -1;
    }

    // This is the one that is used by parsing
    public Project(String fullListing, String projectName, String projectType, String sponsorName, String preassignedStudentIds,
                   int minTeamSize, int maxTeamSize, int projectId) {
        this.fullListing = fullListing;
        this.projectName = projectName;
        this.projectType = projectType;
        this.sponsorName = sponsorName;
        this.preassignedStudentIds = preassignedStudentIds;
        this.minTeamSize = minTeamSize;
        this.maxTeamSize = maxTeamSize;
        this.projectId = projectId;
        this.preferredStudents = new CopyOnWriteArrayList<Student>();
        this.assignedStudents = new ArrayList<Student>();
    }

    // --------------------- Functions --------------------- //

    // Function to assign students that were preassigned to a project
    public void preassignStudents(String studentIds) { // studentIds = { #, #, ..., # }
        String[] idArray = studentIds.split("[\r\n\t\f ]*,[\r\n\t\f ]*"); // Regex allows for any number of spaces before or after ','
        for (String id : idArray) {
            if (this.assignedStudents.size() < this.maxTeamSize) { // If there is space on the project
                Student student = Student.getStudentById(id);
                // Assign student to project
                this.assignedStudents.add(student);
                student.assignedProject = this;
                // Set student as project's top preference
                this.preferredStudents.removeIf(s -> (s == student));
                this.preferredStudents.add(0, student);
            }
        }
    }

    public static void addProject(Project project) {
        projectList.add(project);
    }

    /* Removes student (s) from their assigned project (p) and prevents (s) from reapplying to (p) later on */
    public static void denyStudentFromProject(Student s, Project p) {
        s.assignedProject = (s.assignedProject != p) ? s.assignedProject : null;  // Unassign s from their project only if that project is p.  Otherwise, leave them on the other project.
        s.remainingProjectsToApply.remove(p);
        p.assignedStudents.remove(s);
        p.preferredStudents.remove(s);
    }

    /* Returns the lowestPreferredStudent that is assigned to a given project p */
    public static Student getLowestPreferred(Project p) {
        Student currentLowest = p.assignedStudents.get(0);
        for (Student s : p.assignedStudents) {
            // if student s is less-preferred than the currentLowest, s becomes currentLowest
            if (p.preferredStudents.indexOf(s) > p.preferredStudents.indexOf(currentLowest)) {
                currentLowest = s;
            }
        }
        return currentLowest;
    }

    public static int getNumProjects() {
        return projectList.size();
    }

    // Initializes the list of preferredStudents for a project (randomly)
    public static void setPreferredStudents(Project project) {
        Student.randomizeStudentOrder(Student.studentList);
        for (Student student : Student.studentList) {
            if (student.preferredProjects.contains(project)) {
                project.preferredStudents.add(student);
            }
        }
    }

    public String getFullListing() {
        return fullListing;
    }

    public void setFullListing(String fullListing) {
        this.fullListing = fullListing;
    }
} 
