package ir.mirrajabi.searchdialog;

import android.support.annotation.NonNull;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

import ir.mirrajabi.searchdialog.core.FilterResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

/**
 * Created by MADNESS on 5/14/2017.
 */

public class SimpleSearchFilter<T extends Searchable> extends Filter {
    private ArrayList<T> mItems;
    private FilterResultListener mFilterResultListener;
    private boolean mCheckLCS;
    private final float mAccuracyPercentage;

    public SimpleSearchFilter(List<T> objects, @NonNull FilterResultListener filterResultListener,
                              boolean checkLCS, float accuracyPercentage) {
        mFilterResultListener = filterResultListener;
        mCheckLCS = checkLCS;
        mAccuracyPercentage = accuracyPercentage;
        mItems = new ArrayList<>();
        synchronized (this) {
            mItems.addAll(objects);
        }
    }
    public SimpleSearchFilter(List<T> objects, @NonNull FilterResultListener filterResultListener) {
        mFilterResultListener = filterResultListener;
        mCheckLCS = false;
        mAccuracyPercentage = 0;
        mItems = new ArrayList<>();
        synchronized (this) {
            mItems.addAll(objects);
        }
    }

    @Override
    protected FilterResults performFiltering(CharSequence chars) {
        String filterSeq = chars.toString().toLowerCase();
        FilterResults result = new FilterResults();
        if (filterSeq != null && filterSeq.length() > 0) {
            ArrayList<T> filter = new ArrayList<>();
            for (T object : mItems)
                if (object.getTitle().toLowerCase().contains(filterSeq))
                    filter.add(object);
                else if (mCheckLCS)
                    if (StringsHelper.lcs(object.getTitle(), filterSeq).length()
                            > object.getTitle().length() * mAccuracyPercentage)
                        filter.add(object);

            result.values = filter;
            result.count = filter.size();
        } else {
            synchronized (this) {
                result.values = mItems;
                result.count = mItems.size();
            }
        }
        return result;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        ArrayList<T> filtered = (ArrayList<T>) results.values;
        mFilterResultListener.onFilter(filtered);
    }
}