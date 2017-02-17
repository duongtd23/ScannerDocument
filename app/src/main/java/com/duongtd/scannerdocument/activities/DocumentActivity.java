package com.duongtd.scannerdocument.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.duongtd.scannerdocument.R;
import com.duongtd.scannerdocument.database.DatabaseHandler;
import com.duongtd.scannerdocument.object.Document;

import java.io.File;

public class DocumentActivity extends AppCompatActivity {

    public static final String DOCUMENT = "document";

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        int id = this.getIntent().getIntExtra(DOCUMENT, -1);

        if (id > 0) {
            Document document = DatabaseHandler.getInstance(this).getDocument(id);

            imageView = (ImageView) findViewById(R.id.img_document);

            File file = new File(document.getImg_thumb());
            if (file.exists()) {
                Bitmap bmImg = BitmapFactory.decodeFile(document.getImg_thumb());
                imageView.setImageBitmap(bmImg);
            }
            toolbar.setTitle(document.getName());
        }

    }

}
