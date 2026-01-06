// Course.java
// Ders varlık sınıfı: courseCode, courseName, assignedTeacherId (atanmamışsa null).

public class Course {
    private String courseCode;
    private String courseName;
    private String assignedTeacherId; // atanmadıysa null

    public Course(String courseCode, String courseName, String assignedTeacherId) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.assignedTeacherId = assignedTeacherId;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getAssignedTeacherId() { return assignedTeacherId; }
    public void setAssignedTeacherId(String assignedTeacherId) { this.assignedTeacherId = assignedTeacherId; }

    @Override
    public String toString() {
        // courseCode,courseName,assignedTeacherId
        return courseCode + "," + courseName + "," + (assignedTeacherId == null ? "" : assignedTeacherId);
    }

    public static Course fromString(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length < 2) return null;
        String teacherId = arr.length >= 3 ? arr[2] : "";
        return new Course(arr[0], arr[1], teacherId.isEmpty() ? null : teacherId);
    }
}
