(ns mosql.fun
  (:require [cats.monad.either :refer
             [right left try-either]]))

(defn efy
  "Decorates a function tfn in order to return a either value.
  It returns left for nil  Otherwise right.

  The left format is: {:fn tfn :args args}."
  [tfn]
  (fn [& args]
    (let [v (apply tfn args)]
      (if (some? v)
        (right v)
        (left {:fn tfn :args args})))))

(defn sefy
  "Decorates a function tfn in order to return a either value.
  It returns left for nil or exception. Otherwise right.

  The left format is: {:fn tfn :args args :ex e}."
  [tfn]
  (fn [& args]
    (try
      (let [v (apply tfn args)]
        (if (some? v)
          (right v)
          (left {:fn tfn :args args})))
      (catch Exception e
        (left {:fn tfn :args args :ex e})))))

; Common functions decorated to return either values.
(def efirst (efy first))
(def eget (efy get))


