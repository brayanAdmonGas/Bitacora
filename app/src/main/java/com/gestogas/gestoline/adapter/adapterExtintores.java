package com.gestogas.gestoline.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gestogas.gestoline.DiffUtil.ExtintoresDiff;
import com.gestogas.gestoline.ExtintorCrearEditar;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.data.dataExtintores;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class adapterExtintores extends RecyclerView.Adapter<adapterExtintores.ItemViewHolder>{

    private final Context context;
    private final List<dataExtintores> dataList;
    private final List<dataExtintores> searchlList;
    public adapterExtintores(Context context,List<dataExtintores> dataList){
        this.context = context;
        this.dataList = new ArrayList<>(dataList);
        this.searchlList = new ArrayList<>(dataList);
    }

    public void filter(String text) {
        text = text.toLowerCase().trim();
        dataList.clear();

        if (text.isEmpty()) {
            dataList.addAll(searchlList);
        } else {
            for (dataExtintores item : searchlList) {
                if (
                        item.getNoextintor().toLowerCase().contains(text) ||
                        item.getUbicacion().toLowerCase().contains(text) ||
                        item.getTipoextintor().toLowerCase().contains(text) ||
                        item.getPesokg().toLowerCase().contains(text)
                ) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<dataExtintores> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ExtintoresDiff(this.dataList, newData)
        );
        this.dataList.clear();
        this.dataList.addAll(newData);

        diffResult.dispatchUpdatesTo(this);

        searchlList.clear();
        searchlList.addAll(newData);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_extintores, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        dataExtintores item = dataList.get(position);
        final String idextintor = item.getId();
        final String noextintor = item.getNoextintor();
        final String ubicacion = item.getUbicacion();
        final String fecharecarga = item.getFecharecarga();
        final String tipoextintor = item.getTipoextintor();
        final String peso = item.getPesokg();

        holder.txv_noextintor.setText(noextintor);
        holder.txv_ubicacion.setText(ubicacion);
        holder.txv_tipoExtintor.setText(tipoextintor);
        holder.txv_pesokg.setText(peso);
        try {
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"));
            Date fecha = formatoOriginal.parse(fecharecarga);
            String fechaFormateada = formatoNuevo.format(fecha);
            holder.txv_fecharecarga.setText(fechaFormateada);
        } catch (ParseException e) {
            holder.txv_fecharecarga.setText(fecharecarga); // Por si la conversión falla
        }
        holder.ItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Activity activity = (Activity) context;
                Intent actividad = new Intent(context, ExtintorCrearEditar.class);
                actividad.putExtra("estado","Editar");
                actividad.putExtra("idextintor",idextintor);
                actividad.putExtra("noextintor",noextintor);
                actividad.putExtra("ubicacion",ubicacion);
                actividad.putExtra("fecharecarga",fecharecarga);
                actividad.putExtra("tipoextintor",tipoextintor);
                actividad.putExtra("pesokg",peso);
                actividad.putExtra("titulo","Editar Extintor");
                actividad.putExtra("tituloboton","EDITAR EXTINTOR");
                activity.startActivityForResult(actividad,1);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        TextView txv_noextintor, txv_ubicacion, txv_fecharecarga, txv_tipoExtintor, txv_pesokg;
        public ItemViewHolder(@NonNull View view) {

            super(view);
            txv_noextintor = view.findViewById(R.id.NoExtintor);
            txv_ubicacion = view.findViewById(R.id.Ubicacion);
            txv_fecharecarga = view.findViewById(R.id.FechaRecarga);
            txv_tipoExtintor = view.findViewById(R.id.TipoExtintor);
            txv_pesokg = view.findViewById(R.id.PesoKg);

            ItemView = view;

        }
    }
}
