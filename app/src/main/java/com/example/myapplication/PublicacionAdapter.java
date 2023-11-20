package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicacionAdapter extends RecyclerView.Adapter<PublicacionAdapter.PublicacionViewHolder> {

    private List<Publicacion> publicaciones;

    public PublicacionAdapter(List<Publicacion> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public static class PublicacionViewHolder extends RecyclerView.ViewHolder {
        TextView usuarioTextView;
        TextView fechaTextView;
        TextView opinionTextView;
        TextView likesTextView;
        Button likeButton;
        CircleImageView profileImageView;
        public PublicacionViewHolder(View itemView) {
            super(itemView);
            usuarioTextView = itemView.findViewById(R.id.textView2);
            fechaTextView = itemView.findViewById(R.id.textView3);
            opinionTextView = itemView.findViewById(R.id.textView4);
            likesTextView = itemView.findViewById(R.id.textView5);
            likeButton = itemView.findViewById(R.id.button);
        }
    }

    @NonNull
    @Override
    public PublicacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new PublicacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicacionViewHolder holder, int position) {
        final Publicacion publicacion = publicaciones.get(position);

        holder.usuarioTextView.setText(publicacion.getUsuario());
        holder.fechaTextView.setText(publicacion.getFecha());
        holder.opinionTextView.setText(publicacion.getOpinion());
        holder.likesTextView.setText(publicacion.getLikes() + " Likes");

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Incrementar los likes y actualizar la vista
                publicacion.setLikes(publicacion.getLikes() + 1);
                notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicaciones.size();
    }
}

