package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.transfer.AutomaticTaskSubmitRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/submit")
@Slf4j
public class AutomaticTaskController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submit(@RequestBody AutomaticTaskSubmitRequest request) {
        // Endpoint used to receive and process the task from LC
        // This endpoint should only schedule the task and return 202(Accepted)
        // The scheduled task would be picked up by a background process
        // that will send the result to the received callbackUrl
        LOGGER.info("submit {} >>", request);

        // TODO: implement a method that will schedule the received task in order to process it asynchronously
    }
}
