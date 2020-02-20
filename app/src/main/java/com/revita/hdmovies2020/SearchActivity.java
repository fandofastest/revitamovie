package com.revita.hdmovies2020;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.revita.hdmovies2020.revita_adapter.LiveTvAdapter2;
import com.revita.hdmovies2020.revita_adapter.SearchAdapter;
import com.revita.hdmovies2020.revita_network.RetrofitClient;
import com.revita.hdmovies2020.revita_network.apis.SearchApi;
import com.revita.hdmovies2020.revita_network.model.CommonModel;
import com.revita.hdmovies2020.revita_network.model.SearchModel;
import com.revita.hdmovies2020.revita_network.model.TvModel;
import com.revita.hdmovies2020.revita_utl.ApiResources;
import com.revita.hdmovies2020.revita_utl.ToastMsg;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnItemClickListener {

    private String query="";

    private TextView tvTitle, movieTitle, tvSeriesTv, searchQueryTv;

    private ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView movieRv, tvRv, tvSeriesRv;
    private SearchAdapter movieAdapter, tvSeriesAdapter;
    private LiveTvAdapter2 tvAdapter;
    private List<CommonModel> movieList =new ArrayList<>();
    private List<TvModel> tvList =new ArrayList<>();
    private List<CommonModel> tvSeriesList =new ArrayList<>();

    private ApiResources apiResources;

    private String URL=null;
    private boolean isLoading=false;
    private ProgressBar progressBar;
    private int pageCount=1;

    private LinearLayout movieLayout, tvSeriesLayout, tvLayout;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        boolean isDark = sharedPreferences.getBoolean("dark", false);

        if (isDark) {
            setTheme(R.style.AppThemeDark);
        } else {
            setTheme(R.style.AppThemeLight);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (!isDark) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //---analytics-----------
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "search_activity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        query = getIntent().getStringExtra("q");

        tvTitle=findViewById(R.id.title);
        tvLayout=findViewById(R.id.tv_layout);
        movieLayout=findViewById(R.id.movie_layout);
        tvSeriesLayout=findViewById(R.id.tv_series_layout);
        tvTitle = findViewById(R.id.tv_title);
        movieTitle= findViewById(R.id.movie_title);
        tvSeriesTv = findViewById(R.id.tv_series_title);
        movieRv = findViewById(R.id.movie_rv);
        tvRv = findViewById(R.id.tv_rv);
        tvSeriesRv = findViewById(R.id.tv_series_rv);
        searchQueryTv = findViewById(R.id.title_tv);

        searchQueryTv.setText("Showing Result for : "+query );



        progressBar=findViewById(R.id.item_progress_bar);
        shimmerFrameLayout=findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();



        URL=new ApiResources().getSearchUrl()+"&&q="+query+"&&page=";

        coordinatorLayout=findViewById(R.id.coordinator_lyt);
        movieRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //movieRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 4), true));
        movieRv.setHasFixedSize(true);
        movieAdapter = new SearchAdapter(movieList, this);
        movieAdapter.setOnItemClickListener(this);
        movieRv.setAdapter(movieAdapter);


        tvRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //tvRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 8), true));
        tvRv.setHasFixedSize(true);
        tvAdapter = new LiveTvAdapter2(this, tvList);
        tvRv.setAdapter(tvAdapter);

        tvSeriesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        //tvSeriesRv.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(this, 4), true));
        tvSeriesRv.setHasFixedSize(true);
        tvSeriesAdapter = new SearchAdapter(tvSeriesList, this);
        tvSeriesAdapter.setOnItemClickListener(this);
        tvSeriesRv.setAdapter(tvSeriesAdapter);

        getSearchData();

        //getData(URL,pageCount);

    }


    public void getSearchData() {

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        SearchApi searchApi = retrofit.create(SearchApi.class);
        Call<SearchModel> call = searchApi.getSearchData(Config.API_KEY, query);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, retrofit2.Response<SearchModel> response) {

                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);



                if (response.code() == 200) {

                    SearchModel searchModel = response.body();

                    movieList.addAll(searchModel.getMovie());
                    tvList.addAll(searchModel.getTvChannels());
                    tvSeriesList.addAll(searchModel.getTvseries());

                    if (movieList.size() > 0) {
                        movieAdapter.notifyDataSetChanged();
                    } else {
                        movieLayout.setVisibility(View.GONE);
                    }

                    if (tvList.size() > 0) {
                        tvAdapter.notifyDataSetChanged();
                    } else {
                        tvLayout.setVisibility(View.GONE);
                    }

                    if (tvSeriesList.size() > 0) {
                        tvSeriesAdapter.notifyDataSetChanged();
                    } else {
                        tvSeriesLayout.setVisibility(View.GONE);
                    }

                    if (tvList.size() == 0 && movieList.size() == 0 && tvSeriesList.size() == 0) {
                        coordinatorLayout.setVisibility(View.VISIBLE);
                    }


                } else {
                    new ToastMsg(SearchActivity.this).toastIconSuccess("Something went wrong.");
                    coordinatorLayout.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                coordinatorLayout.setVisibility(View.VISIBLE);
                t.printStackTrace();
                new ToastMsg(SearchActivity.this).toastIconSuccess("Something went wrong.");
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(CommonModel commonModel) {

        String type="";
        if (commonModel.getIsTvseries().equals("1")) {
            type = "tvseries";
        } else {
            type = "movie";
        }

        Intent intent=new Intent(this,DetailsActivity.class);
        intent.putExtra("vType",type);
        intent.putExtra("id",commonModel.getVideosId());
        startActivity(intent);

    }
}
