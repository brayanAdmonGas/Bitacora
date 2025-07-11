package com.gestogas.gestoline.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gestogas.gestoline.R;
import com.gestogas.gestoline.data.dataDetectoresHumo;

import java.util.List;

public class adapterDetectoresHumo extends RecyclerView.Adapter<adapterDetectoresHumo.ItemViewHolder>{
    private final Context context;
    private final List<dataDetectoresHumo> dataList;
    public adapterDetectoresHumo(Context context, List<dataDetectoresHumo> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_mantenimiento_detector_humo, parent, false);
        return new adapterDetectoresHumo.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        dataDetectoresHumo item = dataList.get(position);

        holder.NoDetectorHumo.setText(item.getNo_detector());
        holder.Ubicacion.setText(item.getUbicacion());
        holder.TxtRevision1.setText(item.getRevision1());
        holder.TxtResultado1.setText(item.getResultado1());
        holder.TxtRevision2.setText(item.getRevision2());
        holder.TxtResultado2.setText(item.getResultado2());
        holder.TxtRevision3.setText(item.getRevision3());
        holder.TxtResultado3.setText(item.getResultado3());
        holder.TxtRevision4.setText(item.getRevision4());
        holder.TxtResultado4.setText(item.getResultado4());

    }

    @Override
    public int getItemCount() { return dataList.size(); }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;

        TextView NoDetectorHumo, Ubicacion;
        TextView TxtRevision1, TxtResultado1,
                TxtRevision2, TxtResultado2,
                TxtRevision3, TxtResultado3,
                TxtRevision4, TxtResultado4;


        public ItemViewHolder(@NonNull View view) {
            super(view);

            NoDetectorHumo = view.findViewById(R.id.NoDetectorHumo);
            Ubicacion = view.findViewById(R.id.Ubicacion);
            TxtRevision1 = view.findViewById(R.id.TxtRevision1);
            TxtResultado1 = view.findViewById(R.id.TxtResultado1);
            TxtRevision2 = view.findViewById(R.id.TxtRevision2);
            TxtResultado2 = view.findViewById(R.id.TxtResultado2);
            TxtRevision3 = view.findViewById(R.id.TxtRevision3);
            TxtResultado3 = view.findViewById(R.id.TxtResultado3);
            TxtRevision4 = view.findViewById(R.id.TxtRevision4);
            TxtResultado4 = view.findViewById(R.id.TxtResultado4);

            ItemView = itemView;

        }
    }
}