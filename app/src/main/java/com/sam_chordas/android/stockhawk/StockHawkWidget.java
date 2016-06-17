package com.sam_chordas.android.stockhawk;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StockHawkWidget extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "item_pos";
    public static final String ACTION_ACT = "activity";
    private static final String TAG = "widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Toast.makeText(context, "Updating", Toast.LENGTH_SHORT).show();
        final int count = appWidgetIds.length;

        Log.i(TAG, "onUpdate: count: " + count);

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            Log.i(TAG, "onUpdate: widgetId: " + widgetId);

            Intent svcIntent = new Intent(context, StocksViewService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.stock_hawk_widget);

//            remoteViews.setRemoteAdapter(widgetId, R.id.listView, svcIntent);
            remoteViews.setRemoteAdapter(R.id.stocks_list, svcIntent);

            // START
            Intent intent = new Intent(context, StockHawkWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_item, pendingIntent);
            // END

            final Intent onItemClick = new Intent(context, StockHawkWidget.class);
            onItemClick.setAction("stock_click");
            onItemClick.setData(Uri.parse(onItemClick
                    .toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 0, onItemClick,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.stocks_list,
                    onClickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.stocks_list);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("stock_click")) {
            Intent intentToLaunch = new Intent(context, MyStocksActivity.class);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentToLaunch);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("widget", "onEnabled: ");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

