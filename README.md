# TempRoot v2 — Check Only

Aplikasi Android diagnostik offline dengan tema gelap ungu. **Tidak menyediakan root, exploit, atau dukungan Magisk.** Root Engine hanya halaman informasi.

## Fitur
Dashboard, hasil pemeriksaan, kompatibilitas, Root Engine, laporan salin clipboard, tools, panduan, pengaturan.

## Build
Push semua isi folder ini ke root repository GitHub. Buka Actions > Build TempRoot v2 APK > Run workflow. Unduh artifact `TempRoot-v2-debug` setelah build berhasil.

Build lokal: Java 17, Android SDK 35, Gradle 8.9: `gradle :app:assembleDebug`.

## Keamanan
Tidak meminta permission, tidak menjalankan exploit, tidak mengubah kernel. Properti bootloader dan status root dapat tidak terbaca atau tidak lengkap. Jangan menganggap hasil pemeriksaan sebagai bukti kompatibilitas exploit.
