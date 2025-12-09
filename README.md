# OBS — Öğrenci Bilgi Sistemi 

Bu proje, Java ile yazılmış basit bir Öğrenci Bilgi Sistemi (OBS) örneğidir. Dosya tabanlıdır; tüm veriler `.txt` dosyalarında saklanır (students.txt, teachers.txt, courses.txt, grades.txt). Amaç: üniversite düzeyinde temel OOP kavramlarını (encapsulation, composition, manager pattern) uygulamalı olarak göstermek.

✨ Özellikler
- Rol tabanlı erişim: Admin (şifre: `admin123`), Öğretmen (ID), Öğrenci (No).
- Admin yetenekleri: Öğrenci/Öğretmen/Ders ekle/sil, ders atama, tüm verileri sıfırlama (yedek oluşturur).
- Öğretmen yetenekleri: Kendisine atanmış dersleri görme, vize/final notu girme (sırayla veya öğrenci seçerek), kısmi güncelleme.
- Öğrenci yetenekleri: Ders seçme, seçtiği dersleri görüntüleme, transkript & AGNO gösterimi.
- Dosya tabanlı kalıcılık ve basit yedekleme (.bak.txt).

🎯 Öğrenme Hedefleri
- Sınıf ve nesne modelleme (Student, Teacher, Course).
- Koleksiyon kullanımı (List / ArrayList).
- Nesneler arası ilişki yönetimi (ders ↔ öğretmen, öğrenci ↔ ders).
- Encapsulation ve getter/setter kullanımı.
- Dosya I/O (okuma, yazma, append, truncate) ile kalıcı veri yönetimi.

⚙️ Derleme & Çalıştırma
1. Tüm `.java` dosyalarını aynı klasöre koyun.  
2. Derleyin:
   ```
   javac *.java
   ```
3. Çalıştırın:
   ```
   java Main
   ```

📁 Veri Dosyası Formatları (kısa)
- students.txt: `studentId,name,surname,department`  
- teachers.txt: `teacherId,name,surname`  
- courses.txt: `courseCode,courseName,assignedTeacherId`  
- grades.txt: `studentId,courseCode,midterm,final`

🧪 Örnek Senaryo & Konsol Çıktısı
(Test amaçlı adımlar — program akışında benzer satırlar görünür.)

Admin paneli:
```
Admin şifresini giriniz: admin123
--- ADMIN PANELİ ---
1- Öğretmen Ekle
...
Yeni öğretmen ID: 100 (otomatik).
Ad: Ahmet
Soyad: Yılmaz
Öğretmen eklendi. ID: 100
```

Ders atama:
```
Atanacak Ders Kodu: MATH101
Atanacak Öğretmen ID: 100
Ders 'Matematik I' (MATH101) öğretmen 'Ahmet Yılmaz' olarak atandı.
```

Öğrenci ders seçimi:
```
Öğrenci Numaranız: 1000
1. MATH101 - Matematik I (Ahmet Yılmaz)
Seçmek istediğiniz dersin numarası: 1
Ders seçildi: Matematik I
```

Öğretmen not girişi (sırayla):
```
Not girme modu: 1
Mehmet Kaya için Vize (mevcut: -, 'q' atla): 70
Mehmet Kaya için Final (mevcut: -, 'q' atla): 80
Sırayla not girme tamamlandı.
```

Öğrenci transkripti & AGNO:
```
DersKodu  DersAdı        Vize  Final  Ortalama  Harf  Durum
MATH101   Matematik I    70    80     76.00     CB    Geçti
AGNO: 76.00
```

🔒 Uyarılar & İpuçları
- Dosyaları sıfırlamadan önce otomatik yedek (`*.bak.txt`) oluşturulur; yine de manuel yedek önerilir.
- ID'ler otomatik artar: öğretmen 100'den, öğrenci 1000'den başlar.
- Proje eğitim amaçlıdır; gerçek dünyada bir veritabanı ve kullanıcı doğrulama eklenmelidir.
