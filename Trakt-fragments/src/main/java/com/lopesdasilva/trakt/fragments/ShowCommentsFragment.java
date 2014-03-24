package com.lopesdasilva.trakt.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.Response;
import com.jakewharton.trakt.entities.Shout;
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;

import com.lopesdasilva.trakt.Tasks.ShowAddComment;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by lopesdasilva on 22/05/13.
 */
public class ShowCommentsFragment extends Fragment implements ShowAddComment.OnShowAddCommentTaskCompleted {


    private TvShow show;
    private ServiceManager manager;
    private DownloadShowComments mTaskDownloadComments;
    private List<Shout> mShouts;
    private View rootView;
    private ListView listViewComments;

    public ShowCommentsFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.show_comments, container, false);

        if (savedInstanceState == null) {
            show = (TvShow) getArguments().getSerializable("movie");
            manager = UserChecker.checkUserLogin(getActivity());
            Log.d("Trakt", "ServiceManager: " + manager);
            Log.d("Trakt", "movie received on commentFragments: " + show.title);
            mTaskDownloadComments = new DownloadShowComments();
            mTaskDownloadComments.execute();
        } else {
            mShouts = (List<Shout>) savedInstanceState.get("shouts");
            if (mShouts != null) {
                updateUI(mShouts);
            } else {
                show = (TvShow) getArguments().getSerializable("movie");
                manager = UserChecker.checkUserLogin(getActivity());
                mTaskDownloadComments = new DownloadShowComments();
                mTaskDownloadComments.execute();
            }


        }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("shouts", (Serializable) mShouts);
    }

    @Override
    public void onShowAddCommentComplete(Response response) {
        AQuery aq= new AQuery(rootView);
        ((EditText) aq.id(R.id.editTextAddComment).text("").getView()).clearFocus();
        mTaskDownloadComments = new DownloadShowComments();
        mTaskDownloadComments.execute();
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
        if (getActivity() != null) {
            this.mShouts = response;

            listViewComments = (ListView) rootView.findViewById(R.id.listViewShow_Comments);
            listViewComments.setDivider(null);
            listViewComments.setAdapter(new ShowSeasonsAdapter(getActivity(), response));
            listViewComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mShouts.get(position).spoiler) {
                        mShouts.get(position).spoiler = false;
                        showItem(position, mShouts.get(position));
                    }
                }
            });

            final AQuery aq= new AQuery(rootView);

            aq.id(R.id.imageButtonSendComment).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   String comment= aq.id(R.id.editTextAddComment).getText().toString();
                    ((EditText) aq.id(R.id.editTextAddComment).getView()).setError(null);
                    if(comment.length()!=0){

                        new ShowAddComment(getActivity(),ShowCommentsFragment.this,manager,show,comment).execute();
                    }else
                        ((EditText) aq.id(R.id.editTextAddComment).getView()).setError(getResources().getString(R.string.comment_short));

                }
            });

        }
    }


    private void showItem(int position, Shout shout) {
        View v = listViewComments.getChildAt(position - listViewComments.getFirstVisiblePosition());

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

