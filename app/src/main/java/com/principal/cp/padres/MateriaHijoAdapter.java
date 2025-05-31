package com.principal.cp.padres;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class MateriaHijoAdapter extends RecyclerView.Adapter<MateriaHijoAdapter.ViewHolder> {

    private Context context;
    private List<MateriaHijo> listaMaterias;
    private int idAlumno;

    public MateriaHijoAdapter(Context context, List<MateriaHijo> listaMaterias, int idAlumno) {
        this.context = context;
        this.listaMaterias = listaMaterias;
        this.idAlumno = idAlumno;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreMateria, tvGradoSeccion;
        Button btnVerNotas, btnVerAsistencia;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombreMateria = itemView.findViewById(R.id.tvNombreMateria);
            tvGradoSeccion = itemView.findViewById(R.id.tvGradoSeccion);
            btnVerNotas = itemView.findViewById(R.id.btnVerNotas);
            btnVerAsistencia = itemView.findViewById(R.id.btnVerAsistencia);
        }
    }

    @Override
    public MateriaHijoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_materia_hijo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MateriaHijoAdapter.ViewHolder holder, int position) {
        MateriaHijo materia = listaMaterias.get(position);

        holder.tvNombreMateria.setText(materia.getNombreMateria());
        holder.tvGradoSeccion.setText(materia.getGrado() + " - " + materia.getSeccion());

        holder.btnVerNotas.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerNotasAlumnoActivityPadre.class);
            intent.putExtra("idAsignacion", materia.getIdAsignacion());
            intent.putExtra("idAlumno", idAlumno);
            context.startActivity(intent);
        });

        holder.btnVerAsistencia.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerAsistenciaAlumnoActivityPadre.class);
            intent.putExtra("idAlumno", idAlumno);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }
}
