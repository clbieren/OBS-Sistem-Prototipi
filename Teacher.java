// Teacher.java
// Öğretmen varlık sınıfı.

public class Teacher {
    private String teacherId;
    private String name;
    private String surname;

    public Teacher(String teacherId, String name, String surname) {
        this.teacherId = teacherId;
        this.name = name;
        this.surname = surname;
    }

    public String getTeacherId() { return teacherId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }

    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }

    @Override
    public String toString() {
        // teacherId,name,surname
        return teacherId + "," + name + "," + surname;
    }

    public static Teacher fromString(String line) {
        String[] arr = line.split(",", -1);
        if (arr.length != 3) return null;
        return new Teacher(arr[0], arr[1], arr[2]);
    }
}