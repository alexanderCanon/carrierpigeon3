package director;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.principal.cp.R;

public class AgregarMateriaAdapter extends RecyclerView.Adapter<AgregarMateriaAdapter.ViewHolder> {
    private List<AgregarMateria> listaMaterias;

    public AgregarMateriaAdapter(List<AgregarMateria> listaMaterias) {
        this.listaMaterias = listaMaterias;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion;

        public ViewHolder(View view) {
            super(view);
            tvNombre = view.findViewById(R.id.tvNombreMateria);
            tvDescripcion = view.findViewById(R.id.tvDescripcionMateria);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_agregar_materia, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AgregarMateria materia = listaMaterias.get(position);
        holder.tvNombre.setText(materia.getNombre());
        holder.tvDescripcion.setText(materia.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return listaMaterias.size();
    }
}
