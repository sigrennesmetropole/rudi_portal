package org.rudi.facet.dataverse.helper.tsv;

class CannotChangeAllowControlledVocabularyException extends RuntimeException {

	private static final long serialVersionUID = -8429913186623022589L;

	public CannotChangeAllowControlledVocabularyException(String fieldName) {
		super(String.format(
				"Changing allowControlledVocabulary for field %s is not allowed as it will make existing values unreadable unless database is manually fixed (see https://jira.open-groupe.com/browse/RUDI-2658?focusedCommentId=509888&page=com.atlassian.jira.plugin.system.issuetabpanels%%3Acomment-tabpanel#comment-509888).",
				fieldName));
	}
}
