package felipe.com.br.aguaparatodos.crashlistener;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(formKey = "", mode = ReportingInteractionMode.SILENT)
public class ApplicationCrashListener extends Application {

    @Override
    public void onCreate() {
        ACRA.init(this);
        CrashSender crashSender = new CrashSender();
        ACRA.getErrorReporter().setReportSender(crashSender);
        super.onCreate();
    }
}
