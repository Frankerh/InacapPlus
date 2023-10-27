package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class Perfil extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView descriptionTextView;
    private Button editProfileButton;

    private FirebaseFirestore db;
    private DocumentReference profileRef;
    private ListenerRegistration profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_main);

        // Inicializa las vistas
        profileImageView = findViewById(R.id.profileImageView);
        usernameTextView = findViewById(R.id.usernameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        editProfileButton = findViewById(R.id.editar);

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

        // Inicializa Firebase Firestore y obtén una referencia al perfil del usuario actual
        db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRef = db.collection("Users").document(userId); // Cambio aquí a la colección "Users"

        // Cargar y mostrar los datos del perfil desde Firebase Firestore
        cargarYMostrarDatosDelPerfil();
    }

    private void cargarYMostrarDatosDelPerfil() {
        profileListener = profileRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    // Recupera los datos del perfil desde el documento
                    String nombreUsuario = snapshot.getString("name");
                    String descripcion = snapshot.getString("descripcion");
                    String fotoPerfilUrl = snapshot.getString("profileImageUrl");

                    // Actualiza las vistas en la actividad de perfil con los datos recuperados
                    usernameTextView.setText(nombreUsuario);
                    descriptionTextView.setText(descripcion);

                    // Cargar la imagen de perfil utilizando Glide (asegúrate de agregar la dependencia en tu archivo build.gradle)
                    if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                        Glide.with(Perfil.this).load(fotoPerfilUrl).into(profileImageView);
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detén la escucha de cambios en el perfil al salir de la actividad
        if (profileListener != null) {
            profileListener.remove();
        }
    }
}
