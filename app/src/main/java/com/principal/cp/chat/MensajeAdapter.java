package com.principal.cp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private List<Mensaje> mensajes;
    private int idUsuarioActual; // ID del usuario logueado

    public MensajeAdapter(List<Mensaje> mensajes, int idUsuarioActual) {
        this.mensajes = mensajes;
        this.idUsuarioActual = idUsuarioActual;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;

        public ViewHolder(View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Si el emisor es el usuario actual, usa burbuja derecha
        return mensajes.get(position).getEmisor() == idUsuarioActual ? 1 : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_derecha, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_izquierda, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.txtMensaje.setText(mensaje.getMensaje());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }
}
