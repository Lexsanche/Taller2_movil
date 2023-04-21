package com.example.taller2_movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taller2_movil.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);


    }

    public void camara(View view){
        Intent intentCamera = new Intent(this,Camara.class);
        startActivity(intentCamera);
    }

    public void Mapa(View view){
        Intent intentMapa = new Intent(this, MapasActivity.class);
        startActivity(intentMapa);
    }
}