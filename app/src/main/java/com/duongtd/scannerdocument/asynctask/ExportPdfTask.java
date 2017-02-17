package com.duongtd.scannerdocument.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by duongtd on 17/02/2017.
 */

public class ExportPdfTask extends AsyncTask <Void, Void, Integer>{

    public static final int FAIL = -1;
    public static final int SUCCESS = 0;

    private Bitmap bitmap;
    private String outputFile;
    private Activity activity;
    private ProgressDialog progressDialog;
    private static final String PROGRESS_MESSAGE = "Exporting";
    public AsyncResponse delegate = null;

    public ExportPdfTask(Activity activity, Bitmap bitmap, String outputFile, AsyncResponse delegate) {
        this.activity = activity;
        this.bitmap = bitmap;
        this.outputFile = outputFile;
        this.delegate = delegate;

        progressDialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.gc();

        progressDialog.setMessage(PROGRESS_MESSAGE);
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        try {
            File file = new File(outputFile);
            file.getParentFile().mkdirs();
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(PageSize.A4);
            document.add(image);
            document.close();
//            Toast.makeText(activity, "Success! " + outputFile + " created", Toast.LENGTH_SHORT).show();
        } catch (DocumentException e1) {
//            Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            return FAIL;
        } catch (FileNotFoundException e1) {
//            Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            return FAIL;
        } catch (MalformedURLException e1) {
//            Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            return FAIL;
        } catch (IOException e1) {
//            Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            return FAIL;
        }
        return SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        delegate.processFinish(i);
    }
}
