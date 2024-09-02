package com.greenario.cropcondition.rest.assembler;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.greenario.common.rest.assembler.CollectionRepresentationModelAssembler;
import com.greenario.common.rest.assembler.PagedRepresentationModelAssembler;
import com.greenario.common.rest.models.PageMetadata;
import com.greenario.cropcondition.api.CropConditionCustomField;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.models.CropConditionSummary;
import com.greenario.cropcondition.internal.models.CropConditionSummaryImpl;
import com.greenario.cropcondition.rest.models.DistributionTargetsCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionSummaryPagedRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionSummaryRepresentationModel;

/**
 * EntityModel assembler for {@link CropConditionSummaryRepresentationModel}.
 */
@Component
public class CropConditionSummaryRepresentationModelAssembler
    implements
    PagedRepresentationModelAssembler<CropConditionSummary, CropConditionSummaryRepresentationModel, CropConditionSummaryPagedRepresentationModel>,
    CollectionRepresentationModelAssembler<CropConditionSummary, CropConditionSummaryRepresentationModel, DistributionTargetsCollectionRepresentationModel> {

  private CropConditionService cropConditionService;

  public CropConditionSummaryRepresentationModelAssembler() {
  }

  @Autowired
  public CropConditionSummaryRepresentationModelAssembler(CropConditionService cropConditionService) {
    this.cropConditionService = cropConditionService;
  }

  @NonNull
  @Override
  public CropConditionSummaryRepresentationModel toModel(
      @NonNull CropConditionSummary cropConditionSummary) {
    CropConditionSummaryRepresentationModel repModel = new CropConditionSummaryRepresentationModel();
    repModel.setCropConditionId(cropConditionSummary.getId());
    repModel.setKey(cropConditionSummary.getKey());
    repModel.setName(cropConditionSummary.getName());
    repModel.setDomain(cropConditionSummary.getDomain());
    repModel.setType(cropConditionSummary.getType());
    repModel.setDescription(cropConditionSummary.getDescription());
    repModel.setOwner(cropConditionSummary.getOwner());
    repModel.setMarkedForDeletion(cropConditionSummary.isMarkedForDeletion());
    repModel.setCustom1(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_1));
    repModel.setCustom2(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_2));
    repModel.setCustom3(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_3));
    repModel.setCustom4(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_4));
    repModel.setCustom5(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_5));
    repModel.setCustom6(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_6));
    repModel.setCustom7(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_7));
    repModel.setCustom8(cropConditionSummary.getCustomField(CropConditionCustomField.CUSTOM_8));
    repModel.setOrgLevel1(cropConditionSummary.getOrgLevel1());
    repModel.setOrgLevel2(cropConditionSummary.getOrgLevel2());
    repModel.setOrgLevel3(cropConditionSummary.getOrgLevel3());
    repModel.setOrgLevel4(cropConditionSummary.getOrgLevel4());
    return repModel;
  }

  public CropConditionSummary toEntityModel(CropConditionSummaryRepresentationModel repModel) {
    CropConditionSummaryImpl cropCondition = (CropConditionSummaryImpl) cropConditionService
        .newCropCondition(repModel.getKey(), repModel.getDomain()).asSummary();
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
    return cropCondition;
  }

  @Override
  public CropConditionSummaryPagedRepresentationModel buildPageableEntity(
      Collection<CropConditionSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    return new CropConditionSummaryPagedRepresentationModel(content, pageMetadata);
  }

  @Override
  public DistributionTargetsCollectionRepresentationModel buildCollectionEntity(
      List<CropConditionSummaryRepresentationModel> content) {
    return new DistributionTargetsCollectionRepresentationModel(content);
  }
}
