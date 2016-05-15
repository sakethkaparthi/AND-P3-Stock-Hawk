package com.sam_chordas.android.stockhawk.ui;

import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.QuoteCursorAdapter;

public class ChartActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ChartActivity.class.getSimpleName();
    private String quoteName;
    private Tooltip mTip;
    private LineChartView mChart;
    private final String[] mLabels = {"Jan", "Fev", "Mar", "Apr", "Jun", "May", "Jul", "Aug", "Sep"};
    private float[] mValues = {35f, 47f, 43f, 8f, 65f, 100f, 7f, 83f, 70f};
    private int CURSOR_LOADER_ID = 0;
    private QuoteCursorAdapter mCursorAdapter;
    private Context mContext;
    private Cursor mCursor;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        mChart = (LineChartView) findViewById(R.id.linechart);
        recyclerView = (RecyclerView) findViewById(R.id.old_stocks_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContext = this;
        mTip = new Tooltip(mContext, R.layout.linechart_three_tooltip, R.id.value);

/*        ((TextView) mTip.findViewById(R.id.value))
                .setTypeface(Typeface.createFromAsset(mContext.getAssets(), "OpenSans-Semibold.ttf"));*/

        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(65), (int) Tools.fromDpToPx(25));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
            LineSet dataset = new LineSet(mLabels, mValues);
            dataset.setColor(Color.parseColor("#758cbb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#758cbb"))
                    .setThickness(4)
                    .setDashed(new float[]{10f, 10f})
                    .beginAt(5);
            mChart.addData(dataset);

            dataset = new LineSet(mLabels, mValues);
            dataset.setColor(Color.parseColor("#b3b5bb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#ffc755"))
                    .setThickness(4)
                    .endAt(6);
            mChart.addData(dataset);
            mChart.setBorderSpacing(Tools.fromDpToPx(15))
                    .setAxisBorderValues(0, 200)
                    .setYLabels(AxisController.LabelPosition.NONE)
                    .setLabelsColor(Color.parseColor("#6a84c3"))
                    .setXAxis(false)
                    .setYAxis(false);
            mChart.setOnEntryClickListener(new OnEntryClickListener() {
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {
                    mChart.removeAllViews();
                    mTip.prepare(mChart.getEntriesArea(0).get(entryIndex), mValues[entryIndex]);
                    mChart.showTooltip(mTip, true);
                }
            });
            Runnable chartAction = new Runnable() {
                @Override
                public void run() {
                    mTip.prepare(mChart.getEntriesArea(0).get(3), mValues[3]);
                    mChart.showTooltip(mTip, true);
                }
            };

            Animation anim = new Animation()
                    .setEasing(new BounceEase())
                    .setEndAction(chartAction);

            mChart.show(anim);
            /*ChartTask task = new ChartTask();
            task.execute(quoteName);*/
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            /*mCursorAdapter = new QuoteCursorAdapter(this,null);
            recyclerView.setAdapter(mCursorAdapter);*/
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"0"},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mCursorAdapter.swapCursor(data);
        mCursor = data;
        if (mCursor.moveToFirst()) {
            do {

                // do what ever you want here
                Log.d(TAG, mCursor.getString(1) + "- " + mCursor.getString(2));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
  /*  class ChartTask extends AsyncTask<String, Void, ArrayList<Integer>> {
        String LOG_TAG = ChartTask.class.getSimpleName();

        @Override
        protected ArrayList<Integer> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {
                final String STOCK_BASE_URL =
                        "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20%3D%20%22" + params[0] + "%22%20and%20startDate%20%3D%20%222016-03-23%22%20and%20endDate%20%3D%20%222016-04-02%22&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

                Uri builtUri = Uri.parse(STOCK_BASE_URL).buildUpon()
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to Google web Matrix, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getStockDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private ArrayList<Integer> getStockDataFromJson(String forecastJsonStr) throws JSONException {
            ArrayList<Integer> values = new ArrayList<>();
            JSONObject data = new JSONObject(forecastJsonStr);
            JSONObject query = data.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONArray quote = results.getJSONArray("quote");
            for (int i = 0; i < quote.length(); i++) {
                JSONObject item = quote.getJSONObject(i);
                values.add(Integer.valueOf(item.getString("Close")));
            }
            Collections.reverse(values);
            return values;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> arrayList) {
            super.onPostExecute(arrayList);
            //mValues = arrayList.toArray();
        }
    }*/

}


