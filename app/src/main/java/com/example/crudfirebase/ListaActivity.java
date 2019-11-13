package com.example.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.crudfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {
    List<Modelo> modeloLista = new ArrayList<Modelo>();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseFirestore db;

    CustumeAdapter adapter;
    ProgressDialog progressDialog;

    Button floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        //init firebase
        db = FirebaseFirestore.getInstance();

        //Action

        ActionBar actionBar = getSupportActionBar();//ActionBar();
        actionBar.setTitle("Tela Lista");

        progressDialog = new ProgressDialog(this);
        floatingActionButton = findViewById(R.id.floatadd);

        //inicializado view
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
       //show data in recycleView
        showData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListaActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showData() {
        //set title of progress dialog
        progressDialog.setTitle("Carregando data...");
        progressDialog.show();

        db.collection("documentos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //apagar os antigos para atulizar do banco para remover apagados da view.
                        modeloLista.clear();
                        //called when data is retrived
                        progressDialog.dismiss();
                        for (DocumentSnapshot documentSnapshot:task.getResult()){
                            //Modelo modelo2 = (Modelo) documentSnapshot.toObject(Modelo.class);
                            Modelo modelo = new Modelo(
                            documentSnapshot.getId(),
                            documentSnapshot.get("titulo").toString(),
                            documentSnapshot.get("descricao").toString());

                            modeloLista.add(modelo);
                        }
                        //adapter
                        adapter = new CustumeAdapter(ListaActivity.this,modeloLista);
                        //set adapter to recyclerview.
                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is ant while retireving
                        Toast.makeText(ListaActivity.this,"Falha ao Listar: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void deleteData(int index){
        //set title of progress dialog
        progressDialog.setTitle("Deletando data...");
        progressDialog.show();
        db.collection("documentos").document(modeloLista.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Toast.makeText(ListaActivity.this,"Deletado.",Toast.LENGTH_SHORT).show();
                        showData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when there is any error
                        progressDialog.dismiss();
                        Toast.makeText(ListaActivity.this,"Falha ao deletar: "+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void searchData(String query) {
        progressDialog.setTitle("Search data ...");
        progressDialog.show();
        db.collection("documentos")
                .whereEqualTo("search",query.toLowerCase()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //called when searching is suceeded
                        modeloLista.clear();
                        progressDialog.dismiss();
                        for(DocumentSnapshot doc: task.getResult()){
                            Modelo modelo = new Modelo(
                                    doc.getId().toString(),
                                    doc.getString("titulo").toString(),
                                    doc.getString("descricao").toString());
                            modeloLista.add(modelo);
                        }
                        adapter = new CustumeAdapter(ListaActivity.this,modeloLista);
                        mRecyclerView.setAdapter(adapter);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //when there is any error
                        progressDialog.dismiss();
                        Toast.makeText(ListaActivity.this,"Falha ao buscar"+ e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating menu_main.xml
        getMenuInflater().inflate(R.menu.menu_main,menu);
        //searc view
        MenuItem menuItem = menu.findItem(R.id.buscar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when we press search button.
                searchData(query);//function call with string in searchView as parament
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               //called as and when we type even a single letter.
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.configuracao){
            Toast.makeText(this,"configuração",Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.buscar){

        }
        return super.onOptionsItemSelected(item);
    }
}
