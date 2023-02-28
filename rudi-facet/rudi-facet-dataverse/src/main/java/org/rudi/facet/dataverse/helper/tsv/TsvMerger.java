package org.rudi.facet.dataverse.helper.tsv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TsvMerger {

	/**
	 * Fusionne deux TSV. C'est-à-dire :
	 * <ul>
	 * <li>On conserve du TSV actuel (currentTsv) :<ul>
	 *     <li>l'ordre des lignes dans le contenu du fichier TSV</li>
	 *     <li>le titre (title)</li>
	 *     <li>le champ required, car il est trop difficile de calculer si un champ est requis côté Dataverse ou non (cf commentaires dans TsvGenerator)</li>
	 *     <li>le champ watermark</li>
	 *     <li>le format d'affichage (displayFormat)</li>
	 *     <li>le champ allowControlledVocabulary : <b>on lance même une exception si la spec actuelle du champ n'est pas cohérente avec la TSV actuel !</b></li>
	 *     <li>le champ advancedSearchField</li>
	 *     <li>le champ facetable</li>
	 * </ul></li>
	 * <li>On ajoute les nouvelles lignes mais on supprime les lignes qui n'existent plus dans le TSV généré (generatedTsv)</li>
	 * <li>On recalcule le displayOrder de chaque ligne à la fin du traitement</li>
	 * </ul>
	 *
	 * @param currentTsv   le TSV actuel
	 * @param generatedTsv le TSV généré
	 */
	public Tsv merge(Tsv currentTsv, Tsv generatedTsv) {
		return Tsv.builder()
				.metadataBlock(currentTsv.metadataBlock)
				.datasetField(merge(currentTsv.datasetField, generatedTsv.datasetField))
				.controlledVocabulary(generatedTsv.controlledVocabulary)
				.build();
	}

	private TsvPart<TsvDatasetFieldLine> merge(TsvPart<TsvDatasetFieldLine> currentPart, TsvPart<TsvDatasetFieldLine> generatedPart) {
		final var mergedPart = new TsvPart<TsvDatasetFieldLine>(new ArrayList<>());

		final var stillExistingCurrentLines = SetUtils.intersection(currentPart.lines, generatedPart.lines);
		final var updatedStillExistingCurrentLines = updateStillExistingCurrentLines(stillExistingCurrentLines, generatedPart);
		mergedPart.lines.addAll(updatedStillExistingCurrentLines);

		final var newLines = SetUtils.difference(generatedPart.lines, currentPart.lines);
		if (CollectionUtils.isNotEmpty(newLines)) {
			final int maxDisplayOrder = getMaxDisplayOrder(updatedStillExistingCurrentLines);
			int nextDisplayOrder = maxDisplayOrder + 1;
			for (final var newLine : newLines) {
				final var newLineWithComputedDisplayOrder = newLine.toBuilder()
						.displayOrder(nextDisplayOrder++)
						.build();
				mergedPart.lines.add(newLineWithComputedDisplayOrder);
			}
		}

		return mergedPart;
	}

	private List<TsvDatasetFieldLine> updateStillExistingCurrentLines(Collection<TsvDatasetFieldLine> stillExistingCurrentLines, TsvPart<TsvDatasetFieldLine> generatedPart) {
		final List<TsvDatasetFieldLine> updatesLines = new ArrayList<>(stillExistingCurrentLines.size());
		for (final var currentLine : stillExistingCurrentLines) {
			final TsvDatasetFieldLine generatedLine = generatedPart.getLineWithSameHashStringAs(currentLine);
			if (generatedLine == null) {
				log.warn("Ligne supprimée : " + currentLine);
				continue;
			}
			if (!Objects.equals(generatedLine.allowControlledVocabulary, currentLine.allowControlledVocabulary)) {
				throw new CannotChangeAllowControlledVocabularyException(generatedLine.name);
			}
			updatesLines.add(generatedLine.toBuilder()
					.advancedSearchField(currentLine.advancedSearchField)
					.allowControlledVocabulary(currentLine.allowControlledVocabulary)
					.description(currentLine.description)
					.displayFormat(currentLine.displayFormat)
					.displayOrder(currentLine.displayOrder)
					.facetable(currentLine.facetable)
					.required(currentLine.required)
					.title(currentLine.title)
					.watermark(currentLine.watermark)
					.build());
		}
		return updatesLines;
	}

	private int getMaxDisplayOrder(List<TsvDatasetFieldLine> lines) {
		final var lineWithMax = lines.stream().max(Comparator.comparingInt(TsvDatasetFieldLine::getDisplayOrder)).orElse(null);
		return lineWithMax != null ? lineWithMax.displayOrder : -1;
	}

}
