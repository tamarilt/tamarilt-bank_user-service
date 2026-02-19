package tamarilt.userservice.service;

import tamarilt.userservice.dto.event.ServiceEvent;

import java.util.Map;

public interface EventProducer {

    void sendEvent(ServiceEvent event);

    void sendSuccess(String action, Map<String, Object> details);

    void sendError(String action, Map<String, Object> details);
}
