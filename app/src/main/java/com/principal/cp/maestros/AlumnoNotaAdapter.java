package com.principal.cp.maestros;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class AlumnoNotaAdapter extends RecyclerView.Adapter<AlumnoNotaAdapter.ViewHolder> {

    private List<AlumnoNota> listaAlumnos;
    private double maxNota;

    public AlumnoNotaAdapter(List<AlumnoNota> listaAlumnos, double maxNota) {
        this.listaAlumnos = listaAlumnos;
        this.maxNota = maxNota;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alumno_nota, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlumnoNota alumno = listaAlumnos.get(position);
        holder.txtNombre.setText(alumno.getNombreCompleto());

        holder.edtNota.setText(alumno.nota);
        holder.edtNota.setError(null);

// Evitar múltiples textwatchers
        holder.edtNota.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double valorIngresado = Double.parseDouble(s.toString());

                    if (valorIngresado < 0) {
                        holder.edtNota.setError("La nota no puede ser negativa");
                        alumno.nota = ""; // no guardar si es negativa
                    } else if (valorIngresado > maxNota) {
                        holder.edtNota.setError("La nota no puede ser mayor a " + maxNota);
                        alumno.nota = ""; // no guardar si es inválida
                    } else {
                        holder.edtNota.setError(null);
                        alumno.nota = s.toString();
                    }
                } catch (NumberFormatException e) {
                    holder.edtNota.setError("Ingrese un número válido");
                    alumno.nota = "";
                }
            }
        });


        holder.edtObservacion.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                alumno.observaciones = holder.edtObservacion.getText().toString();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listaAlumnos.size();
    }

    public List<AlumnoNota> getListaAlumnos() {
        return listaAlumnos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        EditText edtNota, edtObservacion;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            edtNota = itemView.findViewById(R.id.edtNota);
            edtObservacion = itemView.findViewById(R.id.edtObservacion);
        }
    }

    // Clase base para simplificar el uso de TextWatcher
    public abstract static class TextWatcherAdapter implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
