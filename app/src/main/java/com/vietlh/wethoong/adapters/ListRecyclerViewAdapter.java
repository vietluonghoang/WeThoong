package com.vietlh.wethoong.adapters;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vietlh.wethoong.DetailsActivity;
import com.vietlh.wethoong.R;
import com.vietlh.wethoong.SearchActivity;
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by vietlh on 2/26/18.
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {
    ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
    Context context;
    UtilsHelper uHelper = new UtilsHelper();
    SearchFor search;

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan){

        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.search = new SearchFor(this.context);
    }

    public void updateView(ArrayList<Dieukhoan> allDieukhoan){
        this.allDieukhoan = allDieukhoan;
        notifyDataSetChanged();
    }

//    public void showDetails(){
//        int itemPosition = mRecyclerView.getChildLayoutPosition(view);
//        String item = mList.get(itemPosition);
//        Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//    }

    @Override
    public ListRecyclerViewAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){

        final View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_items,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            }
        });

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position){

        Dieukhoan dk = allDieukhoan.get(position);
        try {
            if(dk.getMinhhoa().size() > 0) {
                vHolder.showMinhhoa();
                Bitmap bitmap = uHelper.getBitmapFromAssets(context, dk.getMinhhoa().get(0));
                vHolder.setImgView(bitmap);
            }else{
                vHolder.hideMinhhoa();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        vHolder.setBtnBreadscrubs(dk);
        vHolder.setLblDieukhoan(dk.getSo());
        String noidung = "";
        if(dk.getTieude().length() < 1){
            noidung = dk.getNoidung();
        }else {
            noidung = dk.getTieude()+"\n"+dk.getNoidung();
        }
        vHolder.setLblNoidung(noidung);
        vHolder.setLblVanban(dk.getVanban().getMa());
        vHolder.setVanbanId(String.valueOf(dk.getId()));
    }

    @Override
    public int getItemCount(){
        return allDieukhoan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgView;
        private TextView lblVanban;
        private TextView lblDieukhoan;
        private TextView lblNoidung;
        private Button btnBreadscrubs;
        private String vanbanId = "";

        public ViewHolder(View v){

            super(v);
            imgView = v.findViewById(R.id.item).findViewById(R.id.imageView);
            lblVanban = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            lblDieukhoan = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.lblDieukhoan);
            btnBreadscrubs = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.btnBreadscrubs);
            lblNoidung = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblNoidung);
            v.setOnClickListener(this);
        }

        public void hideMinhhoa(){
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = 0;
            imgView.setLayoutParams(parms);
        }

        public void showMinhhoa(){
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.height = 100;
            parms.width = 100;
            imgView.setLayoutParams(parms);
            imgView.setMinimumWidth(100);
        }

        public ImageView getImgView() {
            return imgView;
        }

        public void setImgView(Bitmap bitmap) throws URISyntaxException {
            this.imgView.setImageBitmap(bitmap);
        }

        public TextView getLblVanban() {
            return lblVanban;
        }

        public void setLblVanban(String vanbanCode) {
            this.lblVanban.setText(vanbanCode);
        }

        public TextView getLblDieukhoan() {
            return lblDieukhoan;
        }

        public void setLblDieukhoan(String dieukhoanNumber) {
            this.lblDieukhoan.setText(dieukhoanNumber);
        }

        public TextView getLblNoidung() {
            return lblNoidung;
        }

        public void setLblNoidung(String noidung) {
            this.lblNoidung.setText(noidung);
        }

        public Button getBtnBreadscrubs() {
            return btnBreadscrubs;
        }

        public void setBtnBreadscrubs(Dieukhoan parent) {
            ArrayList<String> vanbanid = new ArrayList<String>();
            vanbanid.add(parent.getVanban().getId() + "");
            String breadscrubs = search.getAncestersNumber(parent,vanbanid);
            this.btnBreadscrubs.setText(breadscrubs);
        }

        public void setVanbanId(String id){
            vanbanId = id.trim();
        }

        @Override
        public void onClick(View v) {
            Log.i("Message","tapping on: "+vanbanId);
            Intent i = new Intent(context.getApplicationContext(), DetailsActivity.class);
            //TODO: need to change the hardcode searchType to something that configurable.
            i.putExtra("vanbanId",vanbanId);
            context.startActivity(i);
        }
    }
}
