package Objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;

public class Student {
    // ------------------- Member Variables ------------------- //
    public int parseId = -1;  // Unique ID value to be assigned during initial parsing of student input file
    public String studentName;
    public String studentId;  // Keep as string to preserve leading 0's (ex. 009142408)
    public String gender;
    public String major;
    public String email;
    public Project project;
    public ArrayList<Project> preferredProjects;
    public ArrayList<Project> remainingProjectsToApply = new ArrayList<Project>();
    public ArrayList<Project> avoid = new ArrayList<Project>();
    public HashMap<String, String> skillsMap = new HashMap<>();
    public Project assignedProject;
    public static ArrayList<Student> studentList = new ArrayList<Student>(); // list of students that gave valud input and were included in the matching
    public static ArrayList<Student> invalidInputStudentList = new ArrayList<Student>();  // list of students that gave invalid input and were excluded from the matching
    public static ArrayList<String> skillsList = new ArrayList<String>();

    // ------------------- Constructors ------------------- //
    public Student() {
        this.studentName = null;
        this.studentId = null;
        this.gender = null;
        this.major = null;
        this.skillsMap = null;
        this.preferredProjects = null;
        this.remainingProjectsToApply = null;
        this.avoid = null;
        this.assignedProject = null;
    }

    public Student(int parseId, String studentName, String studentId, String major, String gender, ArrayList<Project> preferredProjects, ArrayList<Project> remainingProjectsToApply, HashMap<String, String> skillsMap) {
        this.parseId = parseId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.major = major;
        this.gender = gender;
        this.preferredProjects = preferredProjects;
        this.remainingProjectsToApply = remainingProjectsToApply;
        this.skillsMap = skillsMap;
    }


    // ------------------- Functions ------------------- //
    /* Checks if there exists a student that is not yet assigned to a project and that has preferences */
    public static Student getFreeStudentWithPrefs(List<Student> students) {
        for (Student student : students) {
            if (student.assignedProject == null && student.remainingProjectsToApply.size() > 0) {
                return student;
            }
        }
        return null;
    }

    public static void randomizeStudentOrder(List<Student> students) {
        Collections.shuffle(students);
    }

    public static void sortStudentsByTeam(List<Student> students) {
        students.sort(Comparator.nullsLast(Comparator.comparing(Student::getAssignedProject, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))));
    }

    public HashMap<String, String> getSkillsMap() { return skillsMap; }

    public void setSkillsMap(HashMap<String, String> skillsMap) { this.skillsMap = skillsMap; }

    public static Student getStudentById(String id) {
        for (Student student : Student.studentList) {
            if (student.studentId.equals(id)) {
                return student;
            }
        }
        return null;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<Project> getPreferredProjects() {
        return this.preferredProjects;
    }

    public void setPreferredProjects(ArrayList<Project> preferredProjects) {
        this.preferredProjects = preferredProjects;
    }

    public String getAssignedProject() {
        if (this.assignedProject != null) {
            return this.assignedProject.projectName;
        }
        return "-- Unassigned --";
    }

    public static void printStudent(Student stud) {
        System.out.println(String.format("%s, %s, %s, %s", stud.getStudentName(), stud.getStudentId(), stud.getMajor(), stud.getPreferredProjects()));
    }

}

