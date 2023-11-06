package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {
    private ImageView profileImageView;
    private TextView usernameTextView;
    private Button followButton;
    private TextView descriptionTextView;
    private Button editarButton;
    private Button mensajeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profiel); // Corregir el nombre del archivo XML

        // Vincular vistas con elementos del XML
        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        followButton = findViewById(R.id.followButton);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        mensajeButton = findViewById(R.id.mensaje);

        // Recuperar el ID del usuario seleccionado (puedes pasarlo como extra en el Intent)
        String userId = getIntent().getStringExtra("name");

        // Recuperar los datos del usuario desde Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Obtener los datos del usuario
                            String username = document.getString("name");
                            String description = document.getString("descripcion");
                            String profileImageUrl = document.getString("profileImageUrl");

                            // Mostrar los datos en la actividad de perfil
                            usernameTextView.setText(username);
                            descriptionTextView.setText(description);
                            // Cargar la imagen de perfil desde profileImageUrl (usando Glide, Picasso o Firebase Storage)

                            // Manejar el botón de seguir/dejar de seguir
                            followButton.setOnClickListener(view -> {
                                // Agrega lógica para seguir o dejar de seguir al usuario aquí
                            });

                            // Manejar el botón de editar perfil
                            editarButton.setOnClickListener(view -> {
                                // Agrega lógica para editar el perfil aquí
                            });

                            // Manejar el botón de mensajes
                            mensajeButton.setOnClickListener(view -> {
                                // Agrega lógica para iniciar una conversación con el usuario aquí
                            });
                        }
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    Intent homeIntent = new Intent(UserProfileActivity.this, Home.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_chat) {
                    // Abre la actividad Chat
                    Intent chatIntent = new Intent(UserProfileActivity.this, Chat.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_profile) {
                    // Abre la actividad Perfil
                    Intent profileIntent = new Intent(UserProfileActivity.this, Perfil.class);
                    startActivity(profileIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_search) {
                    // Abre la actividad de búsqueda de usuarios
                    Intent searchIntent = new Intent(UserProfileActivity.this, UserSearchActivity.class);
                    startActivity(searchIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                return true;
            }
        });

        // Marcar la opción de perfil como seleccionada al inicio
        bottomNavigationView.setSelectedItemId(R.id.action_profile);
    }
}

