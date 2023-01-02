(ns solution
  (:require [clojure.string :as str]))

;; Advent of Code 2022 - Day 15
;; Beacon Exclusion Zone
;;
;; The distress signal you received lead you to a series of tunnels.
;; You deploy sensors to check for where the distress beacons are coming from.
;; The sensors will fan out and rest in various tunnels and report back
;; their X and Y coordinates as well as the X and Y coordinates of the
;; nearest detected beacon.
;;
;; Part 1 -
;; We want to find gaps in the sensors' reach.
;; Evaluate the X coordinates when Y=2,000,000; how many X positions
;; cannot possibly have a beacon based on the coverage of the sensors?

;; To run:
;; - Start a repl (e.g. 'lein repl')
;; - Connect Calva to the running repl
;; - Run (main) in the repl

;; Helper to easily change the file in a central location for testing
(def INPUT-FILE "input.txt")

(defn distance
  "Calculate the Manhattan distance between two points.
   Note that the input is expected to be two maps that have
   :x and :y defined."
  [p1 p2]
  (let [x-dist (abs (- (:x p1) (:x p2)))
        y-dist (abs (- (:y p1) (:y p2)))]
    (+ x-dist y-dist)))

(defn str-to-point [value]
  (let [parts (str/split value #", ")
        x-str (first parts)
        x-idx (.indexOf x-str "x=")
        x-part (subs x-str (+ 2 x-idx))
        y-str (second parts) ;; Always just "y=....."
        y-part (subs y-str 2)]
    {:x (Integer/parseInt x-part)
     :y (Integer/parseInt y-part)}))

(comment
  (str-to-point "Sensor at x=123, y=456"))

(defn line-to-sensor
  "Given the string representation of a sensor and nearest beacon, returns a tuple
   (size 2 array) containing both the sensor and the beacon."
  [line]
  (let [info  (str/split line #": ")
        p1 (str-to-point (first info))
        p2 (str-to-point (second info))]
    [{:type :sensor
      :x (:x p1) 
      :y (:y p1) 
      :d (distance p1 p2)}
     {:type :beacon
      :x (:x p2)
      :y (:y p2)}]))

(comment
  (line-to-sensor "Sensor at x=246553, y=343125: closest beacon is at x=338784, y=1935796"))

(defn parse
  "Parses the input data lines into a set of sensors and beacons."
  [lines]
  (reduce (fn [acc line]
            (let [tuple (line-to-sensor line)]
              (conj acc (first tuple) (last tuple)))) (set []) lines))

(defn get-max-x
  [sensors]
  (reduce 
   (fn [acc sensor]
     (let [x-d (+ (:x sensor) (:d sensor))]
       (if (< acc x-d) 
         x-d
         acc)))
   0 sensors))

(defn get-min-x
  [sensors]
  (reduce
   (fn [acc sensor]
     (let [x-d (- (:x sensor) (:d sensor))]
       (if (> acc x-d)
         x-d
         acc)))
   0 sensors))

(defn eval-current [x y sensors]
  (if (= 0 (count sensors))
    false
    (let [sensor (first sensors)]
      (if (<= (distance 
               {:x x :y y}
               {:x (:x sensor) :y (:y sensor)}) 
              (:d sensor))
        true ; Position is within the range of the sensor
        ; Yes, recurse. It's ugly but it helps us quit early
        (eval-current x y (rest sensors))))))

(defn do-evaluate
  "Evaluate a specific x position. Adds 1 to the going count
   if the x and y coordinate is within range of any of the
   sensors."
  [acc x y sensors]
  (if (eval-current x y sensors)
      (+ acc 1)
      acc))

(defn evaluate-row
  "Evaluate the given row number to see how many coordinates in the row
   are with sensor coverage (points that could not have a beacon). Keep
   in mind that a beacon being in the row doesn't count."
  [row sensors start end]
  (let [row-xs (range start end)]
    (reduce
     (fn [acc idx] (do-evaluate acc idx row sensors))
     0
     row-xs)))

(defn do-part-1 [row sensors beacons start end]
  (println "Part 1:"
           (-
            (evaluate-row row sensors start end)
            (count (filter #(= row (:y %1)) beacons)))))

(defn main []
  (let [input   (slurp INPUT-FILE) ;; Line-delimited sensors and their beacons
        data    (str/split input #"\n")
        all     (parse data)
        sensors (filter #(= (:type %) :sensor) all)
        beacons (filter #(= (:type %) :beacon) all)
        row     2000000
        ; Filter down the sensors to check against that are even within range of the row
        sensors-within-d (filter #(<= (distance {:x (:x %) :y row} %) (:d %)) sensors)
        max-x   (get-max-x sensors-within-d)
        min-x   (get-min-x sensors-within-d)]
    (do-part-1 row sensors-within-d beacons min-x max-x)))