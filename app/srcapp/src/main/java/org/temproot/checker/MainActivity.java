package org.temproot.checker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Process;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);showHome();}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));d.setStroke(dp(1),Color.rgb(48,43,73));return d;}
    private void init(String title){screen=title;root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);setContentView(root);ScrollView sc=new ScrollView(this);sc.setFillViewport(true);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));page=new LinearLayout(this);page.setOrientation(LinearLayout.VERTICAL);page.setPadding(dp(20),dp(20),dp(20),dp(24));sc.addView(page);if(!title.equals("home")){TextView back=text("‹   "+title,22,WHITE,true);back.setPadding(0,0,0,dp(22));back.setOnClickListener(v->showHome());page.addView(back);} }
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
    private String report(){return "=== TempRoot v2 / Check Only ===\n"+"Model: "+Build.MANUFACTURER+" "+Build.MODEL+"\nAndroid: "+Build.VERSION.RELEASE+" (API "+Build.VERSION.SDK_INT+")\nSecurity patch: "+Build.VERSION.SECURITY_PATCH+"\nABI: "+String.join(", ",Build.SUPPORTED_ABIS)+"\nKernel: "+kernel()+"\nSELinux: "+selinux()+"\nBootloader: "+bootloader()+"\nVerified Boot: "+prop("ro.boot.verifiedbootstate")+"\nRoot: "+rootStatus()+"\nApp UID: "+Process.myUid()+"\nRoot engine: NOT AVAILABLE\nTidak ada exploit dijalankan.\n";}
    private void copy(){ClipboardManager cb=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);cb.setPrimaryClip(ClipData.newPlainText("TempRoot report",report));Toast.makeText(this,"Laporan disalin",Toast.LENGTH_SHORT).show();}
    private void showHome(){init("home");add("◈  TempRoot",30,WHITE,true,10);add("Temporary Root Manager  ·  v2.0",13,MUTED,false,3);LinearLayout c=card();c.addView(text("PERANGKAT",11,PURPLE,true));c.addView(text(Build.MANUFACTURER+" "+Build.MODEL,19,WHITE,true));c.addView(text("Android "+Build.VERSION.RELEASE+"  •  Kernel "+kernel(),12,MUTED,false));LinearLayout info=card();info.addView(text("◈   CHECK ONLY",15,PURPLE,true));info.addView(text("Pemeriksaan aman tanpa perubahan sistem. Root sementara belum tersedia untuk perangkat ini.",13,MUTED,false));button("✦  Mulai Pemeriksaan",()->showCheck());add("FITUR TEMP ROOT",12,MUTED,true,26);tile("◇  Kompatibilitas","Analisis kernel dan status dukungan",()->showCompatibility());tile("⬡  Root Engine","Status engine dan penelitian kompatibilitas",()->showEngine());tile("▤  Laporan","Lihat dan salin diagnostik",()->showReport());tile("⚙  Tools","Pemeriksaan keamanan perangkat",()->showTools());tile("ⓘ  Panduan & Tentang","Informasi penggunaan dan batasan",()->showGuide());tile("☷  Pengaturan","Informasi privasi dan aplikasi",()->showSettings());}
    private void showCheck(){scanned=true;report=report();init("Hasil Pemeriksaan");add("✓  Pemeriksaan selesai",22,Color.rgb(83,223,160),true,4);add("CHECK ONLY  •  Tidak ada perubahan sistem",13,MUTED,false,7);LinearLayout c=card();line(c,"Model",Build.MODEL);line(c,"Android",Build.VERSION.RELEASE+" (API "+Build.VERSION.SDK_INT+")");line(c,"Security patch",Build.VERSION.SECURITY_PATCH);line(c,"Kernel",kernel());line(c,"SELinux",selinux());line(c,"Bootloader",bootloader());line(c,"Verified Boot",prop("ro.boot.verifiedbootstate"));line(c,"Root",rootStatus());line(c,"App UID",String.valueOf(Process.myUid()));add("Catatan: pembacaan properti Android dapat dibatasi. Hasil Unknown bukan bukti status aman atau tidak aman.",12,MUTED,false,16);button("Salin Laporan",()->copy());button("Analisis Kompatibilitas",()->showCompatibility());}
    private void showCompatibility(){init("Kompatibilitas");add("Analisis perangkat",24,WHITE,true,4);String k=kernel();LinearLayout c=card();line(c,"Kernel",k);line(c,"Arsitektur",Build.SUPPORTED_ABIS.length>0?Build.SUPPORTED_ABIS[0]:"Unknown");line(c,"Android",Build.VERSION.RELEASE);line(c,"Bootloader",bootloader());line(c,"SELinux",selinux());add("Status metode: BELUM TERVERIFIKASI",17,PURPLE,true,20);add("TempRoot v2 belum memiliki database profil exploit yang tervalidasi. Kernel 4.19 non-GKI pada perangkat target belum didukung oleh engine yang kita miliki. Tidak ada klaim kompatibilitas otomatis.",13,MUTED,false,9);button("Lihat Root Engine",()->showEngine());}
    private void showEngine(){init("Root Engine");add("⬡",52,PURPLE,true,10);add("Temporary Root Engine",24,WHITE,true,8);add("Status: BELUM TERSEDIA",16,PURPLE,true,14);LinearLayout c=card();line(c,"DirtyFrag","Tidak terintegrasi");line(c,"GhostLock","Tidak terintegrasi");line(c,"KernelSU","Tidak terintegrasi");add("Tidak ada tombol aktivasi root. Aplikasi ini tidak mengunduh, memasang, atau menjalankan exploit.",13,MUTED,false,18);}
    private void showReport(){init("Laporan Diagnostik");if(!scanned)report=report();add("Hasil pemeriksaan lokal",21,WHITE,true,5);LinearLayout c=card();TextView t=text(report,12,WHITE,false);t.setTypeface(Typeface.MONOSPACE);c.addView(t);button("Salin Laporan",()->copy());}
    private void showTools(){init("Tools");tile("Cek Root","Deteksi indikasi binary su (bukan verifikasi root)",()->showCheck());tile("Cek SELinux","Membaca status enforcing / permissive",()->showCheck());tile("Cek Bootloader","Membaca properti boot jika tersedia",()->showCheck());tile("Cek Verified Boot","Membaca properti verified boot",()->showCheck());tile("Cek Kernel","Versi kernel dan ABI perangkat",()->showCheck());}
    private void showGuide(){init("Panduan & Tentang");add("TempRoot v2",26,WHITE,true,6);add("Temporary Root Manager — Check Only",14,PURPLE,true,7);tile("1. Mulai Pemeriksaan","Tekan tombol pemeriksaan pada dashboard.",()->showCheck());tile("2. Baca Hasil","Status Unknown berarti informasi tidak tersedia.",()->showCompatibility());tile("3. Salin Laporan","Bagikan hanya jika kamu nyaman dengan informasi perangkat yang tercantum.",()->showReport());tile("4. Root Engine","Belum tersedia; tidak ada perubahan sistem.",()->showEngine());add("Aplikasi ini bukan Magisk, tidak memberikan root, dan tidak membuktikan perangkat rentan exploit.",13,MUTED,false,20);}
    private void showSettings(){init("Pengaturan");tile("Tampilan","Tema gelap ungu premium (tetap)",()->Toast.makeText(this,"Tema gelap aktif",Toast.LENGTH_SHORT).show());tile("Privasi","Offline, tanpa izin internet atau akses file",()->Toast.makeText(this,"Tidak ada izin jaringan",Toast.LENGTH_SHORT).show());tile("Versi","TempRoot 2.0 · Check Only",()->showGuide());tile("Buat laporan baru","Perbarui informasi perangkat",()->showCheck());}
    @Override public void onBackPressed(){if(screen.equals("home"))super.onBackPressed();else showHome();}
}
