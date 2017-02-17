package com.duongtd.scannerdocument.activities;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.duongtd.scannerdocument.R;
import com.duongtd.scannerdocument.asynctask.AsyncResponse;
import com.duongtd.scannerdocument.asynctask.CropImageTask;
import com.duongtd.scannerdocument.asynctask.ImageProcessingTask;
import com.duongtd.scannerdocument.database.DatabaseHandler;
import com.duongtd.scannerdocument.object.Document;
import com.duongtd.scannerdocument.pdf.PdfManagement;
import com.duongtd.scannerdocument.util.AppConstant;
import com.duongtd.scannerdocument.util.Utils;
import com.duongtd.scannerdocument.util.BitmapUtil;
import com.duongtd.scannerdocument.util.ImageSaver;
import com.duongtd.scannerdocument.widget.PolygonView;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageProcessingActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap bitmap;
    private Uri uriBitmap;
    private Button btnSave, btnCrop, btnPdf;
    private PolygonView mPolygonOutline;
    private FrameLayout mFrameSource;

    PhotoViewAttacher mAttacher;

//    private static final String DIRECTORY_NAME = "ScannerDocument";
    private static final int STEPS = 5;
    private static final int BITMAP_WITDTH_THUMB = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        imageView = (ImageView)findViewById(R.id.imgView);
        mPolygonOutline = (PolygonView)findViewById(R.id.polygon_outline);
        mFrameSource = (FrameLayout)findViewById(R.id.frame_source);

        uriBitmap = this.getIntent().getExtras().getParcelable(MainActivity.IMAGE_URI);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        // (not needed unless you are going to change the drawable later)
        mAttacher = new PhotoViewAttacher(imageView);

        try {
            bitmap = BitmapUtil.getBitmapFromUri(getApplicationContext(), uriBitmap);

            if (bitmap != null) {
                setImage(bitmap);
                mFrameSource.post(() -> initPolygon(bitmap));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportImage();
            }
        });

        btnCrop = (Button)findViewById(R.id.btnCrop);
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<Integer, PointF> points = mPolygonOutline.getPoints();
                if (isScanPointsValid(points)) {
                    System.gc();
                    mPolygonOutline.setVisibility(View.GONE);
                    CropImageTask cropImageTask = new CropImageTask(ImageProcessingActivity.this,
                            bitmap, points, imageView.getWidth(), imageView.getHeight(), new AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {
                            mPolygonOutline.setVisibility(View.GONE);
                            if (output instanceof Bitmap){
                                setImage((Bitmap)output);
                            } else
                                Log.d("error", "Cast type error");
                        }
                    });
                    cropImageTask.execute();
                }
            }
        });

        btnPdf = (Button)findViewById(R.id.btnPdf);
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = Utils.getCurrentTime();
                String output = Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/" + AppConstant.APP_FOLDER + "/" + fileName + ".pdf";
                PdfManagement.exportPdfFile(ImageProcessingActivity.this, bitmap, output);
                String date = Utils.currentTimeNoSecond();

                //image thumbnail
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                        bitmap, BITMAP_WITDTH_THUMB, bitmap.getHeight() * BITMAP_WITDTH_THUMB / bitmap.getWidth(), false);
                String img_thumb = ImageSaver.saveImagePrivateNormal(ImageProcessingActivity.this,
                                        resizedBitmap, fileName, AppConstant.APP_DIR_THUMB);
                writeStogare(img_thumb, fileName, date, output);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.img_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        if (id == R.id.action_filter) {
            ImageProcessingTask imageProcessingTask = new ImageProcessingTask(bitmap, this, new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    if (output instanceof Bitmap){
                        Bitmap _bitmap = (Bitmap) output;
                        setImage(_bitmap);
                    } else
                        Log.d("error", "Cast type error");
                }
            });
            imageProcessingTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setImage(Bitmap _bitmap){
        bitmap = _bitmap;
        imageView.setImageBitmap(_bitmap);
        // If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
        mAttacher.update();
    }

    public void initPolygon(Bitmap _bitmap){
        System.gc();
        Map<Integer, PointF> pointFs = new HashMap<>();

        pointFs.put(0, new PointF(0, 0));
        pointFs.put(1, new PointF(imageView.getWidth(), 0));
        pointFs.put(2, new PointF(0, imageView.getHeight()));
        pointFs.put(3, new PointF(imageView.getWidth(), imageView.getHeight()));

        mPolygonOutline.setPoints(pointFs);
        mPolygonOutline.setVisibility(View.VISIBLE);

        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageView.getWidth() + 2 * padding, imageView.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        mPolygonOutline.setLayoutParams(layoutParams);
    }


    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private void exportImage(){
        ImageSaver imageSaver = new ImageSaver(this);
        imageSaver.setFileName(Utils.getCurrentTime() + ".png");
        imageSaver.setDirectoryName(AppConstant.APP_FOLDER);
        imageSaver.save(bitmap);
    }

    private void writeStogare(String img_thumb, String fileName, String date, String pdf_file){

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance(ImageProcessingActivity.this);
        databaseHandler.addDocument(new Document(img_thumb, fileName, date, pdf_file));
    }
}
