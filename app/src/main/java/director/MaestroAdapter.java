package director;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;
import java.util.List;
public class MaestroAdapter extends RecyclerView.Adapter<MaestroAdapter.MaestroViewHolder> {

    private Context context;
    private List<Maestro> listaMaestros;

    public MaestroAdapter(Context context, List<Maestro> listaMaestros) {
        this.context = context;
        this.listaMaestros = listaMaestros;
    }

    @NonNull
    @Override
    public MaestroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_maestro, parent, false);
        return new MaestroViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MaestroViewHolder holder, int position) {
        Maestro maestro = listaMaestros.get(position);

        String nombreCompleto = maestro.getNombre() + " " + maestro.getApellido();
        holder.txtNombreApellido.setText(nombreCompleto);
        holder.txtEmail.setText(maestro.getEmail());
        holder.txtTelefono.setText("Tel: " + maestro.getTelefono());
        holder.txtDPI.setText("DPI: " + maestro.getDpi());
        holder.txtEstado.setText("Estado: " + maestro.getEstado());

        boolean activo = maestro.getEstado().equalsIgnoreCase("activo");
        holder.switchEstado.setOnCheckedChangeListener(null);
        holder.switchEstado.setChecked(activo);
        holder.switchEstado.setText(activo ? "Activo" : "Inactivo");

        // Cambiar texto del switch y preparar lógica futura
        holder.switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String nuevoEstado = isChecked ? "activo" : "inactivo";
            if (!nuevoEstado.equalsIgnoreCase(maestro.getEstado())) {
                actualizarEstadoEnServidor(maestro.getIdUsuario(), nuevoEstado);
                maestro.setEstado(nuevoEstado); // actualizar estado local también
            }
            holder.switchEstado.setText(nuevoEstado.substring(0, 1).toUpperCase() + nuevoEstado.substring(1));

        });

        holder.btnEditar.setOnClickListener(v -> {
            Toast.makeText(context, "Editar maestro: " + nombreCompleto, Toast.LENGTH_SHORT).show();
            // Aquí más adelante se puede abrir una nueva Activity o un diálogo de edición
        });
    }

    @Override
    public int getItemCount() {
        return listaMaestros.size();
    }

    public static class MaestroViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreApellido, txtEmail, txtTelefono, txtDPI, txtEstado;
        Switch switchEstado;
        ImageButton btnEditar;

        public MaestroViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreApellido = itemView.findViewById(R.id.txtNombreApellido);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtTelefono = itemView.findViewById(R.id.txtTelefono);
            txtDPI = itemView.findViewById(R.id.txtDPI);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            switchEstado = itemView.findViewById(R.id.switchEstado);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }

    private void actualizarEstadoEnServidor(int idUsuario, String estado) {
        String url = "http://34.71.103.241/actualizar_estado_maestro.php";

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                com.android.volley.Request.Method.POST,
                url,
                response -> {
                    Log.d("ESTADO", "Actualización exitosa: " + response);
                    Toast.makeText(context, "Estado actualizado", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("ESTADO", "Error al actualizar estado", error);
                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new java.util.HashMap<>();
                params.put("id_usuario", String.valueOf(idUsuario));
                params.put("estado", estado);
                return params;
            }
        };

        com.android.volley.RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        queue.add(request);
    }


}
