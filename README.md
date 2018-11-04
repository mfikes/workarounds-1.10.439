Workarounds for issues in ClojureScript 1.10.439, in the form of monkey patches.

* [CLJS-2955](https://dev.clojure.org/jira/browse/CLJS-2955).
* [CLJS-2956](https://dev.clojure.org/jira/browse/CLJS-2956).

Simply depend on this library as a [git dep](https://clojure.org/news/2018/01/05/git-deps) and require `workarounds1.10.439.core`. Monkey patching occurs as a side effect when this namespace is required.
