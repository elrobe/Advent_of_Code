(ns solution
  (:require [clojure.string :as str]))

;; Advent of Code 2022 - Day 1
;; Calorie Counting
;;
;; The input is a list of elves and how many calories
;; they're carrying.
;;
;; Part 1 -
;; Find the Elf carrying the most Calories. How many total Calories is that Elf carrying?
;;
;; Part 2 -
;; Find the top three elves carrying the most Calories.

;; To run:
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
    ;; Part 1: Return the highest calorie count
    (println "Part 1:" (first sort))
    ;; Part 2: Return the sum of the max 3 calorie counts 
    (println "Part 2:" (reduce #(+ %1 %2) 0 (take 3 sort)))))

(comment
  (main))