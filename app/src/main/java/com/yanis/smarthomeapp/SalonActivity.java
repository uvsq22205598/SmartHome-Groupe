package com.yanis.smarthomeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SalonActivity extends AppCompatActivity {

    private DatabaseReference mDatabase, cmdAllumer, cmdEteindre, cmdCouleur;
    private boolean isLightOn = false;

    private TextView txtStatus, txtIntensity, txtNoise;
    private Button btnToggle, btnRetour;
    private ImageView imgLedCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        mDatabase = FirebaseDatabase.getInstance().getReference()
                .child("maison").child("salon");

        cmdAllumer = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/salon/allumer_lumiere");
        cmdEteindre = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/salon/eteindre_lumiere");
        cmdCouleur = FirebaseDatabase.getInstance()
                .getReference("maison/commandes/salon/forcer_couleur");

        txtStatus = findViewById(R.id.txt_salon_light_status);
        txtIntensity = findViewById(R.id.txt_salon_intensity);
        txtNoise = findViewById(R.id.txt_salon_noise);
        imgLedCircle = findViewById(R.id.img_led_color_circle);
        btnToggle = findViewById(R.id.btn_toggle_salon_light);
        btnRetour = findViewById(R.id.btn_retour_pieces);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        isLightOn = snapshot.hasChild("lumiere") && Boolean.TRUE.equals(snapshot.child("lumiere").getValue(Boolean.class));

                        Object intensityObj = snapshot.child("intensite").getValue();
                        String noiseEvent = snapshot.child("dernier_evenement").getValue(String.class);
                        String colorName = snapshot.child("couleur").getValue(String.class);

                        txtStatus.setText(isLightOn ? "Allumée" : "Éteinte");
                        btnToggle.setText(isLightOn ? "ÉTEINDRE" : "ALLUMER");

                        if (intensityObj != null) {
                            txtIntensity.setText(intensityObj.toString() + " %");
                        }

                        if (noiseEvent != null) {
                            txtNoise.setText(noiseEvent);
                        }

                        if (colorName != null) {
                            switch (colorName) {
                                case "rouge": imgLedCircle.setColorFilter(Color.RED); break;
                                case "bleu": imgLedCircle.setColorFilter(Color.BLUE); break;
                                case "vert": imgLedCircle.setColorFilter(Color.GREEN); break;
                                case "jaune": imgLedCircle.setColorFilter(Color.YELLOW); break;
                                case "blanc": imgLedCircle.setColorFilter(Color.WHITE); break;
                                default: imgLedCircle.setColorFilter(Color.WHITE);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("SalonActivity", "Erreur : " + e.getMessage());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        btnToggle.setOnClickListener(v -> {
            if (isLightOn) {
                cmdEteindre.setValue(true);
                isLightOn = false;
                txtStatus.setText("Éteinte");
                btnToggle.setText("ALLUMER");
            } else {
                cmdAllumer.setValue(true);
                isLightOn = true;
                txtStatus.setText("Allumée");
                btnToggle.setText("ÉTEINDRE");
            }
        });

        btnRetour.setOnClickListener(v -> finish());

        findViewById(R.id.btn_color_blue).setOnClickListener(v -> cmdCouleur.setValue("bleu"));
        findViewById(R.id.btn_color_red).setOnClickListener(v -> cmdCouleur.setValue("rouge"));
        findViewById(R.id.btn_color_green).setOnClickListener(v -> cmdCouleur.setValue("vert"));
        findViewById(R.id.btn_color_yellow).setOnClickListener(v -> cmdCouleur.setValue("jaune"));
        findViewById(R.id.btn_color_white).setOnClickListener(v -> cmdCouleur.setValue("blanc"));
    }
}
