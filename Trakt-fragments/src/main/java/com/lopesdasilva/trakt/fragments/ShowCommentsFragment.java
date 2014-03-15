package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;
import com.jakewharton.trakt.entities.TvShowSeason;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.DownloadSeasonsInfo;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class ShowCommentsFragment extends ListFragment {


    private TvShow show;
    private ServiceManager manager;
    private DownloadShowComments mTaskDownloadComments;
    private List<Shout> mShouts;

    public ShowCommentsFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        setRetainInstance(true);
        if (savedInstanceState == null) {
            show = (TvShow) getArguments().getSerializable("show");
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "ServiceManager: " + manager);
            Log.d("Trakt", "show received on commentFragments: " + show.title);
            mTaskDownloadComments = new DownloadShowComments();
            mTaskDownloadComments.execute();
        }else{
            mShouts=(List<Shout>) savedInstanceState.get("shouts");
            if(mShouts!=null){
                updateUI(mShouts);
            }else{
                show = (TvShow) getArguments().getSerializable("show");
                manager = UserChecker.checkUserLogin(getActivity());
                mTaskDownloadComments = new DownloadShowComments();
                mTaskDownloadComments.execute();
            }


        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("shouts", (Serializable) mShouts);
    }

    public class DownloadShowComments extends AsyncTask<Void, Void, List<Shout>> {


        private Exception e;

        @Override
        protected List<Shout> doInBackground(Void... voids) {
            try {
                Log.d("Trakt", "Downloading Comments");
                return manager.showService().shouts(show.tvdbId).fire();
            } catch (Exception e) {
                this.e = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Shout> response) {
            mTaskDownloadComments = null;
            if (e == null) {
                Log.d("Trakt", "Download Comments complete");

                updateUI(response);
            } else
                Toast.makeText(getActivity(), "Error Downloading Comments", Toast.LENGTH_SHORT).show();

        }
    }

    private void updateUI(List<Shout> response) {
        if(getActivity()!=null){
        this.mShouts = response;

        getListView().setDivider(null);
        setListAdapter(new ShowSeasonsAdapter(getActivity(), response));
    }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mShouts.get(position).spoiler) {
            mShouts.get(position).spoiler = false;
            showItem(position, mShouts.get(position));
        }

    }

    private void showItem(int position, Shout shout) {
        View v = getListView().getChildAt(position - getListView().getFirstVisiblePosition());

        AQuery aq = new AQuery(v);
        aq.id(R.id.imageViewSpoiler).gone();
        aq.id(R.id.imageViewCommentAvatar).image(shout.user.avatar).visible();
        aq.id(R.id.textViewCommentText).text(shout.shout).visible();
        aq.id(R.id.textViewCommentUsername).text(shout.user.username).visible();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh:mm");
        aq.id(R.id.textViewCommentDate).text(dateFormat.format(shout.inserted.getTime()) + "").visible();


    }

    public class ShowSeasonsAdapter extends BaseAdapter {

        private final List<Shout> mShouts;
        private final LayoutInflater inflater;

        public ShowSeasonsAdapter(Context context, List<Shout> response) {
            mShouts = response;
            inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return mShouts.size();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return mShouts.get(position).spoiler ? 1 : 0;
        }

        @Override
        public Shout getItem(int i) {
            return mShouts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.show_comment_fragment_item, parent, false);
            }

            Shout shout = mShouts.get(position);
            AQuery aq = new AQuery(convertView);

            if (shout.spoiler) {
                //HIDE ALL VIEWS

                aq.id(R.id.imageViewCommentAvatar).gone();
                aq.id(R.id.textViewCommentText).gone();
                aq.id(R.id.textViewCommentUsername).gone();
                aq.id(R.id.textViewCommentDate).gone();

                aq.id(R.id.imageViewSpoiler).visible();
            } else {

                aq.id(R.id.imageViewSpoiler).gone();
                ImageOptions options = new ImageOptions();
                options.round = 360;
                aq.id(R.id.imageViewCommentAvatar).image(shout.user.avatar, options).visible();
                aq.id(R.id.textViewCommentText).text(shout.shout).visible();
                aq.id(R.id.textViewCommentUsername).text(shout.user.username).visible();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy hh:mm");
                aq.id(R.id.textViewCommentDate).text(dateFormat.format(shout.inserted.getTime()) + "").visible();
            }
            aq.recycle(convertView);
            return convertView;
        }
    }

}

