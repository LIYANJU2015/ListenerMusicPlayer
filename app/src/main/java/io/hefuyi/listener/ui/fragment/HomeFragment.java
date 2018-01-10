package io.hefuyi.listener.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.tubewebplayer.YouTubePlayerActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hefuyi.listener.Constants;
import io.hefuyi.listener.ListenerApp;
import io.hefuyi.listener.R;
import io.hefuyi.listener.injector.component.ApplicationComponent;
import io.hefuyi.listener.injector.component.DaggerHomeComponent;
import io.hefuyi.listener.injector.component.HomeComponent;
import io.hefuyi.listener.injector.module.ActivityModule;
import io.hefuyi.listener.injector.module.HomeModule;
import io.hefuyi.listener.mvp.contract.HomeContract;
import io.hefuyi.listener.mvp.model.YouTubeModel;
import io.hefuyi.listener.ui.activity.HomeListActivity;
import io.hefuyi.listener.ui.activity.LocalWebYouTubePlayerActivity;
import io.hefuyi.listener.util.ACache;
import io.hefuyi.listener.util.DensityUtil;
import io.hefuyi.listener.util.ListenerUtil;
import io.hefuyi.listener.widget.GlideRoundTransform;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Response;

/**
 * Created by liyanju on 2018/1/9.
 */

public class HomeFragment extends Fragment implements HomeContract.View{

    private Context mContext;
    private Activity activity;

    @Inject
    HomeContract.Presenter mPresenter;

    @BindView(R.id.view_empty)
    ViewStub emptyView;

    @BindView(R.id.home_recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.progressbar)
    MaterialProgressBar progressBar;

    private MultiItemTypeAdapter itemTypeAdapter;

