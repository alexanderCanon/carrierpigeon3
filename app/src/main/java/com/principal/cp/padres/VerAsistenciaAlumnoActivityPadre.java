package com.principal.cp.padres;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class VerAsistenciaAlumnoActivityPadre extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private int idAlumno;
    private static final String URL = "http://34.71.103.241/asistencia_por_alumno.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_asistencia_alumno_padre);

        calendarView = findViewById(R.id.calendarView);

        idAlumno = getIntent().getIntExtra("idAlumno", -1);
        if (idAlumno != -1) {
            cargarAsistencia();
        } else {
            Toast.makeText(this, "Error: ID del alumno inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarAsistencia() {
        String url = URL + "?id_alumno=" + idAlumno;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Set<CalendarDay> diasPresentes = new HashSet<>();
                        Set<CalendarDay> diasFalta = new HashSet<>();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String fechaStr = obj.getString("fecha"); // formato yyyy-MM-dd
                            int estado = obj.getInt("estado");

                            LocalDate fecha = LocalDate.parse(fechaStr);
                            CalendarDay dia = CalendarDay.from(fecha.getYear(), fecha.getMonthValue() - 1, fecha.getDayOfMonth());

                            if (estado == 1) {
                                diasPresentes.add(dia);
                            } else {
                                diasFalta.add(dia);
                            }
                        }

                        calendarView.addDecorator(new PresenteDecorator(diasPresentes));
                        calendarView.addDecorator(new FaltaDecorator(diasFalta));

                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando asistencia", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // Decorador para días presentes (verde claro)
    public static class PresenteDecorator implements DayViewDecorator {
        private final Set<CalendarDay> fechas;

        public PresenteDecorator(Set<CalendarDay> fechas) {
            this.fechas = fechas;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return fechas.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C8E6C9")));
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
        }
    }

    // Decorador para faltas (rojo claro)
    public static class FaltaDecorator implements DayViewDecorator {
        private final Set<CalendarDay> fechas;

        public FaltaDecorator(Set<CalendarDay> fechas) {
            this.fechas = fechas;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return fechas.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFCDD2")));
            view.addSpan(new ForegroundColorSpan(Color.BLACK));
        }
    }
}
