package com.greenario.cropcondition.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenario.common.api.exceptions.ConcurrencyException;
import com.greenario.common.api.exceptions.DomainNotFoundException;
import com.greenario.common.api.exceptions.InvalidArgumentException;
import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.rest.RestEndpoints;
import com.greenario.cropcondition.api.CropConditionQuery;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.exceptions.NotAuthorizedOnCropConditionException;
import com.greenario.cropcondition.api.exceptions.CropConditionAccessItemAlreadyExistException;
import com.greenario.cropcondition.api.exceptions.CropConditionAlreadyExistException;
import com.greenario.cropcondition.api.exceptions.CropConditionNotFoundException;
import com.greenario.cropcondition.api.models.CropCondition;
import com.greenario.cropcondition.api.models.CropConditionAccessItem;
import com.greenario.cropcondition.api.models.CropConditionSummary;
import com.greenario.cropcondition.internal.models.CropConditionImpl;
import com.greenario.cropcondition.rest.assembler.CropConditionAccessItemRepresentationModelAssembler;
import com.greenario.cropcondition.rest.assembler.CropConditionDefinitionRepresentationModelAssembler;
import com.greenario.cropcondition.rest.assembler.CropConditionRepresentationModelAssembler;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionDefinitionCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionDefinitionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionRepresentationModel;

