package com.principal.cp.maestros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class MaestrosAdapter extends RecyclerView.Adapter<MaestrosAdapter.MaestroViewHolder> {

    private Context context;
    private List<Maestro> maestroList;

    public MaestrosAdapter(Context context, List<Maestro> maestroList) {
        this.context = context;
        this.maestroList = maestroList;
    }

    @Override
    public MaestroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_maestro, parent, false);
        return new MaestroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaestroViewHolder holder, int position) {
        Maestro maestro = maestroList.get(position);
        holder.txtNombre.setText(maestro.getNombre() + " " + maestro.getApellido());
        holder.txtCorreo.setText(maestro.getCorreo());
    }

    @Override
    public int getItemCount() {
        return maestroList.size();
    }

    public static class MaestroViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCorreo;

        public MaestroViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCorreo = itemView.findViewById(R.id.txtCorreo);
        }
    }
}
