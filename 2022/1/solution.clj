(ns solution
  (:require [clojure.string :as str]))

;; How to run:
;; - Start a repl (I used 'lein repl')
;; - Connect Calva to the running repl
;; - Run (main) in the repl

(defn process-data [acc val]
  (if (= "" val) ;; Newline; different elf
    ;; Add a new sum-set to the end
    (conj acc 0)
    ;; Otherwise, add the val and move on
    (let [cur-val (Integer/parseInt val)
          new-tot (+ (last acc) cur-val)]
      ;; Convoluted pop and then re-associate to the end
      (conj (pop acc) new-tot))))

(defn main []
  (let [input (slurp "input.txt") ;; Line-delimited calorie counts
        data  (str/split input #"\n")
        ;; We have out data, now process it
        sums  (reduce process-data [0] data)
        sort  (sort > sums)] ;; Sort in descending order
    ;; Return the sum of the max 3 calorie counts
    (reduce #(+ %1 %2) 0 (take 3 sort))))

(comment
  (main))