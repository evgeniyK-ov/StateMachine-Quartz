package ru.kazachkov.statemachinedemo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.kazachkov.statemachinedemo.services.ProcessService;

@Controller
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @RequestMapping(path = "/start", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter startProcess(String message) {
        var proccessId = processService.createMessageProcess(message, null);
        var emitter = new SseEmitter();
        emitter.onTimeout(() -> ProcessService.publishes.remove(proccessId));
        emitter.onCompletion(() -> ProcessService.publishes.remove(proccessId));
        ProcessService.publishes.put(proccessId, emitter);
        return emitter;
    }


}
