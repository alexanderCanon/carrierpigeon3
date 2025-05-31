package com.principal.cp.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.principal.cp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerMensajes;
    private EditText edtMensaje;
    private ImageButton btnEnviar;

    private List<Mensaje> listaMensajes = new ArrayList<>();
    private MensajeAdapter adapter;

    private DatabaseReference chatRef;
    private int idUsuarioActual;
    private int idDestino; // El otro usuario (padre o maestro)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        edtMensaje = findViewById(R.id.edtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        // Obtener IDs desde intent o SharedPreferences
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("id_usuario", -1);
        idDestino = getIntent().getIntExtra("id_destino", -1); // ID del padre o maestro

        // Crear nombre único del chat (ordenado menor_mayor)
        String chatId = crearChatId(idUsuarioActual, idDestino);

        // Inicializar Firebase Realtime Database
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        chatRef = db.getReference("chats").child(chatId);

        // Configurar RecyclerView
        adapter = new MensajeAdapter(listaMensajes, idUsuarioActual);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(adapter);

        escucharMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensaje());
    } // Fin onCreate

    private String crearChatId(int id1, int id2) {
        return (Math.min(id1, id2)) + "_" + (Math.max(id1, id2));
    }

    private void escucharMensajes() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String prevChildKey) {
                Mensaje msg = snapshot.getValue(Mensaje.class);
                if (msg != null) {
                    listaMensajes.add(msg);
                    adapter.notifyItemInserted(listaMensajes.size() - 1);
                    recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String s) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    } // Fin escucharMensajes

    private void enviarMensaje() {
        String texto = edtMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        Mensaje nuevo = new Mensaje(idUsuarioActual, texto, System.currentTimeMillis());
        chatRef.push().setValue(nuevo);

        edtMensaje.setText("");
    }



}