// StudentManager.java
// Öğrencilerle ilgili tüm işlemler: yükleme, kaydetme, ekleme, silme, ID üretimi ve
// öğrenci paneli (login + menü) işlemlerinin büyük kısmı buraya taşındı.

import java.util.*;

public class StudentManager {
    private final String filePath = "students.txt";
    private List<Student> students;

    public StudentManager() {
        students = new ArrayList<>();
        loadStudents();
    }

    // Dosyadan öğrencileri yükle
    public void loadStudents() {
        students.clear();
        for (String line : FileHelper.readFile(filePath)) {
            Student s = Student.fromString(line);
            if (s != null) students.add(s);
        }
    }

    // Öğrencileri dosyaya kaydet
    public void saveStudents() {
        List<String> lines = new ArrayList<>();
        for (Student s : students) lines.add(s.toString());
        FileHelper.writeFile(filePath, lines);
    }

    // Yeni öğrenci ekle (Student objesi verilir). Aynı isim+soyisim engellenebilir.
    public boolean addStudent(Student s) {
        if (getStudentById(s.getStudentId()) != null) return false;
        for (Student existing : students) {
            if (existing.getName().equalsIgnoreCase(s.getName())
                    && existing.getSurname().equalsIgnoreCase(s.getSurname())) {
                return false;
            }
        }
        students.add(s);
        saveStudents();
        return true;
    }

    // ID'ye göre öğrenci sil
    public boolean removeStudent(String studentId) {
        Student s = getStudentById(studentId);
        if (s == null) return false;
        students.remove(s);
        saveStudents();
        return true;
    }

