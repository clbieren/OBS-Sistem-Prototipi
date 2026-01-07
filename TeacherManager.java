// TeacherManager.java
// Öğretmenlerle ilgili tüm işlemler: yükleme, kaydetme, ekleme, silme, ID üretimi, listeleme,
// ayrıca öğretmen girişi ve not panellerinin bir kısmı burada bulunur (Main sade kalır).

import java.util.*;

public class TeacherManager {
    private final String filePath = "teachers.txt";
    private List<Teacher> teachers;

    public TeacherManager() {
        teachers = new ArrayList<>();
        loadTeachers();
    }

    public void loadTeachers() {
        teachers.clear();
        for (String line : FileHelper.readFile(filePath)) {
            Teacher t = Teacher.fromString(line);
            if (t != null) teachers.add(t);
        }
    }

    public void saveTeachers() {
        List<String> lines = new ArrayList<>();
        for (Teacher t : teachers) lines.add(t.toString());
        FileHelper.writeFile(filePath, lines);
    }

    // Yeni öğretmen ekle (aynı isim soyisim veya aynı ID engelleniyor)
    public boolean addTeacher(Teacher t) {
        if (getTeacherById(t.getTeacherId()) != null) return false;
        for (Teacher ex : teachers) {
            if (ex.getName().equalsIgnoreCase(t.getName()) && ex.getSurname().equalsIgnoreCase(t.getSurname()))
                return false;
        }
        teachers.add(t);
        saveTeachers();
        return true;
    }

    public boolean removeTeacher(String teacherId) {
        Teacher t = getTeacherById(teacherId);
        if (t == null) return false;
        teachers.remove(t);
        saveTeachers();
        return true;
    }

    public Teacher getTeacherById(String teacherId) {
        for (Teacher t : teachers) {
            if (t.getTeacherId().equals(teacherId)) return t;
        }
        return null;
    }

    public List<Teacher> getAllTeachers() {
        return teachers;
    }

    // Otomatik artan öğretmen ID'si üret (100'den başlar)
    public String getNextTeacherId() {
        int max = 99; // başlangıç eşiği
        for (Teacher t : teachers) {
            try {
                int id = Integer.parseInt(t.getTeacherId());
                if (id > max) max = id;
            } catch (NumberFormatException ignored) {}
        }
        return String.valueOf(max + 1);
    }

    // Admin: öğretmen ekleme döngüsü (q ile çık)
    public void adminAddTeachersLoop(Scanner in) {
        System.out.println("Öğretmen ekleme (Çıkmak için 'q' girin).");
        while (true) {
            String nextId = getNextTeacherId();
            System.out.println("Yeni öğretmen ID: " + nextId + " (otomatik).");
            System.out.print("Ad (veya 'q' geri): ");
            String ad = in.nextLine();
            if (ad.equalsIgnoreCase("q")) return;
            System.out.print("Soyad (veya 'q' geri): ");
            String soyad = in.nextLine();
            if (soyad.equalsIgnoreCase("q")) return;
            Teacher t = new Teacher(nextId, ad, soyad);
            if (addTeacher(t)) {
                System.out.println("Öğretmen eklendi. ID: " + nextId);
            } else {
                System.out.println("Eklenemedi: Aynı öğretmen veya ID zaten mevcut.");
            }
            System.out.println("Başka öğretmen eklemek için devam edin, çıkmak için 'q' girin.");
        }
    }

    // Admin: öğretmen silme
    public void adminRemoveTeacher(Scanner in) {
        while (true) {
            System.out.print("Silinecek öğretmen Id (veya 'q' geri): ");
            String id = in.nextLine();
            if (id.equalsIgnoreCase("q")) return;
            if (removeTeacher(id)) { System.out.println("Öğretmen silindi."); return; }
            else System.out.println("Öğretmen bulunamadı. Tekrar deneyin.");
        }
    }

    public void adminListTeachers() {
        List<Teacher> list = getAllTeachers();
        if (list.isEmpty()) System.out.println("Kayıtlı öğretmen yok.");
        else {
            System.out.println("ID   - Ad Soyad");
            for (Teacher t : list) System.out.printf("%-5s - %s %s\n", t.getTeacherId(), t.getName(), t.getSurname());
        }
    }

