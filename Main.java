// Main.java
// Çok sade tutuldu: sadece ana menü ve rollere delege eder.

import java.util.*;

public class Main {
    static Scanner in = new Scanner(System.in);
    static StudentManager studentMgr = new StudentManager();
    static TeacherManager teacherMgr = new TeacherManager();
    static CourseManager courseMgr = new CourseManager();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nOBS'ye Hoşgeldiniz!");
            System.out.println("1 - Admin Girişi");
            System.out.println("2 - Öğretmen Girişi");
            System.out.println("3 - Öğrenci Girişi");
            System.out.println("0 - Çıkış");
            System.out.print("Lütfen seçiminizi yapınız: ");
            String choice = in.nextLine();
            switch (choice) {
                case "1": adminLogin(); break;
                case "2": teacherMgr.handleTeacherSession(in, courseMgr, studentMgr); break; // tamamen teacherManager'a delege
                case "3": studentMgr.handleStudentSession(in, courseMgr, teacherMgr); break; // tamamen studentManager'a delege
                case "0": System.out.println("Çıkılıyor..."); return;
                default: System.out.println("Geçersiz seçim.");
            }
        }
    }

    // ---------------- Admin ----------------
    private static void adminLogin() {
        System.out.print("\nAdmin şifresini giriniz (veya 'q' geri): ");
        String password = in.nextLine();
        if (password.equalsIgnoreCase("q")) return;
        if (password.equals("admin123")) {
            System.out.println("Giriş Başarılı.");
            adminPanel();
        } else {
            System.out.println("Hatalı şifre.");
        }
    }

    // Admin paneli de mümkün olduğunca sade: yönetim işlemlerini ilgili manager'lara devreder.
    private static void adminPanel() {
        while (true) {
            System.out.println("\n--- ADMIN PANELİ ---");
            System.out.println("1- Öğretmen Ekle");
            System.out.println("2- Öğretmen Sil");
            System.out.println("3- Öğretmenleri Listele");
            System.out.println("4- Öğrenci Ekle");
            System.out.println("5- Öğrenci Sil");
            System.out.println("6- Ders Ekle");
            System.out.println("7- Dersi Öğretmene Ata");
            System.out.println("8- Dersleri Listele");
            System.out.println("9- Tüm Verileri Sıfırla");
            System.out.println("0- Çıkış");
            System.out.print("Seçim: ");
            String ch = in.nextLine();
            if (ch.equalsIgnoreCase("reset") || ch.equals("9")) {
                adminResetAllData();
                continue;
            }
            switch (ch) {
                case "1": teacherMgr.adminAddTeachersLoop(in); break;
                case "2": teacherMgr.adminRemoveTeacher(in); break;
                case "3": teacherMgr.adminListTeachers(); break;
                case "4": studentMgr.adminAddStudentsLoop(in); break;
                case "5": studentMgr.adminRemoveStudent(in); break;
                case "6": courseMgr.adminAddCourse(in); break;
                case "7": courseMgr.adminAssignCourse(in, teacherMgr); break;
                case "8": courseMgr.adminListCourses(teacherMgr); break;
                case "0": return;
                default: System.out.println("Geçersiz seçim.");
            }
        }
    }

    // Admin: tüm verileri sıfırlama (yedekleme + truncate + manager reload)
    private static void adminResetAllData() {
        System.out.println("UYARI: Tüm veriler kalıcı olarak silinecek!");
        System.out.print("Devam etmek istiyor musunuz? (evet/hayır): ");
        String ans = in.nextLine();
        if (!ans.equalsIgnoreCase("evet")) {
            System.out.println("İşlem iptal edildi.");
            return;
        }

        // Otomatik yedekleme - oluşturulan .bak dosyalarına kopyalanır
        FileHelper.backupFile("students.txt", "students.bak.txt");
        FileHelper.backupFile("teachers.txt", "teachers.bak.txt");
        FileHelper.backupFile("courses.txt", "courses.bak.txt");
        FileHelper.backupFile("grades.txt", "grades.bak.txt");

        // Gerçek sıfırlama
        FileHelper.resetAllData();

        // Bellekteki verileri de güncelle
        studentMgr.loadStudents();
        teacherMgr.loadTeachers();
        courseMgr.loadCourses();

        System.out.println("Tüm veriler sıfırlandı. Önemli dosyalar yedeklendi: *.bak.txt");
    }
}