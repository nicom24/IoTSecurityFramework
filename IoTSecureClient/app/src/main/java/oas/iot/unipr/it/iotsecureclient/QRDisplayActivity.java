package oas.iot.unipr.it.iotsecureclient;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import oas.iot.unipr.it.iotsecureclient.Model.InfoManager;


public class QRDisplayActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);
        //Get infos to include in QRcode
        String token = InfoManager.getInstance().getMyInfo().getToken().getToken();
        //Generate QRcode and display
        int qrCodeDimension = 500;
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bm;
        try{
            bm = writer.encode(token, BarcodeFormat.QR_CODE,qrCodeDimension,qrCodeDimension);
            Bitmap bitmap = toBitmap(bm);
            ((ImageView)findViewById(R.id.qr_code_image)).setImageBitmap(bitmap);
        }catch(WriterException e){
            e.printStackTrace();
            finish();
        }

    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     * @param matrix the matrix to write.
     * @return the new object.
     */
    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

}
