package com.principal.cp.maestros;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.ArrayList;
import java.util.List;

public class AlumnoAvisoAdapter extends RecyclerView.Adapter<AlumnoAvisoAdapter.ViewHolder> {
    private List<AlumnoAviso> alumnos;

    public AlumnoAvisoAdapter(List<AlumnoAviso> alumnos) {
        this.alumnos = alumnos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkAlumno;
        TextView txtNombre;

        public ViewHolder(View view) {
            super(view);
            chkAlumno = view.findViewById(R.id.chkAlumno);
            txtNombre = view.findViewById(R.id.txtNombreAlumno);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno_checkbox, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlumnoAviso alumno = alumnos.get(position);
        holder.txtNombre.setText(alumno.nombre + " " + alumno.apellido);
        holder.chkAlumno.setChecked(alumno.seleccionado);

        holder.chkAlumno.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alumno.seleccionado = isChecked;
        });
    }

    @Override
    public int getItemCount() {
        return alumnos.size();
    }

    public List<Integer> getIdsSeleccionados() {
        List<Integer> ids = new ArrayList<>();
        for (AlumnoAviso alumno : alumnos) {
            if (alumno.seleccionado) ids.add(alumno.id);
        }
        return ids;
    }
}

