package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.content.Intent;
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
import com.jakewharton.trakt.entities.Movie;
import com.lopesdasilva.trakt.R;
import com.lopesdasilva.trakt.Tasks.SearchMovieTask;
import com.lopesdasilva.trakt.activities.MovieActivity;
import com.lopesdasilva.trakt.activities.ShowActivity;
import com.lopesdasilva.trakt.extras.UserChecker;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lopesdasilva on 03/06/13.
 */
public class SearchMovieFragment extends ListFragment implements SearchMovieTask.onSearchComplete {


    private String query;
    private ServiceManager manager;
    private List<Movie> mMovielist = new LinkedList<Movie>();
    private SearchMovieAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        query = getArguments().getString("query");

        manager = UserChecker.checkUserLogin(getActivity());
        new SearchMovieTask(getActivity(), this, manager, query).execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new SearchMovieAdapter(getActivity(), mMovielist);
        getListView().setDivider(null);
        setListAdapter(mAdapter);

    }

    @Override
    public void onSearchComplete(List<Movie> result) {
        mMovielist.clear();
        mMovielist.addAll(result);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d("Trakt Fragments", "Launching Movie Activity");


        Bundle arguments = new Bundle();
        arguments.putString("movie_imdb", mMovielist.get(position).imdbId);
        Intent i = new Intent(getActivity(), MovieActivity.class);
        i.putExtras(arguments);

        startActivity(i);
        getActivity().overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);


    }

    public class SearchMovieAdapter extends BaseAdapter {

        private final List<Movie> mMovieList;
        private final LayoutInflater inflater;

        public SearchMovieAdapter(Context context, List<Movie> mList) {
            this.mMovieList = mList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMovieList.size();
        }

        @Override
        public Movie getItem(int i) {
            return mMovieList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.searchmove_item, parent, false);
            }
            AQuery aq = new AQuery(convertView);
            aq.id(R.id.imageViewSearchMoviePoster).image(mMovieList.get(position).images.poster, true, true, 100, R.drawable.poster, null, AQuery.FADE_IN);
            aq.id(R.id.textViewSearchMovieTitle).text(mMovieList.get(position).title+" ("+mMovieList.get(position).year+")");
            aq.id(R.id.textViewSearchMovieTag).text(mMovieList.get(position).tagline);
            aq.id(R.id.textViewSearchMovieRatingsPercentage).text(mMovieList.get(position).ratings.percentage+"%");
            aq.id(R.id.textViewSearchMovieOverview).text(mMovieList.get(position).overview);
            return convertView;

        }
    }
}
