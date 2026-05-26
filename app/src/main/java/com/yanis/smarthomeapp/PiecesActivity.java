package com.yanis.smarthomeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;

public class PiecesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);

        // 1. Initialisation des cartes (CardViews) d'après ton XML
        CardView cardSalon = findViewById(R.id.card_salon);
        CardView cardHall = findViewById(R.id.card_hall); // C'était card_hall dans ton XML !
        CardView cardCuisine = findViewById(R.id.card_cuisine);

        // Redirection au clic sur les cartes des pièces
        cardSalon.setOnClickListener(v -> {
            startActivity(new Intent(PiecesActivity.this, SalonActivity.class));
        });

        cardHall.setOnClickListener(v -> {
            startActivity(new Intent(PiecesActivity.this, EntreeActivity.class));
        });

        cardCuisine.setOnClickListener(v -> {
            startActivity(new Intent(PiecesActivity.this, CuisineActivity.class));
        });


        // 2. Gestion de ta barre de navigation personnalisée (LinearLayouts)
        LinearLayout btnNavHome = findViewById(R.id.btn_nav_home);
        LinearLayout btnNavRooms = findViewById(R.id.btn_nav_rooms);
        LinearLayout btnNavSettings = findViewById(R.id.btn_nav_settings);

        // Redirection au clic sur le bouton Accueil
        btnNavHome.setOnClickListener(v -> {
            startActivity(new Intent(PiecesActivity.this, MainActivity.class));
            overridePendingTransition(0, 0); // Évite le clignotement de l'écran
            finish(); // Ferme cette page pour libérer la mémoire
        });

        // Redirection au clic sur le bouton Settings
        btnNavSettings.setOnClickListener(v -> {
            startActivity(new Intent(PiecesActivity.this, SettingsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        // Sur le bouton Pièces, on ne fait rien car on y est déjà
        btnNavRooms.setOnClickListener(v -> {
            // Déjà sur cette page
        });
    }
}