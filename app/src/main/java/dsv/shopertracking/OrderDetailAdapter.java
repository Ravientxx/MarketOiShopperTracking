package dsv.shopertracking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoang on 7/24/16.
 */
public class OrderDetailAdapter extends BaseAdapter {
    public Context contextListView;
    private String TAG_ORDER_ID = "";
    public static Handler handler = new Handler();
    public static OrderDetailChapter chapter;
    ArrayList<Bitmap> imageList;
    List<OrderDetailChapter> order_detail_chapter;
    JSONObject chapterInfo;
    String name;
    String brand;
    String iconPath;
    String qty;
    String unitPrice;
    String chainName;
    String shopName;
    String amount;
    String lat;
    String lon;
    //Boolean bought;

    public List<OrderDetailChapter> get_data_list(JSONObject orderListObj) {
        List<OrderDetailChapter> orderList = new ArrayList<OrderDetailChapter>();
        JSONArray orderListArray = null;
        try{
            orderListArray = orderListObj.getJSONArray("result");
        }catch (Exception e){
            e.printStackTrace();
        }
        int i = 0;
        int leng = orderListArray.length();
        while (i < leng) {
            try {
                chapterInfo = orderListArray.getJSONObject(i);
                name = chapterInfo.getString("name");
                brand = chapterInfo.getString("brand");
                iconPath = chapterInfo.getString("icon_path");
                qty = chapterInfo.getString("qty");
                unitPrice = chapterInfo.getString("unit_price");
                chainName = chapterInfo.getString("chain_name");
                shopName = chapterInfo.getString("shop_name");
                amount = chapterInfo.getString("amount") + chapterInfo.getString("unit");
                lat = chapterInfo.getString("gps_lat");
                lon = chapterInfo.getString("gps_lon");
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chapter = new OrderDetailChapter();
            chapter.name = name;
            chapter.brand = brand;
            chapter.iconPath = iconPath;
            chapter.qty = qty;
            chapter.amount = amount;
            chapter.unitPrice = unitPrice;
            chapter.chainName = chainName;
            chapter.shopName = shopName;
            chapter.lat = lat;
            chapter.lon = lon;
            Boolean check = true;
            for (int j = 0; j <orderList.size() ; j++) {
                if (orderList.get(j).name.equals(name) && orderList.get(j).brand.equals(brand) &&orderList.get(j).qty.equals(qty)){
                    check = false;
                    break;
                }
            }
            if (check) {
                orderList.add(chapter);
            }
        }
        imageList = new ArrayList<Bitmap>();
        return orderList;

    }

    OrderDetailAdapter(JSONObject oderList, Context context) {
        contextListView = context;
        order_detail_chapter = get_data_list(oderList);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return order_detail_chapter.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return order_detail_chapter.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        chapter = order_detail_chapter.get(arg0);

        if (arg1 == null) {
            LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.order_detail_view, arg2, false);
        }
        ImageView chapterImage = (ImageView) arg1.findViewById(R.id.image);
        TextView chapterName = (TextView) arg1.findViewById(R.id.textViewName);
        TextView chapterBrand = (TextView) arg1.findViewById(R.id.textViewBrand);
        TextView chapterChain = (TextView) arg1.findViewById(R.id.textViewChain);
        TextView chapterPrice = (TextView) arg1.findViewById(R.id.textViewPrice);
        TextView chapterQty = (TextView) arg1.findViewById(R.id.textViewQty);
        TextView chapterAmount = (TextView) arg1.findViewById(R.id.textViewAmount);

        chapterName.setText(chapter.name);
        chapterBrand.setText(chapter.brand);
        chapterChain.setText(chapter.chainName);
        chapterPrice.setText(chapter.unitPrice);
        chapterQty.setText(chapter.qty);
        chapterAmount.setText(chapter.amount);
        if (chapter.bought == true){
            arg1.setBackgroundColor(Color.GREEN);
        }
        else {
            arg1.setBackgroundResource(R.drawable.order_detail_view_background);
        }
        if (imageList.size() > arg0) {
            if (imageList.get(arg0) == null) {
                new loadImageTask(chapterImage).execute(chapter.iconPath);
            } else {
                chapterImage.setImageBitmap(imageList.get(arg0));
            }
        }
        else {
            new loadImageTask(chapterImage).execute(chapter.iconPath);
        }
        return arg1;
    }

    public class loadImageTask extends AsyncTask<String,Void,Bitmap>{
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
            imageList.add(image);
            order_detail_chapter.get(imageList.size()-1).imageProduct = image;
        }
    }

}
