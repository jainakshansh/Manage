package me.akshanshjain.manage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Akshansh on 17-01-2018.
 */

public class RateDialog extends Dialog {

    private Button negative, positive;
    private Context mContext;
    private TextView text, description;
    private Typeface quicksand_bold;

    public RateDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rate_dialog);

        negative = findViewById(R.id.neg_button_rate);
        positive = findViewById(R.id.pos_button_rate_button_rate);
        text = findViewById(R.id.thankyou_text);
        description = findViewById(R.id.thankyou_desc);
    }
}
