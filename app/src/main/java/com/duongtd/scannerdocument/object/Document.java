package com.duongtd.scannerdocument.object;

/**
 * Created by duongtd on 16/02/2017.
 */

public class Document {
    private int id;
    private String img_thumb;
    private String name;
    private String date;
    private String pdf_file;

    public Document(String img_thumb, String name, String date, String pdf_file) {
        this.img_thumb = img_thumb;
        this.pdf_file = pdf_file;
        this.name = name;
        this.date = date;
    }

    public Document(int id, String img_thumb, String name, String date, String pdf_file) {
        this.id = id;
        this.img_thumb = img_thumb;
        this.pdf_file = pdf_file;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg_thumb() {
        return img_thumb;
    }

    public void setImg_thumb(String img_thumb) {
        this.img_thumb = img_thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPdf_file() {
        return pdf_file;
    }

    public void setPdf_file(String pdf_file) {
        this.pdf_file = pdf_file;
    }
}
