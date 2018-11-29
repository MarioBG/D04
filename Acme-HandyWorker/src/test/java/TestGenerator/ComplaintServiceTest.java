package TestGenerator; 

import javax.transaction.Transactional;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utilities.AbstractTest;
@ContextConfiguration(locations = {
		"classpath:spring/junit.xml", "classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
	})
	@RunWith(SpringJUnit4ClassRunner.class)
	@Transactional
	public class ComplaintServiceTest extends AbstractTest {

//		@Autowired
//		private ComplaintService	complaintService;
//		@Autowired
//		private RefereeService		refereeservice;
//		@Autowired
//		private ReportService		reportservice;

		
	

	}