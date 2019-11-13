package com.example.crudfirebase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustumeAdapter extends RecyclerView.Adapter<ViewHolder> {

    ListaActivity listaActivity;
    List<Modelo> modelosList;


    public CustumeAdapter(ListaActivity listaActivity, List<Modelo> modelosList) {
        this.listaActivity = listaActivity;
        this.modelosList = modelosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.modelo_layout_card,parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        //handle item click view
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //this will be called when user click item
                String title = modelosList.get(viewType).getTitulo();
                String id = modelosList.get(viewType).getId();
                String descricao = modelosList.get(viewType).getDescricao();
                Toast.makeText(view.getContext(),"id: "+id+" Titulo:" + title + " descricao: "+descricao,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                //this will be called when user click item
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(listaActivity);

                String[] options ={"Update","Delete"};

                alertDialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            //update click
                            String id = modelosList.get(position).getId();
                            String titulo = modelosList.get(position).getTitulo();
                            String descricao = modelosList.get(position).getDescricao();
                            //intent
                            Intent intent = new Intent(listaActivity,MainActivity.class);
                            //put data in intent
                            intent.putExtra("pId",id);
                            intent.putExtra("pTitulo",titulo);
                            intent.putExtra("pDescricao",descricao);
                            //start activity
                            listaActivity.startActivity(intent);

                        }
                        if(i==1){
                            //delete click
                            listaActivity.deleteData(position);
                        }
                    }
                }).create().show();

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextTitulo.setText(modelosList.get(position).getTitulo());
        holder.mTextDescricao.setText(modelosList.get(position).getDescricao());
    }

    @Override
    public int getItemCount() {
        return modelosList.size();
    }
}
