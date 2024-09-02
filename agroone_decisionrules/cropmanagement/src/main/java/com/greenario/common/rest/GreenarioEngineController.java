package com.greenario.common.rest;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenario.AgroOneConfiguration;
import com.greenario.common.api.ConfigurationService;
import com.greenario.common.api.GreenarioEngine;
import com.greenario.common.api.security.CurrentUserContext;
import com.greenario.common.rest.models.CustomAttributesRepresentationModel;
import com.greenario.common.rest.models.GreenarioUserInfoRepresentationModel;
import com.greenario.common.rest.models.VersionRepresentationModel;

/** Controller for GreenarioEngine related projects. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class GreenarioEngineController {

  private final AgroOneConfiguration agroOneConfiguration;
  private final GreenarioEngine greenarioEngine;
  private final CurrentUserContext currentUserContext;
  private final ConfigurationService configurationService;

  @Autowired
  GreenarioEngineController(
      AgroOneConfiguration agroOneConfiguration,
      GreenarioEngine greenarioEngine,
      CurrentUserContext currentUserContext,
      ConfigurationService configurationService) {
    this.agroOneConfiguration = agroOneConfiguration;
    this.greenarioEngine = greenarioEngine;
    this.currentUserContext = currentUserContext;
    this.configurationService = configurationService;
  }

  /**
   * This endpoint retrieves all configured Domains.
   *
   * @return An array with the domain-names as strings
   */
  @GetMapping(path = RestEndpoints.URL_DOMAIN)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getDomains() {
    return ResponseEntity.ok(agroOneConfiguration.getDomains());
  }

  /**
   * This endpoint retrieves the configured plot categories for a
   * specific plot
   * type.
   *
   * @param type the plot type whose categories should be determined. If
   *             not specified all
   *             plot categories will be returned.
   * @return the plot categories for the requested type.
   */
  @GetMapping(path = RestEndpoints.URL_PLOT_CATEGORIES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getPlotCategories(
      @RequestParam(required = false) String type) {
    if (type != null) {
      return ResponseEntity.ok(agroOneConfiguration.getPlotCategoriesByType(type));
    }
    return ResponseEntity.ok(agroOneConfiguration.getAllPlotCategories());
  }

  /**
   * This endpoint retrieves the configured plot types.
   *
   * @return the configured plot types.
   */
  @GetMapping(path = RestEndpoints.URL_PLOT_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getPlotTypes() {
    return ResponseEntity.ok(agroOneConfiguration.getPlotTypes());
  }

  /**
   * This endpoint retrieves all configured plot categories grouped by
   * each plot
   * type.
   *
   * @return the configured plot categories
   */
  @GetMapping(path = RestEndpoints.URL_PLOT_CATEGORIES_BY_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getPlotCategoriesByTypeMap(
      @RequestParam(required = false) String type) {
    return ResponseEntity.ok(agroOneConfiguration.getPlotCategoriesByType(type));
  }

  /**
   * This endpoint retrieves the configured plot categories for a
   * specific plot
   * type.
   *
   * @param type the plot type whose categories should be determined. If
   *             not specified all
   *             farm categories will be returned.
   * @return the farm categories for the requested type.
   */
  @GetMapping(path = RestEndpoints.URL_FARM_CATEGORIES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getFarmCategories(
      @RequestParam(required = false) String type) {
    if (type != null) {
      return ResponseEntity.ok(agroOneConfiguration.getFarmCategoriesByType(type));
    }
    return ResponseEntity.ok(agroOneConfiguration.getAllFarmCategories());
  }

  /**
   * This endpoint retrieves the configured farm types.
   *
   * @return the configured farm types.
   */
  @GetMapping(path = RestEndpoints.URL_FARM_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getFarmTypes() {
    return ResponseEntity.ok(agroOneConfiguration.getFarmTypes());
  }

  /**
   * This endpoint retrieves all configured plot categories grouped by
   * each farm
   * type.
   *
   * @return the configured farm categories
   */
  @GetMapping(path = RestEndpoints.URL_FARM_CATEGORIES_BY_TYPES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<List<String>> getFarmCategoriesByTypeMap(
      @RequestParam(required = false) String type) {
    return ResponseEntity.ok(agroOneConfiguration.getFarmCategoriesByType(type));
  }



  
  /**
   * This endpoint computes all information of the current user.
   *
   * @return the information of the current user.
   */
  @GetMapping(path = RestEndpoints.URL_CURRENT_USER)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<GreenarioUserInfoRepresentationModel> getCurrentUserInfo() {
    GreenarioUserInfoRepresentationModel resource = new GreenarioUserInfoRepresentationModel();
    resource.setUserId(currentUserContext.getUserid());
    resource.setGroupIds(currentUserContext.getGroupIds());
    agroOneConfiguration.getRoleMap().keySet().stream()
        .filter(greenarioEngine::isUserInRole)
        .forEach(resource.getRoles()::add);
    return ResponseEntity.ok(resource);
  }

  /**
   * This endpoint checks if the history module is in use.
   *
   * @return true, when the history is enabled, otherwise false
   */
  @GetMapping(path = RestEndpoints.URL_HISTORY_ENABLED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<Boolean> getIsHistoryProviderEnabled() {
    return ResponseEntity.ok(greenarioEngine.isHistoryEnabled());
  }

  /**
   * This endpoint retrieves the saved custom configuration.
   *
   * @title Get custom configuration
   * @return custom configuration
   */
  @GetMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> getCustomAttributes() {
    Map<String, Object> allCustomAttributes = configurationService.getAllCustomAttributes();
    return ResponseEntity.ok(new CustomAttributesRepresentationModel(allCustomAttributes));
  }

  /**
   * This endpoint overrides the custom configuration.
   *
   * @param customAttributes the new custom configuration
   * @title Set all custom configuration
   * @return the new custom configuration
   */
  @PutMapping(path = RestEndpoints.URL_CUSTOM_ATTRIBUTES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CustomAttributesRepresentationModel> setCustomAttributes(
      @RequestBody CustomAttributesRepresentationModel customAttributes) {
    configurationService.setAllCustomAttributes(customAttributes.getCustomAttributes());
    return ResponseEntity.ok(customAttributes);
  }

  /**
   * Get the current application version.
   *
   * @return The current version.
   */
  @GetMapping(path = RestEndpoints.URL_VERSION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<VersionRepresentationModel> currentVersion() {
    VersionRepresentationModel resource = new VersionRepresentationModel();
    resource.setVersion(AgroOneConfiguration.class.getPackage().getImplementationVersion());
    return ResponseEntity.ok(resource);
  }
}
