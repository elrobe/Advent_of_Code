(ns solution
  (:require [clojure.string :as str]))

(defn process-data [acc val]
  (if (= "" val) ;; Newline; different elf
    ;; In this case, check it against the max
    (let [cur (last (:ary acc))
          max (:max acc)
          acc (if (> cur max) (assoc acc :max cur) acc)]
      ;; Re-associate the array with a blank calorie count for the next elf
      (assoc acc :ary (conj (:ary acc) 0)))
    ;; Otherwise, add the val and move on
    (let [cur-val (Integer/parseInt val)
          new-tot (+ (last (:ary acc)) cur-val)]
      ;; Convoluted pop and then re-associate to the end
      (assoc acc :ary (conj (pop (:ary acc)) new-tot)))))

(defn main []
  (let [input (slurp "input.txt") ;; Line-delimited calorie counts
        data  (str/split input #"\n")
        ;; We have out data, now process it
        agg   (reduce process-data
                      {:max 0
                       :ary [0]}
                      data)]
    ;; Return the max calories
    (:max agg)))

(comment
  (main))