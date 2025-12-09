// FileHelper.java
// Tüm dosya okuma/yazma işlemlerini merkezi olarak yöneten yardımcı sınıf.
// Dosya yoksa oluşturur; hata durumunda program çökmesini engeller.

import java.io.*;
import java.util.*;

public class FileHelper {
    // Dosyayı satır satır okur, yoksa oluşturur.
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            File f = new File(filePath);
            if (!f.exists()) f.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + filePath + " -> " + e.getMessage());
        }
        return lines;
    }

    // Bütün içeriği verilen liste ile yazar (üstüne yazar).
    public static void writeFile(String filePath, List<String> lines) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Dosya yazma hatası: " + filePath + " -> " + e.getMessage());
        }
    }

    // Dosya sonuna ekler (append).
    public static void appendToFile(String filePath, String line) {
        try {
            File f = new File(filePath);
            if (!f.exists()) f.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
            bw.write(line);
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            System.out.println("Dosya ekleme hatası: " + filePath + " -> " + e.getMessage());
        }
    }

    // Dosyanın içeriğini tamamen temizler (truncate).
    public static void clearFile(String filePath) {
        try {
            new FileWriter(filePath, false).close();
        } catch (IOException e) {
            System.out.println("Dosya temizleme hatası: " + filePath + " -> " + e.getMessage());
        }
    }

    // Tüm ana veri dosyalarını sıfırlar (kalıcı)
    public static void resetAllData() {
        List<String> files = Arrays.asList("students.txt", "teachers.txt", "courses.txt", "grades.txt");
        for (String f : files) {
            clearFile(f);
        }
    }

    // Basit yedekleme helper'ı
    public static boolean backupFile(String sourcePath, String backupPath) {
        try {
            File src = new File(sourcePath);
            if (!src.exists()) return false;
            try (InputStream in = new FileInputStream(src);
                 OutputStream out = new FileOutputStream(backupPath)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            return true;
        } catch (IOException e) {
            System.out.println("Yedekleme hatası: " + e.getMessage());
            return false;
        }
    }
}