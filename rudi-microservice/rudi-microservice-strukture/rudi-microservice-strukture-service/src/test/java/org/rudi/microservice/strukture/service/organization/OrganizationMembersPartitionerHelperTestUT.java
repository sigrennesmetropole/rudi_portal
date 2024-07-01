package org.rudi.microservice.strukture.service.organization;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMemberSort;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersPartitionerHelper;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
class OrganizationMembersPartitionerHelperTestUT {

	@Autowired
	private OrganizationMembersPartitionerHelper organizationMembersPartitionerHelper;

	@Autowired
	private OrganizationDao organizationDao;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	DatasetService datasetService;

	@AfterEach
	void tearDown() {
		organizationDao.deleteAll();
	}

	@Test
	void getOrganizationMembersPartition_splits() {
		int partitionSize = 10;

		Set<OrganizationMemberEntity> members = new HashSet<>();
		for (int i = 0; i < 43; i++) {
			OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
			organizationMember.setUserUuid(UUID.randomUUID());
			organizationMember.setRole(OrganizationRole.EDITOR);
			organizationMember.setAddedDate(LocalDateTime.now());
			members.add(organizationMember);
		}

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setDescription("organisation");
		organization.setName("mon organisation");
		organization.setMembers(members);
		organizationDao.save(organization);

		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();
		criteria.setOrganizationUuid(organization.getUuid());

		List<Pageable> partitions = organizationMembersPartitionerHelper.getOrganizationMembersPartition(criteria,
				partitionSize);
		assertFalse(CollectionUtils.isEmpty(partitions));

		// On attend 5 partitions :
		//  - 0 -> 9
		//  - 10 -> 19
		//  - 20 -> 29
		//  - 30 -> 39
		//  - 40 -> 49 (ici 42 -> car 43 membres)
		assertEquals(5, partitions.size());
		List<Pageable> sortedPartititons = partitions.stream().sorted(this::compare).collect(Collectors.toList());
		for (int i = 0; i < sortedPartititons.size(); i++) {
			Pageable partition = sortedPartititons.get(i);
			assertEquals((long) i * partitionSize, partition.getOffset());
			assertEquals(partitionSize, partition.getPageSize());
		}
	}

	@Test
	void partitionToEnrichedMembers_gets_members() {

		List<User> users = new ArrayList<>();
		for (int i = 0; i < 22; i++) {
			User user = new User().uuid(UUID.randomUUID()).login(RandomStringUtils.random(6))
					.lastname(RandomStringUtils.random(6)).firstname(RandomStringUtils.random(6))
					.lastConnexion(LocalDateTime.now());
			users.add(user);
		}
		Map<UUID, User> usersByUuid = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

		Set<OrganizationMemberEntity> members = new HashSet<>();
		for (User user : users) {
			OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
			organizationMember.setUserUuid(user.getUuid());
			organizationMember.setRole(OrganizationRole.EDITOR);
			organizationMember.setAddedDate(LocalDateTime.now());
			members.add(organizationMember);
		}
		Map<UUID, OrganizationMemberEntity> membersByUserUuid = members.stream()
				.collect(Collectors.toMap(OrganizationMemberEntity::getUserUuid, Function.identity()));

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setDescription("organisation");
		organization.setName("mon organisation");
		organization.setMembers(members);
		organizationDao.save(organization);

		int partitionSize = 10;
		Pageable partition = PageRequest.of(0, partitionSize);
		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();
		criteria.setOrganizationUuid(organization.getUuid());

		// On mocke la partie ACL, on se moque de savoir si les users sont filtrés ou pas, on vérifie dans ce test
		// que l'ensemble des opérations se déroule bien
		when(aclHelper.searchUsersWithCriteria(any(), any(), any(), any())).thenReturn(users);

		List<OrganizationUserMember> membersEnriched = organizationMembersPartitionerHelper
				.partitionToEnrichedMembers(partition, criteria);
		assertFalse(CollectionUtils.isEmpty(membersEnriched));
		assertEquals(partitionSize, membersEnriched.size());
		for (OrganizationUserMember enriched : membersEnriched) {
			User correspondingUser = usersByUuid.get(enriched.getUserUuid());
			OrganizationMemberEntity correspondingMember = membersByUserUuid.get(enriched.getUserUuid());
			assertNotNull(correspondingUser);
			assertEquals(correspondingUser.getLogin(), enriched.getLogin());
			assertEquals(correspondingUser.getFirstname(), enriched.getFirstname());
			assertEquals(correspondingUser.getLastname(), enriched.getLastname());
			assertEquals(correspondingUser.getLastConnexion(), enriched.getLastConnexion());
			assertEquals(correspondingMember.getAddedDate().truncatedTo(ChronoUnit.SECONDS),
					enriched.getAddedDate().truncatedTo(ChronoUnit.SECONDS));
			assertEquals(correspondingMember.getRole().toString(), enriched.getRole().toString());
		}
	}

