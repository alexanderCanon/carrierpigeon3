package com.principal.cp.maestros;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(Materia materia);
    }

    private final List<Materia> lista;
    private final OnItemClickListener listener;
    private final Context context;

    public MateriaAdapter(Context context, List<Materia> lista, OnItemClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_materia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Materia materia = lista.get(position);
        holder.txtMateria.setText(materia.getNombre());
        holder.txtGrupo.setText(materia.getGrado() + " " + materia.getSeccion());

        holder.itemView.setOnClickListener(v -> listener.onClick(materia));

        holder.btnOpciones.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnOpciones);
            popup.inflate(R.menu.menu_opciones_materia);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_ver_alumnos) {
                    Intent intent1 = new Intent(context, AlumnosPorMateriaActivity.class);
                    intent1.putExtra("id_asignacion", materia.getId_asignacion());
                    intent1.putExtra("grado", materia.getGrado());
                    intent1.putExtra("seccion", materia.getSeccion());
                    context.startActivity(intent1);
                    return true;

                } else if (itemId == R.id.menu_ver_notas) {
                    Intent intent2 = new Intent(context, VerNotasMateriaActivity.class);
                    intent2.putExtra("id_asignacion", materia.getId_asignacion());
                    intent2.putExtra("nombre", materia.getNombre());
                    context.startActivity(intent2);
                    return true;

                } else if (itemId == R.id.menu_ver_asistencia) {
                    Intent intent3 = new Intent(context, VerAsistenciaMateriaActivity.class);
                    intent3.putExtra("id_asignacion", materia.getId_asignacion());
                    intent3.putExtra("nombre", materia.getNombre());
                    context.startActivity(intent3);
                    return true;

                } else {
                    return false;
                }
            });
            popup.show();
        });
    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMateria, txtGrupo;
        ImageButton btnOpciones;

        ViewHolder(View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
