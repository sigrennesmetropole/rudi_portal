package org.rudi.facet.acl.helper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ProjectKeystoreSearchCriteria {

	private List<UUID> projectUuids;

	private OffsetDateTime minExpirationDate;

	private OffsetDateTime maxExpirationDate;

	private String clientId;
}