    // Öğretmen girişi + panel (Main burayı çağıracak)
    public void handleTeacherSession(Scanner in, CourseManager courseMgr, StudentManager studentMgr) {
        while (true) {
            System.out.print("Öğretmen ID (veya 'q' geri): ");
            String id = in.nextLine();
            if (id.equalsIgnoreCase("q")) return;
            Teacher t = getTeacherById(id);
            if (t != null) {
                System.out.println("Giriş başarılı. Hoşgeldiniz, " + t.getName());
                teacherPanel(id, in, courseMgr, studentMgr);
                return;
            } else {
                System.out.println("Kullanıcı bulunamadı. Tekrar deneyin veya 'q' ile geri dönün.");
            }
        }
    }

    // Öğretmen paneli ve not girişi işlemleri
    private void teacherPanel(String teacherId, Scanner in, CourseManager courseMgr, StudentManager studentMgr) {
        Teacher t = getTeacherById(teacherId);
        if (t == null) return;
        while (true) {
            System.out.println("\n--- " + t.getName() + " Öğretmen Paneli ---");
            System.out.println("1- Derslerimi Listele");
            System.out.println("2- Not Girişi Yap");
            System.out.println("0- Çıkış");
            System.out.print("Seçim: ");
            String ch = in.nextLine();
            switch (ch) {
                case "1": listTeacherCourses(teacherId, courseMgr); break;
                case "2": teacherEnterGrades(teacherId, in, courseMgr, studentMgr); break;
                case "0": return;
                default: System.out.println("Geçersiz seçim.");
            }
        }
    }

    private void listTeacherCourses(String teacherId, CourseManager courseMgr) {
        List<Course> my = courseMgr.getCoursesByTeacher(teacherId);
        if (my.isEmpty()) { System.out.println("Üzerinize atanmış ders yok."); return; }
        System.out.println("Ders Kod - Ders Adı");
        for (Course c : my) System.out.println(c.getCourseCode() + " - " + c.getCourseName());
    }

