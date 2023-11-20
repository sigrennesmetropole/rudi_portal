/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rudi.bpmn.core.bean.Action;
import org.rudi.bpmn.core.bean.FormDefinition;
import org.rudi.bpmn.core.bean.FormSectionDefinition;
import org.rudi.bpmn.core.bean.ProcessDefinition;
import org.rudi.bpmn.core.bean.ProcessFormDefinition;
import org.rudi.bpmn.core.bean.SectionDefinition;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.BpmnSpringBootTest;
import org.rudi.facet.bpmn.bean.AssetDescription1TestData;
import org.rudi.facet.bpmn.bean.AssetDescription2TestData;
import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.rudi.facet.bpmn.bean.workflow.TaskSearchCriteria1TestBean;
import org.rudi.facet.bpmn.dao.workflow.AssetDescription2TestDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescription2TestEntity;
import org.rudi.facet.bpmn.exception.BpmnInitializationException;
import org.rudi.facet.bpmn.exception.FormConvertException;
import org.rudi.facet.bpmn.exception.FormDefinitionException;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.workflow.AssetDescription2TestWorkflowHelper;
import org.rudi.facet.bpmn.mapper.workflow.AssetDescriptionMapper2Test;
import org.rudi.facet.bpmn.service.impl.TaskService1TestImpl;
import org.rudi.facet.bpmn.service.impl.TaskService2TestImpl;
import org.rudi.facet.generator.model.GenerationFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.annotation.Rollback;

/**
 * @author FNI18300
 *
 */
@BpmnSpringBootTest
//@Sql(scripts = {
//		"classpath:org.activiti.db.drop/activiti.h2.drop.engine.sql",
//		"classpath:org.activiti.db.drop/activiti.h2.drop.history.sql",
//}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Rollback(true)
class TaskServiceUT {

	@Autowired
	private InitializationService initializationService;

	@MockBean
	private UtilContextHelper utilContextHelper;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	protected JavaMailSenderImpl javaMailSender;

	@Autowired
	private TaskService2TestImpl test2TaskService;

	@Autowired
	private TaskService1TestImpl test1TaskService;

	@Autowired
	private TaskQueryService<TaskSearchCriteria1TestBean> taskQueryService;

	@Autowired
	private FormService formService;

	@Autowired
	private AssetDescription2TestDao test2AssetDescriptionDao;

	@Autowired
	private AssetDescription2TestWorkflowHelper test2AssetDescriptionHelper;

	@Autowired
	private AssetDescriptionMapper2Test test2AssetDescriptionMapper;

	@Autowired
	private JsonResourceReader jsonResourceReader;

	@Test
	void create_section() throws IOException {
		Page<SectionDefinition> sections = formService.searchSectionDefinitions(
				SectionDefinitionSearchCriteria.builder().name("T*").build(), PageRequest.of(0, 10));
		long sectionsCount = sections.getTotalElements();

		SectionDefinition sectionDefinition1 = buildSection("Test", "Test", "form/section-test.json");
		SectionDefinition sectionDefinition2 = formService.createSectionDefinition(sectionDefinition1);

		assertNotNull(sectionDefinition2);
		assertEquals(sectionDefinition1.getName(), sectionDefinition2.getName());
		assertEquals(sectionDefinition1.getLabel(), sectionDefinition2.getLabel());

		sectionDefinition2.setLabel("Test2");
		SectionDefinition sectionDefinition3 = formService.updateSectionDefinition(sectionDefinition2);
		assertNotNull(sectionDefinition3);
		assertEquals(sectionDefinition3.getName(), sectionDefinition2.getName());
		assertEquals(sectionDefinition3.getLabel(), sectionDefinition2.getLabel());

		sections = formService.searchSectionDefinitions(SectionDefinitionSearchCriteria.builder().name("T*").build(),
				PageRequest.of(0, 10));
		assertEquals(sectionsCount + 1, sections.getTotalElements());

		formService.deleteSectionDefinition(sectionDefinition3.getUuid());
	}

