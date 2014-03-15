package com.lopesdasilva.trakt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.androidquery.AQuery;
import com.jakewharton.trakt.entities.Actor;
import com.jakewharton.trakt.entities.People;
import com.lopesdasilva.trakt.R;

import java.util.List;

/**
 * Created by lopesdasilva on 24/05/13.
 */
public class ShowCastFragment extends ListFragment {

    private People mShowpeople;

    public ShowCastFragment() {
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setRetainInstance(true);
        if (savedInstanceState == null) {
            mShowpeople = (People) getArguments().getSerializable("showpeople");
            Log.d("Trakt", "Received people:" + mShowpeople.actors.size());

            getListView().setDivider(null);
            setListAdapter(new ShowCastAdapter(getActivity(),mShowpeople.actors));
            getListView().setClickable(false);
        }

    }

    public class ShowCastAdapter extends BaseAdapter {


        private final List<Actor> mActorsList;
        private final LayoutInflater inflater;

        public ShowCastAdapter(Context context, List<Actor> actorsList) {
            mActorsList = actorsList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mActorsList.size();
        }

        @Override
        public Actor getItem(int i) {
            return mActorsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.show_cast_fragment_item, parent, false);
            }
            AQuery aq= new AQuery(convertView);
            aq.id(R.id.textViewActorName).text(mActorsList.get(position).name);
            aq.id(R.id.textViewCharacterName).text("as "+mActorsList.get(position).character);
            aq.id(R.id.imageViewActorImage).image(mActorsList.get(position).images.headshot, true, true, 0, 0, null, AQuery.FADE_IN);
            aq.recycle(convertView);
            return convertView;
        }
    }

}
