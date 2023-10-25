package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Perfil extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView usernameTextView;
    private Button followButton;
    private TextView descriptionTextView;
    private Button editProfileButton; // Agregamos un botón para editar el perfil

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_main);

        // Inicializa las vistas
        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        followButton = findViewById(R.id.followButton);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        editProfileButton = findViewById(R.id.editar); // Inicializamos el botón de edición de perfil

        // Configura el botón de seguir/dejar de seguir
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementa aquí la lógica para seguir o dejar de seguir al usuario
                if (followButton.getText().toString().equals("Seguir")) {
                    // Acción de seguir
                    followButton.setText("Dejar de Seguir");
                } else {
                    // Acción de dejar de seguir
                    followButton.setText("Seguir");
                }
            }
        });

        // Configura el botón para editar el perfil
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad "EditarPerfil"
                Intent editProfileIntent = new Intent(Perfil.this, EditarPerfil.class);
                startActivity(editProfileIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        // Configura la navegación desde la actividad de perfil
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    Intent homeIntent = new Intent(Perfil.this, Home.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_chat) {
                    // Abre la actividad Chat
                    Intent chatIntent = new Intent(Perfil.this, Chat.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_profile) {
                    // No hagas nada, ya estás en la actividad Perfil
                    item.setChecked(true); // Marcar la opción de perfil como seleccionada
                }
                return true;
            }
        });

        // Marcar la opción de perfil como seleccionada al inicio
        bottomNavigationView.setSelectedItemId(R.id.action_profile);

        // Cargar y mostrar los datos del perfil desde Firebase Realtime Database
        cargarYMostrarDatosDelPerfil();
    }

    private void cargarYMostrarDatosDelPerfil() {
        // Recupera el ID del usuario actual
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Obtén una referencia a la ubicación de los datos del perfil en Firebase Realtime Database
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("Perfiles").child(userId);

        // Agrega un listener para escuchar cambios en los datos del perfil
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Recupera los datos del perfil desde el snapshot
                    String nombreUsuario = snapshot.child("nombre_usuario").getValue(String.class);
                    String descripcion = snapshot.child("descripcion").getValue(String.class);
                    String fotoPerfilUrl = snapshot.child("foto_perfil_url").getValue(String.class);

                    // Actualiza las vistas en la actividad de perfil con los datos recuperados
                    usernameTextView.setText(nombreUsuario);
                    descriptionTextView.setText(descripcion);

                    // Cargar la imagen de perfil utilizando Glide (asegúrate de agregar la dependencia en tu archivo build.gradle)
                    if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                        Glide.with(Perfil.this).load(fotoPerfilUrl).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar error en caso de que la lectura de datos falle
            }
        });
    }
}
