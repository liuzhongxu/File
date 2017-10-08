package com.example.lzx.file;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by lzx on 2016/7/8.
 */
public class activity2 extends AppCompatActivity {

    private Button downloadBtn1;
    private String url[];
    private TextView tv2;
    private String html;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findls);
        Bmob.initialize(activity2.this, "827201b94822832e4be6b9cfb7d5f252");
        //downloadBtn1 = (Button) findViewById(R.id.downloadBtn1);
        //tv2 = (TextView) findViewById(R.id.tv2);
        //tv2.setTextColor(Color.argb(255, 0, 255, 0));

        BmobQuery<PersonBean> datatable = new BmobQuery<PersonBean>();
        datatable.findObjects(this, new FindListener<PersonBean>() {
            @Override
            public void onSuccess(List<PersonBean> object) {
                Toast.makeText(activity2.this, "查询成功：共" + object.size() + "条数据。", Toast.LENGTH_SHORT).show();
                List<Map<String, Object>> listItems =
                        new ArrayList<Map<String, Object>>();
                for (int b = 0; b < object.size(); b++) {
                    Map<String, Object> listItem = new HashMap<String, Object>();
                    listItem.put("name", object.get(b).getName());
                    if ("docx".equals(object.get(b).getPassword().toString())) {
                        listItem.put("back", R.drawable.qq_leba_list_seek_folder);
                    } else {
                        listItem.put("back", R.drawable.qq_leba_list_seek_individuation);
                    }
                    listItem.put("bmobFile", object.get(b).getFile().getUrl());
                    listItems.add(listItem);
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(
                        activity2.this,
                        listItems,
                        R.layout.list_item1,
                        new String[]{"back", "name","bmobFile"},
                        new int[]{R.id.imageArray, R.id.tv1, R.id.tv2});
                ListView listview1 = (ListView) findViewById(R.id.ls1);
                listview1.setAdapter(simpleAdapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
