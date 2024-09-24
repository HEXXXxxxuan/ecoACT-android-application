package com.go4.application.listener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView Sliding monitoring
 */
public abstract class SearchResultEndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    // Used to mark whether it is sliding upwards
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        // When not sliding
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // Get the last fully displayed itemPosition
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            // Determine if it has slid to the last item and is sliding upwards
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                // Load more
                onLoadMore();
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // A value greater than 0 indicates sliding upwards,
        // while a value less than or equal to 0 indicates stopping or sliding downwards
        isSlidingUpward = dy > 0;
    }

    /**
     * Load more callbacks
     */
    public abstract void onLoadMore();
}
