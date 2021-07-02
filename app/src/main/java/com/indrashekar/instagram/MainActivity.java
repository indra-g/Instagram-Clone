package com.indrashekar.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listview;
    DatabaseReference reference;
    ArrayList<String>username;
    ArrayList<String>id1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.share:
                startActivity(new Intent(MainActivity.this,PostingActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Instagram User's");
        listview=(ListView)findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),UserFeedsActivity.class);
                intent.putExtra("id",id1.get(position));
                startActivity(intent);
                finish();
            }
        });
        username=new ArrayList<String>();
        id1=new ArrayList<String>();
        username.clear();
        ArrayAdapter<String>arrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,username);
        listview.setAdapter(arrayAdapter);
        reference= FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersref=reference.child("Users");
        ValueEventListener eventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String name = ds.child("username").getValue(String.class);
                    String id = ds.child("id").getValue(String.class);
                    username.add(name);
                    id1.add(id);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        usersref.addListenerForSingleValueEvent(eventListener);
    }
}