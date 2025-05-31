package director;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;


import java.util.List;

public class SeleccionarMaestroAdapter extends RecyclerView.Adapter<SeleccionarMaestroAdapter.ViewHolder> {

    private List<MaestroSeleccionable> listaMaestros;
    private Context context;

    public SeleccionarMaestroAdapter(Context context, List<MaestroSeleccionable> listaMaestros) {
        this.context = context;
        this.listaMaestros = listaMaestros;
    }

    @NonNull
    @Override
    public SeleccionarMaestroAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_maestro_checkbox, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull SeleccionarMaestroAdapter.ViewHolder holder, int position) {
        MaestroSeleccionable maestro = listaMaestros.get(position);
        holder.checkBox.setText(maestro.getNombre());
        holder.checkBox.setChecked(maestro.isSeleccionado());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            maestro.setSeleccionado(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return listaMaestros.size();
    }

    public List<MaestroSeleccionable> getSeleccionados() {
        List<MaestroSeleccionable> seleccionados = new java.util.ArrayList<>();
        for (MaestroSeleccionable m : listaMaestros) {
            if (m.isSeleccionado()) {
                seleccionados.add(m);
            }
        }
        return seleccionados;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkboxMaestro);
        }
    }
}
