package org.temproot.checker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Process;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG=Color.rgb(9,8,20), CARD=Color.rgb(23,22,42), PURPLE=Color.rgb(153,79,255), WHITE=Color.WHITE, MUTED=Color.rgb(170,167,194);
    private LinearLayout page, root;
    private String screen="home";
    private boolean scanned=false;
    private String report="";
    private boolean busy=false;
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);showHome();}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),Color.rgb(48,43,73));return d;}
    private void init(String title){screen=title;root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);setContentView(root);ScrollView sc=new ScrollView(this);sc.setFillViewport(true);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(20),dp(20),dp(20),dp(24));sc.addView(page);addNavigation();if(!title.equals("home")){TextView back=text("‹   "+title,22,WHITE,true);back.setPadding(0,0,0,dp(22));back.setOnClickListener(v->showHome());page.addView(back);} }
    private void addNavigation(){
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER);bar.setPadding(dp(8),dp(10),dp(8),dp(12));bar.setBackgroundColor(Color.rgb(17,15,32));
        navItem(bar,"⌂","Beranda",()->showHome());
        navItem(bar,"◉","Scan",()->showScan());
        navItem(bar,"◇","Device",()->showDevice());
        navItem(bar,"▤","Laporan",()->showReport());
        root.addView(bar,new LinearLayout.LayoutParams(-1,-2));
    }
    private void navItem(LinearLayout bar,String glyph,String label,Runnable action){
        LinearLayout item=new LinearLayout(this);item.setOrientation(LinearLayout.VERTICAL);item.setGravity(Gravity.CENTER);
        TextView ico=text(glyph,22,PURPLE,true);ico.setGravity(Gravity.CENTER);item.addView(ico);
        TextView name=text(label,11,MUTED,false);name.setGravity(Gravity.CENTER);item.addView(name);
        bar.addView(item,new LinearLayout.LayoutParams(0,-2,1));item.setOnClickListener(v->action.run());
    }
    private void hero(String eyebrow,String heading,String description){
        LinearLayout c=card();GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(58,31,112),Color.rgb(27,22,52),Color.rgb(20,20,39)});g.setCornerRadius(dp(22));g.setStroke(dp(1),Color.rgb(103,68,160));c.setBackground(g);c.setPadding(dp(22),dp(24),dp(22),dp(24));
        c.addView(text(eyebrow.toUpperCase(Locale.ROOT),11,Color.rgb(207,177,255),true));
        TextView h=text(heading,27,WHITE,true);h.setPadding(0,dp(12),0,dp(8));c.addView(h);
        c.addView(text(description,13,Color.rgb(209,199,230),false));
    }
    private void section(String heading){add(heading,13,MUTED,true,24);}
    private void shareReport(){
        Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_SUBJECT,"TempRoot v2 — Device Report");i.putExtra(Intent.EXTRA_TEXT,report);
        startActivity(Intent.createChooser(i,"Bagikan laporan"));
    }
    private void showScan(){
        init("Pemeriksaan");hero("LOCAL DIAGNOSTICS","Siap memeriksa perangkat","Pemeriksaan hanya membaca informasi sistem. Tidak ada exploit, modifikasi, atau akses root.");
        LinearLayout c=card();line(c,"Informasi sistem","Android & kernel");line(c,"Keamanan","SELinux & Verified Boot");line(c,"Bootloader","Status jika tersedia");line(c,"Root","Indikasi, bukan verifikasi");
        button(busy?"Memeriksa…":"Mulai Scan Aman",()->{if(busy)return;busy=true;showScanning();});
    }
    private void showScanning(){
        init("Memindai");hero("CHECK ONLY","Memeriksa perangkat…","Mengumpulkan data lokal. Tidak ada perubahan pada sistem.");
        add("◉  Android & kernel",16,PURPLE,true,22);add("◉  SELinux & bootloader",16,PURPLE,true,16);add("◉  Verified Boot & indikasi root",16,PURPLE,true,16);
        new Handler(Looper.getMainLooper()).postDelayed(()->{if(isFinishing())return;busy=false;showCheck();},650);
    }
    private void showDevice(){init("Informasi Perangkat");hero("DEVICE OVERVIEW",Build.MANUFACTURER+" "+Build.MODEL,"Ringkasan perangkat berdasarkan API Android dan properti yang dapat dibaca.");LinearLayout c=card();line(c,"Produsen",Build.MANUFACTURER);line(c,"Model",Build.MODEL);line(c,"Android",Build.VERSION.RELEASE);line(c,"API",String.valueOf(Build.VERSION.SDK_INT));line(c,"Patch",Build.VERSION.SECURITY_PATCH);line(c,"Kernel",kernel());line(c,"ABI",Build.SUPPORTED_ABIS.length>0?Build.SUPPORTED_ABIS[0]:"Unknown");button("Periksa keamanan",()->showScan());}
    private void showAbout(){init("Tentang Aplikasi");hero("TEMP ROOT LAB","TempRoot v2","Diagnostik lokal untuk memahami kondisi perangkat. Bukan aplikasi untuk memperoleh root.");LinearLayout c=card();line(c,"Versi","2.1 Preview");line(c,"Mode","Check Only");line(c,"Akses internet","Tidak diminta");line(c,"Root Engine","Tidak tersedia");button("Baca panduan",()->showGuide());}
    private void showPrivacy(){init("Privasi");hero("PRIVACY FIRST","Data tetap di perangkat","Laporan dibuat secara lokal dan hanya dibagikan jika kamu memilihnya.");tile("Tidak ada izin internet","Aplikasi tidak meminta permission INTERNET.",()->{});tile("Kontrol laporan","Tinjau isi laporan sebelum menyalin atau membagikan.",()->showReport());add("Properti perangkat mungkin mengandung informasi teknis. Jangan bagikan laporan ke pihak yang tidak dipercaya.",13,MUTED,false,18);}
    private TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setLineSpacing(dp(3),1);return t;}
    private void add(String s,int size,int color,boolean bold,int top){TextView t=text(s,size,color,bold);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(top);page.addView(t,lp);}
    private LinearLayout card(){LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);c.setPadding(dp(17),dp(16),dp(17),dp(16));c.setBackground(shape(CARD,17));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(14);page.addView(c,lp);return c;}
    private void line(LinearLayout c,String label,String value){LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setPadding(0,dp(9),0,dp(9));TextView a=text(label,13,MUTED,false),b=text(value,13,WHITE,true);row.addView(a,new LinearLayout.LayoutParams(0,-2,1));b.setGravity(Gravity.END);b.setMaxWidth(dp(200));row.addView(b);c.addView(row);View sep=new View(this);sep.setBackgroundColor(Color.rgb(48,43,73));c.addView(sep,new LinearLayout.LayoutParams(-1,dp(1)));}
    private void button(String s,Runnable action){TextView b=text(s,15,WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(16),dp(12),dp(16));GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{Color.rgb(110,39,226),PURPLE});g.setCornerRadius(dp(15));b.setBackground(g);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(16);page.addView(b,lp);b.setOnClickListener(v->action.run());}
    private void tile(String title,String detail,Runnable action){LinearLayout c=card();TextView t=text(title+"     ›",16,WHITE,true);c.addView(t);TextView sub=text(detail,12,MUTED,false);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(5);c.addView(sub,lp);c.setOnClickListener(v->action.run());}
    private String prop(String key){try{java.lang.Process p=Runtime.getRuntime().exec(new String[]{"getprop",key});BufferedReader r=new BufferedReader(new InputStreamReader(p.getInputStream()));String s=r.readLine();r.close();p.destroy();return s==null||s.isEmpty()?"Unknown":s.trim();}catch(Exception e){return "Unknown";}}
    private String selinux(){try{BufferedReader r=new BufferedReader(new java.io.FileReader("/sys/fs/selinux/enforce"));String s=r.readLine();r.close();return "1".equals(s)?"Enforcing":"0".equals(s)?"Permissive":"Unknown";}catch(Exception e){return "Unknown";}}
    private String bootloader(){String v=prop("ro.boot.flash.locked");if(v.equals("1"))return "Locked";if(v.equals("0"))return "Unlocked";v=prop("ro.boot.vbmeta.device_state");if(v.equalsIgnoreCase("locked"))return "Locked";if(v.equalsIgnoreCase("unlocked"))return "Unlocked";return "Unknown";}
    private String rootStatus(){return new File("/system/bin/su").exists()||new File("/system/xbin/su").exists()||new File("/sbin/su").exists()?"su ditemukan (belum diverifikasi)":"Tidak terdeteksi (bukan bukti)";}
    private String kernel(){return System.getProperty("os.version","Unknown");}
    private String report(){return "=== TempRoot v2.1 / Check Only ===\n"+"Model: "+Build.MANUFACTURER+" "+Build.MODEL+"\nAndroid: "+Build.VERSION.RELEASE+" (API "+Build.VERSION.SDK_INT+")\nSecurity patch: "+Build.VERSION.SECURITY_PATCH+"\nABI: "+String.join(", ",Build.SUPPORTED_ABIS)+"\nKernel: "+kernel()+"\nSELinux: "+selinux()+"\nBootloader: "+bootloader()+"\nVerified Boot: "+prop("ro.boot.verifiedbootstate")+"\nRoot: "+rootStatus()+"\nApp UID: "+Process.myUid()+"\nRoot engine: NOT AVAILABLE\nTidak ada exploit dijalankan.\n";}
    private void copy(){ClipboardManager cb=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);cb.setPrimaryClip(ClipData.newPlainText("TempRoot report",report));Toast.makeText(this,"Laporan disalin",Toast.LENGTH_SHORT).show();}
    private void showHome(){init("home");add("◈  TEMP ROOT",28,WHITE,true,10);add("SYSTEM DIAGNOSTICS   /   V2.1",11,PURPLE,true,4);hero("DEVICE STATUS","Know your device.","Pemeriksaan sistem yang jelas, aman, dan sepenuhnya lokal.");LinearLayout c=card();c.addView(text("PERANGKAT",11,PURPLE,true));c.addView(text(Build.MANUFACTURER+" "+Build.MODEL,19,WHITE,true));c.addView(text("Android "+Build.VERSION.RELEASE+"  •  Kernel "+kernel(),12,MUTED,false));LinearLayout info=card();info.addView(text("◈   CHECK ONLY",15,PURPLE,true));info.addView(text("Pemeriksaan aman tanpa perubahan sistem. Root sementara belum tersedia dalam aplikasi ini.",13,MUTED,false));button("✦  Mulai Pemeriksaan",()->showScan());add("FITUR TEMP ROOT",12,MUTED,true,26);tile("◇  Kompatibilitas","Analisis kernel dan status dukungan",()->showCompatibility());tile("⬡  Root Engine","Status engine dan penelitian kompatibilitas",()->showEngine());tile("▤  Laporan","Lihat dan salin diagnostik",()->showReport());tile("⚙  Tools","Pemeriksaan keamanan perangkat",()->showTools());tile("ⓘ  Panduan & Tentang","Informasi penggunaan dan batasan",()->showGuide());tile("☷  Pengaturan","Informasi privasi dan aplikasi",()->showSettings());tile("◉  Informasi Perangkat","Detail perangkat dan sistem",()->showDevice());}
    private void showCheck(){scanned=true;report=report();init("Hasil Pemeriksaan");add("✓  Pemeriksaan selesai",22,Color.rgb(83,223,160),true,4);add("CHECK ONLY  •  Tidak ada perubahan sistem",13,MUTED,false,7);LinearLayout c=card();line(c,"Model",Build.MODEL);line(c,"Android",Build.VERSION.RELEASE+" (API "+Build.VERSION.SDK_INT+")");line(c,"Security patch",Build.VERSION.SECURITY_PATCH);line(c,"Kernel",kernel());line(c,"SELinux",selinux());line(c,"Bootloader",bootloader());line(c,"Verified Boot",prop("ro.boot.verifiedbootstate"));line(c,"Root",rootStatus());line(c,"App UID",String.valueOf(Process.myUid()));add("Catatan: pembacaan properti Android dapat dibatasi. Hasil Unknown bukan bukti status aman atau tidak aman.",12,MUTED,false,16);button("Salin Laporan",()->copy());button("Bagikan Laporan",()->shareReport());button("Analisis Kompatibilitas",()->showCompatibility());}
    private void showCompatibility(){init("Kompatibilitas");add("Analisis perangkat",24,WHITE,true,4);String k=kernel();LinearLayout c=card();line(c,"Kernel",k);line(c,"Arsitektur",Build.SUPPORTED_ABIS.length>0?Build.SUPPORTED_ABIS[0]:"Unknown");line(c,"Android",Build.VERSION.RELEASE);line(c,"Bootloader",bootloader());line(c,"SELinux",selinux());add("Status metode: BELUM TERVERIFIKASI",17,PURPLE,true,20);add("TempRoot v2 belum memiliki database profil exploit yang tervalidasi. Dukungan kernel harus diperiksa terhadap engine yang tervalidasi. Tidak ada klaim kompatibilitas otomatis.",13,MUTED,false,9);button("Lihat Root Engine",()->showEngine());}
    private void showEngine(){init("Root Engine");add("⬡",52,PURPLE,true,10);add("Temporary Root Engine",24,WHITE,true,8);add("Status: BELUM TERSEDIA",16,PURPLE,true,14);LinearLayout c=card();line(c,"DirtyFrag","Tidak terintegrasi");line(c,"GhostLock","Tidak terintegrasi");line(c,"KernelSU","Tidak terintegrasi");add("Tidak ada tombol aktivasi root. Aplikasi ini tidak mengunduh, memasang, atau menjalankan exploit.",13,MUTED,false,18);}
    private void showReport(){init("Laporan Diagnostik");if(!scanned)report=report();add("Hasil pemeriksaan lokal",21,WHITE,true,5);LinearLayout c=card();TextView t=text(report,12,WHITE,false);t.setTypeface(Typeface.MONOSPACE);c.addView(t);button("Salin Laporan",()->copy());button("Bagikan Laporan",()->shareReport());}
    private void showTools(){init("Tools");tile("Cek Root","Deteksi indikasi binary su (bukan verifikasi root)",()->showCheck());tile("Cek SELinux","Membaca status enforcing / permissive",()->showCheck());tile("Cek Bootloader","Membaca properti boot jika tersedia",()->showCheck());tile("Cek Verified Boot","Membaca properti verified boot",()->showCheck());tile("Cek Kernel","Versi kernel dan ABI perangkat",()->showCheck());}
    private void showGuide(){init("Panduan");add("TempRoot v2",26,WHITE,true,6);add("Temporary Root Manager — Check Only",14,PURPLE,true,7);tile("1. Mulai Pemeriksaan","Tekan tombol pemeriksaan pada dashboard.",()->showScan());tile("2. Baca Hasil","Status Unknown berarti informasi tidak tersedia.",()->showCompatibility());tile("3. Salin Laporan","Bagikan hanya jika kamu nyaman dengan informasi perangkat yang tercantum.",()->showReport());tile("4. Root Engine","Belum tersedia; tidak ada perubahan sistem.",()->showEngine());add("Aplikasi ini bukan Magisk, tidak memberikan root, dan tidak membuktikan perangkat rentan exploit.",13,MUTED,false,20);button("Tentang aplikasi",()->showAbout());}
    private void showSettings(){init("Pengaturan");tile("Tampilan","Tema gelap ungu premium (tetap)",()->Toast.makeText(this,"Tema gelap aktif",Toast.LENGTH_SHORT).show());tile("Privasi","Offline, tanpa izin internet atau akses file",()->showPrivacy());tile("Versi","TempRoot 2.1 · Check Only",()->showAbout());tile("Buat laporan baru","Perbarui informasi perangkat",()->showScan());}
    @Override public void onBackPressed(){if(screen.equals("home"))super.onBackPressed();else showHome();}
}
