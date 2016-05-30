package com.uestc.express.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Hashtable;

/**
 * Created by Tobb_Huang on 16/4/12.
 */
public class Utils {

    public static int QRCODE_SIZE = 200;
    public static String CACHE_PATH = Environment.getExternalStorageDirectory() + "/uestc_express/";

    public static PrivateKey getPrivateKey(String filename) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    /**
     * 生成一个二维码图像
     *
     * @param str            传入的字符串
     * @param widthAndHeight 图像的宽高
     * @return
     */
    public static Bitmap createQRCode(String str, int widthAndHeight)
            throws WriterException {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = Color.BLACK;
                }else{
                    pixels[y * width + x] = Color.WHITE;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public static String unicode2utf8(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File saveErrorLog(byte[] data){
        String html=new String(data);
        File f=new File(Environment.getExternalStorageDirectory()+"/log.txt");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f, false));
            bos.write(html.getBytes(Charset.forName("UTF-8")));
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static boolean saveBitmap(Bitmap bm, String filename) {
        try {
            File dir=new File(CACHE_PATH);
            if(!dir.exists()){
                dir.mkdir();
            }

            File f = new File(CACHE_PATH + filename + ".png");
            if(f.exists()){
                f.delete();
            }

            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
