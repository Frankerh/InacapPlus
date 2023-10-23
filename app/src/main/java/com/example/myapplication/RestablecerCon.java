package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RestablecerCon extends AppCompatActivity {

    private EditText correoEditText;
    private Button restablecerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restablecer_main);

        mAuth = FirebaseAuth.getInstance();

        correoEditText = findViewById(R.id.emailEditText);
        restablecerButton = findViewById(R.id.restablecerButton);
        restablecerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restablecerContrasena();
            }
        });
    }

    private void restablecerContrasena() {
        String correo = correoEditText.getText().toString().trim();

        if (correo.isEmpty()) {
            Toast.makeText(this, "Ingrese su dirección de correo electrónico", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RestablecerCon.this, "Se ha enviado un correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RestablecerCon.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RestablecerCon.this, "Error al enviar el correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
