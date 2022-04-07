package dev.vality.provider.applepay.iface.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dev.vality.provider.applepay.domain.SessionRequest;
import dev.vality.provider.applepay.service.CertNotFoundException;
import dev.vality.provider.applepay.service.SessionService;
import dev.vality.woody.api.flow.error.WErrorType;
import dev.vality.woody.api.flow.error.WRuntimeException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/${server.rest.endpoint}")
public class DumbRequestSessionController {

    private final SessionService service;

    private final ObjectWriter writer = new ObjectMapper().writer();

    @ApiOperation(value = "Request ApplePay session")
    @PostMapping(value = "/session", produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            headers = "Content-Type=application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Apple Pay session object"),
            @ApiResponse(code = 500, message = "Internal service error"),
            @ApiResponse(code = 503, message = "Apple Pay service unavailable")})
    @CrossOrigin
    public ResponseEntity<String> getSession(@RequestBody SessionRequest request) {
        log.info("New Session request: {}", request);

        try {
            return ResponseEntity.ok(service.requestSession(request.getMerchantId(), request.getValidationURL(),
                    writer.writeValueAsString(request.getBody())));
        } catch (CertNotFoundException e) {
            log.error("Merchant not found: " + request.getMerchantId(), e);
            return ResponseEntity.badRequest().body("Merchant not found");
        } catch (WRuntimeException e) {
            WErrorType errorType = e.getErrorDefinition().getErrorType();
            if (errorType == WErrorType.UNDEFINED_RESULT || errorType == WErrorType.UNAVAILABLE_RESULT) {
                log.warn("Apple pay service unavailable", e);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Third party service unavailable");
            } else {
                log.error("Failed to request session", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request session");
            }
        } catch (Exception e) {
            log.error("Failed to request session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request session");
        }

    }

}
