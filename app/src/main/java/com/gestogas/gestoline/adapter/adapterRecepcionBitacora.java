package com.gestogas.gestoline.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gestogas.gestoline.DiffUtil.RecepcionBitacoraDiff;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.recepcion.RecepcionBitacoraDetalle;
import com.gestogas.gestoline.data.dataRecepcionBitacora;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adapterRecepcionBitacora extends RecyclerView.Adapter<adapterRecepcionBitacora.ItemViewHolder> {
    private final Context context;
    private final List<dataRecepcionBitacora> dataList;
    private final List<dataRecepcionBitacora> searchlList;

    public adapterRecepcionBitacora(Context context,List<dataRecepcionBitacora> dataList){

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
            for (dataRecepcionBitacora item : searchlList) {
                if (
                        item.getProducto().toLowerCase().contains(text) ||
                        item.getFolio().toLowerCase().contains(text) ||
                        item.getNofactura().toLowerCase().contains(text) ||
                        item.getLitroscompra().toLowerCase().contains(text) ||
                        item.getFecha().toLowerCase().contains(text)
                ) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<dataRecepcionBitacora> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new RecepcionBitacoraDiff(this.dataList, newData)
        );
        this.dataList.clear();
        this.dataList.addAll(newData);

        diffResult.dispatchUpdatesTo(this);

        searchlList.clear();
        searchlList.addAll(newData);
    }

    public void clearData() {
        this.dataList.clear();
        this.searchlList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_recepcion_bitacora, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        dataRecepcionBitacora item = dataList.get(position);

        int id = item.getId();
        String Producto = item.getProducto();

        holder.TxtFolio.setText(item.getFolio());
        holder.NoFactura.setText(item.getNofactura());
        holder.TxtProducto.setText(Producto);
        holder.TxtLitrosCompra.setText(item.getLitroscompra());
        holder.TxtFechaHora.setText(item.getFecha() + ", " + item.getHorallegada() + " a " + item.getHorasalidad());

        String colorHex = productoColores.getOrDefault(Producto, "#000000");
        holder.TxtProducto.setTextColor(Color.parseColor(colorHex));

        holder.ItemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Activity activity = (Activity) context;
                Intent didactic = new Intent(context, RecepcionBitacoraDetalle.class);
                didactic.putExtra("idRecepcion", String.valueOf(id));
                activity.startActivityForResult(didactic,1);

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        TextView NoFactura, TxtProducto, TxtLitrosCompra, TxtFechaHora,TxtFolio;
        CardView cardView;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            NoFactura = view.findViewById(R.id.NoFactura);
            TxtFolio = view.findViewById(R.id.TxtFolio);
            TxtProducto = view.findViewById(R.id.TxtProducto);
            TxtFechaHora = view.findViewById(R.id.TxtFechaHora);
            TxtLitrosCompra = view.findViewById(R.id.TxtLitrosCompra);
            cardView = view.findViewById(R.id.cardView);

            ItemView = view;
        }
    }

    private static final Map<String, String> productoColores = new HashMap<String, String>() {{
        put("BP REGULAR", "#4cd387");
        put("BP PREMIUM", "#f64c0f");
        put("EFITEC 87", "#4a8147");
        put("EFITEC 92", "#b94128");
        put("MAGNA", "#16BB43");
        put("PEMEX MAGNA", "#16BB43");
        put("PREMIUM", "#BB1616");
        put("PEMEX PREMIUM", "#BB1616");
        put("Shell Súper Regular", "#f7cc04");
        put("V Power Premiun", "#e32f18");
        put("G SUPER", "#77bd1e");
        put("G PREMIUM", "#e11682");
        put("G DIESEL", "#5d0e8b");
        put("DIESEL", "#000000");
        put("PEMEX DIESEL", "#000000");
        put("Diésel", "#000000");
    }};

}
