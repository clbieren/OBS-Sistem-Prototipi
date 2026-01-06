// Course.java
public class Course {
    private String courseCode;
    private String courseName;
    private String assignedTeacherId;
    public Course(String code, String name, String assignedTeacherId) {
        this.courseCode=code; this.courseName=name; this.assignedTeacherId=assignedTeacherId;
    }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getAssignedTeacherId() { return assignedTeacherId; }
    public void setAssignedTeacherId(String id) { assignedTeacherId = id; }
    public String toString() {
        return courseCode+","+courseName+","+((assignedTeacherId==null)?"":assignedTeacherId);
    }
    public static Course fromString(String line) {
        String[] arr = line.split(",",-1);
        if (arr.length<2) return null;
        String teacherId = arr.length==3 ? arr[2] : "";
        return new Course(arr[0], arr[1], teacherId.isEmpty()?null:teacherId);
    }
}