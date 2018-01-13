package me.akshanshjain.manage.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import me.akshanshjain.manage.R;

/**
 * Created by Akshansh on 13-01-2018.
 */

public class CategorySpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> categoryList;
    private Typeface quicksand_medium;

    public CategorySpinnerAdapter(Context context, List<String> categoryList) {
        super(context, 0, categoryList);
        this.context = context;
        this.categoryList = categoryList;
        quicksand_medium = Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand_Medium.ttf");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String category = categoryList.get(position);

        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
        }

        TextView spinnerText = view.findViewById(R.id.spinner_text_category);
        spinnerText.setTypeface(quicksand_medium);
        spinnerText.setText(category);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView dropDownText = (TextView) super.getDropDownView(position, convertView, parent);
        dropDownText.setTypeface(quicksand_medium);
        return dropDownText;
    }
}
