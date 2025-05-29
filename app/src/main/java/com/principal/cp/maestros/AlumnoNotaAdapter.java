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

    public AlumnoNotaAdapter(List<AlumnoNota> listaAlumnos) {
        this.listaAlumnos = listaAlumnos;
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
        holder.edtObservacion.setText(alumno.observaciones);

        holder.edtNota.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alumno.nota = s.toString();
            }
        });

        holder.edtObservacion.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alumno.observaciones = s.toString();
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
