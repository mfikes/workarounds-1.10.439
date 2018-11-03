A work around for [CLJS-2955](https://dev.clojure.org/jira/browse/CLJS-2955).

Simply depend on this library as a [git dep](https://clojure.org/news/2018/01/05/git-deps) and require `workaround-cljs-2955.core`. Monkey patching to fix CLJS-2955 occurs as a side effect when this namespace is required.
