package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText correoEditText;
    private EditText contrasenaEditText;
    private Button iniciarSesionButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        correoEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.passwordEditText);
        iniciarSesionButton = findViewById(R.id.loginButton);

        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // El usuario está autenticado, actualiza el estado a "conectado"
            actualizarEstado("conectado");
        }
    }

    private void iniciarSesion() {
        final String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();

        // Realiza la autenticación
        mAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // Inicio de sesión exitoso, redirige al Activity Home
                                actualizarEstado("conectado");
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                                finish(); // Cierra la actividad actual
                            } else {
                                Toast.makeText(MainActivity.this, "Debe verificar su correo electrónico para iniciar sesión.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Inicio de sesión fallido. Por favor, verifique sus credenciales.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void actualizarEstado(String estado) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Actualiza el estado en Firestore
            db.collection("Users").document(userId)
                    .update("estado", estado)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}