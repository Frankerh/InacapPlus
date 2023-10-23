package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Encuentra la vista del logotipo
        ImageView splashLogo = findViewById(R.id.splashLogo);

        // Carga la animación desde el archivo XML
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);

        // Aplica la animación al logotipo
        splashLogo.startAnimation(animation);

        // Inicia una nueva actividad después de que termine la animación
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                // Puedes iniciar la actividad principal o la que desees aquí
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationStart(Animation animation) {
                // No es necesario implementar esta parte
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // No es necesario implementar esta parte
            }
        });
    }
}
