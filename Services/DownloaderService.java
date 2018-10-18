package android.lovefantasy.mlscproxy.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.Tools.T;
import android.lovefantasy.mlscproxy.UI.MainActivity;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloaderService extends Service {

    private DownloadTask downloadTask = null;
    private String downloadUrl = null;
    private DownloadBinder mBinder = new DownloadBinder();
    private onSucceed mSucceed = null;
    private T toast = new T();

    public interface onSucceed {
        void succeed(String path);
    }



   public class DownloadBinder extends Binder {
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask = new DownloadTask();
                downloadTask.setDownloadListener(new DownloadTask.DownloadListener() {
                    @Override
                    public void onProgress(int progress) {
                        getNotificationManager().notify(2, getNotification("CProxy正在下载中...", progress));
                    }

                    @Override
                    public void onSuccess(String path) {
                        downloadTask = null;
                        stopForeground(true);
                        getNotificationManager().notify(2, getNotification(path, 100));
                        toast.makeText("下载核心成功", Toast.LENGTH_SHORT);
                        mSucceed.succeed(path);
                        // stopSelf();
                    }

                    @Override
                    public void onFailed() {
                        downloadTask = null;
                        stopForeground(true);
                        getNotificationManager().notify(2, getNotification("下载失败", -1));
                        toast.makeText("下载核心失败", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onPaused() {
                        downloadTask = null;
                        stopForeground(true);
                        getNotificationManager().notify(2, getNotification("下载暂停", -1));
                        toast.makeText("下载暂停", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onCanceled() {
                        downloadTask = null;
                        stopForeground(true);
                        getNotificationManager().notify(2, getNotification("下载取消", -1));
                        toast.makeText("下载取消", Toast.LENGTH_SHORT);
                    }
                });
                downloadTask.execute(downloadUrl);
                startForeground(2, getNotification("准备下载...", 0));
                toast.makeText("下载开始", Toast.LENGTH_SHORT);
            }
        }
        public void setOnsucceed(onSucceed succeed) {
            mSucceed = succeed;
        }
    }

    public void pauseDownload() {
        if (downloadTask != null) {
            downloadTask.pauseDownload();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        /* if (downloadTask != null) {
            downloadTask.cancelDownload();
            downloadTask.cancel(true);
            downloadTask = null;
        }
        if (mBinder != null)
            mBinder = null;*/
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        if (mBinder == null | downloadTask == null) {
            mBinder = new DownloadBinder();
        }

    }

    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancelDownload();

        } else {
            if (downloadUrl != null) {
                String filename = downloadUrl.substring(downloadUrl.lastIndexOf("/")) + String.valueOf(System.currentTimeMillis());
                String directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CProxy";
                File file = new File(directory + filename);
                if (file.exists()) {
                    file.delete();
                }
                getNotificationManager().cancel(2);
                stopForeground(true);
                toast.makeText("下载取消", Toast.LENGTH_SHORT);
            }
        }
    }

    public DownloaderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        if (progress >= 0) {
            builder.setContentTitle(title);
            builder.setContentText(String.valueOf(progress) + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }


}

class DownloadTask extends AsyncTask<String, Integer, Integer> {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_FAILED = 1;
    public static final int TYPE_PAUSED = 2;
    public static final int TYPE_CANCELED = 3;

    private DownloadListener listener;
    private boolean isCanceled = false;
    private boolean isPaused = false;
    private int lastProgress = -1;
    String filename;
    String directory;
    String time;

    public interface DownloadListener {
        void onProgress(int progress);

        void onSuccess(String path);

        void onFailed();

        void onPaused();

        void onCanceled();
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        listener = downloadListener;

    }

    public DownloadTask() {

    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file = null;
        try {
            long downloadedLength = 0;
            String downloadURL = params[0];
            filename = downloadURL.substring(downloadURL.lastIndexOf("/"));
            directory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CProxy";
            time = String.valueOf(System.currentTimeMillis());
            file = new File(directory);
            if (!file.exists())
                file.mkdirs();
            L.e("directory", directory);
            file = new File(directory  + filename + time);
            if (file.exists())
                file.delete();
            long contentLength = getContentLength(downloadURL);
            if (contentLength == 0)
                return TYPE_FAILED;
            else if (contentLength == downloadedLength) {
                return TYPE_SUCCESS;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(downloadURL).addHeader("RANGE", "bytes=" + downloadedLength + "-").build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedLength);
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                while ((len = is.read(b)) != -1) {
                    if (isCancelled()) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_FAILED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savedFile != null) {
                    savedFile.close();

                }
                if (isCancelled() && file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess(directory  + filename + time);
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            default:
                break;
        }

    }

    public void pauseDownload() {
        isPaused = true;
    }

    public void cancelDownload() {
        isCanceled = true;
    }

    private long getContentLength(String downloadurl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadurl).build();
        Response response = client.newCall(request).execute();
        if (response != null && response.isSuccessful()) {
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }
}
