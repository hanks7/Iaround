package net.iaround.ui.view.encounter;

public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    String getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
