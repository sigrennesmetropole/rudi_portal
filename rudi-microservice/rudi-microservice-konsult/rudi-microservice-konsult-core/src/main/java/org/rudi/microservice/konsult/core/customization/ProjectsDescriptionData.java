package org.rudi.microservice.konsult.core.customization;

import java.util.List;

import lombok.Data;

@Data
public class ProjectsDescriptionData {
	private List<MultilingualText> titles1;

	private List<MultilingualText> titles2;

	private List<MultilingualText> subtitles;

	private List<MultilingualText> descriptions;
}
