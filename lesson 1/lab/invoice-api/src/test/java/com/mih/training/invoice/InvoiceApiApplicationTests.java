package com.mih.training.invoice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "logging.level.com.mih.training.invoice=TRACE")
class InvoiceApiApplicationTests {
	 private static final Logger log = LoggerFactory.getLogger(InvoiceApiApplicationTests.class);
	@Test
	void contextLoads() {
		log.trace("asdasdas");
	}

}
