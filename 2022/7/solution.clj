(ns solution
  (:require [clojure.string :as str]))

;; Advent of Code 2022 - Day 7
;; No Space Left On Device
;;
;; Looks like the device you were given on day 6
;; doesn't have enough memory to perform a system update.
;; The input is a series of commands exploring the device
;; hard drive to map out its files and drives.
;;
;; Part 1 -
;; Return the sum of the size of all directories where
;; the directory size is no bigger than 100,000. Note
;; that you can sum nested directories (e.g. if b/ is
;; within a/, then you can still count both).

;; To run:
;; - Start a repl (e.g. 'lein repl')
;; - Connect Calva to the running repl
;; - Run '(main)' in the repl

(defn parse-command [command]
  (let [pieces (str/split command #" ")
        start  (first pieces)
        next   (second pieces)]
    (if (= "$" start)
      ;; We know it's either a 'cd' or an 'ls'
      (if (= "cd" next)
        ;; If it's a "change-directory" command
        {:type  :cd
         :value (last pieces)}
        ;; Otheriwse, it's just a "list" command (i.e. skip it)
        {:type :ls})
      ;; It's a file/directory
      (if (= "dir" start)
        {:type  :dir
         :value next}
        {:type  :file
         :size  (Integer/parseInt start)
         :value next}))))

;; Construct the file system as a map where $size will
;; be the key for the size of the directory:
;; {:a {:$size ### :b ### :c {}}}
;; :a - directory of size ###
;; :b - file of size ###
;; :c - nested directory
(defn make-file-system [input]
  (let [commands (str/split input #"\n")]
    (reduce
     (fn [acc line]
       (let [command (parse-command line)
             type    (:type command)
             value   (:value command)
             path    (:path acc)
             dir     (get-in acc path)] ;; Get the directory if it exists
         (cond
           (= type :cd) (if (= ".." value)
                          ;; Remove one from the path
                          (assoc acc :path (pop path))
                          ;; Otherwise, update the current path
                          (assoc acc :path (conj path value)))
           (= type :dir) (if (nil? (get dir value))
                           ;; Associate if not in the file system yet
                           (assoc-in acc path (assoc dir value {}))
                           acc)
           (= type :file) (assoc-in acc path (assoc dir value (:size command)))
           ;; Otherwise, must be an "ls" command, so we'll ignore it since
           ;; it basically just tells us the next lines are in the current
           ;; path in the directory
           :else acc)))
     ;; We'll start with a map of the file system that contains a top-level
     ;; key of the current path in the file system as we construct it.
     ;; Note that we expect the first command to always be "cd /"
     {:path [] "/" {}}
     commands)))

;; Fun function to recursively traverse the file system and get and set the
;; size of each directory
(defn size-directory [file-system path-to-dir]
  (let [dir (get-in file-system path-to-dir)]
    (reduce-kv
     (fn [acc k v]
       ;; Get the current size of the directory
       (let [cur-size (or (get-in acc (conj path-to-dir :size)) 0)
             ;; Recurse if the key referse to a nested directory and
             ;; calculate it's size before popping back!
             new-acc  (if (map? v)
                        (size-directory acc (conj path-to-dir k))
                        acc)
             ;; The current key-size is either the file size value or
             ;; the previously-calculated directory :size
             k-size   (if (number? v)
                        v
                        (:size (get-in new-acc (conj path-to-dir k))))]
         (assoc-in new-acc (conj path-to-dir :size) (+ cur-size k-size))))
     ;; The file-system is the accumulator since we want to maintain all
     ;; :size updates to the system
     file-system
     ;; Iterate over the current directory only
     dir)))

(defn part-one [file-system path-to-dir]
  (let [dir (get-in file-system path-to-dir)
        ;; Only keep the sum if it's less than 100,000
        sum (if (<= 100000 (:size dir)) 0 (:size dir))]
    ;; We only need to recursively grab :size values when
    ;; we have a nested directory, which we'll add to the going sum
    (reduce-kv
     (fn [acc k v]
       (if (map? v)
         (+ acc (part-one file-system (conj path-to-dir k)))
         acc))
     sum
     dir)))

(defn main []
  (let [input       (slurp "input.txt")
        file-system (make-file-system input)
        ;; Congrats, we now have a file system.
        ;; Next, let's calculate the size of each directory.
        sized-system (size-directory (dissoc file-system :path) ["/"])
        ;; Now let's get the sum for part-one
        p1 (part-one sized-system ["/"])]
    p1))

(comment
  (main))