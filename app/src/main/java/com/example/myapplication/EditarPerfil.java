package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfil extends AppCompatActivity {

    private ImageView profileImageView;
    private Button changeProfileImageButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText descriptionEditText;
    private Button saveProfileButton;
    private Uri selectedImageUri;

    // Declaración de la variable para manejar la selección de imágenes
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        // Ahora puedes manejar la Uri de la imagen seleccionada
                        if (selectedImageUri != null) {
                            profileImageView.setImageURI(selectedImageUri);
                            // Carga la nueva imagen de perfil a Firebase Storage y actualiza la URL en Firebase Realtime Database
                            uploadImageToFirebaseStorage(selectedImageUri);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarperf_main);

        // Inicializa las vistas
        profileImageView = findViewById(R.id.profileImageView);
        changeProfileImageButton = findViewById(R.id.changeProfileImageButton);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // Configura el botón para cambiar la foto de perfil
        changeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la galería de fotos para seleccionar una nueva imagen de perfil
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        // Configura el botón para guardar el perfil del usuario
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtén los datos ingresados por el usuario
                String nombre = firstNameEditText.getText().toString();
                String apellido = lastNameEditText.getText().toString();
                String nombre_usuario = usernameEditText.getText().toString();
                String descripcion = descriptionEditText.getText().toString();

                // Guarda estos datos en Firebase Realtime Database junto con la URL de la imagen de perfil
                saveUserProfileData(nombre, apellido, nombre_usuario, descripcion);
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
                    Intent homeIntent = new Intent(EditarPerfil.this, Home.class);
                    startActivity(homeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_chat) {
                    // Abre la actividad Chat
                    Intent chatIntent = new Intent(EditarPerfil.this, Chat.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else if (itemId == R.id.action_profile) {
                    Intent chatIntent = new Intent(EditarPerfil.this, Perfil.class);
                    startActivity(chatIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    item.setChecked(true); // Marcar la opción de perfil como seleccionada
                }
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Selección de imagen exitosa
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);
            uploadImageToFirebaseStorage(selectedImageUri);
        } else if (requestCode == 2 && resultCode == RESULT_OK) { // Usamos el nuevo código de solicitud
            // Selección de una nueva imagen exitosa
            selectedImageUri = data.getData();
            profileImageView.setImageURI(selectedImageUri);

            // Carga la nueva imagen de perfil a Firebase Storage y actualiza la URL en Firebase Realtime Database o Firestore
            uploadImageToFirebaseStorage(selectedImageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("profile_images")
                .child(userId)
                .child("profile_image.jpg"); // Nombre y extensión del archivo

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profileImageUrl = uri.toString();
                        saveProfileImageUrlToDatabase(userId, profileImageUrl);
                    });
                })
                .addOnFailureListener(e -> {
                    // Manejar error si la carga falla
                });
    }

    private void saveProfileImageUrlToDatabase(String userId, String profileImageUrl) {
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("Perfiles").child(userId);
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("foto_perfil_url", profileImageUrl);
        profileRef.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    // URL de la imagen de perfil guardada exitosamente en Firebase Realtime Database
                })
                .addOnFailureListener(e -> {
                    // Manejar error si la guardia de la URL de la imagen falla
                });
    }

    private void saveUserProfileData(String nombre, String apellido, String nombre_usuario, String descripcion) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("Perfiles").child(userId);

        // Crear un mapa con los datos del perfil del usuario
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("apellido", apellido);
        userData.put("nombre_usuario", nombre_usuario);
        userData.put("descripcion", descripcion);
        // Agrega más campos de datos según tus necesidades, como "descripción", "foto_perfil_url", etc.

        // Guarda los datos en Firebase Realtime Database
        profileRef.updateChildren(userData)
                .addOnSuccessListener(aVoid -> {
                    // Datos del perfil actualizados exitosamente en Firebase Realtime Database
                    mostrarMensaje("Perfil guardado exitosamente");
                })
                .addOnFailureListener(e -> {
                    // Manejar error si la actualización falla
                    mostrarMensaje("Error al guardar el perfil");
                });
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }

}