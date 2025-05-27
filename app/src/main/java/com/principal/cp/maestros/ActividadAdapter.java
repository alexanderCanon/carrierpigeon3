package com.principal.cp.maestros;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.principal.cp.R;
import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> listaActividades;

    public ActividadAdapter(List<Actividad> listaActividades) {
        this.listaActividades = listaActividades;
    }

    @Override
    public ActividadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ActividadViewHolder holder, int position) {
        Actividad actividad = listaActividades.get(position);
        holder.txtTitulo.setText(actividad.titulo);
        holder.txtTipo.setText("Tipo: " + actividad.tipo);
        holder.txtFecha.setText("Entrega: " + actividad.fecha_entrega);
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtTipo, txtFecha;

        public ActividadViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtFecha = itemView.findViewById(R.id.txtFecha);
        }
    }
}
