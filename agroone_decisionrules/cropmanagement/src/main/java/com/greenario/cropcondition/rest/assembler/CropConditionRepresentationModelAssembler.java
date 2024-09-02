package com.greenario.cropcondition.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.api.exceptions.SystemException;
import com.greenario.cropcondition.api.CropConditionCustomField;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.exceptions.NotAuthorizedOnCropConditionException;
import com.greenario.cropcondition.api.exceptions.CropConditionNotFoundException;
import com.greenario.cropcondition.api.models.CropCondition;
import com.greenario.cropcondition.internal.models.CropConditionImpl;
import com.greenario.cropcondition.rest.CropConditionController;
import com.greenario.cropcondition.rest.models.CropConditionRepresentationModel;

/**
 * Transforms {@link CropCondition} to its resource counterpart
 * {@link CropConditionRepresentationModel}
 * and vice versa.
 */
@Component
public class CropConditionRepresentationModelAssembler
    implements RepresentationModelAssembler<CropCondition, CropConditionRepresentationModel> {

  private final CropConditionService cropConditionService;

  @Autowired
  public CropConditionRepresentationModelAssembler(CropConditionService cropConditionService) {
    this.cropConditionService = cropConditionService;
  }

  @NonNull
  @Override
  public CropConditionRepresentationModel toModel(@NonNull CropCondition cropCondition) {
    CropConditionRepresentationModel repModel = new CropConditionRepresentationModel();
    repModel.setCropConditionId(cropCondition.getId());
    repModel.setKey(cropCondition.getKey());
    repModel.setName(cropCondition.getName());
    repModel.setDomain(cropCondition.getDomain());
    repModel.setType(cropCondition.getType());
    repModel.setDescription(cropCondition.getDescription());
    repModel.setOwner(cropCondition.getOwner());
    repModel.setMarkedForDeletion(cropCondition.isMarkedForDeletion());
    repModel.setCustom1(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_1));
    repModel.setCustom2(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_2));
    repModel.setCustom3(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_3));
    repModel.setCustom4(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_4));
    repModel.setCustom5(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_5));
    repModel.setCustom6(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_6));
    repModel.setCustom7(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_7));
    repModel.setCustom8(cropCondition.getCustomField(CropConditionCustomField.CUSTOM_8));
    repModel.setOrgLevel1(cropCondition.getOrgLevel1());
    repModel.setOrgLevel2(cropCondition.getOrgLevel2());
    repModel.setOrgLevel3(cropCondition.getOrgLevel3());
    repModel.setOrgLevel4(cropCondition.getOrgLevel4());
    repModel.setCreated(cropCondition.getCreated());
    repModel.setModified(cropCondition.getModified());
    try {
      return addLinks(repModel, cropCondition);
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
  }

  public CropCondition toEntityModel(CropConditionRepresentationModel repModel) {
    CropConditionImpl cropCondition = (CropConditionImpl) cropConditionService.newCropCondition(repModel.getKey(),
        repModel.getDomain());
    cropCondition.setId(repModel.getCropConditionId());
    cropCondition.setName(repModel.getName());
    cropCondition.setType(repModel.getType());
    cropCondition.setDescription(repModel.getDescription());
    cropCondition.setOwner(repModel.getOwner());
    cropCondition.setMarkedForDeletion(repModel.getMarkedForDeletion());
    cropCondition.setCustom1(repModel.getCustom1());
    cropCondition.setCustom2(repModel.getCustom2());
    cropCondition.setCustom3(repModel.getCustom3());
    cropCondition.setCustom4(repModel.getCustom4());
    cropCondition.setCustom5(repModel.getCustom5());
    cropCondition.setCustom6(repModel.getCustom6());
    cropCondition.setCustom7(repModel.getCustom7());
    cropCondition.setCustom8(repModel.getCustom8());
    cropCondition.setOrgLevel1(repModel.getOrgLevel1());
    cropCondition.setOrgLevel2(repModel.getOrgLevel2());
    cropCondition.setOrgLevel3(repModel.getOrgLevel3());
    cropCondition.setOrgLevel4(repModel.getOrgLevel4());
    cropCondition.setCreated(repModel.getCreated());
    cropCondition.setModified(repModel.getModified());
    return cropCondition;
  }

  private CropConditionRepresentationModel addLinks(
      CropConditionRepresentationModel resource, CropCondition wb)
      throws CropConditionNotFoundException,
      NotAuthorizedOnCropConditionException,
      NotAuthorizedException {
    resource.add(
        linkTo(methodOn(CropConditionController.class).getCropCondition(wb.getId())).withSelfRel());
    resource.add(
        linkTo(methodOn(CropConditionController.class).getDistributionTargets(wb.getId()))
            .withRel("distributionTargets"));
    resource.add(
        linkTo(methodOn(CropConditionController.class).getCropConditionAccessItems(wb.getId()))
            .withRel("accessItems"));
    resource.add(
        linkTo(methodOn(CropConditionController.class).getCropConditions(null, null, null, null))
            .withRel("allCropConditions"));
    resource.add(
        linkTo(
            methodOn(CropConditionController.class)
                .removeDistributionTargetForCropConditionId(wb.getId()))
            .withRel("removeDistributionTargets"));
    return resource;
  }
}
