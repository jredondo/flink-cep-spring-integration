package org.streamexperiments.cep.operators;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.streamexperiments.cep.serialization.UpdateSerializationSchema;
import org.streamexperiments.models.Update;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefinedFunctions {
    private static Logger logger = LogManager.getLogger(DefinedFunctions.class);

    public static class WindowedUpdatesFunction implements WindowFunction<Update, Collection<Update>, String, TimeWindow> {

        @Override
        public void apply(String key, TimeWindow window, Iterable<Update> updates, Collector<Collection<Update>> out) {
            Set<Update> set = new HashSet<>();

            for(Update update: updates) {
                set.add(update);
            }

            //logger.info("Window processing " + set.size() + " updates.");
        }
    }

    public static class LogFlatMapFunction implements FlatMapFunction<Update, Update> {
        @Override
        public void flatMap(Update update, Collector<Update> out) {
            //logger.info("TESTING flatMap: " + update.toString());
            out.collect(update);
        }
    }

}
