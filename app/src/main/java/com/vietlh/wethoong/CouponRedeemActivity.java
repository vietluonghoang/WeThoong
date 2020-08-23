package com.vietlh.wethoong;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.networking.DeviceInfoCollector;
import com.vietlh.wethoong.networking.MessageContainer;
import com.vietlh.wethoong.networking.NetworkHandler;
import com.vietlh.wethoong.utils.DBConnection;
import com.vietlh.wethoong.utils.GeneralSettings;
import com.vietlh.wethoong.utils.Queries;

import java.util.HashMap;

public class CouponRedeemActivity extends AppCompatActivity implements CallbackActivity {

    private TextView lblState;
    private EditText txtCouponCode;
    private Button btnRedeem;
    private NetworkHandler net;
    private Queries queries = new Queries(DBConnection.getInstance(this));
    private HashMap<String, String> deviceInfo = new HashMap<>();

    //actionCases
    private final String ACTION_CASE_UPDATE_STATE = "updateState";
    private final String ACTION_CASE_REDEEM_CODE = "redeemCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_redeem);
        initComponent();
    }

    private void initComponent() {
        txtCouponCode = (EditText) findViewById(R.id.txtCoupon);
        txtCouponCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateStatusMessage(2);
            }
        });
        lblState = (TextView) findViewById(R.id.lblState);
        btnRedeem = (Button) findViewById(R.id.btnRedeem);
        btnRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitCode();
                updateStatusMessage(0);
                enableConfirmButton(false);
            }
        });
    }

    private void enableConfirmButton(Boolean enable) {
        if (enable) {
            btnRedeem.setEnabled(true);
        } else {
            btnRedeem.setEnabled(false);
        }
    }

    private void updateStatusMessage(int state) {
        switch (state) {
            case 0:
                lblState.setText("Đang xử lý....");
                lblState.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case 1:
                lblState.setText("Mã được xác nhận thành công!");
                lblState.setTextColor(getResources().getColor(R.color.blue));
                break;
            case -1:
                lblState.setText("Mã không đúng. Hãy kiểm tra và thử lại sau!");
                lblState.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            default:
                lblState.setText("");
        }
    }

    private void submitCode() {
        System.out.println("############ Redeeming coupon now. Checking....");
        String couponCode = txtCouponCode.getText().toString().toLowerCase().trim();
        deviceInfo.put("couponCode", couponCode);
        new DeviceInfoCollector(this, getApplicationContext(), ACTION_CASE_REDEEM_CODE).execute(deviceInfo);
    }

    private void checkCodeState() {
        System.out.println("############# check code message");
        try {
            HashMap<String, String> message = (HashMap<String, String>) net.getMessages().getValue(MessageContainer.DATA);
            System.out.println("message: " + message);

            if ("Success".equals(message.get("status"))) {
                HashMap<String, String> config = new HashMap<>();
                config.put(GeneralSettings.APP_CONFIG_KEY_ADSOPTOUT, "1");
                GeneralSettings.isAdsOptout = queries.updateAppConfigsToDatabase(config);
                updateStatusMessage(1);
            } else {
                updateStatusMessage(-1);
            }
            enableConfirmButton(true);

        } catch (Exception e) {
            System.out.println("Fail to checkCodeState: " + e.getMessage());
        }
    }

    @Override
    public void triggerCallbackAction(String actionCase) {
        switch (actionCase) {
            case ACTION_CASE_REDEEM_CODE:
                System.out.println("########### Sending coupon code.....");
                String redeemCouponUrl = "https://wethoong-server.herokuapp.com/redeemcoupon";
                net = new NetworkHandler(this, redeemCouponUrl, NetworkHandler.METHOD_POST, NetworkHandler.CONTENT_TYPE_APPLICATION_JSON, NetworkHandler.MIME_TYPE_APPLICATION_JSON, deviceInfo, ACTION_CASE_UPDATE_STATE);
                net.execute();
                break;
            case ACTION_CASE_UPDATE_STATE:
                net.parseResultStatusData();
                checkCodeState();
                break;
            default:
                System.out.println("++++ no valid action found ++++");
        }

    }
}
