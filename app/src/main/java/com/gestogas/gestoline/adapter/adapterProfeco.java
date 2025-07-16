package com.gestogas.gestoline.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gestogas.gestoline.DiffUtil.ProfecoDiff;
import com.gestogas.gestoline.profeco.ProfecoDetalle;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.data.dataProfeco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adapterProfeco extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final Context context;
    private final List<dataProfeco> dataList;
    private final List<dataProfeco> searchlList;

    public adapterProfeco(Context context, List<dataProfeco> dataList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_profeco, parent, false);
        return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            dataProfeco item = dataList.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            itemHolder.txtFolio.setText(item.getFolio());
            itemHolder.txtFechaHora.setText(item.getFecha() + " " + item.getHora());
            itemHolder.txtLado.setText(item.getLado());
            itemHolder.txtProducto.setText(item.getProductobitacora());
            itemHolder.txtNoDispensario.setText(item.getNodispensario());

            // ✅ Aplicar color al texto del producto
            // Procesar los productos separados por coma
            String[] productos = item.getProductobitacora().split(",");
            SpannableStringBuilder builder = new SpannableStringBuilder();

            for (int i = 0; i < productos.length; i++) {
                String prod = productos[i].trim();
                String colorHex = productoColores.getOrDefault(prod, "#000000");
                int start = builder.length();
                builder.append(prod);
                builder.setSpan(
                        new ForegroundColorSpan(Color.parseColor(colorHex)),
                        start,
                        builder.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                if (i < productos.length - 1) {
                    builder.append(", ");
                }
            }

            itemHolder.txtProducto.setText(builder);


            itemHolder.ItemView.setOnClickListener(v -> {
                Activity activity = (Activity) context;
                Intent actividad = new Intent(context, ProfecoDetalle.class);
                actividad.putExtra("idProfeco", item.getId());
                activity.startActivityForResult(actividad, 1);
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        TextView txtFolio, txtFechaHora, txtLado, txtProducto, txtNoDispensario;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            txtFolio = view.findViewById(R.id.TxtFolio);
            txtFechaHora = view.findViewById(R.id.TxtFechaHora);
            txtLado = view.findViewById(R.id.TxtLado);
            txtProducto = view.findViewById(R.id.TxtProducto);
            txtNoDispensario = view.findViewById(R.id.NoDispensario);

            ItemView = view;
        }
    }

    // 🔷 ViewHolder para item loading
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View view) {
            super(view);
            progressBar = view.findViewById(R.id.progressBar);
        }
    }

    // 🔷 Métodos públicos para agregar o quitar el loading
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
            for (dataProfeco item : searchlList) {
                if (
                        item.getFolio().toLowerCase().contains(text) ||
                                item.getFecha().toLowerCase().contains(text) ||
                                item.getHora().toLowerCase().contains(text) ||
                                item.getProductobitacora().toLowerCase().contains(text) ||
                                item.getLado().toLowerCase().contains(text) ||
                                item.getMotivo().toLowerCase().contains(text) ||
                                item.getNombre().toLowerCase().contains(text) ||
                                item.getObservaciones().toLowerCase().contains(text)
                ) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<dataProfeco> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new ProfecoDiff(this.dataList, newData)
        );

        this.dataList.clear();
        this.dataList.addAll(newData);
        searchlList.clear();
        searchlList.addAll(newData);

        diffResult.dispatchUpdatesTo(this);
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
