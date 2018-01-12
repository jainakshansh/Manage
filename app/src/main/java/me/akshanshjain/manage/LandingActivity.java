package me.akshanshjain.manage;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class LandingActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private TextView overViewText, overviewAmount;
    private Button viewAllCards;
    private TextView monthlyText, monthlyAmount;
    private TextView incomeHeader, incomeAmount, expenseHeader, expenseAmount;

    private Typeface quicksand_bold, quicksand_medium;

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //Adding the fonts to the activity from the assets.
        quicksand_bold = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Bold.ttf");
        quicksand_medium = Typeface.createFromAsset(getAssets(), "fonts/Quicksand_Medium.ttf");

        //Referencing views from the XML and styling them in the layout.
        fab = findViewById(R.id.fab_landing);
        overViewText = findViewById(R.id.overview_text);
        overViewText.setTypeface(quicksand_bold);
        overviewAmount = findViewById(R.id.overview_amount);
        overviewAmount.setTypeface(quicksand_bold);

        viewAllCards = findViewById(R.id.view_all_cards);
        viewAllCards.setTypeface(quicksand_bold);

        monthlyText = findViewById(R.id.monthly_balance_text);
        monthlyText.setTypeface(quicksand_medium);
        monthlyAmount = findViewById(R.id.monthly_balance_amount);
        monthlyAmount.setTypeface(quicksand_bold);

        incomeHeader = findViewById(R.id.income_text_header);
        incomeHeader.setTypeface(quicksand_medium);
        incomeAmount = findViewById(R.id.income_amount);
        incomeAmount.setTypeface(quicksand_bold);
        expenseHeader = findViewById(R.id.expense_text_header);
        expenseHeader.setTypeface(quicksand_medium);
        expenseAmount = findViewById(R.id.expense_amount);
        expenseAmount.setTypeface(quicksand_bold);

        lineChart = findViewById(R.id.line_chart_landing);
        lineChart.setViewPortOffsets(0, 0, 0, 0);
        lineChart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.pictonBlue));
        //No description text required.
        lineChart.getDescription().setEnabled(false);
        //Enabling Touch Gestures.
        lineChart.setTouchEnabled(true);
        //Enabling Scaling and Dragging.
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        setData(100,500);
    }

    private void setData(int count, float range) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(i, val));
        }

        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(3.0f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTypeface(quicksand_bold);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            lineChart.setData(data);
        }
    }
}