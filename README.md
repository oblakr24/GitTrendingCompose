# GitTrendingCompose

A demo app displaying the trending git repositories, sorted by the number of stars descending.

The app will load pages automatically once the user scrolls far enough (close enough to the last item).

The items are loaded into a database and restored or refreshed depending on user action or their age.

The language colors are arbitrarily chosen (to roughly match git's own color scheme) since the default response does not return that information.

Dark mode initially follows system setting and is persisted in case user changes the toggle state.