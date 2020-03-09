package io.aud.authenticationservice.configuration;

import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.DescriptionResolver;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class OperationNotesResourcesReader implements OperationBuilderPlugin {

    private final DescriptionResolver descriptions;

    @Autowired
    public OperationNotesResourcesReader(DescriptionResolver descriptions) {
        this.descriptions = descriptions;
    }

    @Override
    public void apply(OperationContext context) {
        try {
            Optional<PreAuthorize> preAuthorizeAnnotation = context.findAnnotation(PreAuthorize.class);
            if (preAuthorizeAnnotation.isPresent()) {
                String rule = preAuthorizeAnnotation.get().value();
                String apiRoleAccessNoteText = rule
                        .replace("hasAnyAuthority(", "Requires ANY of the following Authorities: ")
                        .replace("hasAuthority(", "Requires ALL of the following Authorities: ")
                        .replace("isAuthenticated()", "Requires the user to be logged in.")
                        .replace(")", "")
                        .replace("'","");

                // add the note text to the Swagger UI
                context.operationBuilder().notes(descriptions.resolve(apiRoleAccessNoteText));
            }
        } catch (Exception ignored) { }
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }
}