    private static ArrayList<Object> mDatas = new ArrayList<>();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        activity = getActivity();
    }

    public void injectDependences() {
        ApplicationComponent applicationComponent = ((ListenerApp) getActivity().getApplication()).getApplicationComponent();
        HomeComponent homeComponent = DaggerHomeComponent.builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(getActivity()))
                .homeModule(new HomeModule())
                .build();
        homeComponent.inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependences();

        mPresenter.attachView(this);
    }

    private int itemWidth = 0;
    private int itemWidth2 = 0;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        itemTypeAdapter = new MultiItemTypeAdapter(getActivity(), mDatas);
        itemTypeAdapter.addItemViewDelegate(new TitleItemDelagate());
        itemTypeAdapter.addItemViewDelegate(new GridItemDelagate());
        recyclerView.setAdapter(itemTypeAdapter);

        if (mDatas.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
            mPresenter.getYoutubeHomeMusic("en");
        }

        itemWidth = (DensityUtil.getScreenWidth(mContext)
                - DensityUtil.dip2px(mContext, 8) * 4) / 3;
        itemWidth2 = (DensityUtil.getScreenWidth(mContext)
                - DensityUtil.dip2px(mContext, 8) * 3) / 2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void showYoutubeData(YouTubeModel youTubeModel) {
        if (!isAdded()) {
            return;
        }
        Log.v("home", "showYoutubeData youTubeModel " + youTubeModel);

        if (youTubeModel == null) {
            youTubeModel = ListenerApp.sYoutubeModel;
        }
        if (youTubeModel != null) {
            showHomeData(youTubeModel);
        } else {
            showEmptyView();
        }
    }

    private void showHomeData(YouTubeModel youTubeModel) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Log.v("home", "showHomeData youTubeModel " + youTubeModel);

        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        mDatas.clear();
        mDatas.addAll(youTubeModel.youTubeItems);
        itemTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyView() {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Log.v("home", "showEmptyView >>");

        if (mDatas.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public ArrayList<YouTubeModel.YouTubeContent> getContentByName(YouTubeModel.Title title) {
        for (Object obj : mDatas) {
            if (obj instanceof HashMap) {
                ArrayList<YouTubeModel.YouTubeContent> list = (ArrayList<YouTubeModel.YouTubeContent>)
                        ((HashMap)obj).get(title);
                if (list != null) {
                    return list;
                }
            }
        }
        return null;
    }

    class GridItemDelagate implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_grid_item_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof HashMap;
        }

        private YouTubeModel.YouTubeContent getContent(ArrayList<YouTubeModel.YouTubeContent> arrayList, int postion) {
            if (arrayList.size() > postion) {
                return arrayList.get(postion);
            }
            return null;
        }

        private void setData(TextView textView, ImageView imageView, View view, int postion,
                             ArrayList<YouTubeModel.YouTubeContent> list) {
            final YouTubeModel.YouTubeContent content = getContent(list, postion);
            if (content != null) {
                view.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(content.icon).crossFade()
                        .placeholder(R.drawable.default_album_art)
                        .error(R.drawable.default_album_art)
                        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext,6))
                        .into(imageView);
                textView.setText(content.name);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        LocalWebYouTubePlayerActivity.launch(activity, content.extra, content.name);
                    }
                });
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }

        private void setData2(TextView textView, ImageView imageView, View view, int postion,
                              ArrayList<YouTubeModel.YouTubeContent> list) {
            final YouTubeModel.YouTubeContent content = getContent(list, postion);
            if (content != null) {
                view.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(content.icon).crossFade()
                        .placeholder(R.drawable.default_album_art)
                        .error(R.drawable.default_album_art)
                        .transform(new CenterCrop(mContext), new GlideRoundTransform(mContext,6))
                        .into(imageView);
                textView.setText(content.name);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        LocalWebYouTubePlayerActivity.launch(activity, content.extra, content.name);
                    }
                });
            } else {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void convert(ViewHolder holder, Object o, int position) {
            HashMap<YouTubeModel.Title, ArrayList<YouTubeModel.YouTubeContent>> map = (HashMap<YouTubeModel.Title,
                    ArrayList<YouTubeModel.YouTubeContent>>)o;
            YouTubeModel.Title key = map.keySet().iterator().next();
            ArrayList<YouTubeModel.YouTubeContent> arrayList = map.get(key);

            View viewRaw1 = holder.getView(R.id.home_grid_raw1);
            View viewRaw2 = holder.getView(R.id.home_grid_raw2);

            ImageView imageView1 = holder.getView(R.id.song_image1);
            imageView1.getLayoutParams().height = itemWidth;
            TextView namgeTV1 = holder.getView(R.id.song_name_tv1);
            View view1 = holder.getView(R.id.list_item_linear1);

            ImageView imageView2 = holder.getView(R.id.song_image2);
            imageView2.getLayoutParams().height = itemWidth;
            TextView namgeTV2 = holder.getView(R.id.song_name_tv2);
            View view2 = holder.getView(R.id.list_item_linear2);

            ImageView imageView3 = holder.getView(R.id.song_image3);
            imageView3.getLayoutParams().height = itemWidth;
            TextView namgeTV3 = holder.getView(R.id.song_name_tv3);
            View view3 = holder.getView(R.id.list_item_linear3);

            ImageView imageView11 = holder.getView(R.id.song_image11);
            imageView11.getLayoutParams().height = itemWidth;
            TextView namgeTV11 = holder.getView(R.id.song_name_tv11);
            View view11 = holder.getView(R.id.list_item_linear11);

            ImageView imageView22 = holder.getView(R.id.song_image22);
            imageView22.getLayoutParams().height = itemWidth;
            TextView namgeTV22 = holder.getView(R.id.song_name_tv22);
            View view22 = holder.getView(R.id.list_item_linear22);

            ImageView imageView33 = holder.getView(R.id.song_image33);
            imageView33.getLayoutParams().height = itemWidth;
            TextView namgeTV33 = holder.getView(R.id.song_name_tv33);
            View view33 = holder.getView(R.id.list_item_linear33);

            if ("fiveBoxView".equals(key.style) || arrayList.size() < 3) {
                viewRaw2.setVisibility(View.GONE);
                if (arrayList.size() == 2) {
                    imageView1.getLayoutParams().height = itemWidth2;
                    imageView2.getLayoutParams().height = itemWidth2;
                }
                setData2(namgeTV1, imageView1, view1, 0, arrayList);
                setData2(namgeTV2, imageView2, view2, 1, arrayList);
                setData2(namgeTV3, imageView3, view3, 2, arrayList);
                return;
            }

            setData(namgeTV1, imageView1, view1, 0, arrayList);


            setData(namgeTV2, imageView2, view2, 1, arrayList);

            setData(namgeTV3, imageView3, view3, 2, arrayList);

            if (arrayList.size() <= 3) {
                viewRaw2.setVisibility(View.GONE);
                return;
            }

            viewRaw2.setVisibility(View.VISIBLE);

            setData(namgeTV11, imageView11, view11, 3, arrayList);

            setData(namgeTV22, imageView22, view22, 4, arrayList);

            setData(namgeTV33, imageView33, view33, 5, arrayList);
        }
    }

    class TitleItemDelagate implements ItemViewDelegate<Object> {

        @Override
        public int getItemViewLayoutId() {
            return R.layout.home_title_item_layout;
        }

        @Override
        public boolean isForViewType(Object item, int position) {
            return item instanceof YouTubeModel.Title;
        }

        @Override
        public void convert(ViewHolder holder, final Object object, int position) {
            final YouTubeModel.Title title = (YouTubeModel.Title) object;
            holder.setText(R.id.home_item_title, title.name);
            holder.setOnClickListener(R.id.title_relative, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeListActivity.launch(activity, title.name, getContentByName(title));
                }
            });
        }
    }
}