    // ID ile öğrenci bul
    public Student getStudentById(String studentId) {
        for (Student s : students) {
            if (s.getStudentId().equals(studentId)) return s;
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return students;
    }

    // Otomatik artan öğrenci ID'si üret (1000'den başlar)
    public String getNextStudentId() {
        int max = 999; // başlangıç eşiği
        for (Student s : students) {
            try {
                int id = Integer.parseInt(s.getStudentId());
                if (id > max) max = id;
            } catch (NumberFormatException ignored) {}
        }
        return String.valueOf(max + 1);
    }

    // Admin tarafında kullanılacak: öğrencileri ekleme döngüsü (q ile çıkış)
    public void adminAddStudentsLoop(Scanner in) {
        System.out.println("Öğrenci ekleme (Çıkmak için 'q' girin).");
        while (true) {
            String nextId = getNextStudentId();
            System.out.println("Yeni öğrenci No: " + nextId + " (otomatik).");
            System.out.print("Ad (veya 'q' geri): ");
            String ad = in.nextLine();
            if (ad.equalsIgnoreCase("q")) return;
            System.out.print("Soyad (veya 'q' geri): ");
            String soyad = in.nextLine();
            if (soyad.equalsIgnoreCase("q")) return;
            System.out.print("Bölüm (veya 'q' geri): ");
            String bolum = in.nextLine();
            if (bolum.equalsIgnoreCase("q")) return;
            Student s = new Student(nextId, ad, soyad, bolum);
            if (addStudent(s)) {
                System.out.println("Öğrenci eklendi. No: " + nextId);
            } else {
                System.out.println("Eklenemedi: Aynı öğrenci veya No zaten mevcut.");
            }
            System.out.println("Başka öğrenci eklemek için devam edin, çıkmak için 'q' girin.");
        }
    }

    // Admin tarafında kullanılacak: öğrenci silme (q ile iptal)
    public void adminRemoveStudent(Scanner in) {
        while (true) {
            System.out.print("Silinecek öğrenci No (veya 'q' geri): ");
            String id = in.nextLine();
            if (id.equalsIgnoreCase("q")) return;
            if (removeStudent(id)) { System.out.println("Öğrenci silindi."); return; }
            else System.out.println("Öğrenci bulunamadı. Tekrar deneyin.");
        }
    }

    // Öğrenci girişi + paneli - Main burayı çağıracak (Main çok sade kalır)
    public void handleStudentSession(Scanner in, CourseManager courseMgr, TeacherManager teacherMgr) {
        while (true) {
            System.out.print("Öğrenci Numaranız (veya 'q' geri): ");
            String id = in.nextLine();
            if (id.equalsIgnoreCase("q")) return;
            Student s = getStudentById(id);
            if (s != null) {
                System.out.println("Giriş başarılı. Hoşgeldin " + s.getName() + "!");
                studentPanel(id, in, courseMgr, teacherMgr);
                return;
            } else {
                System.out.println("Kullanıcı bulunamadı. Tekrar deneyin veya 'q' ile geri dönün.");
            }
        }
    }

    // Öğrenci paneli: ders seçme/transkript/seçilen dersleri görme
    private void studentPanel(String studentId, Scanner in, CourseManager courseMgr, TeacherManager teacherMgr) {
        Student s = getStudentById(studentId);
        if (s == null) return;
        while (true) {
            System.out.println("\n--- " + s.getName() + " Öğrenci Paneli ---");
            System.out.println("1- Ders Seç");
            System.out.println("2- Transkript Görüntüle");
            System.out.println("3- Seçtiğim Dersleri Görüntüle");
            System.out.println("0- Çıkış");
            System.out.print("Seçim: ");
            String ch = in.nextLine();
            switch (ch) {
                case "1": studentSelectCourse(studentId, in, courseMgr, teacherMgr); break;
                case "2": studentViewTranscript(studentId, courseMgr); break;
                case "3": studentViewSelectedCourses(studentId, courseMgr, teacherMgr); break;
                case "0": return;
                default: System.out.println("Geçersiz seçim.");
            }
        }
    }

    // Ders seçme işlemi
    private void studentSelectCourse(String studentId, Scanner in, CourseManager courseMgr, TeacherManager teacherMgr) {
        List<Course> all = courseMgr.getAllCourses();
        if (all.isEmpty()) { System.out.println("Açık ders yok."); return; }
        while (true) {
            System.out.println("Açık dersler:");
            for (int i = 0; i < all.size(); i++) {
                Course c = all.get(i);
                String tname = "-";
                if (c.getAssignedTeacherId() != null) {
                    Teacher t = teacherMgr.getTeacherById(c.getAssignedTeacherId());
                    if (t != null) tname = t.getName() + " " + t.getSurname();
                }
                System.out.println((i + 1) + ". " + c.getCourseCode() + " - " + c.getCourseName() + " (" + tname + ")");
            }
            System.out.print("Seçmek istediğiniz dersin numarası (veya 'q' geri): ");
            String s = in.nextLine();
            if (s.equalsIgnoreCase("q")) return;
            int sel;
            try {
                sel = Integer.parseInt(s) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Geçerli bir sayı girin."); continue;
            }
            if (sel < 0 || sel >= all.size()) { System.out.println("Geçersiz seçim."); continue; }
            Course chosen = all.get(sel);
            if (courseMgr.addStudentCourse(studentId, chosen.getCourseCode())) System.out.println("Ders seçildi: " + chosen.getCourseName());
            else System.out.println("Zaten bu dersi seçmişsiniz!");
            return;
        }
    }

    // Transkript gösterme ve AGNO hesaplama (AGNO hesaplaması CourseManager'da da var; burada delegasyon kullanılıyor)
    private void studentViewTranscript(String studentId, CourseManager courseMgr) {
        List<String[]> notlar = courseMgr.getGradesByStudent(studentId);
        System.out.printf("%-10s %-20s %-8s %-8s %-8s %-6s %-10s\n", "DersKodu", "DersAdı", "Vize", "Final", "Ortalama", "Harf", "Durum");
        for (String[] arr : notlar) {
            String code = arr[1];
            Course c = courseMgr.getCourseByCode(code);
            String name = (c == null) ? code : c.getCourseName();
            String v = (arr.length > 2 && !arr[2].isEmpty()) ? arr[2] : "-";
            String f = (arr.length > 3 && !arr[3].isEmpty()) ? arr[3] : "-";
            double avg = -1;
            if (!"-".equals(v) && !"-".equals(f)) {
                try { avg = Integer.parseInt(v) * 0.4 + Integer.parseInt(f) * 0.6; } catch (Exception e) { avg = -1; }
            }
            String harf = avg < 0 ? "-" : getLetterGrade(avg);
            String durum = avg < 0 ? "-" : (avg >= 60 ? "Geçti" : "Kaldı");
            System.out.printf("%-10s %-20s %-8s %-8s %-8s %-6s %-10s\n", code, name, v, f, (avg < 0 ? "-" : String.format("%.2f", avg)), harf, durum);
        }
        double agno = courseMgr.computeAGNO(studentId);
        if (agno < 0) System.out.println("AGNO: - (yeterli not bilgisi yok)");
        else System.out.println("AGNO: " + String.format("%.2f", agno));
    }

    // Seçtiğim dersleri göster
    private void studentViewSelectedCourses(String studentId, CourseManager courseMgr, TeacherManager teacherMgr) {
        List<String[]> selections = courseMgr.getGradesByStudent(studentId);
        if (selections.isEmpty()) { System.out.println("Henüz ders seçiminiz yok."); return; }
        System.out.printf("%-10s %-20s %-20s\n", "DersKodu", "DersAdı", "Öğretmen");
        for (String[] arr : selections) {
            String code = arr[1];
            Course c = courseMgr.getCourseByCode(code);
            String name = (c == null) ? code : c.getCourseName();
            String tname = "-";
            if (c != null && c.getAssignedTeacherId() != null) {
                Teacher t = teacherMgr.getTeacherById(c.getAssignedTeacherId());
                if (t != null) tname = t.getName() + " " + t.getSurname();
            }
            System.out.printf("%-10s %-20s %-20s\n", code, name, tname);
        }
    }

    // Harf notu hesaplama (aynı kurallar)
    private String getLetterGrade(double avg) {
        if (avg >= 90) return "AA";
        if (avg >= 85) return "BA";
        if (avg >= 80) return "BB";
        if (avg >= 75) return "CB";
        if (avg >= 70) return "CC";
        if (avg >= 65) return "DC";
        if (avg >= 60) return "DD";
        if (avg >= 50) return "FD";
        return "FF";
    }
}