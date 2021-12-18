package com.ikki.immigrant.interfaces;

import com.ikki.immigrant.application.qrlogin.ScanLoginService;
import com.ikki.immigrant.application.qrlogin.SseValueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * @author ikki
 */
@RestController
@RequestMapping("/authentication")
@Slf4j
public class AuthenticationController {

    @Autowired
    ScanLoginService scanLoginService;

    @GetMapping("/qr/")
    public ResponseEntity<Void> generateChannel() throws URISyntaxException {
        String channel = UUID.randomUUID().toString();
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.setLocation(new URI("/authentication/qr/" + channel));
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers(httpHeader).build();
    }


    @GetMapping("/qr/{clientId}")
    public ResponseEntity<SseEmitter> getQRCode(@PathVariable("clientId") String clientId,
                                                @RequestHeader(value = "Last-Event-ID", defaultValue = "-1") Long lastEventId) {
        SseEmitter sseEmitter;
        // it's a retry connection
        if (lastEventId > 0) {
            sseEmitter = scanLoginService.reconnect(clientId, lastEventId);
        } else {
            sseEmitter = scanLoginService.connect(clientId);
        }
        return ResponseEntity.ok(sseEmitter);
    }

    @PostMapping("/qr/{clientId}")
    public ResponseEntity<String> scan(@PathVariable("clientId") String clientId, @RequestBody SseValueObject message) {
        scanLoginService.publish(clientId, message);
        return ResponseEntity.ok("send success");
    }
}
