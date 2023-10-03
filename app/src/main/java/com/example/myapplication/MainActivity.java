package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText correoEditText;
    private EditText contrasenaEditText;
    private Button iniciarSesionButton;
    private TextView registrarseTextView;
    private ImageView logoImageView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        correoEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.passwordEditText);
        iniciarSesionButton = findViewById(R.id.loginButton);
        registrarseTextView = findViewById(R.id.registrate);
        logoImageView = findViewById(R.id.logoImageView);
        TextView olvidasteContrasenaTextView = findViewById(R.id.olvidasteContrasena);


        iniciarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });

        registrarseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registrar.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        });
        olvidasteContrasenaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // código para redirigir al usuario a la actividad de restablecimiento de contraseña
                Intent intent = new Intent(MainActivity.this, RestablecerCon.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void iniciarSesion() {
        final String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();

        if (!correo.endsWith("@inacapmail.cl")) {
            Toast.makeText(this, "Por favor, ingrese su correo institucional", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // El usuario ha iniciado sesión y su correo electrónico está verificado
                                Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                                // Redirige a la actividad Home
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                finish(); // Cierra la actividad actual para que el usuario no pueda volver atrás
                            } else {
                                // El correo electrónico no está verificado, muestra un mensaje de error
                                Toast.makeText(MainActivity.this, "Debe verificar su correo electrónico para iniciar sesión.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // El inicio de sesión falló, muestra un mensaje de error
                            Toast.makeText(MainActivity.this, "Inicio de sesión fallido. Por favor, verifique sus credenciales.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}