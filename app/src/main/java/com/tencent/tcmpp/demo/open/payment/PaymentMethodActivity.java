package com.tencent.tcmpp.demo.open.payment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.tcmpp.demo.R;


public class PaymentMethodActivity extends AppCompatActivity {
    private PaymentListFragment mPayFragment;
    private BindCardFragment mBindCardFragment;
    private TextView titleTextView;
    private double totalFee;
    private String rawData;
    private boolean isShowList = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(
                Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        int vis = getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        getWindow().getDecorView().setSystemUiVisibility(vis);
        setContentView(R.layout.activity_payment_method);
        totalFee = getIntent().getDoubleExtra("totalFee", 88.0);
        rawData = getIntent().getStringExtra("rawData");
        initToolbarClick();
        initChildFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initToolbarClick() {
        titleTextView = findViewById(R.id.tv_pay_title_info);
        titleTextView.setText(R.string.tcmpp_payment_choose);
        findViewById(R.id.iv_pay_back).setOnClickListener(v -> {
            if (isShowList) {
                PaymentManager.g().notifyPaymentCancel();
                finish();
            } else {
                showPayList();
                isShowList = true;
            }
        });
    }

    private void initChildFragment() {
        mPayFragment = new PaymentListFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("total", totalFee);
        bundle.putString("rawData", rawData);
        mPayFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container_pay_method, mPayFragment, "pay_list")
                .commit();
    }

    public void showBindCard() {
        if (null == mBindCardFragment) {
            mBindCardFragment = new BindCardFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_container_pay_method, mBindCardFragment, "bind_card")
                    .commitAllowingStateLoss();
        }
        titleTextView.setText(R.string.tcmpp_payment_add_card);
        getSupportFragmentManager().beginTransaction()
                .hide(mPayFragment)
                .show(mBindCardFragment)
                .commitAllowingStateLoss();
        isShowList = false;
    }

    public void showPayList() {
        titleTextView.setText(R.string.tcmpp_payment_choose);

        getSupportFragmentManager().beginTransaction()
                .hide(mBindCardFragment)
                .show(mPayFragment).commitAllowingStateLoss();
        isShowList = true;
    }

    @Override
    public void onBackPressed() {
        if (isShowList) {
            PaymentManager.g().notifyPaymentCancel();
        }
    }
}
