package sebastian.vielma.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaCultivosActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private TextView bienvenida;
    private ImageView logoutButton, agregarCultivoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_cultivos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener una instancia de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        ListView lista = findViewById(R.id.lista);
        bienvenida = findViewById(R.id.bienvenida);
        logoutButton = findViewById(R.id.backButton);
        agregarCultivoBtn = findViewById(R.id.agregarCultivoBtn);

        // Cerrar sesion
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent login = new Intent(ListaCultivosActivity.this, MainActivity.class);
                startActivity(login);
                Toast.makeText(ListaCultivosActivity.this, "Sesion cerrada correctamente", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        // Agregar un cultivo
        agregarCultivoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent agregarCultivo = new Intent(ListaCultivosActivity.this, AgregarCultivoActivity.class);
                startActivity(agregarCultivo);
            }
        });

        // Consultar la colección "cultivos"
        String userEmail = currentUser.getEmail();
        System.out.println(userEmail);
        db.collection("cultivos").whereEqualTo("userEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cultivo> listaCultivos = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idDocumento = document.getId();
                            String alias = document.getString("alias");
                            String fechaCosecha = document.getString("fechaCosecha");


                            // Agregar cultivo a la lista para mostrar
                            Cultivo cultivo = new Cultivo(idDocumento, alias, fechaCosecha);
                            listaCultivos.add(cultivo);
                        }

                        CustomAdapter pintarLista = new CustomAdapter(ListaCultivosActivity.this, listaCultivos);
                        lista.setAdapter(pintarLista);

                    } else {
                        Toast.makeText(getApplicationContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
                    }
                });

        // Mensaje de bienvenida al usuario
        db.collection("usuarios").whereEqualTo("email", userEmail)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nombreStr = document.getString("nombre");
                        bienvenida.setText("Bienvenido " + nombreStr);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error al leer los datos", Toast.LENGTH_SHORT).show();
                }
            });



    }

}