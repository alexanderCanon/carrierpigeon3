package com.principal.cp.maestros;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;
public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.ViewHolder> {

    private final List<Alumno> lista;

    public AlumnoAdapter(List<Alumno> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alumno, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alumno a = lista.get(position);
        holder.txtNombre.setText(a.getNombre() + " " + a.getApellido());

        holder.btnOpciones.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnOpciones);
            popup.inflate(R.menu.menu_opciones_alumno);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_ver_notas_alumno) {
                    Intent intent = new Intent(v.getContext(), VerNotasAlumnoActivity.class);
                    intent.putExtra("id_alumno", a.getId());
                    v.getContext().startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_ver_asistencia_alumno) {
                    Intent intent = new Intent(v.getContext(), VerAsistenciaAlumnoActivity.class);
                    intent.putExtra("id_alumno", a.getId());
                    v.getContext().startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        ImageButton btnOpciones;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreAlumno);
            btnOpciones = itemView.findViewById(R.id.btnOpcionesAlumno);
        }
    }

}
