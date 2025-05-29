package com.principal.cp.maestros;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.ArrayList;

public class MateriaAsistenciaAdapter extends RecyclerView.Adapter<MateriaAsistenciaAdapter.ViewHolder> {

    private final ArrayList<Materia> listaMaterias;
    private final Context context;

    public MateriaAsistenciaAdapter(ArrayList<Materia> listaMaterias, Context context) {
        this.listaMaterias = listaMaterias;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMateria, txtGrupo;
        ImageButton btnMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }

    @NonNull
    @Override
    public MateriaAsistenciaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_curso_asistencia, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaAsistenciaAdapter.ViewHolder holder, int position) {
        Materia materia = listaMaterias.get(position);

        holder.txtMateria.setText(materia.getNombre());
        holder.txtGrupo.setText(materia.getGrado() + " " + materia.getSeccion());

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMenu);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_asistencia_curso, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                Intent intent = new Intent(context, RegistrarAsistenciaActivity.class);
                intent.putExtra("id_asignacion", materia.getId_asignacion());
                intent.putExtra("grado", materia.getGrado());
                intent.putExtra("seccion", materia.getSeccion());

                if (item.getItemId() == R.id.registrar_asistencia) {
                    intent.putExtra("modo", "registrar");
                } else if (item.getItemId() == R.id.editar_asistencia) {
                    intent.putExtra("modo", "editar");
                }

                context.startActivity(intent);
                return true;
            });

            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }
}
