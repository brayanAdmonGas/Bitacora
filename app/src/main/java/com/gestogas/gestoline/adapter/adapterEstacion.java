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

import com.gestogas.gestoline.DiffUtil.EstacionDiff;
import com.gestogas.gestoline.EstacionGerentes;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.controllers.AppController;
import com.gestogas.gestoline.data.dataEstacion;

import java.util.ArrayList;
import java.util.List;

public class adapterEstacion extends RecyclerView.Adapter<adapterEstacion.ItemViewHolder> {

    private final Context context;
    private final List<dataEstacion> dataList;
    private final List<dataEstacion> searchlList;
    public adapterEstacion(Context context,List<dataEstacion> dataList){
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
            for (dataEstacion item : searchlList) {
                if (
                        item.getRazonsocial().toLowerCase().contains(text) ||
                                item.getPermisocre().toLowerCase().contains(text)
                ) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<dataEstacion> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new EstacionDiff(this.dataList, newData)
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_estaciones, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        dataEstacion item = dataList.get(position);

        final int Idgas = item.getId();
        final String Permisocre = item.getPermisocre();
        final String Razonsocial = item.getRazonsocial();
        final String Direccion = item.getDireccioncompleta();
        final String Productouno = item.getProductouno();
        final String Productodos = item.getProductodos();
        final String Productotres = item.getProductotres();
        final String Logo = item.getLogo();
        final String Latitud = item.getLatitud();
        final String Longitud = item.getLongitud();
        final String Distmax = item.getDistmax();

        holder.RazonSocial.setText(item.getRazonsocial());
        holder.PermisoCre.setText(item.getPermisocre());

        holder.ItemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                AppController.getInstance().ActualizarEstacion(Idgas,Permisocre,Razonsocial,Direccion,Productouno,Productodos,Productotres,Logo,Latitud,Longitud,Distmax);
                AppController.getInstance().LatitudEquipo(String.valueOf(Latitud));
                AppController.getInstance().LongitudEquipo(String.valueOf(Longitud));

                Activity activity = (Activity) context;
                Intent actividad = new Intent(context, EstacionGerentes.class);
                actividad.putExtra("idgas",String.valueOf(Idgas));
                actividad.putExtra("razonsocial",Razonsocial);
                activity.startActivity(actividad);
                activity.finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        TextView RazonSocial, PermisoCre;
        public ItemViewHolder(@NonNull View view) {
            super(view);
            RazonSocial = view.findViewById(R.id.RazonSocial);
            PermisoCre = view.findViewById(R.id.PermisoCre);
            ItemView = view;
        }
    }
}
