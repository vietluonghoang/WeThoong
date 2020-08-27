package com.vietlh.wethoong.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.vietlh.wethoong.utils.GeneralSettings;
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
    private int noidungLengthThreshold = 250;
    private int matchingPrefixThreshold = 10;
    private String keyword = "";

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan, String keyword) {
        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.keyword = keyword;
        this.search = new SearchFor(this.context);
    }

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan, int type, int noidungLengthThreshold) {
        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.search = new SearchFor(this.context);
        this.type = type;
        this.noidungLengthThreshold = noidungLengthThreshold;
    }

    public ListRecyclerViewAdapter(Context context, ArrayList<Dieukhoan> allDieukhoan, int type) {
        this.allDieukhoan = allDieukhoan;
        this.context = context;
        this.search = new SearchFor(this.context);
        this.type = type;
    }

    public void updateView(ArrayList<Dieukhoan> allDieukhoan) {
        this.allDieukhoan = allDieukhoan;
        notifyDataSetChanged();
    }

    public void updateView(ArrayList<Dieukhoan> allDieukhoan, String keyword) {
        this.keyword = keyword;
        this.allDieukhoan = allDieukhoan;
        notifyDataSetChanged();
    }

    @Override
    public ListRecyclerViewAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_items, parent, false);
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
    public void onBindViewHolder(ViewHolder vHolder, int position) {

        Dieukhoan dk = allDieukhoan.get(position);
        try {
            if (dk.getMinhhoa().size() > 0) {
                int defaultMinhhoa = dk.getDefaultMinhhoa();
                vHolder.showMinhhoa();
                Bitmap bitmap = uHelper.getBitmapFromAssets(context, "minhhoa/" + dk.getMinhhoa().get(defaultMinhhoa).replace("\n", "").trim());
                vHolder.setImgView(bitmap);
            } else {
                vHolder.hideMinhhoa();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        vHolder.setBtnBreadscrubs(dk);
        vHolder.setLblDieukhoan(dk.getSo());
        String noidung = "";
        if (dk.getTieude().length() < 1) {
            noidung = dk.getNoidung();
        } else if (dk.getNoidung().length() < 1) {
            noidung = dk.getTieude();
        } else {
            noidung = dk.getTieude() + "\n" + dk.getNoidung();
        }

        //in case recyclerView is showing children dieukhoan
        if (type == 0) {
            vHolder.hideLblVanban(false);
            vHolder.hideBtnBreadscrubs(false);
            vHolder.setLblVanban(GeneralSettings.getVanbanInfo(dk.getVanban().getId(), "shortname"));

            //TODO: the valude of 250 should be configurable
//            if (noidung.length() > noidungLengthThreshold) {
//                noidung = noidung.substring(0, noidungLengthThreshold - 1) + "...";
//            }
            if (noidung.length() > noidungLengthThreshold) {
                String matchingNoidung = populateMatchingKeyword(noidung);
                if (matchingNoidung.length() > noidungLengthThreshold) {
                    noidung = matchingNoidung.substring(0, noidungLengthThreshold - 1) + "...";
                } else {
                    noidung = matchingNoidung;
                }
            }
        } else {
            vHolder.hideLblVanban(true);
            vHolder.hideBtnBreadscrubs(true);
        }

        vHolder.setLblNoidung(noidung);
        vHolder.setDieukhoanId(String.valueOf(dk.getId()));
//        vHolder.fitItemDetails();
    }

    @Override
    public int getItemCount() {
        return allDieukhoan.size();
    }

    private String populateMatchingKeyword(String noidung) {
        if (keyword.length() > 0) {
            String[] slicedKeyword = keyword.split(" ");
            int matchingLength = slicedKeyword.length;

            while (matchingLength > 0) {
                String[][] chunks = chunkArray(slicedKeyword, matchingLength);
                String matchingKeyword = TextUtils.join(" ", chunks[0]);

                if (noidung.toLowerCase().contains(matchingKeyword.toLowerCase())) {
                    String[] slicedNoidung = noidung.toLowerCase().replace(matchingKeyword.toLowerCase(), "|").split("\\|");
                    if (slicedNoidung.length > 1) {
                        if (slicedNoidung[0].length() > matchingPrefixThreshold) {
                            return "..." + noidung.substring(slicedNoidung[0].length() - matchingPrefixThreshold, noidung.length() - 1);
                        } else {
                            return noidung.substring(slicedNoidung[0].length(), noidung.length() - 1);
                        }
                    } else {
                        return noidung;
                    }
                }
                matchingLength -= 1;
            }
        }
        return noidung;
    }

    //split an array into chunks
    private String[][] chunkArray(String[] array, int chunkSize) {
        int chunkedSize = (int) Math.ceil((double) array.length / chunkSize); // chunked array size
        String[][] chunked = new String[chunkedSize][chunkSize];
        for (int index = 0; index < chunkedSize; index++) {
            String[] chunk = new String[chunkSize]; // small array
            System.arraycopy(array, index * chunkSize, chunk, 0, Math.min(chunkSize, array.length - index * chunkSize));
            chunked[index] = chunk;
        }
        return chunked;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgView;
        private LinearLayout item;
        private LinearLayout itemDetails;
        private TextView lblVanban;
        private TextView lblDieukhoan;
        private TextView lblNoidung;
        private Button btnBreadscrubs;
        private String dieukhoanId = "";

        public ViewHolder(View v) {

            super(v);
            imgView = v.findViewById(R.id.item).findViewById(R.id.imageView);
            item = v.findViewById(R.id.item);
            itemDetails = v.findViewById(R.id.itemDetails);
            lblVanban = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblVanban);
            lblDieukhoan = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.lblDieukhoan);
            btnBreadscrubs = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.breadscrubsView).findViewById(R.id.btnBreadscrubs);
            lblNoidung = v.findViewById(R.id.item).findViewById(R.id.itemDetails).findViewById(R.id.lblNoidung);
            v.setOnClickListener(this);
        }

        public void hideMinhhoa() {
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = 0;
            parms.height = 0;
            imgView.setLayoutParams(parms);
        }

        public void showMinhhoa() {
            ViewGroup.LayoutParams parms = imgView.getLayoutParams();
            parms.width = (int) (uHelper.getScreenWidth() * 0.2);
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

        public void hideLblVanban(Boolean isHidden) {
            if (isHidden) {
                lblVanban.setVisibility(View.GONE);
            } else {
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
            String breadscrubs = search.getAncestersNumber(parent, vanbanid);
            this.btnBreadscrubs.setText(breadscrubs);
        }

        public void hideBtnBreadscrubs(Boolean isHidden) {
            if (isHidden) {
                btnBreadscrubs.setVisibility(View.GONE);
            } else {
                btnBreadscrubs.setVisibility(View.VISIBLE);
            }
        }

        public void setDieukhoanId(String id) {
            dieukhoanId = id.trim();
        }

        //this workaround for fitting itemDetail does not work as expected. Layout param width return 0 for image and -1 for item (parent view).
        // This leads to incorrect value set to itemDetail width
        public void fitItemDetails() {
            ViewGroup.LayoutParams params = itemDetails.getLayoutParams();
            int iw = item.getLayoutParams().width;
            int imw = imgView.getLayoutParams().width;
            params.width = item.getLayoutParams().width - imgView.getLayoutParams().width;
            itemDetails.setLayoutParams(params);
        }

        @Override
        public void onClick(View v) {
            Log.i("Message", "tapping on: " + dieukhoanId);
            Intent i = new Intent(context.getApplicationContext(), DetailsActivity.class);
            //TODO: need to change the hardcode dieukhoanId to something that configurable.
            i.putExtra("dieukhoanId", dieukhoanId);
            context.startActivity(i);
        }
    }
}
