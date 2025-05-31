package com.principal.cp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder>{
private List<AnuncioPrueba> anuncios;

public AnuncioAdapter(List<AnuncioPrueba> anuncios) {
    this.anuncios = anuncios;
}
@NonNull
@Override
public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(android.R.layout.simple_list_item_2, parent, false); // Usamos un layout simple por ahora
    return new AnuncioViewHolder(itemView);
}

@Override
public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
    AnuncioPrueba anuncio = anuncios.get(position);
    holder.tituloTextView.setText(anuncio.getTitulo());
    holder.contenidoTextView.setText(anuncio.getContenido());
}

@Override
public int getItemCount() {
    return anuncios.size();
}

public static class AnuncioViewHolder extends RecyclerView.ViewHolder {
    public TextView tituloTextView;
    public TextView contenidoTextView;

    public AnuncioViewHolder(@NonNull View itemView) {
        super(itemView);
        tituloTextView = itemView.findViewById(android.R.id.text1);
        contenidoTextView = itemView.findViewById(android.R.id.text2);
    }
}
}
