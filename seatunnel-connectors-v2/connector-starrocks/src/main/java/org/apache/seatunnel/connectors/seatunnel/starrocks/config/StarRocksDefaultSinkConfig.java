package org.apache.seatunnel.connectors.seatunnel.starrocks.config;

public enum StarRocksDefaultSinkConfig {

    /**
     *
     */
    JDBC_URL("jdbc-url", null),
    /**
     *
     */
    LOAD_URL("load-url", null),
    DATABASE("database-name", null),
    TABLE("table-name", null),
    USERNAME("username", null),
    PASSWORD("password", null),
    SINK_SEMATIC("sink_sematic", "at-least-once"),
    SINK_BUFFER_FLUSH_MAX_BYTES("sink.buffer-flush.max-bytes", "94371840"),
    SINK_BUFFER_FLUSH_MAX_ROWS("sink.buffer-flush.max-rows", "500000"),
    SINK_BUFFER_FLUSH_INTERVAL_MS("sink.buffer-flush.interval-ms", "300000"),
    SINK_MAX_RETRIES("sink.max-retries", "1"),
    SINK_CONNECT_TIMEOUT_MS("sink.connect.timeout-ms", "1000"),
    SINK_PROPERTIES_FORMAT("sink.properties.format", "JSON"),
    SINK_PROPERTIES_COLUMNS("sink.properties.columns", null),
    SINK_PROPERTIES_COLUMN_SEPARATOR("sink.properties.column_separator", "\\x01"),
    SINK_PROPERTIES_ROW_DELIMITER("sink.properties.row_delimiter", "\\x02"),
    SINK_PROPERTIES_IGNORE_JSON_SIZE("sink.properties.ignore_json_size", "false"),
    SINK_PROPERTIES_MAX_FILTER_RATIO("sink.properties.max_filter_ratio", "0"),
    SINK_PROPERTIES_TIMEOUT("sink.properties.timeout", "600"),
    SINK_PROPERTIES_EXEC_MEM_LIMIT("sink.properties.exec_mem_limit", "2097152"),
    SINK_PROPERTIES_JSON_PATHS("sink.properties.jsonpaths", null),
    SINK_PROPERTIES_STRIP_OUTER_ARRAY("sink.properties.strip_outer_array", "true"),
    SINK_PROPERTIES_JSON_ROOT("sink.properties.json_root", "");

    private final String key;
    private final String defaultValue;
    public String value;

    StarRocksDefaultSinkConfig(String key, String defaultValue){
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getKey(){
        return key;
    }

    public String getDefaultValue(){
        return defaultValue;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public static void setValue(String key, String value){
        for (StarRocksDefaultSinkConfig starRocksConfig : StarRocksDefaultSinkConfig.values()) {
            if (starRocksConfig.getKey().equals(key)){
                starRocksConfig.setValue(value);
            }
        }
    }

}
