package org.streamexperiments.cep.config;

public class Configuration {

    private Kafka kafka;

    public Kafka getKafka() {
        return kafka;
    }

    public void setKafka(Kafka kafka) {
        this.kafka = kafka;
    }

    public static class Kafka {

        private String producerTopic;
        private String consumerTopic;
        private String messageKey;
        private String groupId;
        private String autoOffsetReset;
        private boolean enableAutoCommit;
        private String bootstrapServers;
        private String zookeeperConnect;

        public Kafka(){}

        public String getMessageKey() {
            return messageKey;
        }

        public void setMessageKey(String messageKey) {
            this.messageKey = messageKey;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public boolean isEnableAutoCommit() {
            return enableAutoCommit;
        }

        public void setEnableAutoCommit(boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public String getBootstrapServers() {
            return bootstrapServers;
        }

        public void setBootstrapServers(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
        }

        public String getZookeeperConnect() {
            return zookeeperConnect;
        }

        public void setZookeeperConnect(String zookeeperConnect) {
            this.zookeeperConnect = zookeeperConnect;
        }

        public String getProducerTopic() {
            return producerTopic;
        }

        public void setProducerTopic(String producerTopic) {
            this.producerTopic = producerTopic;
        }

        public String getConsumerTopic() {
            return consumerTopic;
        }

        public void setConsumerTopic(String consumerTopic) {
            this.consumerTopic = consumerTopic;
        }
    }

}
