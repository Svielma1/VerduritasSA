package sebastian.vielma.verduritassa;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Cultivo> {

    private FirebaseAuth mAuth;
    private Context context;
    private List<Cultivo> cultivos;
    FirebaseFirestore db;


    public CustomAdapter(Context context, List<Cultivo> cultivos) {
        super(context, R.layout.list_item, cultivos);
        this.context = context;
        this.cultivos = cultivos;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Cultivo cultivoActual = cultivos.get(position);
        TextView textoItem = convertView.findViewById(R.id.texto_item);
        textoItem.setText(cultivoActual.getAlias() + ", " + cultivoActual.getFechaCosecha());

        ImageView editarCultivoBtn = convertView.findViewById(R.id.editarCultivoBtn);

        editarCultivoBtn.setOnClickListener(v -> {
            PopupMenu menuEditar = new PopupMenu(context, editarCultivoBtn);
            menuEditar.getMenuInflater().inflate(R.menu.menu_item, menuEditar.getMenu());
            menuEditar.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.opcionEditar) {
                    String documentID = cultivoActual.getIdDocumento();
                    System.out.println(documentID + " => ESTE ES EL dato que busco");
                    Toast.makeText(context, "Editar " + cultivos.get(position), Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.opcionEliminar) {
                    String documentID = cultivoActual.getIdDocumento();
                    eliminarCultivo(documentID);
                    actualizarLista(position);
                    return true;
                }
                return false;
            });

            menuEditar.show();
        });

        return convertView;
    }

    private void eliminarCultivo(String documentID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cultivos")
                .document(documentID)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Cultivo eliminado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show());

    }

    public void actualizarLista(int position) {
        cultivos.remove(position);
        notifyDataSetChanged();
    }
}
