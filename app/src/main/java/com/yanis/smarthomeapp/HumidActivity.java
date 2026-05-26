package com.yanis.smarthomeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HumidActivity extends AppCompatActivity {

    private TextView txtHumid, txtAlerte, txtEvent;
    private DatabaseReference cuisineRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humid);

        txtHumid = findViewById(R.id.text_humid_valeur);
        txtAlerte = findViewById(R.id.text_humid_alerte);
        txtEvent = findViewById(R.id.text_humid_evenement);
        ImageButton btnBack = findViewById(R.id.btn_back);

        cuisineRef = FirebaseDatabase.getInstance().getReference("maison/cuisine");

        cuisineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Double humidite = snapshot.child("humidite").getValue(Double.class);
                Boolean alerte = snapshot.child("alerte").getValue(Boolean.class);
                String evenement = snapshot.child("dernier_evenement").getValue(String.class);

                if (humidite != null) {
                    txtHumid.setText(String.format("%.1f %%", humidite));
                } else {
                    txtHumid.setText("-- %");
                }

                if (alerte != null && alerte) {
                    txtAlerte.setText("⚠ ALERTE humidité");
                    txtAlerte.setTextColor(getColor(R.color.soft_red));
                    txtAlerte.setVisibility(View.VISIBLE);
                } else {
                    txtAlerte.setText("● Normal");
                    txtAlerte.setTextColor(getColor(R.color.accent_blue));
                    txtAlerte.setVisibility(View.VISIBLE);
                }

                if (evenement != null && !evenement.equals("aucun")) {
                    txtEvent.setText("Dernier : " + evenement);
                    txtEvent.setVisibility(View.VISIBLE);
                } else {
                    txtEvent.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        btnBack.setOnClickListener(v -> finish());
    }
}
