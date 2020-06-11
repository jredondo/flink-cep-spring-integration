/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streamexperiments.config.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Kafka configuration.
 */
@ConfigurationProperties("kafka")
@ConditionalOnProperty(value="kafka.enabled", havingValue = "true")
public class KafkaAppProperties {

	private String groupId;

	private String topic;

	private String messageKey;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessageKey() {
		return this.messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

}
