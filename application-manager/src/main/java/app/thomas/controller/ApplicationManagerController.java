package app.thomas.controller;

import app.thomas.controller.model.BuyShippingInput;
import app.thomas.manager.ApplicationManager;
import app.thomas.model.ShippingResult;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application-manager")
@Observed(name = "applicationManager", contextualName = "controller")
@Validated
public class ApplicationManagerController {

    @Autowired
    private ApplicationManager applicationManager;

    @Operation(
            operationId = "buyShipping",
            summary = "Buy shipping",
            description = "Buy shipping",
            tags = {"Shipping"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "404", description = "User or wallet not found / Endpoint does not exists", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "405", description = "Method not allowed", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "415", description = "Unsupported Media Type", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping(path = "/shipping/{userID}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ShippingResult buyShipping(@Pattern(regexp = "\\d+") @PathVariable String userID, @RequestBody @Valid BuyShippingInput request) {
        return applicationManager.buyShipping(Long.valueOf(userID), request.getCost());
    }



}
