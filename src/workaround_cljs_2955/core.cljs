(ns workaround-cljs-2955.core
  (:require-macros
   [cljs.spec.test.alpha]
   [workaround-cljs-2955.core]))

(workaround-cljs-2955.core/monkey-patch-check)
