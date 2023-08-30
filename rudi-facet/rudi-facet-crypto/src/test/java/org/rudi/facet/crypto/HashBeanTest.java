/**
 * RUDI Portail
 */
package org.rudi.facet.crypto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashBeanTest {

	private String field1;

	private boolean field2;

	private LocalDateTime field3;

	private List<Long> field4;
}
