(ns parser
  (:import java.util.Date java.io.RandomAccessFile java.nio.channels.FileChannel))

(defn
  safe-char
  "Converts int to char without throwing an exception if the integer
  has a negative value. Used for parsing the main binary file."
  [c] (if (> c 0) (char c) c))


(defn read-field
  [buf n type]

  (defn null-string
    "Read a nul-terminated string. Stop at  or at
     length n, whichever comes first."
    [buf n]
    (let [bytes (doall (for [_ (range n)] (char (.get buf))))]
      (apply str (take-while #(not= % \u 0000) bytes))))

  (defn read-field-aux
    "Read n data and return it as a vector if n is greater than 1,
     as a vector otherwise"
    [n type]
    (let [f ({:byte (memfn get)
              :word (memfn getShort)
              :dword (memfn getInt)} type)
          vec (into [] (for [_ (range n)] (f buf)))]
      (if (= n 1)
        (first vec)
        vec)))

  (cond
    (= type :string) (null-string buf n)
    (some #{type} [:byte :word :dword]) (read-field-aux n type)))

(defn parse-buffer
  "A v-form is a vector of the form: [:field-name length :type func?]
   Each v-form is read from buf and the whole data is return as a map
   If a field-name is nil, the data is not returned (but the field is
   read nonetheless to move forward into the buffer."
  [buf & v-forms]
  (apply
    hash-map
    (mapcat (fn [[field-name size type func]]
              (let [data (read-field buf size type)]
                (if (nil? field-name)
                  nil
                  [field-name (if func
                    (func data)
                    data)])))
      v-forms)))

(defn parse-players-data [data] data)

(defn parse-headers
  [buf]
  (parse-buffer buf
    [:game-engine 1 :byte]
    [:game-frames 1 :dword]
    [nil 3 :byte]
    [:save-time 1 :dword #(Date. (long (* 1000 %)))]
    [nil 12 :byte]
    [:game-name 28 :string]
    [:map-width 1 :word]
    [:map-height 1 :word]
    [nil 16 :byte]
    [:creator-name 24 :string]
    [nil 1 :byte]
    [:map-name 26 :string]
    [nil 38 :byte]
    [:players-data 432 :byte parse-players-data]
    [:player-spot-color 8 :dword]
    [:player-spot-index 8 :byte]))


;
;    File file = new File("filename");
;    // Create a read-only memory-mapped file
;    FileChannel roChannel = new RandomAccessFile(file, "r").getChannel();
;
;    ByteBuffer readonlybuffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel
;        .size());

(defn file-channel
  "Returns a read only FileChannel for a given file name"
  [filename]
  (. (RandomAccessFile. "replay.SC2Replay" "r") getChannel))

(defn file-channel-to-byte
  "Converts FileChannel to ByteBuffer"
  [channel]
  (. channel map (java.nio.channels.FileChannel$MapMode/READ_ONLY) 0 (.size channel)))

(defn file-to-byte
  "Returns a ByteBuffer for a given filename"
  [filename]
  (file-channel-to-byte (file-channel filename)))


(parse-headers (file-to-byte "replay.SC2Replay"))
