package com.greenario.cropcondition.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.greenario.common.rest.assembler.CollectionRepresentationModelAssembler;
import com.greenario.common.rest.assembler.PagedRepresentationModelAssembler;
import com.greenario.common.rest.models.PageMetadata;
import com.greenario.cropcondition.api.CropConditionPermission;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.exceptions.NotAuthorizedOnCropConditionException;
import com.greenario.cropcondition.api.exceptions.CropConditionNotFoundException;
import com.greenario.cropcondition.api.models.CropConditionAccessItem;
import com.greenario.cropcondition.internal.models.CropConditionAccessItemImpl;
import com.greenario.cropcondition.rest.CropConditionController;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemPagedRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemRepresentationModel;

/**
 * Transforms {@link CropConditionAccessItem} to its resource counterpart {@link
 * CropConditionAccessItemRepresentationModel} and vice versa.
 */
@Component
public class CropConditionAccessItemRepresentationModelAssembler
    implements
    CollectionRepresentationModelAssembler<CropConditionAccessItem, CropConditionAccessItemRepresentationModel, CropConditionAccessItemCollectionRepresentationModel>,
    PagedRepresentationModelAssembler<CropConditionAccessItem, CropConditionAccessItemRepresentationModel, CropConditionAccessItemPagedRepresentationModel> {

  private final CropConditionService cropConditionService;

  @Autowired
  public CropConditionAccessItemRepresentationModelAssembler(CropConditionService cropConditionService) {
    this.cropConditionService = cropConditionService;
  }

  @NonNull
  @Override
  public CropConditionAccessItemRepresentationModel toModel(@NonNull CropConditionAccessItem wbAccItem) {
    CropConditionAccessItemRepresentationModel repModel = new CropConditionAccessItemRepresentationModel();
    repModel.setAccessId(wbAccItem.getAccessId());
    repModel.setCropConditionId(wbAccItem.getCropConditionId());
    repModel.setCropConditionKey(wbAccItem.getCropConditionKey());
    repModel.setAccessItemId(wbAccItem.getId());
    repModel.setAccessName(wbAccItem.getAccessName());
    repModel.setPermRead(wbAccItem.getPermission(CropConditionPermission.READ));
    repModel.setPermReadCropConditionMonitorings(
        wbAccItem.getPermission(CropConditionPermission.READCROPCONDITIONMONITORINGS));
    repModel.setPermOpen(wbAccItem.getPermission(CropConditionPermission.OPEN));
    repModel.setPermAppend(wbAccItem.getPermission(CropConditionPermission.APPEND));
    repModel.setPermEditCropConditionMonitorings(
        wbAccItem.getPermission(CropConditionPermission.EDITCROPCONDITIONMONITORINGS));
    repModel.setPermTransfer(wbAccItem.getPermission(CropConditionPermission.TRANSFER));
    repModel.setPermDistribute(wbAccItem.getPermission(CropConditionPermission.DISTRIBUTE));
    repModel.setPermCustom1(wbAccItem.getPermission(CropConditionPermission.CUSTOM_1));
    repModel.setPermCustom2(wbAccItem.getPermission(CropConditionPermission.CUSTOM_2));
    repModel.setPermCustom3(wbAccItem.getPermission(CropConditionPermission.CUSTOM_3));
    repModel.setPermCustom4(wbAccItem.getPermission(CropConditionPermission.CUSTOM_4));
    repModel.setPermCustom5(wbAccItem.getPermission(CropConditionPermission.CUSTOM_5));
    repModel.setPermCustom6(wbAccItem.getPermission(CropConditionPermission.CUSTOM_6));
    repModel.setPermCustom7(wbAccItem.getPermission(CropConditionPermission.CUSTOM_7));
    repModel.setPermCustom8(wbAccItem.getPermission(CropConditionPermission.CUSTOM_8));
    repModel.setPermCustom9(wbAccItem.getPermission(CropConditionPermission.CUSTOM_9));
    repModel.setPermCustom10(wbAccItem.getPermission(CropConditionPermission.CUSTOM_10));
    repModel.setPermCustom11(wbAccItem.getPermission(CropConditionPermission.CUSTOM_11));
    repModel.setPermCustom12(wbAccItem.getPermission(CropConditionPermission.CUSTOM_12));
    return repModel;
  }

  public CropConditionAccessItem toEntityModel(CropConditionAccessItemRepresentationModel repModel) {
    CropConditionAccessItemImpl wbAccItemModel = (CropConditionAccessItemImpl) cropConditionService
        .newCropConditionAccessItem(
            repModel.getCropConditionId(), repModel.getAccessId());
    wbAccItemModel.setCropConditionKey(repModel.getCropConditionKey());
    wbAccItemModel.setAccessName(repModel.getAccessName());
    wbAccItemModel.setPermission(CropConditionPermission.READ, repModel.isPermRead());
    wbAccItemModel.setPermission(CropConditionPermission.READCROPCONDITIONMONITORINGS,
        repModel.isPermReadCropConditionMonitorings());
    wbAccItemModel.setPermission(CropConditionPermission.OPEN, repModel.isPermOpen());
    wbAccItemModel.setPermission(CropConditionPermission.APPEND, repModel.isPermAppend());
    wbAccItemModel.setPermission(CropConditionPermission.EDITCROPCONDITIONMONITORINGS,
        repModel.isPermEditCropConditionMonitorings());
    wbAccItemModel.setPermission(CropConditionPermission.TRANSFER, repModel.isPermTransfer());
    wbAccItemModel.setPermission(CropConditionPermission.DISTRIBUTE, repModel.isPermDistribute());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_1, repModel.isPermCustom1());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_2, repModel.isPermCustom2());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_3, repModel.isPermCustom3());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_4, repModel.isPermCustom4());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_5, repModel.isPermCustom5());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_6, repModel.isPermCustom6());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_7, repModel.isPermCustom7());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_8, repModel.isPermCustom8());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_9, repModel.isPermCustom9());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_10, repModel.isPermCustom10());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_11, repModel.isPermCustom11());
    wbAccItemModel.setPermission(CropConditionPermission.CUSTOM_12, repModel.isPermCustom12());
    wbAccItemModel.setId(repModel.getAccessItemId());
    return wbAccItemModel;
  }

  public CropConditionAccessItemCollectionRepresentationModel toGreenarioCollectionModelForSingleCropCondition(
      String cropConditionId, List<CropConditionAccessItem> cropConditionAccessItems)
      throws CropConditionNotFoundException, NotAuthorizedOnCropConditionException {
    CropConditionAccessItemCollectionRepresentationModel pageModel = toGreenarioCollectionModel(
        cropConditionAccessItems);
    pageModel.add(
        linkTo(methodOn(CropConditionController.class).getCropCondition(cropConditionId))
            .withRel("cropCondition"));
    return pageModel;
  }

  @Override
  public CropConditionAccessItemCollectionRepresentationModel buildCollectionEntity(
      List<CropConditionAccessItemRepresentationModel> content) {
    return new CropConditionAccessItemCollectionRepresentationModel(content);
  }

  @Override
  public CropConditionAccessItemPagedRepresentationModel buildPageableEntity(
      Collection<CropConditionAccessItemRepresentationModel> content, PageMetadata pageMetadata) {
    return new CropConditionAccessItemPagedRepresentationModel(content, pageMetadata);
  }
}
