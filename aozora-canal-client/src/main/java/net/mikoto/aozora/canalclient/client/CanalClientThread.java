package net.mikoto.aozora.canalclient.client;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.mikoto.aozora.canalclient.client.support.BeginningSupport;
import net.mikoto.aozora.canalclient.model.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author mikoto
 * &#064;date 2024/3/23
 * Create for aozora
 */
@Log
@Component
public class CanalClientThread implements Runnable {
    private final Config config;
    private final BeginningSupport beginningSupport;

    @Autowired
    public CanalClientThread(Config config, BeginningSupport support) {
        this.config = config;
        this.beginningSupport = support;
    }

    @Override
    @SneakyThrows
    public void run() {
        CanalConnector canalConnector= CanalConnectors.newSingleConnector(new InetSocketAddress(config.getHost(), config.getPort()),
                config.getDestinations(), config.getUsername(), config.getPassword());
        canalConnector.connect();
        canalConnector.subscribe(config.getDatabase() + ".*");
        while (true) {
            Message message = canalConnector.get(100);
            List<CanalEntry.Entry> entries = message.getEntries();

            if (entries.isEmpty()) {
                Thread.sleep(5000);
                continue;
            }

            for (CanalEntry.Entry entry : entries) {
                String tableName = entry.getHeader().getTableName();
                CanalEntry.EntryType entryType = entry.getEntryType();
                ByteString storeValue = entry.getStoreValue();

                if (!CanalEntry.EntryType.ROWDATA.equals(entryType)) {
                    continue;
                }

                CanalEntry.RowChange rowChange;
                try {
                    rowChange = CanalEntry.RowChange.parseFrom(storeValue);
                } catch (InvalidProtocolBufferException e) {
                    throw new RuntimeException(e);
                }
                List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();
                CanalEntry.EventType eventType = rowChange.getEventType();

                for (CanalEntry.RowData rowData : rowDataList) {
                    JSONObject beforeData = new JSONObject();
                    beforeData.fluentPut("tableName", tableName);
                    beforeData.fluentPut("eventType", eventType.name());
                    for (CanalEntry.Column column : rowData.getBeforeColumnsList())
                    {
                        beforeData.fluentPut(column.getName(),column.getValue());
                    }

                    JSONObject afterData = new JSONObject();
                    afterData.fluentPut("tableName", tableName);
                    afterData.fluentPut("eventType", eventType.name());
                    for (CanalEntry.Column column : rowData.getAfterColumnsList())
                    {
                        afterData.fluentPut(column.getName(),column.getValue());
                    }

                    if (!eventType.name().equals("DELETE")) {
                        beginningSupport.support(beforeData, afterData);
                    }
                }
            }
        }
    }
}
