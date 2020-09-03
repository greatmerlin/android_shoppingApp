package com.example.shoppingapp.persistence;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.model.EntityInShoppingList;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class DataAdapter extends FirebaseRecyclerAdapter<EntityInShoppingList, DataAdapter.MyViewHolder> {

    private final DataClickedListener onDataClicked;

    public DataAdapter(int modelLayout, Query ref, DataClickedListener onDataClicked) {
        super(EntityInShoppingList.class, modelLayout, MyViewHolder.class, ref);
        this.onDataClicked = onDataClicked;
    }

    public DataAdapter(int modelLayout, DatabaseReference ref, DataClickedListener onDataClicked) {
        super(EntityInShoppingList.class, modelLayout, MyViewHolder.class, ref);
        this.onDataClicked = onDataClicked;
    }

    @Override
    protected void populateViewHolder(MyViewHolder myViewHolder, final EntityInShoppingList entityInShoppingList, final int i) {

        myViewHolder.bindData(entityInShoppingList, getRef(i).getKey(), this.onDataClicked );
    }



    //subclass for displaying each item
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private View view;

        public MyViewHolder(View itemView){
            super(itemView);
            view = itemView;
        }

        public void bindData(
                final EntityInShoppingList entityInShoppingList,
                final String databaseKey,
                final DataClickedListener onDataClicked) {

            setItem(entityInShoppingList.getItem());
            setNote(entityInShoppingList.getNote());
            setPrice(entityInShoppingList.getChf());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDataClicked.dataClicked(entityInShoppingList, databaseKey);
                }
            });
        }

        void setItem(String item){
            TextView myItem = view.findViewById(R.id.item);
            myItem.setText(item);
        }

        void setNote(String note){
            TextView myNote = view.findViewById(R.id.note);
            myNote.setText(note);
        }

        void setPrice(double price){
            TextView myPrice = view.findViewById(R.id.chf);
            String stringPrice = String.valueOf(price);
            myPrice.setText(stringPrice);
        }
    }

}
