package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public class AuthenticationManager {

        private FirebaseAuth mAuth;
        private FirebaseFirestore db;

        public AuthenticationManager() {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        }

        public void iniciarSesion(String correo, String contrasena, OnLoginListener listener) {
            mAuth.signInWithEmailAndPassword(correo, contrasena)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                listener.onSuccess();
                            } else {
                                listener.onEmailNotVerified();
                            }
                        } else {
                            listener.onFailure();
                        }
                    });
        }

        // Otros métodos para registro, restablecimiento de contraseña, etc.
    }

    // Interfaz para manejar eventos de inicio de sesión
    public interface OnLoginListener {
        void onSuccess();

        void onFailure();

        void onEmailNotVerified();
    }
}