	@Test
	void create_form() throws IOException {
		SectionDefinition sectionDefinition2 = createSection("Test", "Test", "form/section-test.json");
		SectionDefinition sectionDefinition5 = createSection("Comment", "Comment", "form/section-comment.json");

		FormDefinition formDefinition1 = buildFormDefinition("Form1", sectionDefinition2);
		FormDefinition formDefinition2 = formService.createFormDefinition(formDefinition1);

		assertNotNull(formDefinition2);
		assertEquals(formDefinition1.getName(), formDefinition2.getName());
		assertEquals(formDefinition1.getFormSectionDefinitions().size(),
				formDefinition2.getFormSectionDefinitions().size());

		Page<FormDefinition> formDefinitions = formService.searchFormDefinitions(
				FormDefinitionSearchCriteria.builder().formName("F*").build(), PageRequest.of(0, 10));
		long formDefinitionCount = formDefinitions.getTotalElements();

		FormSectionDefinition formSectionDefinition2 = new FormSectionDefinition();
		formSectionDefinition2.setOrder(1);
		formSectionDefinition2.setReadOnly(false);
		formSectionDefinition2.setSectionDefinition(sectionDefinition5);
		formDefinition2.setFormSectionDefinitions(Arrays.asList(formSectionDefinition2));
		FormDefinition formDefinition3 = formService.updateFormDefinition(formDefinition2);

		assertNotNull(formDefinition3);
		assertEquals(formDefinition3.getName(), formDefinition3.getName());
		assertEquals(formDefinition3.getFormSectionDefinitions().size(),
				formDefinition3.getFormSectionDefinitions().size());

		formDefinitions = formService.searchFormDefinitions(
				FormDefinitionSearchCriteria.builder().formName("F*").build(), PageRequest.of(0, 10));
		assertEquals(formDefinitions.getTotalElements(), formDefinitionCount);

		formService.deleteFormDefinition(formDefinition3.getUuid());
	}

	@Test
	void create_process_form() throws IOException {
		SectionDefinition sectionDefinition1 = createSection("Comment", "Comment", "form/section-comment.json");
		FormDefinition formDefinition1 = createFormDefinition("Form1", sectionDefinition1);

		ProcessFormDefinition processFormDefinition1 = buildProcessFormDefinition("test", "UserTask_1",
				formDefinition1);
		ProcessFormDefinition processFormDefinition2 = formService.createProcessFormDefinition(processFormDefinition1);

		assertNotNull(processFormDefinition2);
		assertEquals(processFormDefinition1.getProcessDefinitionId(), processFormDefinition2.getProcessDefinitionId());
		assertEquals(processFormDefinition1.getUserTaskId(), processFormDefinition2.getUserTaskId());
		assertEquals(processFormDefinition1.getFormDefinition().getUuid(),
				processFormDefinition2.getFormDefinition().getUuid());

		Page<ProcessFormDefinition> processFormDefinitions = formService
				.searchProcessFormDefinitions(ProcessFormDefinitionSearchCriteria.builder().acceptFlexRevision(true)
						.processDefinitionId("test").userTaskId("UserTask_1").build(), PageRequest.of(0, 10));
		assertNotNull(processFormDefinitions);

		formService.deleteProcessFormDefinition(processFormDefinition2.getUuid());
	}

	@Test
	void load_workflow() throws BpmnInitializationException {
		Mockito.when(utilContextHelper.getAuthenticatedUser()).thenReturn(createAuthenticatedUser());
		Mockito.when(aclHelper.getUserByLogin(any())).thenReturn(createUser());

		// chargement de la carte de workflow
		List<ProcessDefinition> definitions1 = initializationService.searchProcessDefinitions();

		assertNotNull(definitions1);
		int size = definitions1.size();

		initializeWorkflow();

		List<ProcessDefinition> definitions2 = initializationService.searchProcessDefinitions();
		assertNotNull(definitions2);
		assertEquals(definitions2.size(), size + 1);
	}