import static com.greenario.common.internal.util.CheckedFunction.wrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for all {@link CropConditionDefinitionRepresentationModel} related
 * endpoints.
 */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class CropConditionDefinitionController {

  private final CropConditionService cropConditionService;
  private final CropConditionDefinitionRepresentationModelAssembler cropConditionDefinitionAssembler;
  private final CropConditionRepresentationModelAssembler cropConditionAssembler;
  private final CropConditionAccessItemRepresentationModelAssembler accessItemAssembler;
  private final ObjectMapper mapper;

  @Autowired
  CropConditionDefinitionController(
      CropConditionService cropConditionService,
      CropConditionDefinitionRepresentationModelAssembler cropConditionDefinitionAssembler,
      CropConditionRepresentationModelAssembler cropConditionAssembler,
      CropConditionAccessItemRepresentationModelAssembler accessItemAssembler,
      ObjectMapper mapper) {
    this.cropConditionService = cropConditionService;
    this.cropConditionDefinitionAssembler = cropConditionDefinitionAssembler;
    this.cropConditionAssembler = cropConditionAssembler;
    this.accessItemAssembler = accessItemAssembler;
    this.mapper = mapper;
  }

  /**
   * This endpoint exports all CropConditions with the corresponding CropCondition
   * Access Items and
   * Distribution Targets. We call this data structure CropCondition Definition.
   *
   * @title Export CropConditions
   * @param domain Filter the export for a specific domain.
   * @return all cropConditions.
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION_DEFINITIONS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CropConditionDefinitionCollectionRepresentationModel> exportCropConditions(
      @RequestParam(required = false) String[] domain) {
    CropConditionQuery query = cropConditionService.createCropConditionQuery();
    Optional.ofNullable(domain).ifPresent(query::domainIn);

    List<CropConditionSummary> cropConditionSummaryList = query.list();

    CropConditionDefinitionCollectionRepresentationModel pageModel = cropConditionSummaryList.stream()
        .map(CropConditionSummary::getId)
        .map(wrap(cropConditionService::getCropCondition))
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(), cropConditionDefinitionAssembler::toGreenarioCollectionModel));

    return ResponseEntity.ok(pageModel);
  }

  /**
   * This endpoint imports a list of CropCondition Definitions.
   *
   * <p>
   * This does not exactly match the REST norm, but we want to have an option to
   * import all
   * settings at once. When a logical equal (key and domain are equal)
   * CropCondition
   * already exists an
   * update will be executed. Otherwise a new CropCondition will be created.
   *
   * @title Import CropConditions
   * @param file the list of CropCondition Definitions which will be imported to
   *             the
   *             current system.
   * @return no content
   * @throws IOException                                  if multipart file cannot
   *                                                      be parsed.
   * @throws NotAuthorizedException                       if the user is not
   *                                                      authorized.
   * @throws DomainNotFoundException                      if domain information is
   *                                                      incorrect.
   * @throws CropConditionAlreadyExistException           if any CropCondition
   *                                                      already
   *                                                      exists when trying to
   *                                                      create
   *                                                      a new one.
   * @throws CropConditionNotFoundException               if do not exists a
   *                                                      {@linkplain CropCondition}
   *                                                      in the system
   *                                                      with the used id.
   * @throws InvalidArgumentException                     if any CropCondition has
   *                                                      invalid information or
   *                                                      authorization
   *                                                      information in
   *                                                      {@linkplain CropCondition}s'
   *                                                      definitions is
   *                                                      incorrect.
   * @throws CropConditionAccessItemAlreadyExistException if a
   *                                                      CropConditionAccessItem
   *                                                      for the same
   *                                                      CropCondition and access
   *                                                      id
   *                                                      already exists.
   * @throws ConcurrencyException                         if CropCondition was
   *                                                      updated
   *                                                      by an other user
   * @throws NotAuthorizedOnCropConditionException        if the current user has
   *                                                      not correct permissions
   */
  @PostMapping(path = RestEndpoints.URL_CROPCONDITION_DEFINITIONS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> importCropConditions(@RequestParam("file") MultipartFile file)
      throws IOException,
      DomainNotFoundException,
      InvalidArgumentException,
      CropConditionAlreadyExistException,
      CropConditionNotFoundException,
      CropConditionAccessItemAlreadyExistException,
      ConcurrencyException,
      NotAuthorizedOnCropConditionException,
      NotAuthorizedException {
    CropConditionDefinitionCollectionRepresentationModel definitions = mapper.readValue(
        file.getInputStream(),
        new TypeReference<CropConditionDefinitionCollectionRepresentationModel>() {
        });

    // key: logical ID
    // value: system ID (in database)
    Map<String, String> systemIds = cropConditionService.createCropConditionQuery().list().stream()
        .collect(Collectors.toMap(this::logicalId, CropConditionSummary::getId));
    checkForDuplicates(definitions.getContent());

    // key: old system ID
    // value: system ID
    Map<String, String> idConversion = new HashMap<>();

    // STEP 1: update or create cropConditions from the import
    for (CropConditionDefinitionRepresentationModel definition : definitions.getContent()) {
      CropCondition importedWb = cropConditionAssembler.toEntityModel(definition.getCropCondition());
      String newId;
      CropConditionImpl wbWithoutId = (CropConditionImpl) removeId(importedWb);
      if (systemIds.containsKey(logicalId(importedWb))) {
        CropCondition modifiedWb = cropConditionService.getCropCondition(importedWb.getKey(), importedWb.getDomain());
        wbWithoutId.setModified(modifiedWb.getModified());
        cropConditionService.updateCropCondition(wbWithoutId);

        newId = systemIds.get(logicalId(importedWb));
      } else {
        newId = cropConditionService.createCropCondition(wbWithoutId).getId();
      }

      // Since we would have a n² runtime when doing a lookup and updating the access
      // items we
      // decided to
      // simply delete all existing accessItems and create new ones.
      boolean authenticated = definition.getAuthorizations().stream()
          .anyMatch(
              access -> (access.getCropConditionId().equals(importedWb.getId()))
                  && (access.getCropConditionKey().equals(importedWb.getKey())));
      if (!authenticated && !definition.getAuthorizations().isEmpty()) {
        throw new InvalidArgumentException(
            "The given Authentications for CropCondition "
                + importedWb.getId()
                + " don't match in CropConditionId and CropConditionKey. "
                + "Please provide consistent CropConditionDefinitions");
      }
      for (CropConditionAccessItem accessItem : cropConditionService.getCropConditionAccessItems(newId)) {
        cropConditionService.deleteCropConditionAccessItem(accessItem.getId());
      }
      for (CropConditionAccessItemRepresentationModel authorization : definition.getAuthorizations()) {
        authorization.setCropConditionId(newId);
        cropConditionService.createCropConditionAccessItem(
            accessItemAssembler.toEntityModel(authorization));
      }
      idConversion.put(importedWb.getId(), newId);
    }

    // STEP 2: update distribution targets
    // This can not be done in step 1 because the system IDs are only known after
    // step 1
    for (CropConditionDefinitionRepresentationModel definition : definitions.getContent()) {
      List<String> distributionTargets = new ArrayList<>();
      for (String oldId : definition.getDistributionTargets()) {
        if (idConversion.containsKey(oldId)) {
          distributionTargets.add(idConversion.get(oldId));
        } else if (systemIds.containsValue(oldId)) {
          distributionTargets.add(oldId);
        } else {
          throw new InvalidArgumentException(
              String.format(
                  "invalid import state: CropCondition '%s' does not exist in the given import list",
                  oldId));
        }
      }

      cropConditionService.setDistributionTargets(
          // no verification necessary since the cropCondition was already imported in
          // step
          // 1.
          idConversion.get(definition.getCropCondition().getCropConditionId()), distributionTargets);
    }
    return ResponseEntity.noContent().build();
  }

  private CropCondition removeId(CropCondition importedWb) {
    CropConditionRepresentationModel wbRes = cropConditionAssembler.toModel(importedWb);
    wbRes.setCropConditionId(null);
    return cropConditionAssembler.toEntityModel(wbRes);
  }

  private void checkForDuplicates(Collection<CropConditionDefinitionRepresentationModel> definitions)
      throws CropConditionAlreadyExistException {
    Set<String> identifiers = new HashSet<>();
    for (CropConditionDefinitionRepresentationModel definition : definitions) {
      String identifier = logicalId(cropConditionAssembler.toEntityModel(definition.getCropCondition()));
      if (identifiers.contains(identifier)) {
        throw new CropConditionAlreadyExistException(
            definition.getCropCondition().getKey(), definition.getCropCondition().getDomain());
      }
      identifiers.add(identifier);
    }
  }

  private String logicalId(CropConditionSummary cropCondition) {
    return logicalId(cropCondition.getKey(), cropCondition.getDomain());
  }

  private String logicalId(String key, String domain) {
    return key + "|" + domain;
  }
}
