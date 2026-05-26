package com.yanis.smarthomeapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CuisineActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseCuisine;
    private DatabaseReference mDatabaseCommandes; // Référence vers le noeud commandes
    private TextView txtTemp, txtHumidite, txtStatus, txtMsgAlerte;
    private CardView cardStatus;
    private ImageView imgAlerte, btnRetour;
    private Button btnAcquitter;

    private boolean alerteAcquittee = false;
    private double ancienneTemp = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);

        // --- CHEMINS FIREBASE COMPATIBLES AVEC TES CAPTURES ---
        mDatabaseCuisine = FirebaseDatabase.getInstance().getReference()
                .child("maison")
                .child("cuisine");

        mDatabaseCommandes = FirebaseDatabase.getInstance().getReference()
                .child("maison")
                .child("commandes")
                .child("cuisine");

        // --- LIAISON INTERFACE UTILISATEUR (UI) ---
        txtTemp = findViewById(R.id.txt_cuisine_temp);
        txtHumidite = findViewById(R.id.txt_cuisine_humidity);
        txtStatus = findViewById(R.id.txt_cuisine_status);
        txtMsgAlerte = findViewById(R.id.txt_cuisine_msg_alerte);
        cardStatus = findViewById(R.id.card_status_cuisine);
        imgAlerte = findViewById(R.id.img_cuisine_alerte);
        btnAcquitter = findViewById(R.id.btn_acquitter_alerte);
        btnRetour = findViewById(R.id.btn_cuisine_retour);

        // --- CLIC SUR LE BOUTON RETOUR ---
        btnRetour.setOnClickListener(v -> {
            finish();
        });

        // --- ÉCOUTEUR FIREBASE (TEMPS RÉEL) ---
        mDatabaseCuisine.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        // Récupération des données capteurs
                        Object tempObj = snapshot.child("temperature").getValue();
                        Object humObj = snapshot.child("humidite").getValue();

                        // Récupération dynamique du seuil configuré sur Firebase (Défaut : 30)
                        double seuilTemp = 30.0;
                        if (snapshot.hasChild("seuil_temperature")) {
                            Object seuilObj = snapshot.child("seuil_temperature").getValue();
                            if (seuilObj != null) {
                                seuilTemp = Double.parseDouble(seuilObj.toString());
                            }
                        }

                        // Vérification de l'état de l'alarme selon tes variables Firebase
                        Boolean alerteActive = snapshot.child("alerte_active").getValue(Boolean.class);
                        if (alerteActive == null) {
                            alerteActive = snapshot.child("alerte").getValue(Boolean.class);
                        }

                        double currentTemp = 0.0;
                        if (tempObj != null) {
                            currentTemp = Double.parseDouble(tempObj.toString());
                            txtTemp.setText(currentTemp + "°C");
                        }
                        if (humObj != null) {
                            txtHumidite.setText(humObj.toString() + "%");
                        }

                        // Sécurité : Si la température repasse sous le seuil, on réinitialise l'acquittement
                        if (currentTemp <= seuilTemp && ancienneTemp > seuilTemp) {
                            alerteAcquittee = false;
                            // On remet automatiquement reset_alerte à false sur Firebase pour le prochain événement
                            mDatabaseCommandes.child("reset_alerte").setValue(false);
                        }
                        ancienneTemp = currentTemp;

                        // --- LOGIQUE DE DÉTECTION DU DANGER VIA LE VRAI SEUIL ---
                        boolean isDanger = (currentTemp > seuilTemp) || (alerteActive != null && alerteActive);

                        if (isDanger && !alerteAcquittee) {
                            // Mode Alerte : Rouge
                            cardStatus.setCardBackgroundColor(Color.parseColor("#B00020"));
                            txtStatus.setText("ALERTE ACTIVÉE");
                            imgAlerte.setVisibility(View.VISIBLE);
                            imgAlerte.setImageResource(android.R.drawable.stat_notify_error);
                            btnAcquitter.setVisibility(View.VISIBLE);

                            if (currentTemp > seuilTemp) {
                                txtMsgAlerte.setText("Température critique (>" + seuilTemp + "°C) !");
                            } else {
                                txtMsgAlerte.setText("Danger détecté dans la cuisine !");
                            }
                        } else {
                            // Mode Normal ou Alerte éteinte de force localement
                            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
                            txtStatus.setText("SÉCURISÉ");
                            imgAlerte.setVisibility(View.GONE);

                            if (alerteAcquittee && currentTemp > seuilTemp) {
                                txtMsgAlerte.setText("Alarme coupée par l'utilisateur (Température : " + currentTemp + "°C)");
                                btnAcquitter.setVisibility(View.VISIBLE);
                            } else {
                                txtMsgAlerte.setText("Toutes les conditions sont normales");
                                btnAcquitter.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("CuisineActivity", "Erreur lors du traitement : " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // --- ACTION : ARRÊTER L'ALARME (CORRIGÉ POUR ÉCRIRE SUR LES BONS CLÉS) ---
        btnAcquitter.setOnClickListener(v -> {
            // 1. Activation de l'état acquitté local
            alerteAcquittee = true;

            // 2. Écriture de la commande de réinitialisation sous "commandes -> cuisine -> reset_alerte"
            mDatabaseCommandes.child("reset_alerte").setValue(true);

            // 3. Désactivation des variables d'alertes et buzzer sous "cuisine"
            mDatabaseCuisine.child("alerte").setValue(false);
            mDatabaseCuisine.child("alerte_active").setValue(false);
            mDatabaseCuisine.child("alerte_intrusion").setValue(false);
            mDatabaseCuisine.child("buzzer").setValue(false);

            // 4. Changement immédiat de l'interface graphique pour enlever le bloc rouge
            cardStatus.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
            txtStatus.setText("SÉCURISÉ");
            imgAlerte.setVisibility(View.GONE);
            txtMsgAlerte.setText("Alarme arrêtée. Signal de reset envoyé.");
            btnAcquitter.setVisibility(View.GONE);

            Toast.makeText(this, "Signal d'arrêt envoyé au système", Toast.LENGTH_SHORT).show();
        });
    }
}