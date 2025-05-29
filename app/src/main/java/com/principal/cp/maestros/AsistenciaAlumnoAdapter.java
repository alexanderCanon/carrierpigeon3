package com.principal.cp.maestros;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.ArrayList;

public class AsistenciaAlumnoAdapter extends RecyclerView.Adapter<AsistenciaAlumnoAdapter.ViewHolder> {

    private final ArrayList<AsistenciaAlumno> listaAlumnos;
    private final Context context;

    public AsistenciaAlumnoAdapter(ArrayList<AsistenciaAlumno> listaAlumnos, Context context) {
        this.listaAlumnos = listaAlumnos;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        CheckBox checkAsistencia;
        EditText edtObservaciones;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreAlumno);
            checkAsistencia = itemView.findViewById(R.id.checkAsistencia);
            edtObservaciones = itemView.findViewById(R.id.edtObservaciones);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_asistencia_alumno, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AsistenciaAlumno alumno = listaAlumnos.get(position);
        holder.txtNombre.setText(alumno.getNombre());
        holder.checkAsistencia.setChecked(alumno.isPresente());
        holder.checkAsistencia.setOnCheckedChangeListener((buttonView, isChecked) -> alumno.setPresente(isChecked));
        holder.edtObservaciones.setText(alumno.getObservaciones());

        holder.edtObservaciones.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alumno.setObservaciones(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @Override
    public int getItemCount() {
        return listaAlumnos.size();
    }
}
