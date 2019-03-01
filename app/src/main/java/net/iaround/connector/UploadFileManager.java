package net.iaround.connector;

import android.os.AsyncTask;
import android.text.TextUtils;

import net.iaround.tools.CommonFunction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Class: 上传图片
 * Author：gh
 * Date: 2016/12/14 20:17
 * Email：jt_gaohang@163.com
 */
public class UploadFileManager {

    private static UploadFileManager instance = null;

    private UploadFileManager() {

    }

    public static UploadFileManager getInstance() {
        if (instance == null) {
            instance = new UploadFileManager();
        }
        return instance;
    }

    /**
     * 上传图片
     *
     * @param url
     * @param params
     * @param files
     * @param type
     * @param fileName
     */
    public void requestHttpPost(String url, Map<String, String> params, Map<String, File> files, String type, String fileName, UploadImageCallback callback) {
        UploadImageTask uploadTask = new UploadImageTask(url, params, files, type, fileName, callback);
        uploadTask.execute();
    }

    protected class UploadImageTask extends AsyncTask<Void, Integer, String> {
        Map<String, File> fileMap;
        Map<String, String> params;
        String uploadUrl;
        String fileType;
        String fileName;
        UploadImageCallback callback;

        public UploadImageTask(String url, Map<String, String> params, Map<String, File> files, String type,
                               String fileName, UploadImageCallback callback) {
            this.fileMap = files;
            this.params = params;
            this.uploadUrl = url;
            this.fileType = type;
            this.fileName = fileName;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            String response = "";
            try {
                response = requestHttpPostMultipart(uploadUrl, params, fileMap, fileType, fileName);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            if (TextUtils.isEmpty(result)) {
                callback.onUploadFileError("");
                CommonFunction.log("onPostExecute error.....");
            } else {
                callback.onUploadFileFinish(result);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    public static String requestHttpPostMultipart(String actionUrl, Map<String, String> params,
                                                  Map<String, File> files, String type, String fileName) throws IOException {
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
        }
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(sb.toString().getBytes());
        if (files != null) {
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + file.getKey() + "\""
                        + LINEND);
                sb1.append("Content-Type: " + type + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                is.close();
                outStream.write(LINEND.getBytes());
            }
        }
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        boolean success = conn.getResponseCode() == 200;
        if (success) {
            InputStream in = conn.getInputStream();
            InputStreamReader isReader = new InputStreamReader(in);
            BufferedReader bufReader = new BufferedReader(isReader);
            String line = null;
            String data = "";
            while ((line = bufReader.readLine()) != null) {
                data += line;
            }
            outStream.close();
            conn.disconnect();

            CommonFunction.log("upload ok....");
            return data;
        } else {
            CommonFunction.log("upload error....");
        }
        return "";
    }
}
