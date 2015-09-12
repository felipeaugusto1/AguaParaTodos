package felipe.com.br.aguaparatodos.gcm;

import felipe.com.br.aguaparatodos.R;
import felipe.com.br.aguaparatodos.activities.DetalheOcorrencia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationCustomUtil {
    private static NotificationManager mNotificationManager;

    public static void sendNotification(Context context, String title,
                                        String ocorrenciaId, String message) {
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent telaDetalheOcorrencia = new Intent(context, DetalheOcorrencia.class);

        telaDetalheOcorrencia.setAction(Long.toString(System.currentTimeMillis()));

        telaDetalheOcorrencia.putExtra("ocorrencia_id", ocorrenciaId);
        telaDetalheOcorrencia.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                telaDetalheOcorrencia, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(R.drawable.com_facebook_button_icon)
                .setSound(
                        RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle("Nova ocorrência de água parada: ").setContentText(message);

        mBuilder.setContentIntent(contentIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(AndroidSystemUtil.randInt(), notification);
    }
}
