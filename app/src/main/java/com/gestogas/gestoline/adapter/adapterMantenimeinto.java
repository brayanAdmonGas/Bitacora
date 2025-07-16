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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gestogas.gestoline.DiffUtil.MantenimeintoDiff;
import com.gestogas.gestoline.R;
import com.gestogas.gestoline.data.dataMantenimiento;
import com.gestogas.gestoline.mantenimiento.*;

import java.util.ArrayList;
import java.util.List;

public class adapterMantenimeinto extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final Context context;
    private final List<dataMantenimiento> dataList;
    private final List<dataMantenimiento> searchList;

    public adapterMantenimeinto(Context context, List<dataMantenimiento> dataList) {
        this.context = context;
        this.dataList = new ArrayList<>(dataList);
        this.searchList = new ArrayList<>(dataList);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contenido_lista_mantenimiento, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            dataMantenimiento item = dataList.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;

            int id = item.getId();
            String idequipo = item.getIdequipo();
            String numVerificacion = item.getNumverificacion();
            String descripcion = item.getDescripcion();

            if (item.getEstado().equals("0")) {
                itemHolder.TxtEstado.setText("Pendiente");
                itemHolder.TxtEstado.setTextColor(Color.parseColor("#FFC107"));
            } else {
                itemHolder.TxtEstado.setText("Finalizado");
                itemHolder.TxtEstado.setTextColor(Color.parseColor("#008000"));
            }

            itemHolder.TxtFolio.setText(item.getFolio());
            itemHolder.TxtDescripcion.setText(item.getDescripcion());
            itemHolder.TxtFechaHora.setText(item.getFecha() + ", " + item.getHora());

            itemHolder.ItemView.setOnClickListener(view -> {
                Activity activity = (Activity) context;
                Intent intent;

                switch (idequipo) {
                    case "0":
                        intent = new Intent(context, MantenimientoCorrectivoDetalle.class);
                        break;
                    case "20":
                        intent = new Intent(context, item.getEstado().equals("1")
                                ? MantenimientoPreventivoExtintorDetalle.class
                                : MantenimientoPreventivoExtintor.class);
                        break;
                    case "43":
                        intent = new Intent(context, item.getEstado().equals("1")
                                ? MantenimientoPreventivoTanqueDetalle.class
                                : MantenimientoPreventivoTanque.class);
                        break;
                    case "48":
                        intent = new Intent(context, item.getEstado().equals("1")
                                ? MantenimientoPreventivoDetectorHumoDetalle.class
                                : MantenimientoPreventivoDetectorHumo.class);
                        break;
                    default:
                        intent = new Intent(context, item.getEstado().equals("1")
                                ? MantenimientoPreventivoRevisarDetalle.class
                                : MantenimientoPreventivoRevisar.class);
                        break;
                }

                intent.putExtra("idMantenimiento", String.valueOf(id));
                intent.putExtra("NumeroEquipo", idequipo);
                intent.putExtra("NombreEquipo", descripcion);
                intent.putExtra("numVerificacion", numVerificacion);
                intent.putExtra("NumeroPagina", "1");
                intent.putExtra("estado", item.getEstado());

                activity.startActivityForResult(intent, 1);
            });

        } // LoadingViewHolder no requiere bind, solo muestra el ProgressBar
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // 🔷 ViewHolder para item normal
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        View ItemView;
        TextView TxtEstado, TxtFolio, TxtDescripcion, TxtFechaHora;

        public ItemViewHolder(@NonNull View view) {
            super(view);
            TxtEstado = view.findViewById(R.id.txtEstado);
            TxtFolio = view.findViewById(R.id.TxtFolio);
            TxtDescripcion = view.findViewById(R.id.TxtDescripcion);
            TxtFechaHora = view.findViewById(R.id.TxtFechaHora);
            ItemView = itemView;
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

    // 🔷 Filtro
    public void filter(String text) {
        text = text.toLowerCase().trim();
        dataList.clear();

        if (text.isEmpty()) {
            dataList.addAll(searchList);
        } else {
            for (dataMantenimiento item : searchList) {
                if (item.getDescripcion().toLowerCase().contains(text) ||
                        item.getFolio().toLowerCase().contains(text) ||
                        item.getFecha().toLowerCase().contains(text) ||
                        item.getHora().toLowerCase().contains(text)) {
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // 🔷 Update con DiffUtil
    public void updateData(List<dataMantenimiento> newData) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new MantenimeintoDiff(this.dataList, newData)
        );
        this.dataList.clear();
        this.dataList.addAll(newData);

        diffResult.dispatchUpdatesTo(this);

        searchList.clear();
        searchList.addAll(newData);
    }
}
