package com.vietlh.wethoong.entities;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vietlh.wethoong.R;
import com.vietlh.wethoong.utils.RedirectionHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class Phantich {
    private String idKey;
    private String author;
    private String title;
    private String shortContent;
    private String source;
    private String sourceInapp;
    private int revision;
    private ArrayList<HashMap<String, String>> rawContentDetailed = new ArrayList<>();

    public Phantich(String idKey, String author, String title, String shortContent, String source, String sourceInapp
            , String revision, HashMap<String, String> rawContentDetailed) {
        this.idKey = idKey;
        this.author = author;
        this.title = title;
        this.shortContent = shortContent;
        this.source = source;
        this.sourceInapp = sourceInapp;
        this.revision = Integer.parseInt(revision);
        this.rawContentDetailed.add(rawContentDetailed);
    }

    public Phantich(String idKey, String author, String title, String shortContent, String source, String sourceInapp
            , String revision, String contentOrder, String content, String minhhoa, String minhhoaType) {
        this.idKey = idKey;
        this.author = author;
        this.title = title;
        this.shortContent = shortContent;
        this.source = source;
        this.sourceInapp = sourceInapp;
        this.revision = Integer.parseInt(revision);
        HashMap<String, String> raw = new HashMap<>();
        raw.put("id_key", idKey);
        raw.put("author", author);
        raw.put("title", title);
        raw.put("shortcontent", shortContent);
        raw.put("source", source);
        raw.put("source_inapp", sourceInapp);
        raw.put("revision", revision);
        raw.put("contentorder", contentOrder);
        raw.put("content", content);
        raw.put("minhhoa", minhhoa);
        raw.put("minhhoatype", minhhoaType);

        this.rawContentDetailed.add(raw);
    }

    public Phantich() {
        this.idKey = "";
        this.author = "";
        this.title = "";
        this.shortContent = "";
        this.source = "";
        this.sourceInapp = "";
        this.revision = 0;
    }

    public String getIdKey() {
        return idKey;
    }

    public String getAuthor() {
        return author;
    }

    public String getTittle() {
        return title;
    }

    public String getShortContent() {
        return shortContent;
    }

    public String getSource() {
        return source;
    }

    public String getSourceInapp() {
        return sourceInapp;
    }

    public int getRevision() {
        return revision;
    }

    public HashMap<String, ViewGroup> getContentDetails(Context context) {
        HashMap<String, ViewGroup> contentDetailed = new HashMap<>();
            for (HashMap<String, String> content : rawContentDetailed) {
                PhantichChitiet phantichChitiet = new PhantichChitiet(context);
                if ("img".equals(content.get("minhhoatype"))) {
                    phantichChitiet.initChitietWithImage(Integer.parseInt(content.get("contentorder")), content.get("content"), content.get("minhhoa"));
                }else{
                    phantichChitiet.initChitietWithLink(Integer.parseInt(content.get("contentorder")), content.get("content"), content.get("minhhoa"));
                }
                contentDetailed.put(String.valueOf(phantichChitiet.getOrder()),phantichChitiet.getWrapper());
            }
        return contentDetailed;
    }

    public ArrayList<HashMap<String, String>> getRawContentDetailed() {
        return rawContentDetailed;
    }

    public void updateRawContentDetailed(HashMap<String, String> rawContentDetailed) {
        this.rawContentDetailed.add(rawContentDetailed);
    }

    class PhantichChitiet {
        private int order = 0;
        private Context context;
        private String imageLink = "";
        RedirectionHelper redirectionHelper = new RedirectionHelper();
        TextView lblNoidung;
        LinearLayout wrapper;

        public PhantichChitiet(Context context) {
            this.context = context;
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            wrapper = new LinearLayout(context);
            wrapper.setLayoutParams(layoutParams);
            wrapper.setOrientation(LinearLayout.VERTICAL);
            wrapper.setPadding(2,2,2,2);
            lblNoidung = new TextView(context);
            lblNoidung.setLayoutParams(layoutParams);
        }

            public int getOrder() {
                return order;
            }

            public LinearLayout getWrapper() {
                return wrapper;
            }

            //TO DO: cần xử lý ảnh từ web
            public void initChitietWithImage(int order, String noidung, String imgSrc){
                this.order = order;
                this.imageLink = imgSrc;
                ImageView minhhoaImg = new ImageView(context);
                WebImage webImage = new WebImage(minhhoaImg);
                webImage.execute(imgSrc);
                generateWrapper(noidung, minhhoaImg);
            }

            public void initChitietWithLink(int order, String noidung, final String urlLink) {
                this.order = order;
                Button minhhoaLink = new Button(context);
                minhhoaLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redirectionHelper.openUrlInExternalBrowser(context, urlLink);
                    }
                });
                if (urlLink.length() > 0) {
                    generateWrapper(noidung, minhhoaLink);
                }else{
                    generateWrapper(noidung);
                }

            }

            private void generateWrapper(String noidung) {
                lblNoidung.setText(noidung);
                wrapper.addView(lblNoidung);
            }

            private void generateWrapper(String noidung, View minhhoa)  {
                lblNoidung.setText(noidung);
                wrapper.addView(lblNoidung);
                wrapper.addView(minhhoa);
            }

    }
}
