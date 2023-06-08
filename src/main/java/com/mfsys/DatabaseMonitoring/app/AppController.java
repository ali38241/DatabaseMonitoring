package com.mfsys.DatabaseMonitoring.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class AppController {

	@GetMapping("/hi")
	public String abc() {
		return "hi";
	}
}
