package br.cta.ipev.h125.gpsstatus;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothNovatelManager {

    private static final String TAG = "NovatelBluetooth";

    // UUID padrão para perfil SPP (Serial Port Profile)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private final OnMessageReceivedListener listener;

    // Interface para retornar as mensagens recebidas para a Activity/Fragment
    public interface OnMessageReceivedListener {
        void onMessageReceived(String message);
        void onConnectionStatusChanged(String status);
        void onError(String error);
    }

    public BluetoothNovatelManager(OnMessageReceivedListener listener) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.listener = listener;
    }

    /**
     * Inicia a conexão com o receptor Novatel usando o endereço MAC.
     */
    public synchronized void connect(String macAddress) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            listener.onError("Bluetooth desativado ou não suportado no dispositivo.");
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        // Cancela threads existentes tentando conectar
        stop();

        listener.onConnectionStatusChanged("Conectando ao Novatel...");
        connectThread = new ConnectThread(device);
        connectThread.start();
    }

    /**
     * Para todas as conexões ativas.
     */
    public synchronized void stop() {
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        listener.onConnectionStatusChanged("Desconectado.");
    }

    public synchronized void sendCommand(String command) {

        if (connectedThread != null) {

            connectedThread.write(command);

        } else {

            Log.e(TAG, "Nenhuma conexão ativa.");

            if (listener != null) {

                listener.onError("Bluetooth não conectado.");

            }

        }

    }

    // Thread responsável por estabelecer a conexão Bluetooth
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                // Cria o socket RFCOMM usando o UUID padrão serial
                tmp = device.createRfcommSocketToServiceRecord(SPP_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Falha ao criar o Socket RFCOMM", e);
            }
            socket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            // Sempre cancele o discovery antes de conectar para não travar a banda
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
            } catch (IOException connectException) {
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Não foi possível fechar o socket após falha de conexão", closeException);
                }
                listener.onError("Conexão falhou: " + connectException.getMessage());
                return;
            }

            // Conexão estabelecida com sucesso, inicia a thread de leitura
            synchronized (BluetoothNovatelManager.this) {
                connectThread = null;
            }
            connectedThread = new ConnectedThread(socket);
            connectedThread.start();
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Erro ao fechar o socket da ConnectThread", e);
            }
        }
    }

    // Thread responsável por gerenciar o fluxo de dados recebidos (InputStream)
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {

            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {

                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {

                Log.e(TAG, "Erro ao obter streams", e);
            }

            this.inputStream = tmpIn;
            this.outputStream = tmpOut;
            listener.onConnectionStatusChanged("Conectado com sucesso!");

            // ==========================
            // ENVIA COMANDO AUTOMÁTICO
            // ==========================
            write("LOG BT1 LOGFILESTATUS ONTIME 3");
            write("LOG BT1 GPGSA ONTIME 3");

        }

        public void run() {
            // Usamos BufferedReader para ler linha por linha (ideal para sentenças NMEA ou logs Novatel terminados em \r\n)
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while (true) {
                try {
                    line = reader.readLine();
                    if (line != null && listener != null) {
                        listener.onMessageReceived(line);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Conexão perdida ou fechada pelo dispositivo receptor.", e);
                    listener.onError("Conexão perdida.");
                    break;
                }
            }
        }

        public void write(String command) {

            try {

                String fullCommand = command + "\r\n";
                outputStream.write(fullCommand.getBytes(StandardCharsets.US_ASCII));
                outputStream.flush();
                Log.d(TAG, "CMD SENT: " + command);

            } catch (IOException e) {

                Log.e(TAG, "Erro ao enviar comando", e);

            }

        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Erro ao fechar o socket da ConnectedThread", e);
            }
        }
    }
}
