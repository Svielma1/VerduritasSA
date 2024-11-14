package sebastian.vielma.verduritassa;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AgregarCultivoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    private EditText fechaPlante;
    private int year, month, day;

    Spinner etCultivo;
    EditText etFechaPlante;
    Button registrar;
    HashMap<String, Integer> diasCosecha;
    ImageView backButton;
    EditText alias;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_cultivo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fechaPlante = findViewById(R.id.fechaPlante);

        fechaPlante.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarCultivoActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        fechaPlante.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }, year, month, day);
            datePickerDialog.show();

        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        etCultivo = findViewById(R.id.cultivo);
        etFechaPlante = findViewById(R.id.fechaPlante);
        registrar = findViewById(R.id.registrar);
        backButton = findViewById(R.id.backButton);
        alias = findViewById(R.id.alias);

        diasCosecha = new HashMap<>();
        diasCosecha.put("Tomates", 80);
        diasCosecha.put("Cebollas", 120);
        diasCosecha.put("Lechugas", 85);
        diasCosecha.put("Apio", 150);
        diasCosecha.put("Choclo", 90);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cultivo = etCultivo.getSelectedItem().toString();
                String fechaPlantacion = etFechaPlante.getText().toString();

                if((cultivo.equals("Selecciona un cultivo...")) || (fechaPlantacion.isEmpty())) {
                    Toast.makeText(AgregarCultivoActivity.this, "Campos incompletos", Toast.LENGTH_SHORT).show();
                } else {
                    DateTimeFormatter formatear = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        formatear = DateTimeFormatter.ofPattern("d/M/yyyy");
                    }
                    LocalDate fechaPlante = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        fechaPlante = LocalDate.parse(fechaPlantacion, formatear);
                    }

                    // Calcula la fecha de cosecha
                    int diasParaCosecha = diasCosecha.get(cultivo);
                    LocalDate fechaCosecha = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        fechaCosecha = fechaPlante.plus(diasParaCosecha, ChronoUnit.DAYS);
                    }

                    String fechaCosechaStr = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        fechaCosechaStr = fechaCosecha.format(formatear);
                    }

                    //Aca añado a la base de datos
                    registrarCultivo(fechaCosechaStr);

                    etCultivo.setSelection(0);
                    etFechaPlante.setText("");

                    Intent registrar = new Intent(AgregarCultivoActivity.this, ListaCultivosActivity.class);
                    Toast.makeText(AgregarCultivoActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(registrar);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listaCultivos = new Intent(AgregarCultivoActivity.this, ListaCultivosActivity.class);
                startActivity(listaCultivos);
            }
        });
    }

    //registrar un cultivo
    private void registrarCultivo(String fechaCosechaStr){
        String aliasStr = alias.getText().toString();
        String fechaSiembraStr = fechaPlante.getText().toString();
        String plantaStr = etCultivo.getSelectedItem().toString();
        String userEmail = currentUser.getEmail();

        // Crear un objeto para almacenar datos
        Map<String, Object> cultivo = new HashMap<>();
        cultivo.put("alias", aliasStr);
        cultivo.put("fechaSiembra", fechaSiembraStr);
        cultivo.put("fechaCosecha", fechaCosechaStr);
        cultivo.put("planta", plantaStr);
        cultivo.put("userEmail", userEmail);

        // Guardar los datos en Firestore
        db.collection("cultivos")
                .add(cultivo)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Cultivo guardado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Error al guardar cultivo", Toast.LENGTH_SHORT).show();
                });
    }

}
