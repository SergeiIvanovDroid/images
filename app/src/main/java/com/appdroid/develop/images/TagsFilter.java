package com.appdroid.develop.images;

import android.widget.Filter;

public class TagsFilter extends Filter {

    private ImagesAdapter imagesAdapter;

    public TagsFilter(ImagesAdapter imagesAdapter) {
        this.imagesAdapter = imagesAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        return new FilterResults();
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        imagesAdapter.filterResuls(constraint.toString());
    }
}
