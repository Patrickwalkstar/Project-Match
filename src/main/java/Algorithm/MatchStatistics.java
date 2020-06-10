package Algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import Objects.Student;
import Objects.Project;

public class MatchStatistics {

    public static HashMap<String, Double[]> calculateMatchStats(ArrayList<Student> students) {
        int x = -1;
        double pref1 = 0.0, pref2 = 0.0, pref3 = 0.0, pref4 = 0.0, pref5orMore = 0.0;
        HashMap<String, Double[]> matchStatistics = new HashMap<String, Double[]>();

        for (Student student : students) {
            x = student.preferredProjects.indexOf(student.assignedProject);

            switch (x) {
                case 0:
                    pref1++;
                    break;
                case 1:
                    pref2++;
                    break;
                case 2:
                    pref3++;
                    break;
                case 3:
                    pref4++;
                    break;
                default:
                    pref5orMore++; // Could include students that do not get assigned
            }
        }

        double top4 = pref1 + pref2 + pref3 + pref4;
        //Calculate and store each satisfaction score, the percentage of students that recieved their nth preference.
        matchStatistics.put("First", new Double[] { pref1, (pref1 / (double) students.size()) });
        matchStatistics.put("Second", new Double[] { pref2, (pref2 / (double) students.size()) });
        matchStatistics.put("Third", new Double[] { pref3, (pref3 / (double) students.size()) });
        matchStatistics.put("Fourth", new Double[] { pref4, (pref4 / (double) students.size()) });
        matchStatistics.put("Fifth+", new Double[] { (students.size() - top4), (1.0 - (top4 / (double)students.size())) });

        return matchStatistics;
    }

    public static ArrayList<Project> CalculateProjectPopularity(ArrayList<Student> students, ArrayList<Project> projects) {
        ArrayList<Project> top3Projects = new ArrayList<Project>();
        return top3Projects;
    }
}