package com.vietlh.wethoong.adapters;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vietlh.wethoong.R;
import com.vietlh.wethoong.entities.Dieukhoan;
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

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan){

        this.allDieukhoan = allDieukhoan;

        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgView;
        private TextView lblVanban;
        private TextView lblDieukhoan;
        private TextView lblNoidung;
        private Button btnBreadscrubs;

        public ViewHolder(View v){

            super(v);
            imgView = v.findViewById(R.id.item).findViewById(R.id.imageView);
            lblVanban = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            lblDieukhoan = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.lblDieukhoan);
            btnBreadscrubs = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.btnBreadscrubs);
            lblNoidung = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblNoidung);

        }

        public void hideMinhhoa(){
            imgView.setMaxWidth(0);
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

        public void setBtnBreadscrubs(int parentId) {
            String breadscrubs = "";
            this.btnBreadscrubs.setText(breadscrubs);
        }
    }

    @Override
    public ListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position){

        try {
            if(allDieukhoan.get(position).getMinhhoa().size() > 0) {
                Bitmap bitmap = uHelper.getBitmapFromAssets(context, "/minhhoa/QC41-Bang_01.png");
                Vholder.setImgView(bitmap);
            }else{
                Vholder.hideMinhhoa();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Vholder.setBtnBreadscrubs(allDieukhoan.get(position).getCha());
        Vholder.setLblDieukhoan(allDieukhoan.get(position).getSo());
        Vholder.setLblNoidung(allDieukhoan.get(position).getNoidung());
        Vholder.setLblVanban(allDieukhoan.get(position).getVanban().getMa());
    }

    @Override
    public int getItemCount(){
        return allDieukhoan.size();
    }
}
