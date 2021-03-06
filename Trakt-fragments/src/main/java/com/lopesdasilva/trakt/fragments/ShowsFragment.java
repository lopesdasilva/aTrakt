package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadTrendingShows;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.activities.ShowsActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 04/06/13.
 */
public class ShowsFragment extends Fragment implements DownloadTrendingShows.onTrendingShowListTaskComplete {

    private View rootView;
    private List<TvShow> mShowsList = new LinkedList<TvShow>();
    private DownloadTrendingShows mTrendingShowsTask;
    private ServiceManager manager;
    private LayoutInflater inflater;
    private LinearLayout mShowLayout;


    public ShowsFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.shows_fragment, container, false);
//


//        setRetainInstance(true);
        if (savedInstanceState == null) {
            manager = UserChecker.checkUserLogin(getActivity());
            mTrendingShowsTask = new DownloadTrendingShows(this, getActivity(), manager);
            mTrendingShowsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {

            mShowsList = (List<TvShow>) savedInstanceState.get("shows");
            if (mShowsList.size() != 0)
                updateView(mShowsList);
            else {
                manager = UserChecker.checkUserLogin(getActivity());
                mTrendingShowsTask = new DownloadTrendingShows(this, getActivity(), manager);
                mTrendingShowsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        rootView.findViewById(R.id.buttonShowsSeeMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ShowsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);

            }
        });


        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("shows", (Serializable) mShowsList);
    }

    @Override
    public void onTrendingShowListTaskComplete(List<TvShow> response) {
        this.mShowsList = response;
        updateView(mShowsList);
    }

    public void updateView(List<TvShow> response) {
        rootView.findViewById(R.id.progressBarShows).setVisibility(View.GONE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (getActivity() != null) {
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;


            float pixels = (int) (115 * (metrics.densityDpi / 160f));

//        Toast.makeText(getActivity(), "pixels" + pixels + " columns=" + (int) (widthPixels / pixels), Toast.LENGTH_SHORT).movie();

            mShowLayout = (LinearLayout) rootView.findViewById(R.id.linearLayoushows);

            for (TvShow m : response.subList(0, (int) (widthPixels / pixels))) {

                RelativeLayout showItem = (RelativeLayout) inflater.inflate(R.layout.shows_grid_item, null).findViewById(R.id.relativeLayoutShowsShow);

                setShow(showItem, m);
                mShowLayout.addView(showItem);

                LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) showItem.getLayoutParams();
                layoutparams.setMargins(0, 0, 5, 0);

            }


        }
    }

    private void setShow(RelativeLayout showItem, final TvShow show) {

        final AQuery aq = new AQuery(showItem);
        aq.id(R.id.imageViewShowsPoster).progress(R.id.progress).image(show.images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
        aq.id(R.id.textViewShowsShowTitle).text(show.title);
        aq.id(R.id.textViewShowsShowPercentage).text(show.ratings.percentage + "%");
        aq.id(R.id.textViewShowsShowNumberVotes).text(show.ratings.votes + " votes");
        aq.id(R.id.textViewShowsShowViewers).text(show.watchers + " viewers");

        if (show.watched)
            aq.id(R.id.imageViewShowsShowSeenTag).visible();
        else
            aq.id(R.id.imageViewShowsShowSeenTag).gone();



        if("0".equals(show.ratingAdvanced)) {
            aq.id(R.id.relativeLayoutAdvanceRating).gone();
        } else if("1".equals(show.ratingAdvanced)){
            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_1);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("2".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_2);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("3".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_3);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("4".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_4);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("5".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_5);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("6".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_6);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("7".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_7);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("8".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_8);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("9".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_9);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
        } else if("10".equals(show.ratingAdvanced)){

            aq.id(R.id.relativeLayoutAdvanceRatingBackground).background(R.drawable.rate_tag_triangle_10);
            aq.id(R.id.relativeLayoutAdvanceRating).visible();
            aq.id(R.id.textViewShowRatingAdvance).text(show.ratingAdvanced);
            aq.id(R.id.textViewShowRatingAdvance).margin(0,0,1,0);
        }


        showItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        aq.id(R.id.imageViewShowsPoster).getImageView().setColorFilter(0xaf0099cc, PorterDuff.Mode.SRC_ATOP);
                        break;

                    case MotionEvent.ACTION_UP:
                        aq.id(R.id.imageViewShowsPoster).getImageView().clearColorFilter();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        aq.id(R.id.imageViewShowsPoster).getImageView().clearColorFilter();
                        break;
                }

                return false;
            }
        });
        showItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Trakt Fragments", "Launching Show Activity");

                Bundle arguments = new Bundle();

                if(show.imdbId!=null && !"".equals(show.imdbId))
                    arguments.putString("show_imdb", show.imdbId);
                else if(show.tvdbId!=null && !"".equals(show.tvdbId))
                    arguments.putString("show_imdb", show.tvdbId);
                else
                    arguments.putString("show_imdb", show.title.replace(" ","-")+"-"+show.year);


                Intent intent = new Intent(getActivity(), ShowActivity.class);
                intent.putExtras(arguments);

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
            }
        });


    }

    public class ShowsGridAdapter extends BaseAdapter {

        private final List<TvShow> mList;
        private final LayoutInflater inflater;

        public ShowsGridAdapter(Context context, List<TvShow> mList) {

            this.mList = mList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.shows_grid_item, parent, false);
            }

            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewShowsPoster).progress(R.id.progress).image(mList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewShowsShowTitle).text(mList.get(position).title);
            aq.id(R.id.textViewShowsShowPercentage).text(mList.get(position).ratings.percentage + "%");
            return convertView;
        }
    }
}
