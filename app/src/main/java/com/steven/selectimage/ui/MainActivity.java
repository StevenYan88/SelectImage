package com.steven.selectimage.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.steven.selectimage.R;
import com.steven.selectimage.widget.recyclerview.SpaceGridItemDecoration;
import com.steven.selectimage.ui.adapter.SelectedImageAdapter;
import com.steven.selectimage.utils.TDevice;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int SELECT_IMAGE_REQUEST = 0x0011;

    private RecyclerView mSelectedImageRv;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectedImageRv = findViewById(R.id.rv_selected_image);
        mSelectedImageRv.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        mSelectedImageRv.addItemDecoration(new SpaceGridItemDecoration((int) TDevice.dipToPx(getResources(), 1)));
        findViewById(R.id.btn_select).setOnClickListener(v -> selectImage());
    }

    private void selectImage() {
        int isPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (isPermission == PackageManager.PERMISSION_GRANTED) {
            startActivity();
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, SelectImageActivity.class);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity();
            } else {
                Toast.makeText(this, "需要您的存储权限!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE_REQUEST && data != null) {
                ArrayList<String> selectedImages = data.getStringArrayListExtra(SelectImageActivity.EXTRA_RESULT);
                SelectedImageAdapter adapter = new SelectedImageAdapter(this, selectedImages, R.layout.selected_image_item);
                mSelectedImageRv.setAdapter(adapter);

            }
        }
    }
}
