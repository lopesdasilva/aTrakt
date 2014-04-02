package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.jakewharton.trakt.ServiceManager;
import com.jakewharton.trakt.entities.TvShow;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.SearchShowTask;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchShowFragment extends ListFragment implements SearchShowTask.onSearchComplete {


    private String query;
    private ServiceManager manager;
    private List<TvShow> mShowlist = new LinkedList<TvShow>();
    private SearchShowAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        query = getArguments().getString("query");

        manager = UserChecker.checkUserLogin(getActivity());

        new SearchShowTask(getActivity(), this, manager, query).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SearchShowAdapter(getActivity(), mShowlist);
        getListView().setDivider(null);
        setListAdapter(mAdapter);

    }

    @Override
    public void onSearchComplete(List<TvShow> result) {
        mShowlist.clear();
        mShowlist.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("Trakt Fragments", "Launching Show Activity");


        Bundle arguments = new Bundle();
        arguments.putString("show_imdb", mShowlist.get(position).imdbId);

        Intent i = new Intent(getActivity(), ShowActivity.class);
        i.putExtras(arguments);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);


    }

    public class SearchShowAdapter extends BaseAdapter {

        private final List<TvShow> mShowList;
        private final LayoutInflater inflater;

        public SearchShowAdapter(Context context, List<TvShow> mList) {
            this.mShowList = mList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mShowList.size();
        }

        @Override
        public TvShow getItem(int i) {
            return mShowList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.searchshow_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewSearchShowPoster).image(mShowList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewSearchShowTitle).text(mShowList.get(position).title + " (" + mShowList.get(position).year + ")");
            aq.id(R.id.textViewSearchShowRatingsPercentage).text(mShowList.get(position).ratings.percentage + "%");
            aq.id(R.id.textViewSearchShowOverview).text(mShowList.get(position).overview);
            return convertView;

        }
    }
}
