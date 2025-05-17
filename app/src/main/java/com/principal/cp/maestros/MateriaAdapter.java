package com.principal.cp.maestros;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public MateriaAdapter(List<Materia> lista, OnItemClickListener listener) {
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
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMateria, txtGrupo;
        ViewHolder(View itemView) {
            super(itemView);
            txtMateria = itemView.findViewById(R.id.txtMateria);
            txtGrupo = itemView.findViewById(R.id.txtGrupo);
        }
    }
}
