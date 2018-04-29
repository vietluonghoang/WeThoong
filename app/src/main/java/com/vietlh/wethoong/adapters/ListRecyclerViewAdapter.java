package com.vietlh.wethoong.adapters;

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
import com.vietlh.wethoong.entities.Dieukhoan;
import com.vietlh.wethoong.utils.SearchFor;
import com.vietlh.wethoong.utils.UtilsHelper;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by vietlh on 2/26/18.
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Dieukhoan> allDieukhoan = new ArrayList<>();
    private Context context;
    private UtilsHelper uHelper = new UtilsHelper();
    private SearchFor search;
    private int type = 0; //default value for normal view

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan){
        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.search = new SearchFor(this.context);
    }

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan, int type){
        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.search = new SearchFor(this.context);
        this.type = type;
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
                Bitmap bitmap = uHelper.getBitmapFromAssets(context, "minhhoa/" + dk.getMinhhoa().get(0));
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
        }else if (dk.getNoidung().length() < 1){
            noidung = dk.getTieude();
        } else {
            noidung = dk.getTieude()+"\n"+dk.getNoidung();
        }

        //in case recyclerView is showing children dieukhoan
        if(type == 0){
            vHolder.hideLblVanban(false);
            vHolder.hideBtnBreadscrubs(false);
            vHolder.setLblVanban(dk.getVanban().getMa());
            if (noidung.length() > 250){
                noidung = noidung.substring(0,249) + "...";
            }
        }else {
            vHolder.hideLblVanban(true);
            vHolder.hideBtnBreadscrubs(true);
        }

        vHolder.setLblNoidung(noidung);
        vHolder.setDieukhoanId(String.valueOf(dk.getId()));
        vHolder.fitItemDetails();
    }

    @Override
    public int getItemCount(){
        return allDieukhoan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imgView;
        private LinearLayout itemDetails;
        private TextView lblVanban;
        private TextView lblDieukhoan;
        private TextView lblNoidung;
        private Button btnBreadscrubs;
        private String dieukhoanId = "";

        public ViewHolder(View v){

            super(v);
            imgView = v.findViewById(R.id.item).findViewById(R.id.imageView);
            itemDetails = v.findViewById(R.id.itemDetails);
            lblVanban = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            lblDieukhoan = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.lblDieukhoan);
            btnBreadscrubs = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.btnBreadscrubs);
            lblNoidung = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblNoidung);
            v.setOnClickListener(this);
        }

        public void hideMinhhoa(){
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = 0;
            parms.height = 0;
            imgView.setLayoutParams(parms);
        }

        public void showMinhhoa(){
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = (int)(uHelper.getScreenWidth() * 0.2);
            parms.height = parms.width;
            imgView.setLayoutParams(parms);
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

        public void hideLblVanban(Boolean isHidden){
            if (isHidden){
                lblVanban.setVisibility(View.GONE);
            }else {
                lblVanban.setVisibility(View.VISIBLE);
            }
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

        public void hideBtnBreadscrubs(Boolean isHidden){
            if (isHidden){
                btnBreadscrubs.setVisibility(View.GONE);
            }else {
                btnBreadscrubs.setVisibility(View.VISIBLE);
            }
        }

        public void setDieukhoanId(String id){
            dieukhoanId = id.trim();
        }

        //workaround for fitting itemDetail
        public void fitItemDetails(){
            ViewGroup.LayoutParams params = itemDetails.getLayoutParams();
            params.width = uHelper.getScreenWidth() - imgView.getLayoutParams().width;
            itemDetails.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Log.i("Message","tapping on: "+ dieukhoanId);
            Intent i = new Intent(context.getApplicationContext(), DetailsActivity.class);
            //TODO: need to change the hardcode dieukhoanId to something that configurable.
            i.putExtra("dieukhoanId", dieukhoanId);
            context.startActivity(i);
        }
    }
}
