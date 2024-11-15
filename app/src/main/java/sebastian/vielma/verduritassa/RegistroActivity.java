package sebastian.vielma.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    EditText email, nombre, pais, genero, password;
    Button registerButton;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        email = findViewById(R.id.email);
        nombre = findViewById(R.id.nombre);
        pais = findViewById(R.id.pais);
        genero = findViewById(R.id.genero);
        password = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString();
                String contrasenaStr = password.getText().toString();

                //registra al usuario
                registrarse(emailStr, contrasenaStr);

            }
        });

    }

    private void registrarse(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String nombreStr = nombre.getText().toString();
                String paisStr = pais.getText().toString();
                String generoStr = genero.getText().toString();

                if (task.isSuccessful()) {
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("nombre", nombreStr);
                    usuario.put("email", email);
                    usuario.put("pais", paisStr);
                    usuario.put("genero", generoStr);

                    // Guardar los datos en Firestore
                    db.collection("usuarios")
                            .add(usuario)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getApplicationContext(), "Usuario guardado con éxito", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                            });

                    Intent listaCultivos = new Intent(RegistroActivity.this, ListaCultivosActivity.class);
                    startActivity(listaCultivos);
                } else {
                    Toast.makeText(RegistroActivity.this, "Authentication failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}