package com.example.giovanny.consumer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton floatingActionButton;

    private RecyclerView recyclerVProduct;
    //Firebase
    private DatabaseReference mreference;
    private FirebaseDatabase mFirebaseDatabase;
    private EditText  changeAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButton);

        changeAmount = new EditText(this);
        changeAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        //call the Database just the first 10 groups
        Query events = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("list");




        recyclerVProduct = (RecyclerView) findViewById(R.id.rv_listproducst);
        mreference = events.getRef();
        mreference.keepSynced(true);

        recyclerVProduct.setHasFixedSize(true);
        recyclerVProduct.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        FirebaseRecyclerAdapter<Product,ItemViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Product,MainActivity.ItemViewHolder>
                        (Product.class,R.layout.cv_item_product,MainActivity.ItemViewHolder.class,mreference) {

                    @Override
                    public  void populateViewHolder(ItemViewHolder groupViewHolder, final Product model , int position){

                        groupViewHolder.setData(model.getName(),model.getAmount());
                        groupViewHolder.cardViewEvent1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDialog(model.getUidItem(),model.getAmount());
                                //Log.d("onCLick", model.getUidItem()+model.getAmount());
                            }
                        });

                    }
                };

        recyclerVProduct.setAdapter(firebaseRecyclerAdapter);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateMarketListActivity.class);
                startActivity(intent);
            }
        });
    }





    public void updateAmountFirebase(String uidItem, int amount){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mreference = mFirebaseDatabase.getReference();
        mreference.child("users").child(firebaseUser.getUid()).child("list").child(uidItem).child("amount").setValue(amount);
        //mreference.child("products").child(uidItem).child("amount").setValue(amount);
    }

    public void showDialog(final String uidItem, final int amountItem){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Cantidad Comprada")
                .setView(changeAmount)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String amountxtView = changeAmount.getText().toString();
                        int newAmount = amountItem + Integer.parseInt(amountxtView);
                        changeAmount.setText("");

                        if (Integer.parseInt(amountxtView)!=0){
                            updateAmountFirebase(uidItem,newAmount);
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

        public void setData(String name,int amount){
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textName.setText(name);

            textAmount = (TextView) itemView.findViewById(R.id.txt_amount);
            textAmount.setText(String.valueOf(amount));


        }

    }
}
