(ns solution
  (:require [clojure.string :as str])
  (:import [java.util UUID]))

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
;;
;; Part 2 -
;; The handheld device is indicating the distress signal is coming from
;; a more restricted region of 0 to 4,000,000 for both X and Y. There
;; should only be one spot in that region that isn't picked up by any sensor.
;; What is the tuning frequency of the one undetected spot:
;; (X * 4,000,000) + Y?

;; To run:
;; - Start a repl (e.g. 'lein repl')
;; - Connect Calva to the running repl
;; - Run (main) in the repl

(def USE-SAMPLE false)

;; Helper to easily change the file in a central location for testing
(def INPUT-FILE (if USE-SAMPLE
                  "sample_input.txt"
                  "input.txt"))

;;;;;;;;;;;;;;;;;;;;;
;; Input Parsing Code
;; (Both Parts)
;;;;;;;;;;;;;;;;;;;;;

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

;;;;;;;;;;;;;;
;; Part 1 Code
;;;;;;;;;;;;;;

;; Get the max X position + beacon distance across all sensors.
;; This is the maximum X position we'll need to end checking at.
(defn get-max-x
  [sensors]
  (reduce 
   (fn [acc sensor]
     (let [x-d (+ (:x sensor) (:d sensor))]
       (if (< acc x-d) 
         x-d
         acc)))
   0 sensors))

;; Get the min X position - beacon distance across all sensors.
;; This is the minimum X position we'll need to start checking from.
(defn get-min-x
  [sensors]
  (reduce
   (fn [acc sensor]
     (let [x-d (- (:x sensor) (:d sensor))]
       (if (> acc x-d)
         x-d
         acc)))
   0 sensors))

;; Checks to see if the current (x, y) coordinate can be
;; detected by any of the sensors.
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

(defn do-part-1 [sensors beacons]
  (let [row (if USE-SAMPLE 10 2000000)
        ; Filter down the sensors to check against that are even within range of the row
        sensors-within-d (filter #(<= (distance {:x (:x %) :y row} %) (:d %)) sensors)
        start (get-min-x sensors-within-d)
        end   (get-max-x sensors-within-d)]
    (println "Part 1:"
             (-
              (evaluate-row row sensors-within-d start end)
              (count (filter #(= row (:y %1)) beacons))))))

;;;;;;;;;;;;;;
;; Part 2 Code
;;;;;;;;;;;;;;

(defn check-pos [cur-pos sensors min-val max-val x-inc y-inc]
  ; If we have an X position (not nil) then we found something already,
  ; so pop out of the evaluation
  (if (and (:x cur-pos) (:y cur-pos))
    cur-pos
    ; Else, evaluate
    (let [x (:cur-x cur-pos)
          y (:cur-y cur-pos)]
      (if (and
           (and (>= x min-val) (<= x max-val))
           (and (>= y min-val) (<= y max-val)))
        (if (eval-current x y sensors) ; If detected?
          ; "Increment" the X and Y positions
          (merge cur-pos {:cur-x (x-inc x) :cur-y (y-inc y)})
          ; Not detected?! Return the coords
          (merge cur-pos {:x x :y y}))
        ; Failure case if the point is outside the box
        (merge cur-pos {:cur-x (x-inc x) :cur-y (y-inc y)})))))

;; Going from (X - dist, Y) to (X, Y - dist)
(defn check-sensor-with-direction [start-x start-y dist inc-x inc-y sensors min-val max-val]
  (let [evaluation
        (reduce (fn [cur-pos _]
                  (check-pos cur-pos sensors min-val max-val inc-x inc-y))
                {:cur-x start-x ; Start positions
                 :cur-y start-y
                 ; We're aiming to populate these
                 :x nil
                 :y nil}
                (range 1 dist))]
    (if (:x evaluation)
      [(:x evaluation) (:y evaluation)]
      nil)))

;; I know what you're thinking when you see this function...
;; "Why, Eli? Why?"
;; Well I couldn't think of a cleaner way to do this in Clojure.
;; Additionally, it's not entirely performance friendly which is a bummer
;; since we evaluate all of the X-extremes twice. It's negligible though.
(defn check-sensor [x-y sensor sensors min-val max-val]
  ;; First check and see if 'val' has a coordinate yet, where if we have one
  ;; already then pop back out of the evaluation
  (if (seq x-y)
    x-y
    ;; Else, evaluate this sensor
    (let [dist (:d sensor)
          x (:x sensor)
          y (:y sensor)
          x-y2 (check-sensor-with-direction
                (- x dist 1) y dist
                ; Go west to north
                (partial + 1) ; x + 1 each step
                (partial + -1) ; y - 1 each step
                sensors min-val max-val)]
      (if x-y2
        x-y2
        (let [x-y3 (check-sensor-with-direction
                    (- x dist 1) y dist
                    (partial + 1) ; Go west to south
                    (partial + 1)
                    sensors min-val max-val)]
          (if x-y3
            x-y3
            (let [x-y4 (check-sensor-with-direction
                        (+ x dist 1) y dist
                        (partial - 1) ; Go east to north
                        (partial - 1)
                        sensors min-val max-val)]
              (if x-y4
                x-y4
                (let [x-y5 (check-sensor-with-direction
                            (+ x dist 1) y dist
                            (partial + -1) ; Go east to south
                            (partial + 1)
                            sensors min-val max-val)]
                  (or x-y5 []))))))))))

;; Wrapper around a "reduce" call to check each individual sensor
(defn find-undetectable-x-y [sensors min-val max-val]
  (reduce (fn [x-y sensor]
            ; Filter out the current sensor we're evaluating out of range for
            (let [filtered-sensors (filter #(not= (:id sensor) (:id %)) sensors)]
              (check-sensor
               x-y
               sensor ; Current sensor to evaluate
               filtered-sensors
               min-val
               max-val)))
          [] ; Start value, tuple of (x, y)
          sensors))

;; The strategy here is to go through each sensor and check only the spots just outside
;; of each sensor's range. As soon as we find a single spot that isn't detected by any
;; of the sensors, then that's what we'll use.
(defn do-part-2 [sensors]
  (let [min-val 0
        max-val (if USE-SAMPLE 20 4000000)
        ; Associate sensors with an ID so we don't evaluate the same sensor within itself
        sensors-with-ids (map #(assoc % :id (UUID/randomUUID)) sensors)
        position (find-undetectable-x-y sensors-with-ids min-val max-val)
        ; Extract the (x, y) with default values of 0 in case of errors
        x (or (first position) 0)
        y (or (second position) 0)]
    (println "Part 2:" (+ (* x 4000000) y))))

;;;;;;;;;;;;;;
;; Code to Run
;;;;;;;;;;;;;;

(defn main []
  (let [input   (slurp INPUT-FILE) ;; Line-delimited sensors and their beacons
        data    (str/split input #"\n")
        all     (parse data)
        sensors (filter #(= (:type %) :sensor) all)
        beacons (filter #(= (:type %) :beacon) all)]
    (do-part-1 sensors beacons)
    (do-part-2 sensors)))