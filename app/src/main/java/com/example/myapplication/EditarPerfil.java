package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditarPerfil extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private EditText usernameEditText;
    private EditText descriptionEditText;
    private Button saveProfileButton;
    private ImageView profileImageView;
    private Button changeProfileImageButton;

    private FirebaseFirestore db;
    private DocumentReference profileRef;
    private StorageReference storageReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarperf_main);

        // Inicializa las vistas
        usernameEditText = findViewById(R.id.usernameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        profileImageView = findViewById(R.id.profileImageView);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);

        // Inicializa Firebase Firestore y obtén una referencia al perfil del usuario actual
        db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        profileRef = db.collection("Users").document(userId);

        // Inicializa Firebase Storage y obtén una referencia para guardar la imagen de perfil
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId + ".jpg");

        // Cargar y mostrar los datos actuales del perfil y la foto de perfil
        cargarYMostrarDatosActualesDelPerfil();
        cargarYMostrarFotoDePerfil();

        // Configura el botón para guardar el perfil
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarPerfil();
            }
        });

        // Configura el botón para cambiar la foto de perfil
        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Configura la navegación desde la actividad de edición de perfil
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    // Abre la actividad Home
                    // Agrega la lógica para abrir la actividad Home
                } else if (itemId == R.id.action_chat) {
                    // Abre la actividad Chat
                    // Agrega la lógica para abrir la actividad Chat
                } else if (itemId == R.id.action_profile) {
                    // Abre la actividad Perfil
                    // Agrega la lógica para abrir la actividad Perfil
                }
                return true;
            }
        });
    }

    // Cargar y mostrar los datos actuales del perfil
    private void cargarYMostrarDatosActualesDelPerfil() {
        profileRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombreActual = documentSnapshot.getString("name");
                String descripcionActual = documentSnapshot.getString("descripcion");

                usernameEditText.setText(nombreActual);
                descriptionEditText.setText(descripcionActual);
            }
        });
    }

    // Cargar y mostrar la foto de perfil actual
    private void cargarYMostrarFotoDePerfil() {
        profileRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fotoPerfilUrl = documentSnapshot.getString("profileImageUrl");

                if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                    // Utiliza Glide para cargar y mostrar la imagen de perfil en el ImageView
                    Glide.with(this).load(fotoPerfilUrl).into(profileImageView);
                }
            }
        });
    }

    private void guardarPerfil() {
        String nuevoNombre = usernameEditText.getText().toString();
        String nuevaDescripcion = descriptionEditText.getText().toString();

        // Actualiza los datos del perfil en Firebase Firestore
        profileRef.update("name", nuevoNombre, "descripcion", nuevaDescripcion)
                .addOnSuccessListener(aVoid -> {
                    // Los datos del perfil se actualizaron exitosamente
                    // Puedes mostrar un mensaje de éxito o navegar a otra actividad (perfil)
                    Intent perfilIntent = new Intent(EditarPerfil.this, Perfil.class);
                    startActivity(perfilIntent);
                })
                .addOnFailureListener(e -> {
                    // Maneja el error si la actualización falla
                });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri); // Muestra la nueva imagen seleccionada en el ImageView

            // Sube la nueva imagen de perfil a Firebase Storage
            if (selectedImageUri != null) {
                UploadTask uploadTask = storageReference.putFile(selectedImageUri);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // La imagen se cargó exitosamente en Firebase Storage
                    // Ahora, actualiza la referencia de la imagen de perfil en Firestore
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        profileRef.update("profileImageUrl", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    // La referencia de la imagen de perfil se actualizó exitosamente
                                    // Puedes mostrar un mensaje de éxito o navegar a otra actividad
                                })
                                .addOnFailureListener(e -> {
                                    // Maneja el error si la actualización falla
                                });
                    });
                }).addOnFailureListener(exception -> {
                    // Ocurrió un error al subir la nueva imagen de perfil a Firebase Storage
                    // Maneja el error aquí
                });
            }
        }
    }
}