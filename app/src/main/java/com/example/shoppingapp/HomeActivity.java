package com.example.shoppingapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.maps.MapActivity;
import com.example.shoppingapp.model.EntityInShoppingList;
import com.example.shoppingapp.persistence.DataAdapter;
import com.example.shoppingapp.persistence.DataClickedListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private TextView totalSumResult;
    private String item;
    private double price;
    private String note;
    private String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        totalSumResult = findViewById(R.id.totalAmount);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView = findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser databaseUser = firebaseAuth.getCurrentUser();

        assert databaseUser != null;
        String uID = databaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.sl)).child(uID);
        databaseReference.keepSynced(true);
        final Toolbar toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.titel);
        FloatingActionButton fab_btn = findViewById(R.id.fab);
        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double totalAmount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EntityInShoppingList entityInShoppingList = snapshot.getValue(EntityInShoppingList.class);
                    assert entityInShoppingList != null;
                    totalAmount += entityInShoppingList.getChf();
                    String twoDecimalDouble = String.format("%.2f", totalAmount);
                    String stringTotalChf;
                    stringTotalChf = twoDecimalDouble + getString(R.string.chfTotal);
                    totalSumResult.setText(stringTotalChf);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    private void customDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        final LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.input_data, null);
        final AlertDialog dialog = myDialog.create();
        dialog.setView(view);
        final AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoTest);

        final EditText chf = view.findViewById(R.id.edtChf);
        final EditText note = view.findViewById(R.id.edtNote);
        Button btn_save = view.findViewById(R.id.btnSave);

        String[] autoCompletedItems = getResources().getStringArray(R.array.autoCompleteArray);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoCompletedItems);
        autoCompleteTextView.setAdapter(adapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    String newItem = autoCompleteTextView.getText().toString().trim();
                    String chfItem = chf.getText().toString().trim();
                    String noteItem = note.getText().toString().trim();

                    double chfDouble = Double.parseDouble(chfItem);

                    if (TextUtils.isEmpty(newItem)) {
                        autoCompleteTextView.setError(getString(R.string.requiredField));
                        return;
                    }
                    if (TextUtils.isEmpty(chfItem)) {
                        chf.setError(getString(R.string.requiredField));
                        return;
                    }
                    if (TextUtils.isEmpty(noteItem)) {
                        note.setError(getString(R.string.requiredField));
                        return;
                    }

                    String id = databaseReference.push().getKey();
                    EntityInShoppingList entityInShoppingList = new EntityInShoppingList(newItem, chfDouble, noteItem, id);
                    assert id != null;
                    databaseReference.child(id).setValue(entityInShoppingList);
                    Toast.makeText(getApplicationContext(), R.string.item_add, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } catch (java.lang.NumberFormatException e) {
                    chf.setError(getString(R.string.requiredField));
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {

        super.onStart();

        final DataClickedListener onDataClicked = new DataClickedListener() {
            @Override
            public void dataClicked(EntityInShoppingList entityInShoppingList, String databaseKey) {
                postKey = databaseKey;
                item = entityInShoppingList.getItem();
                note = entityInShoppingList.getNote();
                price = entityInShoppingList.getChf();

                updateData();
            }
        };

        DataAdapter dataAdapter = new DataAdapter(R.layout.item_data, databaseReference, onDataClicked);

        recyclerView.setAdapter(dataAdapter);
    }

    public void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        @SuppressLint("InflateParams") View myView = inflater.inflate(R.layout.update_input, null);

        final AlertDialog dialog = myDialog.create();
        dialog.setView(myView);

        final EditText editItem = myView.findViewById(R.id.edtItemUpdate);
        final EditText editNote = myView.findViewById(R.id.edtNoteUpdate);
        final EditText editPrice = myView.findViewById(R.id.edtChfUpdate);

        editItem.setText(item);
        editItem.setSelection(item.length());
        editNote.setText(note);
        editNote.setSelection(note.length());
        editPrice.setText(String.valueOf(price));
        editPrice.setSelection(String.valueOf(price).length());

        Button btnUpdate = myView.findViewById(R.id.btnSaveUpdate);
        Button btnDelete = myView.findViewById(R.id.btnDeleteUpdate);
        Button btnCancel = myView.findViewById(R.id.btnCancelUpdate);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    item = editItem.getText().toString().trim();
                    note = editNote.getText().toString().trim();
                    String myPrice;
                    myPrice = editPrice.getText().toString().trim();
                    double doublePrice = Double.parseDouble(myPrice);
                    EntityInShoppingList entityInShoppingList = new EntityInShoppingList(item, doublePrice, note, postKey);

                    if (TextUtils.isEmpty(item)) {
                        editItem.setError(getString(R.string.requiredField));
                        return;
                    }
                    if (TextUtils.isEmpty(myPrice)) {
                        editPrice.setError(getString(R.string.requiredField));
                        return;
                    }
                    if (TextUtils.isEmpty(note)) {
                        editNote.setError(getString(R.string.requiredField));
                        return;
                    }

                    databaseReference.child(postKey).setValue(entityInShoppingList);
                    dialog.dismiss();

                } catch (java.lang.NumberFormatException e) {
                    editPrice.setError(getString(R.string.requiredField));
                    e.printStackTrace();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(postKey).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.menuMaps:
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}