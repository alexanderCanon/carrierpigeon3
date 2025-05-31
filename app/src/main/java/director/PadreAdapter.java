package director;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.principal.cp.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PadreAdapter extends RecyclerView.Adapter<PadreAdapter.PadreViewHolder> {

    private List<Padre> listaPadres;
    private Context context;

    public PadreAdapter(Context context, List<Padre> listaPadres) {
        this.context = context;
        this.listaPadres = listaPadres;
    }

    @Override
    public PadreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_padre, parent, false);
        return new PadreViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(PadreViewHolder holder, int position) {
        Padre padre = listaPadres.get(position);

        holder.txtNombrePadre.setText(padre.getNombre() + " " + padre.getApellido());
        holder.txtEmailTelefono.setText("📧 " + padre.getEmail() + "  |  📞 " + padre.getTelefono());
        holder.txtDpi.setText("DPI: " + padre.getDpi());

        // Configurar estado del switch
        boolean estaActivo = padre.getEstado().equalsIgnoreCase("activo");

        // Primero quitamos cualquier listener viejo
        holder.switchEstado.setOnCheckedChangeListener(null);
        // Luego actualizamos visualmente sin disparar el evento
        holder.switchEstado.setChecked(estaActivo);

        holder.switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String nuevoEstado = isChecked ? "activo" : "inactivo";
            if (!nuevoEstado.equalsIgnoreCase(padre.getEstado())) {
                actualizarEstadoEnServidor(padre.getDpi(), nuevoEstado);
                padre.setEstado(nuevoEstado); // actualizar estado local también
            }
        });

        holder.btnEditarPadre.setOnClickListener(v -> {
            mostrarDialogoEditar(padre, position);
        });
    }

    @Override
    public int getItemCount() {
        return listaPadres.size();
    }

    public static class PadreViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombrePadre, txtEmailTelefono, txtDpi;
        SwitchCompat switchEstado;
        Button btnEditarPadre;

        public PadreViewHolder(View itemView) {
            super(itemView);
            txtNombrePadre = itemView.findViewById(R.id.txtNombrePadre);
            txtEmailTelefono = itemView.findViewById(R.id.txtEmailTelefono);
            txtDpi = itemView.findViewById(R.id.txtDpi);
            switchEstado = itemView.findViewById(R.id.switchEstado);
            btnEditarPadre = itemView.findViewById(R.id.btnEditarPadre);
        }
    }
    private void actualizarEstadoEnServidor(String dpi, String nuevoEstado) {
        String url = "http://34.71.103.241/actualizar_estado_padre.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "⚠️ " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "⚠️ Error procesando la respuesta del servidor", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(context, "❌ Error de red al cambiar estado", Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("dpi", dpi);
                params.put("estado", nuevoEstado);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    private void mostrarDialogoEditar(Padre padre, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // ahora sí es una Activity


        //AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Editar padre");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_padre, null);
        builder.setView(dialogView);

        EditText edtNombre = dialogView.findViewById(R.id.edtEditarNombre);
        EditText edtApellido = dialogView.findViewById(R.id.edtEditarApellido);
        EditText edtEmail = dialogView.findViewById(R.id.edtEditarEmail);
        EditText edtTelefono = dialogView.findViewById(R.id.edtEditarTelefono);
        EditText edtDpi = dialogView.findViewById(R.id.edtEditarDPI);

        edtNombre.setText(padre.getNombre());
        edtApellido.setText(padre.getApellido());
        edtEmail.setText(padre.getEmail());
        edtTelefono.setText(padre.getTelefono());
        edtDpi.setText(padre.getDpi());


        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = edtNombre.getText().toString();
            String nuevoApellido = edtApellido.getText().toString();
            String nuevoEmail = edtEmail.getText().toString();
            String nuevoTelefono = edtTelefono.getText().toString();
            String nuevoDpi = edtDpi.getText().toString().trim();


            actualizarDatosEnServidor(padre.getDpi(), nuevoNombre, nuevoApellido, nuevoEmail, nuevoTelefono, nuevoDpi, position);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void actualizarDatosEnServidor(String dpi, String nombre, String apellido, String email, String telefono, String nuevoDpi, int position) {
        String url = "http://34.71.103.241/editar_padre.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(context, "Padre actualizado", Toast.LENGTH_SHORT).show();

                            // Actualizar localmente
                            Padre padre = listaPadres.get(position);
                            padre.setNombre(nombre);
                            padre.setApellido(apellido);
                            padre.setEmail(email);
                            padre.setTelefono(telefono);
                            padre.setDpi(nuevoDpi);
                            notifyItemChanged(position);
                        } else {
                            Toast.makeText(context, "Error: " + obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("dpi", dpi);
                parametros.put("nombre", nombre);
                parametros.put("apellido", apellido);
                parametros.put("email", email);
                parametros.put("telefono", telefono);
                parametros.put("nuevo_dpi", nuevoDpi); // para actualizar el campo en la BD

                return parametros;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

}
