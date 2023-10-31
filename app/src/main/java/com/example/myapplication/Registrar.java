package com.example.myapplication;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class Registrar extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private EditText emailText;
    private EditText Nametext;
    private EditText passwordText;
    private EditText passwordconfirmText;
    private Button loginButton;
    private Button uploadProfileButton;
    private CircleImageView profileImage;
    private Uri selectedImageUri;
    private TextView inicia;

    private String email = "";
    private String name = "";
    private String password = "";
    private String passwordconfirm = "";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailText = findViewById(R.id.emailText);
        Nametext = findViewById(R.id.NameText);
        passwordText = findViewById(R.id.passwordText);
        passwordconfirmText = findViewById(R.id.passwordconfirmText);
        loginButton = findViewById(R.id.loginButton);
        uploadProfileButton = findViewById(R.id.uploadProfileButton);
        profileImage = findViewById(R.id.profileImage);
        inicia = findViewById(R.id.inicia);

        inicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Configura la intención para abrir la actividad de Inicio de Sesión
                Intent intent = new Intent(Registrar.this, MainActivity.class);
                startActivity(intent);
            }
        });

        uploadProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                name = Nametext.getText().toString();
                password = passwordText.getText().toString();
                passwordconfirm = passwordconfirmText.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordconfirm.isEmpty()) {
                    if (!email.endsWith("@inacapmail.cl")) {
                        Toast.makeText(Registrar.this, "El correo debe ser de dominio @inacapmail.cl", Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(passwordconfirm)) {
                        Toast.makeText(Registrar.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 6) {
                        Toast.makeText(Registrar.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    } else {
                        registerUser();
                    }
                } else {
                    Toast.makeText(Registrar.this, "Debe completar los campos requeridos", Toast.LENGTH_SHORT).show();
                }
            }
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
            profileImage.setImageURI(selectedImageUri); // Muestra la imagen seleccionada en la vista

            // Cargar la imagen seleccionada en el CircleImageView utilizando Glide
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(profileImage);
        }
    }

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registrar.this, "Se ha enviado un correo de verificación a " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Registrar.this, "No se pudo enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    uploadProfileImageToFirebaseStorage(user);
                } else {
                    Toast.makeText(Registrar.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadProfileImageToFirebaseStorage(final FirebaseUser user) {
        if (selectedImageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference profileImageRef = storageRef.child("profile_images/" + user.getUid() + ".jpg");
            UploadTask uploadTask = profileImageRef.putFile(selectedImageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("name", name);
                    userData.put("profileImageUrl", imageUrl);
                    String uid = user.getUid();
                    db.collection("Users").document(uid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                startActivity(new Intent(Registrar.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(Registrar.this, "Los datos no se registraron correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
            }).addOnFailureListener(exception -> {
                // Ocurrió un error al subir la foto de perfil a Firebase Storage
            });
        } else {
            String uid = user.getUid();
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", email);
            userData.put("name", name);
            db.collection("Users").document(uid).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if (task2.isSuccessful()) {
                        startActivity(new Intent(Registrar.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Registrar.this, "Los datos no se registraron correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}