package com.principal.cp.padres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.ArrayList;

public class ActividadHijoAdapter extends RecyclerView.Adapter<ActividadHijoAdapter.ViewHolder> {

    private ArrayList<ActividadHijo> listaActividades;

    public ActividadHijoAdapter(ArrayList<ActividadHijo> listaActividades) {
        this.listaActividades = listaActividades;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad_hijo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActividadHijo actividad = listaActividades.get(position);

        holder.tvTitulo.setText(actividad.getTitulo());
        holder.tvFecha.setText("Entrega: " + actividad.getFechaEntrega());
        holder.tvEstado.setText("Estado: " + actividad.getEstado());

        if (!actividad.getNota().isEmpty()) {
            holder.tvNota.setText("Nota: " + actividad.getNota());
        } else {
            holder.tvNota.setText("Nota: Pendiente");
        }
    }

    @Override
    public int getItemCount() {
        return listaActividades.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvFecha, tvEstado, tvNota;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloActividad);
            tvFecha = itemView.findViewById(R.id.tvFechaEntrega);
            tvEstado = itemView.findViewById(R.id.tvEstadoActividad);
            tvNota = itemView.findViewById(R.id.tvNotaActividad);
        }
    }
    public void actualizarLista(ArrayList<ActividadHijo> nuevas) {
        this.listaActividades = nuevas;
        notifyDataSetChanged();
    }

}
