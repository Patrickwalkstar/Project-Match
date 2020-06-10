package Algorithm;
import java.util.List;
import Objects.Student;
import Objects.Project;

public class MatchAlgorithm {
    public static void runAlgorithm(List<Student> students, List<Project> projects) {
        // Initialize each student to be unassigned
        for (Student student : students) {
            student.assignedProject = null;
        }
        // Assign each project to be unassigned
        for (Project project : projects) {
            project.assignedStudents.clear();
            Project.setPreferredStudents(project);

            // If the project has pre-assigned students, assign them
            if (project.preassignedStudentIds.length() > 0) {
                project.preassignStudents(project.preassignedStudentIds);
            }
        }

        Student s = Student.getFreeStudentWithPrefs(students);
        while(s != null) {
            // Provisionally assign Student s to their #1 preferred project
            Project p = s.remainingProjectsToApply.get(0);
            if (p.assignedStudents == null) {
                System.out.println("Error at student: " + s.studentName);
            }
            p.assignedStudents.add(s);
            s.assignedProject = p;

            // If p has too many students, remove the lowestPreferredStudent assigned to p
            if (p.assignedStudents.size() > p.maxTeamSize) {
                Student lowestPreferredStudent = Project.getLowestPreferred(p);
                Project.denyStudentFromProject(lowestPreferredStudent, p); // Remove lowestPreferredStudent & prevent them from reapplying to p
            }

            // If p is full, remove all students who have p as a preference, 
            // but who are preferred less than the least preferred student already assigned to p
            if (p.assignedStudents.size() == p.maxTeamSize) {
                Student lowestPreferredStudent = Project.getLowestPreferred(p);
                for (Student potentialStudent : p.preferredStudents) {
                    // if the potentialStudent is less-preferred than the least-preferred student assigned to project p, remove them as a potential student for p
                    if (p.preferredStudents.indexOf(lowestPreferredStudent) < p.preferredStudents.indexOf(potentialStudent)) {
                        Project.denyStudentFromProject(potentialStudent, p);
                    }
                }
            }
            s = Student.getFreeStudentWithPrefs(students);
        }
        Student.sortStudentsByTeam(students); // Sort students by project team
    }
}