package me.akshanshjain.manage.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import me.akshanshjain.manage.R;

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

        quicksand_bold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand_Bold.ttf");
        negative.setTypeface(quicksand_bold);
        positive.setTypeface(quicksand_bold);
        text.setTypeface(quicksand_bold);
        description.setTypeface(quicksand_bold);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(9);
                dismiss();
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=me.akshanshjain.manage")));
                dismiss();
            }
        });
    }
}
