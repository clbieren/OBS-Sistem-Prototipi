//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Comparator; // alfabetik sıralama için

public class Main {

    //  Student sinifi
    static class Student {
        String name;
        int age;
        double grade;

        public Student(String name, int age, double grade) {
            this.name = name;
            this.age = age;
            this.grade = grade;
        }

        @Override
        public String toString() {
            return "Name: " + name + ", Age: " + age + ", Grade: " + grade;
        }
    }

    // Studentmanager sinifi
    static class StudentManager {
        ArrayList<Student> students = new ArrayList<>();

        public void addStudent(Student s) {
            students.add(s);
            students.sort(Comparator.comparing(student -> student.name)); // alfabetik sıralama
            System.out.println("Eklendi: " + s.name);
        }

        public void listStudents() {
            if (students.isEmpty()) {
                System.out.println("Öğrenci yok.");
                return;
            }
            System.out.println("\n--- Öğrenci Listesi ---");
            for (Student s : students) {
                System.out.println(s);
            }
        }
    }

    //  Main metod
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("\n1 - Öğrenci Ekle");
            System.out.println("2 - Öğrencileri Listele");
            System.out.println("3 - Çıkış");
            System.out.print("Seçim: ");

            int secim;
            if (input.hasNextInt()) {
                secim = input.nextInt();
                input.nextLine(); // buffer temizle
            } else {
                System.out.println("Geçersiz seçim!");
                input.nextLine();
                continue;
            }

            if (secim == 1) {
                System.out.print("İsim: ");
                String name = input.nextLine();

                System.out.print("Yaş: ");
                int age = input.nextInt();

                System.out.print("Not: ");
                double grade = input.nextDouble();
                input.nextLine(); // buffer temizle

                Student s = new Student(name, age, grade);
                manager.addStudent(s);

            } else if (secim == 2) {
                manager.listStudents();

            } else if (secim == 3) {
                System.out.println("Program kapatılıyor...");
                break;

            } else {
                System.out.println("Geçersiz seçim.");
            }
        }

        input.close();
    }
}


