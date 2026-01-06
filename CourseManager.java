// CourseManager.java
// Derslerle ve not/öğrenci-ders kayıtlarıyla ilgili işlemler burada.
// Ayrıca admin için ders ekleme/atama/listeleme yardımcı metodları eklendi.

import java.util.*;

public class CourseManager {
    private final String courseFile = "courses.txt";
    private final String gradeFile = "grades.txt";
    private List<Course> courses;

    public CourseManager() {
        courses = new ArrayList<>();
        loadCourses();
    }

    public void loadCourses() {
        courses.clear();
        for (String line : FileHelper.readFile(courseFile)) {
            Course c = Course.fromString(line);
            if (c != null) courses.add(c);
        }
    }

    public void saveCourses() {
        List<String> lines = new ArrayList<>();
        for (Course c : courses) lines.add(c.toString());
        FileHelper.writeFile(courseFile, lines);
    }

    public boolean addCourse(Course c) {
        if (getCourseByCode(c.getCourseCode()) != null) return false;
        courses.add(c);
        saveCourses();
        return true;
    }

    public boolean removeCourse(String courseCode) {
        Course c = getCourseByCode(courseCode);
        if (c == null) return false;
        courses.remove(c);
        saveCourses();
        return true;
    }

    public Course getCourseByCode(String code) {
        for (Course c : courses) if (c.getCourseCode().equals(code)) return c;
        return null;
    }

    public List<Course> getAllCourses() { return courses; }

    // Ders atama: bir öğretmene birden fazla ders atanabilir
    public boolean assignCourseToTeacher(String courseCode, String teacherId) {
        Course c = getCourseByCode(courseCode);
        if (c == null) return false;
        c.setAssignedTeacherId(teacherId);
        saveCourses();
        return true;
    }

    public List<Course> getCoursesByTeacher(String teacherId) {
        List<Course> res = new ArrayList<>();
        for (Course c : courses) {
            if (teacherId.equals(c.getAssignedTeacherId())) res.add(c);
        }
        return res;
    }

    // Bir öğrencinin bir dersi seçip seçmediğini kontrol eder
    public boolean isCourseSelectedByStudent(String studentId, String courseCode) {
        for (String line : FileHelper.readFile(gradeFile)) {
            String[] arr = line.split(",", -1);
            if (arr.length >= 2 && arr[0].equals(studentId) && arr[1].equals(courseCode)) return true;
        }
        return false;
    }

    // Öğrenci dersi seçer (grades.txt'e öğrenciId,courseCode,,
    public boolean addStudentCourse(String studentId, String courseCode) {
        if (isCourseSelectedByStudent(studentId, courseCode)) return false;
        FileHelper.appendToFile(gradeFile, studentId + "," + courseCode + ",,");
        return true;
    }

