package com.gestogas.gestoline.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

public class adapterRecepcionBitacora extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final Context context;
    private final List<dataRecepcionBitacora> dataList;
    private final List<dataRecepcionBitacora> searchlList;

    public adapterRecepcionBitacora(Context context,List<dataRecepcionBitacora> dataList){

        this.context = context;
        this.dataList = new ArrayList<>(dataList);
        this.searchlList = new ArrayList<>(dataList);

    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_recepcion_bitacora, parent, false);
        return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
        dataRecepcionBitacora item = dataList.get(position);
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        int id = item.getId();
        String Producto = item.getProducto();

            itemHolder.TxtFolio.setText(item.getFolio());
            itemHolder.NoFactura.setText(item.getNofactura());
            itemHolder.TxtProducto.setText(Producto);
            itemHolder.TxtLitrosCompra.setText(item.getLitroscompra());
            itemHolder.TxtFechaHora.setText(item.getFecha() + ", " + item.getHorallegada() + " a " + item.getHorasalidad());

        String colorHex = productoColores.getOrDefault(Producto, "#000000");
            itemHolder.TxtProducto.setTextColor(Color.parseColor(colorHex));

            itemHolder.ItemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                Activity activity = (Activity) context;
                Intent didactic = new Intent(context, RecepcionBitacoraDetalle.class);
                didactic.putExtra("idRecepcion", String.valueOf(id));
                activity.startActivityForResult(didactic,1);

            }
        });

        }

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

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    public void addLoading() {
        dataList.add(null);
        notifyItemInserted(dataList.size() - 1);
    }

    public void removeLoading() {
        int position = dataList.size() - 1;
        if (position >= 0 && dataList.get(position) == null) {
            dataList.remove(position);
            notifyItemRemoved(position);
        }
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
