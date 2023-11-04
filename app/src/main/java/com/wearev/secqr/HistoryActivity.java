package com.wearev.secqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView rvHistoryCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Objects.requireNonNull(getSupportActionBar()).setTitle("History ");
        rvHistoryCard = findViewById(R.id.rvHistoryCards);
        getOrRefreshQRCards();
    }

    private void getOrRefreshQRCards() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<ParentItem> parentItemArrayList = new ArrayList<>();

        ArrayList<DBDataModel> databaseDataList = databaseHelper.getHistoryData();

        for (DBDataModel data : databaseDataList) {
            ParentItem parentItem = new ParentItem(data.getRegNo(),data.getVehicleClass(), data.getDate(),data.getQRBytes());
            parentItemArrayList.add(parentItem);
        }
//
        MyHistoryAdapter myAdapter = new MyHistoryAdapter(parentItemArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvHistoryCard.setLayoutManager(linearLayoutManager);

        myAdapter.setOnShareClickListener(position ->
                shareItem(databaseDataList.get(position).getQRBytes(),
                        databaseDataList.get(position).getRegNo()));

        myAdapter.setOnDeleteClickListener(position ->
                deleteItem(databaseHelper, databaseDataList, position, myAdapter));
        rvHistoryCard.setAdapter(myAdapter);
    }

    private void deleteItem(DatabaseHelper databaseHelper, ArrayList<DBDataModel> items, int position, MyHistoryAdapter adapter) {
        if (position >= 0 && position < items.size()) {
            DBDataModel itemToDelete = items.get(position);

            databaseHelper.deleteVehicleInfo(itemToDelete.getId());
            items.remove(position);
            adapter.notifyItemRemoved(position);
            getOrRefreshQRCards();
        } else {
            Toast.makeText(this, "Please Refresh this page first !", Toast.LENGTH_SHORT).show();
         }
    }

    private void shareItem(byte[] imageBytes, String text) {

        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        Uri imageUri = getImageUri(this, imageBitmap);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.histry_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId==R.id.optRefresh){
            getOrRefreshQRCards();
        }else {
            Toast.makeText(this, "Select", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}