    // Tam değiştirerek not ekleme/güncelleme (mevcut değer korunmaz)
    public void updateGrade(String studentId, String courseCode, String midterm, String finale) {
        List<String> all = FileHelper.readFile(gradeFile);
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            String[] arr = all.get(i).split(",", -1);
            if (arr.length >= 2 && arr[0].equals(studentId) && arr[1].equals(courseCode)) {
                all.set(i, studentId + "," + courseCode + "," + midterm + "," + finale);
                updated = true;
                break;
            }
        }
        if (!updated) all.add(studentId + "," + courseCode + "," + midterm + "," + finale);
        FileHelper.writeFile(gradeFile, all);
    }

    // Kısmi güncelleme: eğer midInput veya finalInput boş veya null ise mevcut değeri korur.
    public void updateGradePartial(String studentId, String courseCode, String midInput, String finalInput) {
        List<String> all = FileHelper.readFile(gradeFile);
        boolean updated = false;
        for (int i = 0; i < all.size(); i++) {
            String[] arr = all.get(i).split(",", -1);
            if (arr.length >= 2 && arr[0].equals(studentId) && arr[1].equals(courseCode)) {
                String existingMid = arr.length > 2 ? arr[2] : "";
                String existingFinal = arr.length > 3 ? arr[3] : "";
                String newMid = (midInput == null || midInput.isEmpty()) ? existingMid : midInput;
                String newFinal = (finalInput == null || finalInput.isEmpty()) ? existingFinal : finalInput;
                all.set(i, studentId + "," + courseCode + "," + newMid + "," + newFinal);
                updated = true;
                break;
            }
        }
        if (!updated) {
            String mid = (midInput == null) ? "" : midInput;
            String fin = (finalInput == null) ? "" : finalInput;
            all.add(studentId + "," + courseCode + "," + mid + "," + fin);
        }
        FileHelper.writeFile(gradeFile, all);
    }

    // Bir derse kayıtlı öğrencilerin (kayit satırları) döndürülmesi
    // Her satır arr: [studentId, courseCode, midterm, final]
    public List<String[]> getStudentsByCourse(String courseCode) {
        List<String[]> res = new ArrayList<>();
        for (String line : FileHelper.readFile(gradeFile)) {
            String[] arr = line.split(",", -1);
            if (arr.length >= 2 && arr[1].equals(courseCode)) res.add(arr);
        }
        return res;
    }

    // Bir öğrencinin tüm not/sınıf seçimleri (satır bazlı)
    public List<String[]> getGradesByStudent(String studentId) {
        List<String[]> res = new ArrayList<>();
        for (String line : FileHelper.readFile(gradeFile)) {
            String[] arr = line.split(",", -1);
            if (arr.length >= 2 && arr[0].equals(studentId)) res.add(arr);
        }
        return res;
    }

    // Öğrencinin AGNO'sunu hesaplar: her derste vize%40 + final%60, sadece hem vize hem final varsa hesaba katılır.
    public double computeAGNO(String studentId) {
        List<String[]> grades = getGradesByStudent(studentId);
        double sum = 0;
        int count = 0;
        for (String[] arr : grades) {
            if (arr.length > 2 && arr.length > 3 && !arr[2].isEmpty() && !arr[3].isEmpty()) {
                try {
                    double v = Double.parseDouble(arr[2]);
                    double f = Double.parseDouble(arr[3]);
                    double avg = v * 0.4 + f * 0.6;
                    sum += avg;
                    count++;
                } catch (NumberFormatException ignored) {}
            }
        }
        if (count == 0) return -1;
        return sum / count;
    }

    // Admin yardımcıları: kurs ekleme, atama ve listeleme (teacher bilgisi gerektiğinde TeacherManager verilir)
    public void adminAddCourse(Scanner in) {
        System.out.print("Ders kodu (veya 'q' geri): ");
        String code = in.nextLine();
        if (code.equalsIgnoreCase("q")) return;
        if (getCourseByCode(code) != null) { System.out.println("Bu ders kodu zaten var."); return; }
        System.out.print("Ders adı (veya 'q' geri): ");
        String name = in.nextLine();
        if (name.equalsIgnoreCase("q")) return;
        Course c = new Course(code, name, null);
        if (addCourse(c)) System.out.println("Ders eklendi.");
        else System.out.println("Ders eklenemedi.");
    }

    public void adminAssignCourse(Scanner in, TeacherManager teacherMgr) {
        System.out.print("Atanacak Ders Kodu (veya 'q' geri): ");
        String code = in.nextLine();
        if (code.equalsIgnoreCase("q")) return;
        Course c = getCourseByCode(code);
        if (c == null) { System.out.println("Ders bulunamadı."); return; }
        while (true) {
            System.out.print("Atanacak Öğretmen ID (veya 'q' geri): ");
            String tId = in.nextLine();
            if (tId.equalsIgnoreCase("q")) return;
            Teacher tch = teacherMgr.getTeacherById(tId);
            if (tch == null) {
                System.out.println("Öğretmen bulunamadı. Tekrar deneyin.");
                continue;
            }
            if (assignCourseToTeacher(code, tId)) {
                System.out.println("Ders '" + c.getCourseName() + "' (" + c.getCourseCode() + ") öğretmen '" + tch.getName() + " " + tch.getSurname() + "' olarak atandı.");
            } else System.out.println("Atama başarısız.");
            return;
        }
    }

    public void adminListCourses(TeacherManager teacherMgr) {
        List<Course> all = getAllCourses();
        if (all.isEmpty()) { System.out.println("Kayıtlı ders yok."); return; }
        System.out.printf("%-8s %-20s %-20s\n", "Kod", "Ders Adı", "Öğretmen");
        for (Course c : all) {
            String tname = "-";
            if (c.getAssignedTeacherId() != null) {
                Teacher t = teacherMgr.getTeacherById(c.getAssignedTeacherId());
                if (t != null) tname = t.getName() + " " + t.getSurname();
            }
            System.out.printf("%-8s %-20s %-20s\n", c.getCourseCode(), c.getCourseName(), tname);
        }
    }
}
