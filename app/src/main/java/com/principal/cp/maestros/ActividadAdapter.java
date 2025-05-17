package com.principal.cp.maestros;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.principal.cp.R;
import com.principal.cp.maestros.Actividad;

import java.util.List;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> lista;

    public ActividadAdapter(List<Actividad> lista) {
        this.lista = lista;
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtFecha, txtTipo;

        public ActividadViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTituloActividad);
            txtFecha = itemView.findViewById(R.id.txtFechaEntrega);
            txtTipo = itemView.findViewById(R.id.txtTipoActividad);
        }
    }

    @Override
    public ActividadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActividadViewHolder holder, int position) {
        Actividad a = lista.get(position);
        holder.txtTitulo.setText(a.titulo);
        holder.txtFecha.setText("Entrega: " + a.fecha_entrega);
        holder.txtTipo.setText("Tipo: " + a.tipo);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}
