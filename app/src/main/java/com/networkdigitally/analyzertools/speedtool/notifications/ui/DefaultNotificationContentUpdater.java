package com.networkdigitally.analyzertools.speedtool.notifications.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.networkdigitally.analyzertools.speedtool.R;
import com.networkdigitally.analyzertools.speedtool.models.WeatherPresentation;
import com.networkdigitally.analyzertools.speedtool.utils.formatters.WeatherFormatter;


/**
 * Update notification content for default notification view.
 */
public class DefaultNotificationContentUpdater extends NotificationContentUpdater {
    private final WeatherFormatter formatter;

    public DefaultNotificationContentUpdater(@NonNull WeatherFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public boolean isLayoutCustom() {
        return false;
    }

    @Override
    public void updateNotification(@NonNull WeatherPresentation weatherPresentation,
                                   @NonNull NotificationCompat.Builder notification,
                                   @NonNull Context context
    ) throws NullPointerException {
        //noinspection ConstantConditions
        if (weatherPresentation == null)
            throw new NullPointerException("weatherPresentation is null");
        //noinspection ConstantConditions
        if (notification == null)
            throw new NullPointerException("notification is null");
        //noinspection ConstantConditions
        if (context == null)
            throw new NullPointerException("context is null");

        super.updateNotification(weatherPresentation, notification, context);

        notification
                .setCustomContentView(null)
                .setContent(null)
                .setCustomBigContentView(null)
                .setColorized(false)
                .setColor(NotificationCompat.COLOR_DEFAULT);

        if (formatter.isEnoughValidData(weatherPresentation.getWeather())) {
            String temperature = formatter.getTemperature(weatherPresentation.getWeather(),
                    weatherPresentation.getTemperatureUnits(),
                    weatherPresentation.isRoundedTemperature());
            notification
                    .setContentTitle(temperature)
                    .setContentText(formatter.getDescription(weatherPresentation.getWeather()))
                    .setLargeIcon(formatter.getWeatherIconAsBitmap(weatherPresentation.getWeather(), context));
        } else {
            notification.setContentTitle(context.getString(R.string.no_data))
                    .setContentText(null)
                    .setLargeIcon(null);
        }
    }
}