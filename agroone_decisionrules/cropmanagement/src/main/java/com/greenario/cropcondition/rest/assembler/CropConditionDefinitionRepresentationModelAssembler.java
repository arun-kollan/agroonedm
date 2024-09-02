package com.greenario.cropcondition.rest.assembler;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.api.exceptions.SystemException;
import com.greenario.common.rest.assembler.CollectionRepresentationModelAssembler;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.exceptions.NotAuthorizedOnCropConditionException;
import com.greenario.cropcondition.api.exceptions.CropConditionNotFoundException;
import com.greenario.cropcondition.api.models.CropCondition;
import com.greenario.cropcondition.api.models.CropConditionAccessItem;
import com.greenario.cropcondition.api.models.CropConditionSummary;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionDefinitionCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionDefinitionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionRepresentationModel;

/**
 * Transforms {@link CropCondition} into a
 * {@link CropConditionDefinitionRepresentationModel} containing
 * all additional information about that cropCondition.
 */
@Component
public class CropConditionDefinitionRepresentationModelAssembler
    implements
    CollectionRepresentationModelAssembler<CropCondition, CropConditionDefinitionRepresentationModel, CropConditionDefinitionCollectionRepresentationModel> {

  private final CropConditionService cropConditionService;
  private final CropConditionAccessItemRepresentationModelAssembler accessItemAssembler;
  private final CropConditionRepresentationModelAssembler cropConditionAssembler;

  @Autowired
  public CropConditionDefinitionRepresentationModelAssembler(
      CropConditionService cropConditionService,
      CropConditionAccessItemRepresentationModelAssembler accessItemAssembler,
      CropConditionRepresentationModelAssembler cropConditionAssembler) {
    this.cropConditionService = cropConditionService;
    this.accessItemAssembler = accessItemAssembler;
    this.cropConditionAssembler = cropConditionAssembler;
  }

  @NonNull
  public CropConditionDefinitionRepresentationModel toModel(@NonNull CropCondition cropCondition) {
    CropConditionRepresentationModel basket = cropConditionAssembler.toModel(cropCondition);
    Collection<CropConditionAccessItemRepresentationModel> authorizations;
    Set<String> distroTargets;
    try {
      List<CropConditionAccessItem> cropConditionAccessItems = cropConditionService
          .getCropConditionAccessItems(basket.getCropConditionId());
      authorizations = accessItemAssembler.toCollectionModel(cropConditionAccessItems).getContent();
      distroTargets = cropConditionService.getDistributionTargets(cropCondition.getId()).stream()
          .map(CropConditionSummary::getId)
          .collect(Collectors.toSet());
    } catch (CropConditionNotFoundException
        | NotAuthorizedException
        | NotAuthorizedOnCropConditionException e) {
      throw new SystemException("Caught Exception", e);
    }

    CropConditionDefinitionRepresentationModel repModel = new CropConditionDefinitionRepresentationModel();

    repModel.setCropCondition(basket);
    repModel.setAuthorizations(authorizations);
    repModel.setDistributionTargets(distroTargets);
    return repModel;
  }

  @Override
  public CropConditionDefinitionCollectionRepresentationModel buildCollectionEntity(
      List<CropConditionDefinitionRepresentationModel> content) {
    return new CropConditionDefinitionCollectionRepresentationModel(content);
  }
}
