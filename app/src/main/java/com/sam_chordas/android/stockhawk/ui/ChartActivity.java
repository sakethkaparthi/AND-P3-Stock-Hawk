package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

public class ChartActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ChartActivity.class.getSimpleName();
    private String quoteName;
    private int CURSOR_LOADER_ID = 0;
    private Context mContext;
    private Cursor mCursor;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription("Past stock quote data");
        chart.setDescriptionTextSize(10f);
        chart.setDescriptionColor(ContextCompat.getColor(this, R.color.gold));
        chart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.gold));
        quoteName = getIntent().getStringExtra("symbol");
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ? AND " + QuoteColumns.SYMBOL + " = ?",
                new String[]{"0", quoteName},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        ArrayList<Entry> vals = new ArrayList<Entry>();
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                Log.d(TAG, mCursor.getString(1) + "- " + mCursor.getString(2));
                vals.add(new Entry(Float.parseFloat(mCursor.getString(2)), 6 - i));
                i++;
            } while (i < 7 && mCursor.moveToNext());
        }
        LineDataSet dataset = new LineDataSet(vals, quoteName);
        dataset.setLineWidth(2f);
        dataset.setCircleRadius(4f);
        dataset.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataset);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        xVals.add("");
        LineData ldata = new LineData(xVals, dataSets);
        ldata.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gold));
        ldata.setValueTextSize(10f);
        chart.setData(ldata);
        chart.invalidate();
        mCursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}


