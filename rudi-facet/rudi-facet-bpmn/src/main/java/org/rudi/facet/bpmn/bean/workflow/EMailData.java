/**
 * 
 */
package org.rudi.facet.bpmn.bean.workflow;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class EMailData {

	public static final String FILE_PREFIX = "file:";

	private String subject;

	private String body;

	private Map<String, Object> data;

	public EMailData(String subject, String body) {
		this.subject = subject;
		this.body = body;
	}

	public void addData(String key, Object value){
		if (data == null) {
			data = new HashMap<>();
		}
		data.put(key, value);
	}

	public boolean hasSubject() {
		return StringUtils.isNotEmpty(getSubject());
	}

	public boolean isSubjectFile() {
		return subject != null && subject.startsWith(FILE_PREFIX);
	}

	public boolean hasBody() {
		return StringUtils.isNotEmpty(getBody());
	}

	public boolean isBodyFile() {
		return body != null && body.startsWith(FILE_PREFIX);
	}

	public String getBodyFile() {
		if (isBodyFile()) {
			return getItemFile(getBody());
		} else {
			return null;
		}
	}

	public String getSubjectFile() {
		if (isSubjectFile()) {
			return getItemFile(getSubject());
		} else {
			return null;
		}
	}

	protected String getItemFile(String input) {
		return input.substring(FILE_PREFIX.length());
	}

}
