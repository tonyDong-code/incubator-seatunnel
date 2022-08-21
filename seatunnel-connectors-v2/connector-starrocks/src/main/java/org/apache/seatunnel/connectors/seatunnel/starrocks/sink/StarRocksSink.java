package org.apache.seatunnel.connectors.seatunnel.starrocks.sink;

import com.google.auto.service.AutoService;
import org.apache.seatunnel.api.common.PrepareFailException;
import org.apache.seatunnel.api.sink.SeaTunnelSink;
import org.apache.seatunnel.api.sink.SinkWriter;
import org.apache.seatunnel.api.table.type.SeaTunnelDataType;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import org.apache.seatunnel.shade.com.typesafe.config.Config;

import java.io.IOException;

@AutoService(SeaTunnelSink.class)
public class StarRocksSink implements SeaTunnelSink<SeaTunnelRow, Void, Void, Void>{
    private static final String PLUGIN_NAME = "StarRocks";
    private Config config;
    private SeaTunnelRowType seaTunnelRowType;

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public void prepare(Config pluginConfig) throws PrepareFailException {
        this.config = pluginConfig;
    }

    @Override
    public void setTypeInfo(SeaTunnelRowType seaTunnelRowType) {
        this.seaTunnelRowType = seaTunnelRowType;
    }

    @Override
    public SeaTunnelDataType<SeaTunnelRow> getConsumedType() {
        return this.seaTunnelRowType;
    }

    @Override
    public SinkWriter<SeaTunnelRow, Void, Void> createWriter(SinkWriter.Context context) throws IOException {
        return new StarRocksSinkWriter();
    }
}
