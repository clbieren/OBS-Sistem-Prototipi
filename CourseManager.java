// CourseManager.java
import java.util.*;

public class CourseManager {
    private final String courseFile = "courses.txt";
    private final String gradeFile = "grades.txt";
    private List<Course> courses;
    public CourseManager() { courses = new ArrayList<>(); loadCourses(); }
    public void loadCourses() {
        courses.clear();
        for (String line : FileHelper.readFile(courseFile)) {
            Course c = Course.fromString(line);
            if (c!=null) courses.add(c);
        }
    }
    public void saveCourses() {
        List<String> lines = new ArrayList<>();
        for (Course c : courses) lines.add(c.toString());
        FileHelper.writeFile(courseFile, lines);
    }
    public boolean addCourse(Course c) {
        if (getCourseByCode(c.getCourseCode())!=null) return false;
        courses.add(c); saveCourses(); return true;
    }
    public boolean removeCourse(String courseCode) {
        Course c = getCourseByCode(courseCode);
        if (c==null) return false;
        courses.remove(c); saveCourses(); return true;
    }
    public Course getCourseByCode(String code) {
        for (Course c : courses)
            if (c.getCourseCode().equals(code)) return c;
        return null;
    }
    public List<Course> getAllCourses() { return courses; }
    // Ders öğretmene atanır; bir öğretmene istenen sayıda ders atanabilir
    public boolean assignCourseToTeacher(String courseCode, String teacherId) {
        Course c = getCourseByCode(courseCode);
        if (c == null) return false;
        c.setAssignedTeacherId(teacherId); saveCourses(); return true;
    }
    public List<Course> getCoursesByTeacher(String teacherId) {
        List<Course> result = new ArrayList<>();
        for (Course c : courses)
            if (teacherId.equals(c.getAssignedTeacherId()))
                result.add(c);
        return result;
    }
    // Öğrenci derse kaydolur
    public boolean addStudentCourse(String studentId, String courseCode) {
        for (String line : FileHelper.readFile(gradeFile)) {
            String[] arr = line.split(",");
            if (arr.length>=2 && arr[0].equals(studentId) && arr[1].equals(courseCode))
                return false;
        }
        FileHelper.appendToFile(gradeFile, studentId + "," + courseCode + ",,");
        return true;
    }
    // Not ekleme/güncelleme
    public void updateGrade(String studentId, String courseCode, String midterm, String finale) {
        List<String> all = FileHelper.readFile(gradeFile);
        boolean updated = false;
        for (int i=0;i<all.size();i++) {
            String[] arr = all.get(i).split(",");
            if (arr.length>=2 && arr[0].equals(studentId) && arr[1].equals(courseCode)) {
                all.set(i, studentId + "," + courseCode + "," + midterm + "," + finale);
                updated=true; break;
            }
        }
        if (!updated) all.add(studentId + "," + courseCode + "," + midterm + "," + finale);
        FileHelper.writeFile(gradeFile, all);
    }
    public List<String[]> getStudentsByCourse(String courseCode) {
        List<String[]> result = new ArrayList<>(), lines = new ArrayList<>();
        for (String l : FileHelper.readFile(gradeFile)) {
            String[] arr = l.split(",");
            if (arr.length>=2 && arr[1].equals(courseCode)) result.add(arr);
        }
        return result;
    }
    public List<String[]> getGradesByStudent(String studentId) {
        List<String[]> result = new ArrayList<>();
        for (String l : FileHelper.readFile(gradeFile)) {
            String[] arr = l.split(",");
            if (arr.length>=2 && arr[0].equals(studentId)) result.add(arr);
        }
        return result;
    }
}