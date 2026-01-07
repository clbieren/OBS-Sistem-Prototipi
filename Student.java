// Student.java
// Öğrenci varlık sınıfı - encapsulation uygulanmış.

public class Student {
    private String studentId;
    private String name;
    private String surname;
    private String department;

    public Student(String studentId, String name, String surname, String department) {
        this.studentId = studentId;
        this.name = name;
        this.surname = surname;
        this.department = department;
    }

    // Getter / Setter
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getDepartment() { return department; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        // Dosyaya kaydetme formatı: studentId,name,surname,department
        return studentId + "," + name + "," + surname + "," + department;
    }

    public static Student fromString(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length != 4) return null;
        return new Student(arr[0], arr[1], arr[2], arr[3]);
    }
}