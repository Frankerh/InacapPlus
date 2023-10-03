package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Registrar extends AppCompatActivity {

    private EditText correoEditText;
    private EditText contrasenaEditText;
    private EditText confirmContrasenaEditText;
    private TextView loginTextView;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar_main);
        mAuth = FirebaseAuth.getInstance();
        loginTextView= findViewById(R.id.inicia);
        correoEditText = findViewById(R.id.emailEditText);
        contrasenaEditText = findViewById(R.id.passwordEditText);
        confirmContrasenaEditText = findViewById(R.id.passwordconfirm);
        Button registrarButton = findViewById(R.id.loginButton);

        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registrar.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registrarUsuario() {
        final String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();
        String confirmContrasena = confirmContrasenaEditText.getText().toString().trim();

        if (!correo.endsWith("@inacapmail.cl")) {
            Toast.makeText(this, "Por favor, ingrese su correo institucional", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contrasena.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!contrasena.equals(confirmContrasena)) {
            Toast.makeText(this, "Las contraseñas no coinciden. Por favor, verifique.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && !user.isEmailVerified()) {
                                // El correo electrónico no se ha verificado todavía, enviemos un correo de verificación
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Envío de correo de verificación exitoso
                                                    Toast.makeText(Registrar.this, "Se ha enviado un correo de verificación a su dirección de correo.", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(Registrar.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                } else {
                                                    // Error al enviar el correo de verificación
                                                    Toast.makeText(Registrar.this, "Ha ocurrido un error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                // El correo ya está verificado o el usuario es nulo (esto no debería suceder)
                                // Agrega aquí el código para redirigir al usuario a la pantalla de inicio de sesión
                            }
                        } else {
                            // El registro falló, muestra un mensaje de error
                            Toast.makeText(Registrar.this, "El registro ha fallado. Por favor, inténtelo nuevamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

