package com.example.lzx.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	//private ImageView userImage;
	private Button uploadBtn,downloadBtn,lookBtn,seeBtn;
	private ProgressDialog progressDialog;
	private TextView textView;
	
	private final String IMAGE_TYPE="*/*";
	private final int IMAGE_CODE=1;
	
	private Bitmap TestBitmap;
	private String Path="";
	private String url="";
	private String name="";


	
	private Bitmap mBitmap;
	
	private Handler mhandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progressDialog.dismiss();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Bmob.initialize(MainActivity.this, "827201b94822832e4be6b9cfb7d5f252");
		initView();
		initListener();
//		initData();
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				 try {
					 	Bitmap bitmap1=null;
		                URL myUrl;
		                url = "http://file.bmob.cn/" + url;
                		Toast.makeText(MainActivity.this, url+"", Toast.LENGTH_SHORT).show();
                        myUrl=new URL(url);
                        HttpURLConnection conn=(HttpURLConnection)myUrl.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.connect();
                        InputStream is=conn.getInputStream();
                        bitmap1=BitmapFactory.decodeStream(is);
					    //把bitmap转成圆形
                        BitmapUtil bmuUtil = new BitmapUtil(MainActivity.this);
                        mBitmap=bmuUtil.toRoundBitmap(bitmap1);
                        is.close();
                        Message msg = mhandler.obtainMessage(1, mBitmap);
        				mhandler.sendMessage(msg);
                } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
				 Looper.loop();
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode != RESULT_OK) {
			Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
		ContentResolver resolver = getContentResolver();
		if (requestCode == IMAGE_CODE) {
			try {
				Uri originUri = data.getData();
				Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, originUri);
				TestBitmap = bm;
				String[] proj = {MediaStore.Images.Media.DATA};
				Cursor cursor = managedQuery(originUri, proj, null, null, null);
				
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				
				String path = cursor.getString(column_index);
				Path = path.substring(20);
				textView.setText(Path);
				Toast.makeText(MainActivity.this, Path, Toast.LENGTH_SHORT).show();
//				BitmapUtil bitmapUtil = new BitmapUtil(MainActivity.this);
//				Bitmap myBitmap = bitmapUtil.toRoundBitmap(TestBitmap);
//				userImage.setImageBitmap(myBitmap);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	
	private void initListener() {

		lookBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i1 = new Intent();
				i1.setClass(MainActivity.this,activity2.class);
				startActivity(i1);
			}
		});
		
		seeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
				getIntent.setType(IMAGE_TYPE);
				startActivityForResult(getIntent,IMAGE_CODE);
			}
		});
		
		uploadBtn.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unused")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File file = new File("/mnt/sdcard/"+Path);
				if (file != null) {
					final BmobFile bmobFile = new BmobFile(file);
					final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setMessage("正在上传。。。");
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
					bmobFile.upload(MainActivity.this, new UploadFileListener() {

						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							url = bmobFile.getUrl();
							name = bmobFile.getFilename();
							String filenameArray[] = bmobFile.getFilename().split("\\.");
							String back = filenameArray[filenameArray.length-1];
							insertObject(new PersonBean(name,back,bmobFile));
							Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
							progressDialog.dismiss();
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(MainActivity.this, "上传失败"+arg1, Toast.LENGTH_SHORT).show();
						}

					});
				}else {
						Toast.makeText(MainActivity.this, "文件为空", Toast.LENGTH_SHORT).show();
					}

				}
			
		});
		
//		initData();
		
		downloadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("正在上传。。。");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();
						 try {
				                URL myUrl;
		                        myUrl=new URL(url);
							    Uri uri = Uri.parse(url);
							    Intent it = new Intent(Intent.ACTION_VIEW, uri);
							    startActivity(it);
		                } catch (MalformedURLException e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                } catch (IOException e) {
		                        // TODO Auto-generated catch block
		                        e.printStackTrace();
		                }
						 Looper.loop();
					}
				}).start();
				
	        }
		});
	}

	private void initView() {
		uploadBtn = (Button) findViewById(R.id.uploadBtn);
		downloadBtn = (Button) findViewById(R.id.downloadBtn);
		lookBtn = (Button) findViewById(R.id.lookBtn);
        textView = (TextView) findViewById(R.id.textv1);
		seeBtn = (Button) findViewById(R.id.seeBtn);
	}
	
	private void insertObject(final BmobObject obj){
		obj.save(MainActivity.this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "创建数据成功", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "创建数据失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
