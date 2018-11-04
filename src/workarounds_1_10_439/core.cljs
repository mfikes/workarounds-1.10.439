(ns workarounds-1-10-439.core
  (:require-macros
   [cljs.spec.test.alpha :as stest]
   [workarounds-1-10-439.core])
  (:require
   [cljs.spec.alpha :as s]
   [cljs.spec.test.alpha :as stest]
   [cljs.stacktrace :as st]))

(workarounds-1-10-439.core/monkey-patch-check)
(workarounds-1-10-439.core/monkey-patch-with-instrument-disabled)

(defn spec-checking-fn
  [v f fn-spec]
  (let [fn-spec (@#'s/maybe-spec fn-spec)
        conform! (fn [v role spec data args]
                   (let [conformed (s/conform spec data)]
                     (if (= ::s/invalid conformed)
                       (let [caller (#'stest/find-caller
                                     (st/parse-stacktrace
                                       (stest/get-host-port)
                                       (.-stack (js/Error.))
                                       (stest/get-env) nil))
                             ed (merge (assoc (s/explain-data* spec [] [] [] data)
                                         ::s/fn (stest/->sym v)
                                         ::s/args args
                                         ::s/failure :instrument)
                                  (when caller
                                    {::caller caller}))]
                         (throw (ex-info
                                  (str "Call to " v " did not conform to spec." )
                                  ed)))
                       conformed)))
        pure-variadic? (and (-> (meta v) :top-fn :variadic?)
                            (zero? (-> (meta v) :top-fn :max-fixed-arity)))
        apply' (fn [f args]
                 (if (and (nil? args)
                          pure-variadic?)
                   (.cljs$core$IFn$_invoke$arity$variadic f)
                   (apply f args)))
        ret (fn [& args]
              (if (.-*instrument-enabled* js/cljs.spec.test.alpha)
                (stest/with-instrument-disabled
                  (when (:args fn-spec) (conform! v :args (:args fn-spec) args args))
                  (try
                    (set! stest/*instrument-enabled* true)
                    (apply' f args)
                    (finally
                      (set! stest/*instrument-enabled* false))))
                (apply' f args)))]
    (when-not pure-variadic?
      (stest/setup-static-dispatches f ret 20)
      (when-some [variadic (.-cljs$core$IFn$_invoke$arity$variadic f)]
        (set! (.-cljs$core$IFn$_invoke$arity$variadic ret)
          (fn [& args]
            (apply variadic args)))))
    ret))

(set! stest/spec-checking-fn spec-checking-fn)
