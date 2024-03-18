package com.sp.productmanagement.command.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/management")
public class EventsReplayController {
	
	@Autowired
	private EventProcessingConfiguration eventProcessingConfiguration;
	
	@PostMapping("eventprocessor/{processorName}/reset")
	public ResponseEntity<String> replayEvents(@PathVariable String processorName) {
		Optional<TrackingEventProcessor> eventProcessor = 
				eventProcessingConfiguration.eventProcessor(processorName, TrackingEventProcessor.class);
		if(eventProcessor.isPresent()) {
			TrackingEventProcessor trackingEventProcessor = eventProcessor.get();
			trackingEventProcessor.shutDown();
			trackingEventProcessor.resetTokens();
			trackingEventProcessor.start();
			
			return ResponseEntity.ok()
					.body(String.format("Tracking event processor name:{%s} has been reset", processorName));
		}
		return ResponseEntity.badRequest()
					.body(String.format("Tracking event processor name:{%s} is not present",processorName));
	}
	
}