	@Test
	void extractPage_extracts_pages() throws AppServiceBadRequestException {
		List<OrganizationUserMember> enricheds = new ArrayList<>();
		for (int i = 0; i < 22; i++) {
			OrganizationUserMember enriched = new OrganizationUserMember();
			enriched.setLogin(RandomStringUtils.random(6));
			enriched.setFirstname(RandomStringUtils.random(6));
			enriched.setLastname(RandomStringUtils.random(6));
			enriched.setAddedDate(LocalDateTime.now());
			enriched.setLastConnexion(LocalDateTime.now());
			enriched.setRole(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR);
			enricheds.add(enriched);
		}

		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 10));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(10, page.getContent().size());
		List<OrganizationUserMember> page1Content = page.getContent();

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 10));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		List<OrganizationUserMember> page2Content = page.getContent();
		assertTrue(page1Content.stream().noneMatch(page2Content::contains));
	}

	@Test
	void extractPage_elements_are_sorted_by_firstName() throws AppServiceBadRequestException {

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().firstname("Cquelquechose");
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().firstname("Aquelquechose");
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().firstname("Bquelquechose");
		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, OrganizationMemberSort.FIRSTNAME.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("Aquelquechose", page.getContent().get(0).getFirstname());
		assertEquals("Bquelquechose", page.getContent().get(1).getFirstname());

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 2, sort));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("Cquelquechose", page.getContent().get(0).getFirstname());
	}

	@Test
	void extractPage_elements_are_sorted_by_lastName() throws AppServiceBadRequestException {

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().lastname("Cquelquechose");
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().lastname("aquelquechose");
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().lastname("Bquelquechose");
		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, OrganizationMemberSort.LASTNAME.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("aquelquechose", page.getContent().get(0).getLastname());
		assertEquals("Bquelquechose", page.getContent().get(1).getLastname());

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 2, sort));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("Cquelquechose", page.getContent().get(0).getLastname());
	}

	@Test
	void extractPage_elements_are_sorted_by_login_reversed() throws AppServiceBadRequestException {

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().login("cquelquechose");
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().login("Aquelquechose");
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().login("Bquelquechose");
		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, OrganizationMemberSort.LOGIN.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("cquelquechose", page.getContent().get(0).getLogin());
		assertEquals("Bquelquechose", page.getContent().get(1).getLogin());

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 2, sort));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals("Aquelquechose", page.getContent().get(0).getLogin());
	}

	@Test
	void extractPage_elements_are_sorted_by_last_connexion() throws AppServiceBadRequestException {

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime firstDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0, 0);
		LocalDateTime secondDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 2,
				0);
		LocalDateTime thirdDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 5, 0);

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().lastConnexion(thirdDate);
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().lastConnexion(firstDate);
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().lastConnexion(secondDate);
		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, OrganizationMemberSort.LAST_CONNEXION.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(firstDate, page.getContent().get(0).getLastConnexion());
		assertEquals(secondDate, page.getContent().get(1).getLastConnexion());

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 2, sort));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(thirdDate, page.getContent().get(0).getLastConnexion());
	}

	@Test
	void extractPage_elements_are_sorted_by_added_date_reversed() throws AppServiceBadRequestException {

		LocalDateTime today = LocalDateTime.now();
		LocalDateTime firstDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0, 0);
		LocalDateTime secondDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 2,
				0);
		LocalDateTime thirdDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 5, 0);

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().addedDate(thirdDate);
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().addedDate(firstDate);
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().addedDate(secondDate);
		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, OrganizationMemberSort.ADDED_DATE.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(thirdDate, page.getContent().get(0).getAddedDate());
		assertEquals(secondDate, page.getContent().get(1).getAddedDate());

		page = organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(1, 2, sort));
		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(firstDate, page.getContent().get(0).getAddedDate());
	}

	@Test
	void extractPage_elements_are_sorted_by_role() throws AppServiceBadRequestException {

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember()
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR);
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember()
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR);
		enricheds.add(a);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, OrganizationMemberSort.ROLE.getValue()));
		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 2, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR,
				page.getContent().get(0).getRole());
		assertEquals(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR,
				page.getContent().get(1).getRole());
	}

	@Test
	@Disabled
	void extractPage_elements_are_sorted_on_mutli_sort() throws AppServiceBadRequestException {

		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember().login("Cquelquechose")
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR);
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember().login("Bquelquechose")
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR);
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember().login("Aquelquechose")
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR);

		enricheds.add(b);

		Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, OrganizationMemberSort.ROLE.getValue()),
				new Sort.Order(Sort.Direction.ASC, OrganizationMemberSort.LOGIN.getValue()));

		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 3, sort));

		assertNotNull(page);
		assertFalse(CollectionUtils.isEmpty(page.getContent()));
		assertEquals(3, page.getContent().size());
		OrganizationUserMember firstMember = page.getContent().get(0);
		OrganizationUserMember secondMember = page.getContent().get(1);
		OrganizationUserMember thirdMember = page.getContent().get(2);
		assertEquals(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR, firstMember.getRole());
		assertEquals("Aquelquechose", firstMember.getLogin());
		assertEquals(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR, secondMember.getRole());
		assertEquals("Bquelquechose", secondMember.getLogin());
		assertEquals(org.rudi.microservice.strukture.core.bean.OrganizationRole.ADMINISTRATOR, thirdMember.getRole());
		assertEquals("Cquelquechose", thirdMember.getLogin());
	}

	@Test
	void extractPage_ok_when_no_sort() throws AppServiceBadRequestException {
		List<OrganizationUserMember> enricheds = new ArrayList<>();
		OrganizationUserMember c = new OrganizationUserMember();
		enricheds.add(c);
		OrganizationUserMember a = new OrganizationUserMember();
		enricheds.add(a);
		OrganizationUserMember b = new OrganizationUserMember();
		enricheds.add(b);

		Sort sort = Sort.unsorted();

		Page<OrganizationUserMember> page = organizationMembersPartitionerHelper.extractPage(enricheds,
				PageRequest.of(0, 3, sort));
		assertNotNull(page);
		assertTrue(CollectionUtils.isNotEmpty(page.getContent()));
		assertEquals(3, page.getContent().size());
	}

	@Test
	void extractPage_badRequest_on_unknown_order() {
		List<OrganizationUserMember> enricheds = new ArrayList<>();
		Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "dklfjdsklfjs"));
		assertThrows(AppServiceBadRequestException.class,
				() -> organizationMembersPartitionerHelper.extractPage(enricheds, PageRequest.of(0, 2, sort)));
	}

	/**
	 * Comparaison entre 2 partitions pour tri
	 *
	 * @param partition1 première partition
	 * @param partition2 deuxième partition
	 * @return ordre de tri par rapport à l'un
	 */
	private int compare(Pageable partition1, Pageable partition2) {
		if (partition1.getOffset() < partition2.getOffset()) {
			return -1;
		} else if (partition1.getOffset() > partition2.getOffset()) {
			return 1;
		}

		return 0;
	}
}
