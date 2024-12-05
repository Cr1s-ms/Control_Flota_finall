package com.cms_dev.control_flota.ui.conductores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cms_dev.control_flota.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConductoresFragment extends Fragment {

    private EditText editTextTask, vehiculo;
    private Button buttonAdd;
    private ListView listViewTasks;

    private FirebaseFirestore db;

    // Lista de conductores para mostrar en el ListView
    private ArrayList<String> conductoresList;
    private ArrayAdapter<String> adapter;

    public ConductoresFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conductores, container, false);
        editTextTask = view.findViewById(R.id.editTextTask);
        vehiculo = view.findViewById(R.id.vehiculo);
        buttonAdd = view.findViewById(R.id.buttonAdd1);
        listViewTasks = view.findViewById(R.id.listViewTasks);

        // Inicializar Firebase Firestore
        db = FirebaseFirestore.getInstance();
        conductoresList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, conductoresList);
        listViewTasks.setAdapter(adapter);
        // Agregar conductor al hacer clic en el botón
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarConductor();
            }
        });

        // Cargar conductores desde Firestore
        cargarConductores();

        return view;
    }

    // Método para agregar conductor
    private void agregarConductor() {
        String nombreConductor = editTextTask.getText().toString();
        String vehiculoAsignado = vehiculo.getText().toString();

        // Verificacion de que ambos campos no estén vacíos
        if (nombreConductor.isEmpty() || vehiculoAsignado.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingrese todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un Map para los datos del conductor
        Map<String, Object> conductorMap = new HashMap<>();
        conductorMap.put("nombre", nombreConductor);
        conductorMap.put("vehiculo", vehiculoAsignado);

        // Agregar el conductor a la colección "conductores" en Firestore

        db.collection("conductores")
                .add(conductorMap)
                .addOnSuccessListener(new OnSuccessListener<com.google.firebase.firestore.DocumentReference>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Conductor agregado correctamente", Toast.LENGTH_SHORT).show();
                        // Limpiar campos al agregar el conductor
                        editTextTask.setText("");
                        vehiculo.setText("");
                        // Volver a cargar los datos
                        cargarConductores();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Error al agregar conductor", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para cargar conductores desde Firestore y mostrar en el ListView
    private void cargarConductores() {
        db.collection("conductores")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Limpiar la lista de conductores antes de agregar los nuevos datos
                        conductoresList.clear();

                        // Recorrer los documentos obtenidos de Firestore
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String nombre = document.getString("nombre");
                            String vehiculo = document.getString("vehiculo");

                            // Agregar el conductor a la lista
                            if (nombre != null && vehiculo != null) {
                                conductoresList.add(nombre + " - " + vehiculo);
                            }
                        }

                        // Notificar al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Error al cargar conductores", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
