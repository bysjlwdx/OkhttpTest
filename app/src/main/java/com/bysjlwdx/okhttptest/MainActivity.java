package com.bysjlwdx.okhttptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String bai = "http://120.27.23.105/product/getProducts?pscid=39&page=1";
    private RecyclerView rv;
    Bean bean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //初始化ImageLoader
        ImageLoaderConfiguration cofn = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(cofn);
        DisplayImageOptions simple = DisplayImageOptions.createSimple();
        okURL();
        init();

    }


    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
    }
    // 样式
    private void init() {
        //LinearLayoutManager布局样式
        LinearLayoutManager manager=new LinearLayoutManager(this);
        //  GridLayoutManager布局样式
        //GridLayoutManager manager = new GridLayoutManager(this, 2);
        //设置样式
        rv.setLayoutManager(manager);
        //设置方向   VERTICAL垂直方向
        //想要添加分割线，必须要写上这行代码
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
    //okhttp的网络请求
    private void okURL() {
        OkHttp3Utils.doGet(bai, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Gson解析
                Gson gson = new Gson();
                String strs = response.body().string();
                bean = gson.fromJson(strs, Bean.class);
                //判断call,如果返回ture就继续执行，否则就不行
                if (response.isSuccessful()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //创建适配器
                            Myadapter adapter = new Myadapter();
                            //设置适配器
                            rv.setAdapter(adapter);
                            //刷新适配器
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }
    class Myadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        public static final int TYPE_ONE=0;
        public static final int TYPE_TWO=1;
        MyViewHolder holder;
        MyViewHolder1 holder1;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType==TYPE_ONE){
                View view=View.inflate(MainActivity.this,R.layout.list_item,null);
                return new MyViewHolder(view);
            }else {
                View view1=View.inflate(MainActivity.this,R.layout.list_item1,null);
                return new MyViewHolder1(view1);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder){
                ((MyViewHolder) holder).tv1.setText(bean.getData().get(position).getTitle());
                ((MyViewHolder) holder).tv2.setText(bean.getData().get(position).getPrice()+"");
                //分割字符串
                String[] split = bean.getData().get(position).getImages().split("\\|");
                ImageLoader.getInstance().displayImage(split[0],((MyViewHolder) holder).iv);
            }
            if (holder instanceof MyViewHolder1){
                ((MyViewHolder1) holder).tv1_1.setText(bean.getData().get(position).getTitle());
                ((MyViewHolder1) holder).tv1_2.setText(bean.getData().get(position).getPrice()+"");
            }
        }
        //要实现多条目就必须重写getItemViewType方法

        @Override
        public int getItemViewType(int position) {
            if (bean.getData().get(position).getItemtype()==1){
                return TYPE_ONE;
            }
            else if (bean.getData().get(position).getItemtype()==0){
                return TYPE_ONE;
            }else {
                return TYPE_TWO;
            }
        }

        @Override
        public int getItemCount() {
            return bean.getData().size();
        }
        class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView iv;
            TextView tv1,tv2;
            public MyViewHolder(View itemView) {
                super(itemView);
                iv=itemView.findViewById(R.id.iv_1);
                tv1=itemView.findViewById(R.id.tv_1);
                tv2=itemView.findViewById(R.id.tv_2);
            }
        }
        class MyViewHolder1 extends RecyclerView.ViewHolder{
            TextView tv1_1,tv1_2;
            public MyViewHolder1(View itemView) {
                super(itemView);
                tv1_1=itemView.findViewById(R.id.tv1_1);
                tv1_2=itemView.findViewById(R.id.tv1_2);
            }
        }
    }

}
