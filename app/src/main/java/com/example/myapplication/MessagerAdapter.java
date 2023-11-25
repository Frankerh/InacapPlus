package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MessagerAdapter extends RecyclerView.Adapter<MessagerAdapter.MessageViewHolder> {

    private List<Messager> messageList;

    // Constructor
    public MessagerAdapter(List<Messager> messageList) {
        this.messageList = messageList != null ? messageList : new ArrayList<>();
    }

    // Método para actualizar la lista de mensajes
    public void updateMessages(List<Messager> messages) {
        this.messageList = messages != null ? messages : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Método para inflar el diseño de cada elemento de la lista
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    // Método para vincular los datos a la vista
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messager message = messageList.get(position);
        holder.bind(message);
    }

    // Método para obtener la cantidad de elementos en la lista
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Clase ViewHolder para mantener las referencias de los elementos de la vista
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageTextView);
        }

        public void bind(Messager message) {
            // Usar getSenderId() y getReceiverId() para obtener los IDs del remitente y destinatario
            messageText.setText("Sender ID: " + message.getSenderId() + ", Receiver ID: " + message.getReceiverId());
        }
    }
}