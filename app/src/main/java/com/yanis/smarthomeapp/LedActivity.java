package com.yanis.smarthomeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LedActivity extends AppCompatActivity {

    private DatabaseReference dbCuisine, dbSalon, dbChambre;
    private String stateCuisine, stateSalon, stateChambre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        // Bouton Retour
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Références Firebase
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbCuisine = db.getReference("maison").child("led_cuisine");
        dbSalon = db.getReference("maison").child("led_salon");
        dbChambre = db.getReference("maison").child("led_chambre");

        // Gestion LED CUISINE
        setupLedControl(dbCuisine, findViewById(R.id.btn_led_cuisine), findViewById(R.id.img_cuisine), findViewById(R.id.status_cuisine));

        // Gestion LED SALON
        setupLedControl(dbSalon, findViewById(R.id.btn_led_salon), findViewById(R.id.img_salon), findViewById(R.id.status_salon));

        // Gestion LED CHAMBRE
        setupLedControl(dbChambre, findViewById(R.id.btn_led_chambre), findViewById(R.id.img_chambre), findViewById(R.id.status_chambre));
    }

    private void setupLedControl(DatabaseReference ref, CardView card, ImageView img, TextView status) {
        // Lire l'état
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String val = snapshot.exists() ? snapshot.getValue().toString() : "OFF";
                status.setText(val);
                if (val.equals("ON")) {
                    img.setColorFilter(Color.parseColor("#2979FF")); // Bleu si allumé
                    status.setTextColor(Color.parseColor("#2979FF"));
                } else {
                    img.setColorFilter(Color.parseColor("#88FFFFFF")); // Gris si éteint
                    status.setTextColor(Color.parseColor("#88FFFFFF"));
                }
                card.setTag(val); // Stocker l'état actuel dans le tag
            }
            @Override public void onCancelled(DatabaseError error) {}
        });

        // Changer l'état au clic
        card.setOnClickListener(v -> {
            String currentState = v.getTag() != null ? v.getTag().toString() : "OFF";
            ref.setValue(currentState.equals("ON") ? "OFF" : "ON");
        });
    }
}