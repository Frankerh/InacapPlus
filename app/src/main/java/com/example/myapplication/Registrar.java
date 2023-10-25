package com.example.myapplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Registrar extends AppCompatActivity {

    private EditText emailText;
    private EditText Nametext;
    private EditText passwordText;
    private EditText passwordconfirmText;
    private Button loginButton;

    // Variables de datos a registrar
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

    private void registerUser() {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = mAuth.getCurrentUser();
                    // Verifica si el usuario ha iniciado sesión con un correo electrónico y si el correo se ha verificado.
                    if (user != null && !user.isEmailVerified()) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Registrar.this, "Se ha enviado un correo de verificación a " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                            // Puedes redirigir al usuario a una pantalla de verificación o realizar otras acciones.
                                        } else {
                                            Toast.makeText(Registrar.this, "No se pudo enviar el correo de verificación", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("email", email);
                    userData.put("name", name);

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
                } else {
                    Toast.makeText(Registrar.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}