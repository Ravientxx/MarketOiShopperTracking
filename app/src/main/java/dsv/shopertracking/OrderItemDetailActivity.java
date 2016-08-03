package dsv.shopertracking;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by hoang on 8/3/16.
 */
public class OrderItemDetailActivity extends Activity {
    TextView itemName;
    TextView itemChain;
    TextView itemContainer;
    TextView itemQty;
    TextView itemAmount;
    TextView itemPrice;
    ImageView itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_detail_fullview);

        itemName = (TextView) findViewById(R.id.txtName);
        itemChain = (TextView) findViewById(R.id.txtChain);
        itemContainer = (TextView) findViewById(R.id.txtContainer);
        itemQty = (TextView) findViewById(R.id.txtQuantity);
        itemAmount = (TextView) findViewById(R.id.txtAmount);
        itemPrice = (TextView) findViewById(R.id.txtPrice);
        itemImage = (ImageView) findViewById(R.id.itemImage);


        itemName.setText(OrderDetailActivity.currentItem.name);
        itemChain.setText(OrderDetailActivity.currentItem.chainName);
        itemContainer.setText(OrderDetailActivity.currentItem.brand);
        itemQty.setText(OrderDetailActivity.currentItem.qty);
        itemAmount.setText(OrderDetailActivity.currentItem.amount);
        itemPrice.setText(OrderDetailActivity.currentItem.unitPrice);
        itemImage.setImageBitmap(OrderDetailActivity.currentItem.imageProduct);
        //System.out.print(OrderDetailActivity.currentItem.imageProduct);
        //new loadImageTask(itemImage).execute(OrderDetailActivity.currentItem.iconPath);

    }

    /*public class loadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView image;
        loadImageTask(ImageView img){
            image = img;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap img = null;
            try{
                InputStream in = new URL(url).openStream();
                img = BitmapFactory.decodeStream(in);
            }catch (Exception e){
                e.printStackTrace();
            }
            return img;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            this.image.setImageBitmap(image);
        }
    }*/
}
