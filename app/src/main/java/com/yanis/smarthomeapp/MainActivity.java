package com.yanis.smarthomeapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView welcomeTitle, avgTemp, avgHumid, alarmStatus;
    private DatabaseReference dbCuisine, dbAlarmes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        welcomeTitle = findViewById(R.id.text_main_welcome);
        avgTemp = findViewById(R.id.main_avg_temp);
        avgHumid = findViewById(R.id.main_avg_humid);
        alarmStatus = findViewById(R.id.main_alarm_status);

        if (user != null) {
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                welcomeTitle.setText("Bienvenue chez vous, " + name + " !");
            } else {
                welcomeTitle.setText("Bienvenue chez vous !");
            }
        }

        // Firebase en temps reel
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbCuisine = db.getReference("maison/cuisine");
        dbAlarmes = db.getReference("maison/alarmes");

        dbCuisine.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object tempObj = snapshot.child("temperature").getValue();
                    Object humObj = snapshot.child("humidite").getValue();
                    if (tempObj != null) avgTemp.setText(tempObj.toString().replace(".", ",") + "°C");
                    if (humObj != null) avgHumid.setText(humObj.toString().replace(".", ",") + "%");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        dbAlarmes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean alarme = snapshot.child("alarme_generale").getValue(Boolean.class) != null
                        && snapshot.child("alarme_generale").getValue(Boolean.class);
                if (alarme) {
                    alarmStatus.setText("ACTIVE");
                    alarmStatus.setTextColor(0xFFF44336);
                } else {
                    alarmStatus.setText("Inactive");
                    alarmStatus.setTextColor(0xFF4CAF50);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        CardView cardTemp = findViewById(R.id.card_temp);
        CardView cardHumid = findViewById(R.id.card_humid);
        CardView cardSecurity = findViewById(R.id.card_security);

        cardTemp.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TempActivity.class)));
        cardHumid.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HumidActivity.class)));
        cardSecurity.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SecurityActivity.class)));

        LinearLayout btnHome = findViewById(R.id.btn_nav_home);
        LinearLayout btnRooms = findViewById(R.id.btn_nav_rooms);
        LinearLayout btnSettings = findViewById(R.id.btn_nav_settings);

        btnRooms.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PiecesActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            welcomeTitle.setText("Bienvenue chez vous, " + user.getDisplayName() + " !");
        }
    }
}
