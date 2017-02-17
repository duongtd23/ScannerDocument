package com.duongtd.scannerdocument.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duongtd.scannerdocument.R;
import com.duongtd.scannerdocument.object.Document;
import com.duongtd.scannerdocument.util.BitmapUtil;

import java.io.File;
import java.util.List;

/**
 * Created by duongtd on 17/02/2017.
 */

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Document> documents;

    public DocumentAdapter(List<Document> documents){
        this.documents = documents;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView txtName, txtDate, txtId;
//        public TextView metaData;

        public MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.row_image);
            txtName = (TextView) view.findViewById(R.id.row_title);
            txtDate = (TextView) view.findViewById(R.id.row_date);
            txtId = (TextView) view.findViewById(R.id.txtId);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), NewsActivity.class);
//                    v.getContext().startActivity(intent);
                }
            });
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.document_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder)holder;
        Document document = documents.get(position);
        File file = new File(document.getImg_thumb());
        if (file.exists()) {
            Bitmap bmImg = BitmapFactory.decodeFile(document.getImg_thumb());
            viewHolder.image.setImageBitmap(bmImg);
        }
        viewHolder.txtId.setText(document.getId());
        viewHolder.txtName.setText(document.getName());
        viewHolder.txtDate.setText(document.getDate());
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }
}
