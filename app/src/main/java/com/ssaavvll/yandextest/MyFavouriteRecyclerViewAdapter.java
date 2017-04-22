package com.ssaavvll.yandextest;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ssaavvll.yandextest.FavouriteFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyFavouriteRecyclerViewAdapter extends RecyclerView.Adapter<MyFavouriteRecyclerViewAdapter.ViewHolder> {

    private final List<TranslateItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Activity mActivity;

    public MyFavouriteRecyclerViewAdapter(List<TranslateItem> items, OnListFragmentInteractionListener listener, Activity activity) {
        mValues = items;
        mListener = listener;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        /* Set values to view */
        holder.mItem = mValues.get(position);
        holder.idRecord.setText(mValues.get(position).getId() + "");
        holder.mTextFrom.setText(mValues.get(position).getTextFrom());
        holder.mTextTo.setText(mValues.get(position).getTextTo());
        holder.mLangs.setText(mValues.get(position).getLangFrom() + "-" + mValues.get(position).getLangTo());
        if (mValues.get(position).getFav()) {
            holder.mFav.setImageResource(R.drawable.ic_favicon_active);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        public final TextView idRecord;
        public final TextView mTextFrom;
        public final TextView mTextTo;
        public final TextView mLangs;
        public final ImageButton mFav;
        public TranslateItem mItem;

        public ViewHolder(View view) {
            /* Get views */
            super(view);
            mView = view;
            idRecord = (TextView) view.findViewById(R.id.idRecord);
            mTextFrom = (TextView) view.findViewById(R.id.textFrom);
            mTextTo = (TextView) view.findViewById(R.id.textTo);
            mLangs = (TextView) view.findViewById(R.id.textFromLang);
            mFav = (ImageButton) view.findViewById(R.id.favHistory);
            mFav.setOnClickListener(this);
            /* on click listener on whole item */
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long id = Long.parseLong(idRecord.getText().toString());
                    ((MainActivity)mActivity).openTranslate(id);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTextFrom.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            /* onClick listener for favourite button*/
            ConstraintLayout constraintLayout = (ConstraintLayout)v.getParent();
            TextView textView = (TextView) constraintLayout.findViewById(R.id.idRecord);
            long recordId = Long.parseLong(textView.getText().toString());
            String selection = TranslateContract.History._ID + " = ?";
            String[] selectionArgs = { recordId + "" };
            /* Get existing record */
            Cursor cursor = MainActivity.getDb().query(TranslateContract.History.TABLE_NAME,
                    TranslateContract.History.allColumns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null);
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                int fav = cursor.getInt(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_FAVOURITE));
                /* Change value fav */
                fav ^= 1;
                if (fav == 1) {
                    mFav.setImageResource(R.drawable.ic_favicon_active);
                    Toast.makeText(v.getContext(), R.string.addToFav, Toast.LENGTH_SHORT).show();
                } else {
                    mFav.setImageResource(R.drawable.ic_favicon);
                    Toast.makeText(v.getContext(), R.string.delFromFav, Toast.LENGTH_SHORT).show();
                }
                /* Put new value */
                ContentValues contentValues = new ContentValues();
                contentValues.put(TranslateContract.History.COLUMN_NAME_FAVOURITE, fav);
                int af = MainActivity.getDb().update(TranslateContract.History.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
            } else
                Log.d("Error", "invalid data in database");
        }
    }
}
