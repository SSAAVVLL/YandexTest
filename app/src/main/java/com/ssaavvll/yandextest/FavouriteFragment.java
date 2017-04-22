package com.ssaavvll.yandextest;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FavouriteFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private Fragment fragment;
    private boolean notEmpty;
    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavouriteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragment = this;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_list, container, false);
        /* Select all elements, that added to favourite */
        String selection = TranslateContract.History.COLUMN_NAME_FAVOURITE + " = ?";
        String[] selectionArgs = { "1" };
        Cursor cursor = MainActivity.getDb().query(TranslateContract.History.TABLE_NAME,
                TranslateContract.History.allColumns,
                selection,
                selectionArgs,
                null,
                null,
                TranslateContract.History._ID + " DESC");
        notEmpty = cursor.getCount() != 0;
        if (notEmpty) {
            /* Populate items from cursor */
            List<TranslateItem> items = new ArrayList<TranslateItem>();
            while (cursor.moveToNext())
                items.add(new TranslateItem(cursor.getLong(cursor.getColumnIndex(TranslateContract.History._ID)),
                        cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_TEXT_FROM)),
                        cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_TEXT_TO)),
                        cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_LANG_FROM)),
                        cursor.getString(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_LANG_TO)),
                        cursor.getInt(cursor.getColumnIndex(TranslateContract.History.COLUMN_NAME_FAVOURITE))
                ));

            /* Set the adapter */
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new MyFavouriteRecyclerViewAdapter(items, mListener, getActivity()));
            }
        } else {
            /* inflate empty view */
            view = inflater.inflate(R.layout.list_empty, container, false);
            ((TextView)view.findViewById(R.id.emptyText)).setText(R.string.favouriteEmpty);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.clearFavourite:
                /* clearFavourite action */
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.alertClearFavouriteTitle)
                        .setMessage(R.string.alertClearFavouriteMessage)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* Delete all records added to favourite */
                                // Define 'where' part of query.
                                String selection = TranslateContract.History.COLUMN_NAME_FAVOURITE + " = ?";
                                // Specify arguments in placeholder order.
                                String[] selectionArgs = { "1" };
                                // Issue SQL statement.
                                /* delete records */
                                MainActivity.getDb().delete(TranslateContract.History.TABLE_NAME, selection, selectionArgs);
                                /* reatach fragment */
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).detach(fragment).attach(fragment).commit();
                                /* notify user*/
                                Toast.makeText(getContext(), R.string.clearedFavourite, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /* Add button clearFavourite */
        if (notEmpty)
            menu.add(0, R.id.clearFavourite, 0, R.string.clearFavourite).setIcon(R.drawable.ic_delete_24px).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(TranslateItem item);
    }
}