	private void initializeWorkflow() throws BpmnInitializationException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("bpmn/test.bpmn20.xml");
		// URL url = Thread.currentThread().getContextClassLoader().getResource("bpmn/handle-group.bpmn20.xml");
		DocumentContent bpmn = new DocumentContent("test.bpmn20.xml", GenerationFormat.XML.getMimeType(),
				new File(url.getFile()));
		initializationService.updateProcessDefinition("test", bpmn);
	}

	@Test
	void start_update_workflow() throws BpmnInitializationException, InvalidDataException, FormConvertException,
			FormDefinitionException, IOException {
		Mockito.when(utilContextHelper.getAuthenticatedUser()).thenReturn(createAuthenticatedUser());
		Mockito.when(aclHelper.getUserByLogin(any())).thenReturn(createUser());

		// création des formulaires
		SectionDefinition sectionDefinition1 = createSection("Comment", "Comment", "form/section-comment.json");
		FormDefinition formDefinition1 = createFormDefinition("Form1", sectionDefinition1);
		ProcessFormDefinition processFormDefinition1 = buildProcessFormDefinition("test", "UserTask_1",
				formDefinition1);
		formService.createProcessFormDefinition(processFormDefinition1);

		SectionDefinition sectionDefinition2 = createSection("Test", "Test", "form/section-test.json");
		FormDefinition formDefinition2 = createFormDefinition("Form2", sectionDefinition2);
		ProcessFormDefinition processFormDefinition2 = buildProcessFormDefinition("test", "draft", formDefinition2);
		formService.createProcessFormDefinition(processFormDefinition2);

		// chargement de la carte de workflow
		List<ProcessDefinition> definitions1 = initializationService.searchProcessDefinitions();

		assertNotNull(definitions1);
		int size = definitions1.size();

		initializeWorkflow();

		List<ProcessDefinition> definitions2 = initializationService.searchProcessDefinitions();
		assertNotNull(definitions2);
		assertEquals(definitions2.size(), size + 1);

		// création d'un tâche
		AssetDescription1TestData draft = new AssetDescription1TestData();
		draft.setDescription("Test workflow");
		draft.setA("toto");
		draft.setProcessDefinitionKey("test");
		draft.setFunctionalStatus("mon statut fonctionnel");
		Task t1 = test1TaskService.createDraft(draft);
		assertNotNull(t1);
		assertNotNull(t1.getAsset());

		// modification
		t1.getAsset().setDescription("titi");
		Task t1bis = test1TaskService.updateTask(t1);
		assertNotNull(t1bis);
		assertNotNull(t1bis.getAsset());
		assertEquals("titi", t1bis.getAsset().getDescription());

		Page<Task> ts = taskQueryService.searchTasks(TaskSearchCriteria1TestBean.builder().a("toto").build(),
				Pageable.unpaged());
		long tsCount = ts.getTotalElements();

		// doCallRealMethod().when(javaMailSender).createMimeMessage();
		doNothing().when(javaMailSender).send((MimeMessage) any());

		// execution démarrage
		Task t2 = test1TaskService.startTask(t1);
		assertNotNull(t2);

		ts = taskQueryService.searchTasks(TaskSearchCriteria1TestBean.builder().a("toto").build(), PageRequest.of(0, 10));
		assertNotNull(ts);
		assertEquals(ts.getTotalElements(), tsCount + 1);

		Task t3 = ts.stream().sorted(Comparator.comparing(Task::getCreationDate).reversed()).findFirst().orElse(null);

		test1TaskService.claimTask(t3.getId());

		t3.getAsset().getForm().getSections().get(0).getFields().get(0).setValues(Arrays.asList("commentaire"));
		Task t4 = test1TaskService.updateTask(t3);

		// test création entity sans le draft
		AssetDescription2TestData t21bis = createTest2AssetDescription();

		Task t22 = test2TaskService.createDraft(t21bis);
		assertNotNull(t22);
		assertNotNull(t22.getAsset());
		Task t23 = test2TaskService.startTask(t22);
		assertNotNull(t23);

		ts = taskQueryService.searchTasks(TaskSearchCriteria1TestBean.builder().a("toto").build(), Pageable.unpaged());
		assertNotNull(ts);
		assertEquals(ts.getTotalElements(), tsCount + 2);

		ts = taskQueryService.searchTasks(
				TaskSearchCriteria1TestBean.builder().status(Arrays.asList(Status.DRAFT, Status.PENDING)).build(),
				Pageable.unpaged());
		assertNotNull(ts);
		assertEquals(ts.getTotalElements(), tsCount + 2);

		ts = taskQueryService.searchTasks(TaskSearchCriteria1TestBean.builder().description("%es%").build(),
				Pageable.unpaged());
		assertNotNull(ts);
		assertEquals(ts.getTotalElements(), tsCount + 1);

		Action a = t4.getActions().get(0);
		test1TaskService.doIt(t4.getId(), a.getName());

		ts = taskQueryService.searchTasks(
				TaskSearchCriteria1TestBean.builder().status(Arrays.asList(Status.DRAFT, Status.PENDING)).build(),
				Pageable.unpaged());
		assertNotNull(ts);
		assertEquals(ts.getTotalElements(), tsCount + 1);

	}

	private AssetDescription2TestData createTest2AssetDescription() throws FormConvertException, InvalidDataException {
		AssetDescription2TestData draft2 = new AssetDescription2TestData();
		draft2.setDescription("Test2 workflow");
		draft2.setA("toto");
		AssetDescription2TestEntity t21 = test2AssetDescriptionHelper.createAssetEntity(draft2);
		t21.setProcessDefinitionKey("test");
		t21.setStatus(Status.DRAFT);
		t21.setFunctionalStatus("Draft");
		t21.setInitiator("xxx");
		t21.setCreationDate(LocalDateTime.now());
		t21.setUpdatedDate(t21.getCreationDate());
		test2AssetDescriptionDao.save(t21);
		AssetDescription2TestData t21bis = test2AssetDescriptionMapper.entityToDto(t21);
		return t21bis;
	}

	private User createUser() {
		User u = new User();
		u.setLogin("test@test.com");
		u.setType(org.rudi.facet.acl.bean.UserType.PERSON);
		u.setFirstname("test");
		u.setLastname("test");
		return u;
	}

	private AuthenticatedUser createAuthenticatedUser() {
		AuthenticatedUser authenticatedUser = new AuthenticatedUser("test@test.com", UserType.PERSON);
		authenticatedUser.setRoles(Arrays.asList("ADMINISTRATOR"));
		return authenticatedUser;
	}

	protected SectionDefinition buildSection(String name, String label, String resource) throws IOException {
		SectionDefinition sectionDefinition1 = new SectionDefinition();
		sectionDefinition1.setName(name);
		sectionDefinition1.setLabel(label);
		URL sectionTestURL = Thread.currentThread().getContextClassLoader().getResource(resource);
		sectionDefinition1
				.setDefinition(FileUtils.readFileToString(new File(sectionTestURL.getFile()), StandardCharsets.UTF_8));
		return sectionDefinition1;
	}

	protected SectionDefinition createSection(String name, String label, String resource) throws IOException {
		return formService.createSectionDefinition(buildSection(name, label, resource));
	}

	protected FormDefinition buildFormDefinition(String name, SectionDefinition sectionDefinition1) {
		FormDefinition formDefinition1 = new FormDefinition();
		formDefinition1.setName("Form1");
		FormSectionDefinition formSectionDefinition1 = new FormSectionDefinition();
		formSectionDefinition1.setOrder(1);
		formSectionDefinition1.setReadOnly(false);
		formSectionDefinition1.setSectionDefinition(sectionDefinition1);
		formDefinition1.addFormSectionDefinitionsItem(formSectionDefinition1);
		return formDefinition1;
	}

	protected FormDefinition createFormDefinition(String name, SectionDefinition sectionDefinition1) {
		return formService.createFormDefinition(buildFormDefinition(name, sectionDefinition1));
	}

	protected ProcessFormDefinition buildProcessFormDefinition(String processDefinitionId, String userTaskId,
			FormDefinition formDefinition1) {
		ProcessFormDefinition processFormDefinition1 = new ProcessFormDefinition();
		processFormDefinition1.setProcessDefinitionId(processDefinitionId);
		processFormDefinition1.setFormDefinition(formDefinition1);
		processFormDefinition1.setUserTaskId(userTaskId);
		return processFormDefinition1;
	}

	// Comme on ne sait pas comment exécuter le script de création qu'une seule fois pour toutes les classes de tests BPMN, on met tous les tests BPMN
	// dans la même classe
	@Test
	void createOrUpdateAllSectionAndProcessFormDefinitions() throws IOException, JSONException {
		final var processFormDefinitions = formService.createOrUpdateAllSectionAndFormDefinitions();
		removeLineFeeds(processFormDefinitions);

		// On fixe les UUID aléatoires
		processFormDefinitions.forEach(this::removeAllUuids);

		final var expectedProcessFormDefinitions = jsonResourceReader
				.readList("bpmn/expected/all-process-form-definitions.json", ProcessFormDefinition.class);
		removeLineFeeds(expectedProcessFormDefinitions);

		RudiAssertions.assertThat(processFormDefinitions).isJsonEqualTo(expectedProcessFormDefinitions);
	}

	private void removeLineFeeds(Collection<ProcessFormDefinition> expectedProcessFormDefinitions) {
		for (final var expectedProcessFormDefinition : expectedProcessFormDefinitions) {
			final var formDefinition = expectedProcessFormDefinition.getFormDefinition();
			if (formDefinition != null) {
				final var formSectionDefinitions = formDefinition.getFormSectionDefinitions();
				if (formSectionDefinitions != null) {
					for (final var formSectionDefinition : formSectionDefinitions) {
						final var sectionDefinition = formSectionDefinition.getSectionDefinition();
						if (sectionDefinition != null) {
							final var replacedDefinition = sectionDefinition.getDefinition().replaceAll("\r?\n",
									StringUtils.EMPTY);
							sectionDefinition.setDefinition(replacedDefinition);
						}
					}
				}
			}
		}
	}

	private void removeAllUuids(ProcessFormDefinition processFormDefinition) {
		processFormDefinition.setUuid(null);
		final var formDefinition = processFormDefinition.getFormDefinition();
		formDefinition.setUuid(null);
		formDefinition.getFormSectionDefinitions().forEach(formSectionDefinition -> {
			formSectionDefinition.setUuid(null);
			formSectionDefinition.getSectionDefinition().setUuid(null);
		});
	}
}
