package com.duongtd.scannerdocument.pdf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.duongtd.scannerdocument.asynctask.AsyncResponse;
import com.duongtd.scannerdocument.asynctask.CompressImageTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

/**
 * Created by duongtd on 15/02/2017.
 */

public class PdfManagement {

    public static void exportPdfFile(Context context, String input, String output){
        File file = new File(output);
        file.getParentFile().mkdirs();
        try {
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter.getInstance(document, new FileOutputStream(output));
            document.open();

            Image image = Image.getInstance(input);
            image.scaleToFit(PageSize.A4);
            document.add(image);
            document.close();
            Toast.makeText(context, "pdf success", Toast.LENGTH_SHORT).show();
        } catch(FileNotFoundException e) {
            Toast.makeText(context, "pdf fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch(DocumentException e) {
            Toast.makeText(context, "pdf fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch(IOException e) {
            Toast.makeText(context, "pdf fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    //so slow after compress
    public static void exportPdfFile(Activity activity, Bitmap bitmap, String outputFile){
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CompressImageTask compressImageTask = new CompressImageTask(bitmap, activity, new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output instanceof ByteArrayOutputStream) {
                    try {
                        File file = new File(outputFile);
                        file.getParentFile().mkdirs();
                        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
                        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                        document.open();
                        ByteArrayOutputStream temp = (ByteArrayOutputStream)output;
                        Image image = Image.getInstance(temp.toByteArray());
                        image.scaleToFit(PageSize.A4);
                        document.add(image);
                        document.close();
                        Toast.makeText(activity, "Success! " + outputFile + " created", Toast.LENGTH_SHORT).show();
                    } catch (DocumentException e1) {
                        Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (FileNotFoundException e1) {
                        Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (MalformedURLException e1) {
                        Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        Toast.makeText(activity, "pdf fail", Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    }
                }
            }
        });
        compressImageTask.execute(stream);
    }
}
