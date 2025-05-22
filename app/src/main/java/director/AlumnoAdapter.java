package director;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.List;

public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.ViewHolder> {

    private List<Alumno> lista;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditarClick(Alumno alumno);
        void onEliminarClick(Alumno alumno);
    }

    public AlumnoAdapter(List<Alumno> lista, Context context, OnItemClickListener listener) {
        this.lista = lista;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_alumno_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Alumno a = lista.get(position);
        holder.txtNombre.setText(a.getNombre());
        holder.txtDetalles.setText(a.getGrado() + " - " + a.getSeccion());

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(a));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(a));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDetalles;
        Button btnEditar, btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDetalles = itemView.findViewById(R.id.txtDetalles);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