    // Not giriş işlemleri (gradeType, mod vb.)
    private void teacherEnterGrades(String teacherId, Scanner in, CourseManager courseMgr, StudentManager studentMgr) {
        List<Course> my = courseMgr.getCoursesByTeacher(teacherId);
        if (my.isEmpty()) { System.out.println("Atanmış ders yok."); return; }

        // Ders seçimi
        int sel = -1;
        while (true) {
            System.out.println("Dersleriniz:");
            for (int i = 0; i < my.size(); i++) System.out.println((i + 1) + ". " + my.get(i).getCourseCode() + " - " + my.get(i).getCourseName());
            System.out.print("Not girmek istediğiniz dersin numarası (veya 'q' geri): ");
            String s = in.nextLine();
            if (s.equalsIgnoreCase("q")) return;
            try {
                sel = Integer.parseInt(s) - 1;
                if (sel < 0 || sel >= my.size()) { System.out.println("Geçersiz numara."); continue; }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Geçerli bir sayı girin.");
            }
        }
        Course chosen = my.get(sel);

        // Hangi tür not girilecek?
        int gradeType = -1;
        while (true) {
            System.out.println("Hangi notu girmek istiyorsunuz? 1-Vize  2-Final  3-Her ikisi  0-İptal");
            String gt = in.nextLine();
            if (gt.equalsIgnoreCase("q") || gt.equals("0")) return;
            if (gt.equals("1") || gt.equals("2") || gt.equals("3")) {
                gradeType = Integer.parseInt(gt);
                break;
            } else System.out.println("Geçersiz seçim.");
        }

        // Not girme modu: 1 - sırayla, 2 - öğrenci seçerek
        while (true) {
            System.out.println("Not girme modu seçin: 1- Sırayla not gir  2- Öğrenci seçerek not gir  0-İptal");
            String mode = in.nextLine();
            if (mode.equalsIgnoreCase("q") || mode.equals("0")) return;
            if (mode.equals("1")) {
                // Sırayla
                List<String[]> students = courseMgr.getStudentsByCourse(chosen.getCourseCode());
                if (students.isEmpty()) { System.out.println("Bu dersi seçmiş öğrenci yok."); return; }
                for (String[] rec : students) {
                    String sid = rec[0];
                    Student st = studentMgr.getStudentById(sid);
                    String name = (st == null) ? sid : st.getName() + " " + st.getSurname();
                    String curMid = rec.length > 2 ? rec[2] : "";
                    String curFin = rec.length > 3 ? rec[3] : "";
                    String inputMid = null, inputFin = null;
                    if (gradeType == 1 || gradeType == 3) {
                        System.out.print(name + " için Vize (mevcut: " + (curMid.isEmpty() ? "-" : curMid) + ", boş bırakıp Enter ile koru, 'q' atla): ");
                        String v = in.nextLine();
                        if (v.equalsIgnoreCase("q")) { System.out.println("Bu öğrenci için atlandı."); continue; }
                        if (!v.isEmpty()) inputMid = v;
                    }
                    if (gradeType == 2 || gradeType == 3) {
                        System.out.print(name + " için Final (mevcut: " + (curFin.isEmpty() ? "-" : curFin) + ", boş bırakıp Enter ile koru, 'q' atla): ");
                        String f = in.nextLine();
                        if (f.equalsIgnoreCase("q")) { System.out.println("Bu öğrenci için atlandı."); continue; }
                        if (!f.isEmpty()) inputFin = f;
                    }
                    courseMgr.updateGradePartial(sid, rec[1], inputMid, inputFin);
                }
                System.out.println("Sırayla not girme tamamlandı.");
                return;
            } else if (mode.equals("2")) {
                // Öğrenci seçerek
                List<String[]> students = courseMgr.getStudentsByCourse(chosen.getCourseCode());
                if (students.isEmpty()) { System.out.println("Bu dersi seçmiş öğrenci yok."); return; }
                while (true) {
                    System.out.println("Öğrenciler:");
                    for (int i = 0; i < students.size(); i++) {
                        String sid = students.get(i)[0];
                        Student st = studentMgr.getStudentById(sid);
                        String name = (st == null) ? sid : st.getName() + " " + st.getSurname();
                        System.out.println((i + 1) + ". " + sid + " - " + name);
                    }
                    System.out.print("Öğrenci numarası seçin (veya 'q' geri): ");
                    String sIdx = in.nextLine();
                    if (sIdx.equalsIgnoreCase("q")) return;
                    int idx;
                    try {
                        idx = Integer.parseInt(sIdx) - 1;
                        if (idx < 0 || idx >= students.size()) { System.out.println("Geçersiz seçim."); continue; }
                    } catch (NumberFormatException e) { System.out.println("Geçerli bir sayı girin."); continue; }
                    String[] rec = students.get(idx);
                    String sid = rec[0];
                    Student st = studentMgr.getStudentById(sid);
                    String name = (st == null) ? sid : st.getName() + " " + st.getSurname();
                    String curMid = rec.length > 2 ? rec[2] : "";
                    String curFin = rec.length > 3 ? rec[3] : "";
                    String inputMid = null, inputFin = null;
                    if (gradeType == 1 || gradeType == 3) {
                        System.out.print(name + " için Vize (mevcut: " + (curMid.isEmpty() ? "-" : curMid) + ", boş bırakıp Enter ile koru, 'q' atla): ");
                        String v = in.nextLine();
                        if (v.equalsIgnoreCase("q")) { System.out.println("Bu öğrenci için atlandı."); continue; }
                        if (!v.isEmpty()) inputMid = v;
                    }
                    if (gradeType == 2 || gradeType == 3) {
                        System.out.print(name + " için Final (mevcut: " + (curFin.isEmpty() ? "-" : curFin) + ", boş bırakıp Enter ile koru, 'q' atla): ");
                        String f = in.nextLine();
                        if (f.equalsIgnoreCase("q")) { System.out.println("Bu öğrenci için atlandı."); continue; }
                        if (!f.isEmpty()) inputFin = f;
                    }
                    courseMgr.updateGradePartial(sid, rec[1], inputMid, inputFin);
                    System.out.println("Notlar kaydedildi. Başka öğrenci seçmek için devam edin, çıkmak için 'q' girin.");
                }
            } else {
                System.out.println("Geçersiz seçim.");
            }
        }
    }
}