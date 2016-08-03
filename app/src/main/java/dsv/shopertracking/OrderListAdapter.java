package dsv.shopertracking;

import android.content.Context;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;


/**
 * Created by hocnv on 5/19/15.
 */
public class OrderListAdapter extends BaseAdapter {

    public Context contextListView;
    private static final String TAG_ID = "id";
    private static final String TAG_STATUS = "status";
    private static final String TAG_CART = "cart";
    private static final String TAG_MOID = "mo_id";
    private String TAG_ORDER_ID ="";
    public static Handler handler = new Handler();
    public static OrderListChapter chapter;





    List<OrderListChapter> order_list_chapter;
    JSONObject chapterInfo;
    //JSONArray cartInfo;
    String idChapter,sttInfo,moId;
    public List<OrderListChapter> get_data_list(JSONArray orderListObj) {
        List<OrderListChapter> orderList = new ArrayList<OrderListChapter>();
        int i = 0;
        int leng = orderListObj.length();
        while (i<leng)
        {
            try {
                chapterInfo = orderListObj.getJSONObject(i);
                idChapter = chapterInfo.getString(TAG_ID);
               // cartInfo = chapterInfo.getJSONArray(TAG_CART);
                sttInfo = chapterInfo.getString(TAG_STATUS);
                moId = chapterInfo.getString(TAG_MOID);
                i++;
            }catch (JSONException e){
                e.printStackTrace();
            }
            chapter = new OrderListChapter();
            chapter.chapterName = "Order ";
            chapter.chapterId = "#"+idChapter;
            chapter.chapterMoId = moId;
        //    chapter.cart = cartInfo;
            chapter.chapterStatus = sttInfo;
            orderList.add(chapter);

        }

        return orderList;

    }

    OrderListAdapter(JSONArray oderList,Context context ){
        contextListView = context;
        order_list_chapter = get_data_list(oderList);

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return order_list_chapter.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return order_list_chapter.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        // TODO Auto-generated method stub
        chapter = order_list_chapter.get(arg0);

        if (chapter.chapterStatus.equals("9")){
            if(arg1==null)
            {
                LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.order_list_view_old, arg2,false);
            }
            TextView chapterName = (TextView)arg1.findViewById(R.id.textView1);
            TextView chapterId = (TextView)arg1.findViewById(R.id.textView2);
            chapterName.setText(chapter.chapterName);
            chapterId.setText(chapter.chapterId);
        }else{
            if(arg1==null)
        {
            LayoutInflater inflater = (LayoutInflater) contextListView.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            arg1 = inflater.inflate(R.layout.order_list_view, arg2,false);
        }
            TextView chapterName = (TextView)arg1.findViewById(R.id.textView1);
            TextView chapterId = (TextView)arg1.findViewById(R.id.textView2);
            View circle = arg1.findViewById(R.id.circle);
            TextView status = (TextView)arg1.findViewById(R.id.statusOrder);

            chapterName.setText(chapter.chapterName);
            chapterId.setText(chapter.chapterId);
            if (chapter.chapterStatus.equals("1")){
                circle.setBackground(arg1.getResources().getDrawable(R.drawable.circle_offine));
                status.setText("not started yet");
            }
            else if (chapter.chapterStatus.equals("2")){
                circle.setBackground(arg1.getResources().getDrawable(R.drawable.circle_offine));
                status.setText("approved");
            }
            else if (chapter.chapterStatus.equals("3")){
                circle.setBackground(arg1.getResources().getDrawable(R.drawable.circle_online));
                status.setText("started");
            }
            else if (chapter.chapterStatus.equals("4")){
                circle.setBackground(arg1.getResources().getDrawable(R.drawable.circle_offine));
                status.setText("shopped");
            }
        }
        return arg1;
    }

}