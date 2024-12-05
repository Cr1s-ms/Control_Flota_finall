package com.cms_dev.control_flota.ui.seguimiento;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cms_dev.control_flota.R;
import com.cms_dev.control_flota.ui.MqttHandler;

public class SeguimientoFragment extends Fragment {

    private static final String BROKER = "tcp://broker.emqx.io:1883";  // Dirección del broker MQTT
    private static final String CLIENT_ID = "cliente_12345678954";     // ID único del cliente
    private static final String TOPIC_SUB1 = "test/IoT";              // Primer tópico para suscripción
    private static final String TOPIC_SUB2 = "test/IoT2";             // Segundo tópico para suscripción

    private MqttHandler mqttHandler;
    private TextView textViewCoordenadas;
    private TextView textView2Coordenadas;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_seguimiento, container, false);

        // Inicializar vistas
        textViewCoordenadas = root.findViewById(R.id.textViewCoordenadas);
        textView2Coordenadas = root.findViewById(R.id.textView2Coordenadas);

        // Configurar MQTT
        configurarMQTT();

        return root;
    }

    private void configurarMQTT() {
        // Crear una instancia de MqttHandler
        mqttHandler = new MqttHandler();

        // Configurar el listener para manejar mensajes recibidos
        mqttHandler.setMessageListener((topic, message) -> {
            Log.d("MQTT", "Mensaje recibido en el tópico " + topic + ": " + message);

            // Actualizar los TextView según el tópico
            requireActivity().runOnUiThread(() -> {
                if (topic.equals(TOPIC_SUB1)) {
                    textViewCoordenadas.setText("Última coordenada: " + message);
                } else if (topic.equals(TOPIC_SUB2)) {
                    textView2Coordenadas.setText("Datos del vehículo: " + message);
                }
            });
        });

        // Intentar conectar al broker MQTT
        mqttHandler.connect(BROKER, CLIENT_ID);

        // Suscribirse a ambos tópicos
        mqttHandler.subscribe(TOPIC_SUB1);
        mqttHandler.subscribe(TOPIC_SUB2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mqttHandler != null) {
            mqttHandler.disconnect();
        }
    }
}
