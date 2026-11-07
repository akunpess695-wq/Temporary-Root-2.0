package org.temproot.checker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.Process;
import android.graphics.Typeface;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.content.ClipData;
import android.content.ClipboardManager;

public final class MainActivity extends Activity {
    private String report;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(28, 32, 28, 24);
        TextView title = new TextView(this);
        title.setText("TempRoot Checker — Check Only");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        layout.addView(title);
        TextView warning = new TextView(this);
        warning.setText("Tidak melakukan root, tidak menjalankan exploit, dan tidak mengubah kernel. Hasil bukan bukti kompatibilitas exploit.\n");
        warning.setTextSize(15);
        layout.addView(warning);
        report = "Perangkat: " + Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                 "Android: " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")\n" +
                 "Security patch: " + Build.VERSION.SECURITY_PATCH + "\n" +
                 "ABI: " + java.util.Arrays.toString(Build.SUPPORTED_ABIS) + "\n" +
                 "Kernel: " + System.getProperty("os.version", "unknown") + "\n" +
                 "UID aplikasi: " + Process.myUid() + " (bukan bukti root sistem)\n" +
                 "Bootloader: tidak diperiksa (gunakan Brevent/ADB)\n" +
                 "Root engine: TIDAK ADA\n" +
                 "Status: CHECK ONLY";
        TextView body = new TextView(this);
        body.setText(report);
        body.setTextSize(16);
        body.setTextIsSelectable(true);
        layout.addView(body);
        TextView copy = new TextView(this);
        copy.setText("SALIN LAPORAN");
        copy.setTextSize(17);
        copy.setPadding(0, 28, 0, 20);
        copy.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
            if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("TempRoot Checker", report));
            android.widget.Toast.makeText(this, "Laporan disalin", android.widget.Toast.LENGTH_SHORT).show();
        });
        layout.addView(copy);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }
}
