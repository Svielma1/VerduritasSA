package sebastian.vielma.verduritassa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;

    private EditText email, password;
    private Button loginButton;
    private Button googleSignInButton;
    private TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar los componentes
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.botonGoogleLogin);
        registerTextView = findViewById(R.id.registerTextView);
        mAuth = FirebaseAuth.getInstance();

        // Inicio de sesión
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = email.getText().toString();
                String contrasena = password.getText().toString();

                if(!validarDatos()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Llamar a firebase
                    loguearse(usuario, contrasena);
                }
            }
        });

        // Inicio de sesion con google
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show();
                // Aquí puedes implementar la autenticación con Google
            }
        });

        // Registro
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registro = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(registro);
            }
        });
    }

    private void loguearse(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Intent listaCultivos = new Intent(MainActivity.this, ListaCultivosActivity.class);
                    listaCultivos.putExtra("email", email);
                    startActivity(listaCultivos);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validarDatos(){

        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            return false;
        } else {
            return true;
        }
    }


}