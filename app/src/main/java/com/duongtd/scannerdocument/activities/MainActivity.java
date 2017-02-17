package com.duongtd.scannerdocument.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duongtd.scannerdocument.MyNDK;
import com.duongtd.scannerdocument.R;
import com.duongtd.scannerdocument.adapters.DocumentAdapter;
import com.duongtd.scannerdocument.database.DatabaseHandler;
import com.duongtd.scannerdocument.object.Document;
import com.duongtd.scannerdocument.util.ActivityHelper;
import com.duongtd.scannerdocument.util.BitmapUtil;
import com.duongtd.scannerdocument.util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final CharSequence CHOOSE_PHOTO = "Choose Photo";
    public static final int ACTIVITY_CAPTURE_IMAGE = 1001;
    public static final int ACTIVITY_OPEN_IMAGE = 1002;
    public static final String IMAGE_URI = "IMAGE_URI";

    private File file;

    private Uri fileUri;

    private RecyclerView recyclerView;

    private DocumentAdapter documentAdapter;

    private List<Document> documentList = new ArrayList<Document>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(MainActivity.this.getExternalCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
                fileUri = Uri.fromFile(file);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);
                ActivityHelper.selectMenu(MainActivity.this, ACTIVITY_CAPTURE_IMAGE, ACTIVITY_OPEN_IMAGE, fileUri);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView0);
        initDocumentSaved();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DatabaseHandler db = DatabaseHandler.getInstance(this);
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            File file = new File(this.getExternalCacheDir(),
//                    String.valueOf(System.currentTimeMillis()) + ".jpg");
//            fileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent, ACTIVITY_CAPTURE_IMAGE);

        } else if (id == R.id.nav_gallery) {
//            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_OPENABLE);
//            i.setType("image/*");
//            this.startActivityForResult(Intent.createChooser(i, CHOOSE_PHOTO), ACTIVITY_OPEN_IMAGE);

        } else if (id == R.id.nav_slideshow) {

            // Inserting Contacts
            Log.d("Insert: ", "Inserting ..");
            db.addDocument(new Document("a", "b", "c", "d"));

        } else if (id == R.id.nav_manage) {
            Log.d("Count  ",  db.getDocumentsCount() + " ");
//            Toast.makeText(MainActivity.this, db.getDocumentsCount(), Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_share) {
//            DatabaseHandler db = DatabaseHandler.getInstance(MainActivity.this);
            db.onUpgrade(db.getWritableDatabase(), 1, 1);

        } else if (id == R.id.nav_send) {
            Log.d("new data ", new SimpleDateFormat("HH:mm MM-dd-yyyy").format(new Date()));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ACTIVITY_OPEN_IMAGE:

                    Uri mUri = data.getData();

//                    Bitmap mBitmap = null;
                    Intent intent = new Intent(getApplicationContext(), ImageProcessingActivity.class);
                    intent.putExtra(IMAGE_URI, mUri);
                    startActivity(intent);
                    break;
                case ACTIVITY_CAPTURE_IMAGE:
                    Intent intent2 = new Intent(getApplicationContext(), ImageProcessingActivity.class);
                    intent2.putExtra(IMAGE_URI, fileUri);
//                    boolean deleted = file.delete();
//                    Toast.makeText(this, deleted + "", Toast.LENGTH_LONG).show();
                    startActivity(intent2);
                    break;
            }
        }
    }

    private void initDocumentSaved(){

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        documentList = DatabaseHandler.getInstance(this).getAllDocuments();
        documentAdapter = new DocumentAdapter(documentList);

//        documentList.add(new Document("a", "b","c", "d"));
        documentAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(documentAdapter);
    }
}
