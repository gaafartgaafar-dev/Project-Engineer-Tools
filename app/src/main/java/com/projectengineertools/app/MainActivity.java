package com.projectengineertools.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(244, 247, 250);
    private final int NAVY = Color.rgb(11, 19, 32);
    private final int TEAL = Color.rgb(14, 116, 144);
    private final int TEXT = Color.rgb(30, 41, 59);
    private final int MUTED = Color.rgb(100, 116, 139);
    private final int CARD = Color.WHITE;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
    private SharedPreferences prefs;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("project_engineer_tools", MODE_PRIVATE);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(28));
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        scroll.addView(root);

        addHeader();
        addProgressCalculator();
        addDelayCalculator();
        addEotCalculator();
        addNotes();
        addFooter();

        setContentView(scroll);
    }

    private void addHeader() {
        LinearLayout box = card();
        box.setBackgroundColor(NAVY);

        TextView title = text("Project Engineer Tools", 24, Color.WHITE, true);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        box.addView(title);

        TextView sub = text("أدوات سريعة لمهندس المشروع", 15, Color.rgb(203, 213, 225), false);
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        sub.setPadding(0, dp(6), 0, 0);
        box.addView(sub);

        root.addView(box, marginBottom(14));
    }

    private void addProgressCalculator() {
        LinearLayout c = card();
        c.addView(sectionTitle("حساب تقدم المشروع"));

        EditText planned = numberField("التقدم المخطط %");
        EditText actual = numberField("التقدم الفعلي %");
        TextView result = resultText();
        Button btn = actionButton("احسب الانحراف");

        btn.setOnClickListener(v -> {
            Double p = parseDouble(planned);
            Double a = parseDouble(actual);
            if (p == null || a == null) {
                showError("أدخل النسبتين بشكل صحيح.");
                return;
            }
            double variance = a - p;
            String status = variance >= 0 ? "متقدم / مطابق للخطة" : "متأخر عن الخطة";
            result.setText(String.format(Locale.US, "الانحراف: %.2f%%\nالحالة: %s", variance, status));
        });

        c.addView(planned);
        c.addView(actual);
        c.addView(btn);
        c.addView(result);
        root.addView(c, marginBottom(14));
    }

    private void addDelayCalculator() {
        LinearLayout c = card();
        c.addView(sectionTitle("حساب أيام التأخير"));

        EditText plannedDate = dateField("التاريخ المخطط");
        EditText actualDate = dateField("التاريخ الفعلي");
        TextView result = resultText();
        Button btn = actionButton("احسب التأخير");

        btn.setOnClickListener(v -> {
            Date p = parseDate(plannedDate);
            Date a = parseDate(actualDate);
            if (p == null || a == null) {
                showError("اختر التاريخ المخطط والتاريخ الفعلي.");
                return;
            }
            long days = daysBetween(p, a);
            if (days > 0) result.setText("التأخير: " + days + " يوم");
            else if (days == 0) result.setText("لا يوجد تأخير.");
            else result.setText("الإنجاز مبكر بـ " + Math.abs(days) + " يوم");
        });

        c.addView(plannedDate);
        c.addView(actualDate);
        c.addView(btn);
        c.addView(result);
        root.addView(c, marginBottom(14));
    }

    private void addEotCalculator() {
        LinearLayout c = card();
        c.addView(sectionTitle("حاسبة EOT"));
        TextView hint = text("احسب عدد الأيام بين تاريخ الإنجاز الأصلي والتاريخ المعدل.", 13, MUTED, false);
        hint.setPadding(0, 0, 0, dp(8));
        c.addView(hint);

        EditText original = dateField("تاريخ الإنجاز الأصلي");
        EditText revised = dateField("تاريخ الإنجاز المعدل");
        TextView result = resultText();
        Button btn = actionButton("احسب مدة EOT");

        btn.setOnClickListener(v -> {
            Date o = parseDate(original);
            Date r = parseDate(revised);
            if (o == null || r == null) {
                showError("اختر التاريخين أولاً.");
                return;
            }
            long days = daysBetween(o, r);
            result.setText(days >= 0 ? "مدة EOT: " + days + " يوم" : "التاريخ المعدل أسبق من التاريخ الأصلي بـ " + Math.abs(days) + " يوم");
        });

        c.addView(original);
        c.addView(revised);
        c.addView(btn);
        c.addView(result);
        root.addView(c, marginBottom(14));
    }

    private void addNotes() {
        LinearLayout c = card();
        c.addView(sectionTitle("ملاحظات الموقع"));

        EditText notes = new EditText(this);
        notes.setHint("اكتب ملاحظاتك أو الإجراءات المطلوبة...");
        notes.setText(prefs.getString("notes", ""));
        notes.setTextSize(15);
        notes.setTextColor(TEXT);
        notes.setHintTextColor(MUTED);
        notes.setGravity(Gravity.TOP | Gravity.RIGHT);
        notes.setMinLines(5);
        notes.setPadding(dp(12), dp(10), dp(12), dp(10));
        notes.setBackgroundColor(Color.rgb(248, 250, 252));

        Button save = actionButton("حفظ الملاحظات");
        save.setOnClickListener(v -> {
            prefs.edit().putString("notes", notes.getText().toString()).apply();
            Toast.makeText(this, "تم حفظ الملاحظات على الهاتف", Toast.LENGTH_SHORT).show();
        });

        c.addView(notes, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        c.addView(save);
        root.addView(c, marginBottom(14));
    }

    private void addFooter() {
        TextView footer = text("الإصدار 1.0 • يعمل بدون إنترنت", 12, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        root.addView(footer);
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(dp(16), dp(16), dp(16), dp(16));
        c.setBackgroundColor(CARD);
        c.setElevation(dp(2));
        c.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        return c;
    }

    private TextView sectionTitle(String s) {
        TextView t = text(s, 18, NAVY, true);
        t.setPadding(0, 0, 0, dp(10));
        return t;
    }

    private TextView text(String s, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private EditText numberField(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(16);
        e.setTextColor(TEXT);
        e.setHintTextColor(MUTED);
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        e.setGravity(Gravity.RIGHT);
        e.setPadding(dp(10), dp(10), dp(10), dp(10));
        return e;
    }

    private EditText dateField(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint + "  DD/MM/YYYY");
        e.setTextSize(16);
        e.setTextColor(TEXT);
        e.setHintTextColor(MUTED);
        e.setFocusable(false);
        e.setClickable(true);
        e.setGravity(Gravity.RIGHT);
        e.setPadding(dp(10), dp(10), dp(10), dp(10));
        e.setOnClickListener(v -> showDatePicker(e));
        return e;
    }

    private Button actionButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackgroundColor(TEAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(50));
        p.setMargins(0, dp(10), 0, dp(8));
        b.setLayoutParams(p);
        return b;
    }

    private TextView resultText() {
        TextView r = text("", 15, TEXT, true);
        r.setPadding(0, dp(4), 0, 0);
        return r;
    }

    private void showDatePicker(EditText target) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar chosen = Calendar.getInstance();
                    chosen.set(year, month, dayOfMonth, 0, 0, 0);
                    chosen.set(Calendar.MILLISECOND, 0);
                    target.setText(sdf.format(chosen.getTime()));
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    private Double parseDouble(EditText e) {
        try {
            return Double.parseDouble(e.getText().toString().trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private Date parseDate(EditText e) {
        try {
            sdf.setLenient(false);
            return sdf.parse(e.getText().toString().trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private long daysBetween(Date from, Date to) {
        long diff = to.getTime() - from.getTime();
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private LinearLayout.LayoutParams marginBottom(int bottomDp) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(bottomDp));
        return p;
    }

    private int dp(int v) {
        float d = getResources().getDisplayMetrics().density;
        return Math.round(v * d);
    }
}
