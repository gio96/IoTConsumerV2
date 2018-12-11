package com.example.giovanny.consumer;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CreateMarketListActivity extends AppCompatActivity {

    private RecyclerView recyclerVProduct;
    //Firebase
    private DatabaseReference mreference;
    private FirebaseDatabase mFirebaseDatabase;
    private EditText  changeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_market_list);

        changeAmount = new EditText(this);
        changeAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        //call the Database just the first 10 groups
        Query events = FirebaseDatabase.getInstance().getReference().child("products");


        recyclerVProduct = (RecyclerView) findViewById(R.id.rv_createList);
        mreference = events.getRef();
        mreference.keepSynced(true);

        recyclerVProduct.setHasFixedSize(true);
        recyclerVProduct.setLayoutManager(new LinearLayoutManager(CreateMarketListActivity.this));

        FirebaseRecyclerAdapter<Product,CreateMarketListActivity.ItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Product,CreateMarketListActivity.ItemViewHolder>
                        (Product.class,R.layout.cv_item_create_list,CreateMarketListActivity.ItemViewHolder.class,mreference) {

                    @Override
                    public  void populateViewHolder(CreateMarketListActivity.ItemViewHolder groupViewHolder, final Product model , int position){

                        groupViewHolder.setData(model.getName());

                        groupViewHolder.cardViewEvent1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(model.getUidItem(),model.getName(),model.getAmount());
                                //Log.d("onCLick", model.getUidItem()+model.getAmount());
                            }
                        });

                    }
                };

        recyclerVProduct.setAdapter(firebaseRecyclerAdapter);
    }

    public void updateAmountFirebase(String uidItem, String name,int amount){
        Product product = new Product(uidItem,name,amount);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mreference = mFirebaseDatabase.getReference();
        mreference.child("users").child(firebaseUser.getUid()).child("list").child(uidItem).setValue(product);
        //mreference.child("products").child(uidItem).child("amount").setValue(amount);
    }

    public void showDialog(final String uidItem, final String name,final int amountItem){
        new AlertDialog.Builder(CreateMarketListActivity.this)
                .setTitle("Agregar Producto a Lista de Mercado")
                .setView(changeAmount)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String amountxtView = changeAmount.getText().toString();
                        int newAmount = amountItem + Integer.parseInt(amountxtView);
                        changeAmount.setText("");

                        if (Integer.parseInt(amountxtView)!=0){
                            updateAmountFirebase(uidItem,name,newAmount);
                        }else{
                            Toast.makeText(getApplicationContext(),"No se puede agregar", Toast.LENGTH_LONG);
                        }
                        dialog.cancel();

                        if(changeAmount.getParent()!=null)
                            ((ViewGroup)changeAmount.getParent()).removeView(changeAmount);

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(changeAmount.getParent()!=null)
                            ((ViewGroup)changeAmount.getParent()).removeView(changeAmount); // <- fix
                        changeAmount.setText("");
                        dialog.cancel();
                    }
                }).show();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        View mview;
        TextView textName,textAmount;

        CardView cardViewEvent1 = (CardView) itemView.findViewById(R.id.cv_product);
        public  ItemViewHolder(View itemView){
            super(itemView);
            mview = itemView;
        }

        public void setData(String name){
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textName.setText(name);


        }

    }
}
