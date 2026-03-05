package com.redhat.cajun.navy.datagenerate;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;


public class RestClientVerticle extends AbstractVerticle {

    private static Logger log = LoggerFactory.getLogger(RestClientVerticle.class);

    private WebClient client;

    private String incidentServiceHost;

    private int incidentServicePort;

    private String incidentServiceCreatePath;

    private String incidentServiceResetPath;

    private String responderServiceHost;

    private int responderServicePort;

    private String responderServiceResetPath;

    private String responderServiceClearPath;

    private String missionServiceResetPath;

    private String missionServiceHost;

    private int missionServicePort;

    private String processServiceResetPath;

    private String processServiceHost;

    private int processServicePort;

    private String incidentPriorityServiceHost;

    private int incidentPriorityServicePort;

    private String incidentPriorityServiceResetPath;

    private String responderSimulatorHost;

    private int responderSimulatorPort;

    private String responderSimulatorResetPath;

    private String responderServiceCreatePath = null;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer("rest-client-queue", this::onMessage);
        client = WebClient.create(vertx);
        incidentServiceHost = config().getString("incident.service.host");
        incidentServicePort = config().getInteger("incident.service.port");
        incidentServiceResetPath = config().getString("incident.service.path.reset");
        incidentServiceCreatePath = config().getString("incident.service.path.create");
        responderServiceHost = config().getString("responder.service.host");
        responderServicePort = config().getInteger("responder.service.port");
        responderServiceResetPath = config().getString("responder.service.path.reset");
        responderServiceClearPath = config().getString("responder.service.path.clear");
        responderServiceCreatePath = config().getString("responder.service.path.create");
        missionServiceHost = config().getString("mission.service.host");
        missionServicePort = config().getInteger("mission.service.port");
        missionServiceResetPath = config().getString("mission.service.path.reset");
        incidentPriorityServiceHost = config().getString("incidentpriority.service.host");
        incidentPriorityServicePort = config().getInteger("incidentpriority.service.port");
        incidentPriorityServiceResetPath = config().getString("incidentpriority.service.path.reset");
        processServiceHost = config().getString("process.service.host");
        processServicePort = config().getInteger("process.service.port");
        processServiceResetPath = config().getString("process.service.path.reset");
        responderSimulatorHost = config().getString("responder.simulator.host");
        responderSimulatorPort = config().getInteger("responder.simulator.port");
        responderSimulatorResetPath = config().getString("responder.simulator.path.reset");
        startFuture.complete();
    }

    public enum ErrorCodes {
        NO_ACTION_SPECIFIED,
        BAD_ACTION
    }


    private void onMessage(Message<JsonObject> message) {

        String action = message.headers().get("action");
        switch (action) {
            case "create-incident":
                createIncident(message);
                break;

            case "reset-incidents":
                resetIncidents();
                break;

            case "reset-responders":
                resetResponders();
                break;

            case "clear-responders":
                clearResponders();
                break;

            case "create-responders":
                createResponders(message);
                break;

            case "reset-missions":
                resetMissions();
                break;

            case "reset-responder-simulator":
                resetResponderSimulator();
                break;

            case "abort-process-instances":
                abortProcessInstances();
                break;

            case "reset-incident-priority":
                resetIncidentPriority();
                break;

            default:
                message.fail(ErrorCodes.BAD_ACTION.ordinal(),"Message failed");
        }
    }

    private void resetResponders() {
        client.post(responderServicePort, responderServiceHost, responderServiceResetPath)
                .send(handleResponse("Reset responders", "Reset responders failed."));
    }

    private void clearResponders() {
        client.post(responderServicePort, responderServiceHost, responderServiceClearPath)
                .send(handleResponse("Clear responders", "Clear responders failed."));
    }

    private void createIncident(Message<JsonObject> message) {
        String victimName = message.body().getString("victimName");
        client.post(incidentServicePort, incidentServiceHost, incidentServiceCreatePath)
                .sendJsonObject(message.body(), handleResponse("Create incident for " + victimName, "Create incident for " + victimName + " failed."));
    }

    private void createResponders(Message<JsonObject> message) {
        client.post(responderServicePort, responderServiceHost, responderServiceCreatePath)
                .putHeader("Content-Type", "application/json")
                .sendBuffer(message.body().getJsonArray("responders").toBuffer(), handleResponse("Create responders", "Create Responders failed."));
    }

    private void resetIncidents() {
        client.post(incidentServicePort, incidentServiceHost, incidentServiceResetPath)
                .send(handleResponse("Reset incidents", "Reset incidents failed."));
    }

    private void resetMissions() {
        client.post(missionServicePort, missionServiceHost, missionServiceResetPath)
                .send(handleResponse("Reset missions", "Reset missions failed."));
    }

    private void abortProcessInstances() {
        client.post(processServicePort, processServiceHost, processServiceResetPath)
                .send(handleResponse("Reset pInstances", "Reset pInstances failed."));
    }

    private void resetResponderSimulator() {
        client.post(responderSimulatorPort, responderSimulatorHost, responderSimulatorResetPath)
                .send(handleResponse("Reset responder simulator", "Reset responder simulator failed."));
    }

    private void resetIncidentPriority() {
        client.post(incidentPriorityServicePort, incidentPriorityServiceHost, incidentPriorityServiceResetPath)
                .send(handleResponse("Reset incident priority", "Reset incident priority failed."));
    }

    private Handler<AsyncResult<HttpResponse<Buffer>>> handleResponse(String successMsg, String errorMsg) {
        return ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                log.info(successMsg + ": HTTP response status " + response.statusCode());
            } else {
                log.error(errorMsg, ar.cause());
            }
        };
    }
}

