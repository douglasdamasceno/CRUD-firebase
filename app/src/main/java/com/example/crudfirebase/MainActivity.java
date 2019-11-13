package com.example.crudfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText titulo;
    EditText descricao;
    Button btnAdd;
    Button btnLista;

    ProgressDialog pd;
    FirebaseFirestore db;

    //dado da intent
    String pId,pTitulo,pDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titulo = findViewById(R.id.titleEt);
        descricao = findViewById(R.id.descriptionEt);
        btnAdd = findViewById(R.id.btnAdd);
        btnLista = findViewById(R.id.btnLista);
        ActionBar actionBar = getSupportActionBar();//ActionBar();

        //
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            //update data
            actionBar.setTitle("Tela Atualizar");
            btnAdd.setText("Atualizar");
            pId = bundle.getString("pId");
            pTitulo = bundle.getString("pTitulo");
            pDescricao = bundle.getString("pDescricao");

            titulo.setText(pTitulo);
            descricao.setText(pDescricao);

        }else{
            //new data
            actionBar.setTitle("Tela Add");
            btnAdd.setText("Add");
        }


        pd = new ProgressDialog(this);




        db = FirebaseFirestore.getInstance();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = getIntent().getExtras();
                if(bundle1 !=null){
                    //updating
                    String id = pId;
                    String titu = titulo.getText().toString();
                    String desc = descricao.getText().toString();
                    updateData(id,titu,desc);

                }else{
                    //add
                    //input dado
                    String titu = titulo.getText().toString();
                    String desc = descricao.getText().toString();
                    titulo.setText("");
                    descricao.setText("");
                    createData(titu,desc);
                }

            }
        });

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ListaActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateData(String id, String titu, String desc) {
        pd.setTitle("Updating data ...");
        pd.show();
        db.collection("documentos").document(id)
                .update("titulo",titu,
                        "search",titu.toLowerCase(),
                        "descricao",desc)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //called when upadate sucess.
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Atualizado!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //called when update faill.
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Falha ao Atualizar: "+e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });
    }


    private void createData(String titu, String desc) {
        pd.setTitle("Adding data to Firestore!");
        pd.show();
        //gerenado id aleatorio para o banco
        String id = UUID.randomUUID().toString();
        //preparando dados
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", id);
        doc.put("titulo", titu);
        doc.put("search", titu.toLowerCase());
        doc.put("descricao", desc);

        db.collection("documentos")
                .add(doc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Criado!",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(MainActivity.this,"Falha ao criar: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

    }

}
