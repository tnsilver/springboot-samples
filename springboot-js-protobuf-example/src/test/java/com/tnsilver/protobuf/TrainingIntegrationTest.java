package com.tnsilver.protobuf;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.CaseUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.web.client.RestTemplate;

import com.google.protobuf.util.JsonFormat;
import com.tnsilver.Application;
import com.tnsilver.protobuf.TrainingProtos.Course;
import com.tnsilver.protobuf.TrainingProtos.Courses;
import com.tnsilver.protobuf.TrainingProtos.Student;
import com.tnsilver.protobuf.TrainingProtos.Student.PhoneNumber;
import com.tnsilver.protobuf.TrainingProtos.Student.PhoneType;
import com.tnsilver.protobuf.TrainingProtos.Students;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class TrainingIntegrationTest {

	@Autowired
	private RestTemplate restTemplate;

	@LocalServerPort
	private int port;

	@Test
	public void testRestTemplateGetCourse() throws IOException {
		String url = "http://localhost:" + port + "/courses/2";
		ResponseEntity<Course> response = restTemplate.getForEntity(url, Course.class);
		Course course = response.getBody();
		String json = JsonFormat.printer().print(course);
		assertResponse(json);
	}

	@Test
	@DisplayName("test get course")
	public void testGetCourse() throws IOException {
		String url = "http://localhost:" + port + "/courses/";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpResponse = httpClient.execute(new HttpGet(url + "2"));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Course course = Course.parseFrom(responseStream);
		String json = JsonFormat.printer().print(course);
		assertResponse(json);
	}

	@Test
	@DisplayName("test get courses")
	public void testGetCourses() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Courses courses = Courses.parseFrom(responseStream);
		String json = JsonFormat.printer().print(courses);
		assertResponse(json);
	}

	@Test
	@DisplayName("test update course")
	public void testUpdateCourseName() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpGetResponse = httpClient.execute(new HttpGet(url + "/2"));
		InputStream getResponseStream = httpGetResponse.getEntity().getContent();
		Course course = Course.parseFrom(getResponseStream);
		assertEquals("Protobuf with SpringBoot", course.getCourseName());
		course = course.toBuilder().setCourseName("Testing with SpringBoot").build();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(course.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(post);
		assertEquals(201, httpPostResponse.getStatusLine().getStatusCode());
		InputStream postResponseStream = httpPostResponse.getEntity().getContent();
		course = Course.parseFrom(postResponseStream);
		assertEquals("Testing with SpringBoot", course.getCourseName());
	}

	@Test
	@DisplayName("test update students")
	public void testUpdateStudents() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpGetResponse = httpClient.execute(new HttpGet(url + "/2"));
		InputStream getResponseStream = httpGetResponse.getEntity().getContent();
		Course course = Course.parseFrom(getResponseStream);
		assertEquals("Protobuf with SpringBoot", course.getCourseName());
		List<Student> students = course.getStudentList().stream().map(student -> student.toBuilder().setEmail(student.getEmail().replace("simpson", "johnson")).build()).collect(Collectors.toList());
		course = course.toBuilder().setCourseName("Testing with SpringBoot").clearStudent().addAllStudent(students).build();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(course.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(post);
		assertEquals(201, httpPostResponse.getStatusLine().getStatusCode());
		InputStream postResponseStream = httpPostResponse.getEntity().getContent();
		course = Course.parseFrom(postResponseStream);
		assertEquals("Testing with SpringBoot", course.getCourseName());
		course.getStudentList().forEach(student -> assertThat(student.getEmail(), containsString("johnson")));
	}

	@Test
	@DisplayName("test delete course")
	public void testDeleteCourse() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpGetResponse = httpClient.execute(new HttpGet(url + "/1"));
		InputStream getResponseStream = httpGetResponse.getEntity().getContent();
		Course course = Course.parseFrom(getResponseStream);
		assertEquals("Dummy Course", course.getCourseName());
		HttpDeleteWithBody delete = new HttpDeleteWithBody(url + "/1");
		delete.setHeader("Content-Type", "application/x-protobuf");
		delete.setEntity(new ByteArrayEntity(course.toByteArray(), ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(delete);
		assertEquals(HttpStatus.NO_CONTENT.value(), httpPostResponse.getStatusLine().getStatusCode());
	}

	@Test
	@DisplayName("test delete course by id")
	public void testDeleteCourseById() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpGetResponse = httpClient.execute(new HttpGet(url + "/1"));
		InputStream getResponseStream = httpGetResponse.getEntity().getContent();
		Course course = Course.parseFrom(getResponseStream);
		assertEquals("Dummy Course", course.getCourseName());
		HttpDelete delete = new HttpDelete(url + "/1");
		HttpResponse httpPostResponse = httpClient.execute(delete);
		assertEquals(HttpStatus.NO_CONTENT.value(), httpPostResponse.getStatusLine().getStatusCode());
	}

	@Test
	@DisplayName("test delete student by id")
	public void testDeleteStudentById() throws IOException {
		int courseId = 1, studentId=1000;
		// delete student 1000 of course 1
		String url = "http://localhost:" + port + "/students/";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(url + "/" + studentId);
		HttpResponse httpResponse = httpClient.execute(delete);
		assertEquals(HttpStatus.NO_CONTENT.value(), httpResponse.getStatusLine().getStatusCode());
        // get course id 1 and makes sure student 1000 is not there
		url = "http://localhost:" + port + "/courses/" + courseId + "/students";
		httpResponse = httpClient.execute(new HttpGet(url));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Students students = Students.parseFrom(responseStream);
		assertThat(students.getCourseId(), equalTo(courseId));
		students.getStudentsList().forEach(student -> assertThat(student.getId(), not(equalTo(studentId))));
	}

	@Test
	@DisplayName("test delete phone by id")
	public void testDeletePhoneById() throws IOException {
		int courseId = 2, studentId=1101, phoneId=110;
		// delete phone 110 of student 1101 of course 2
		String url = "http://localhost:" + port + "/phones";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpDelete delete = new HttpDelete(url + "/" + phoneId);
		HttpResponse httpResponse = httpClient.execute(delete);
		assertEquals(HttpStatus.NO_CONTENT.value(), httpResponse.getStatusLine().getStatusCode());
        // get course id 2 students list and makes sure student 1101 does not have phone 110
		url = "http://localhost:" + port + "/courses/" + courseId + "/students";
		httpResponse = httpClient.execute(new HttpGet(url));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Students students = Students.parseFrom(responseStream);
		assertThat(students.getCourseId(), equalTo(courseId));
		Student student = students.getStudentsList().stream().filter(s -> s.getId() == studentId).findAny().orElse(null);
		assertNotNull(student);
		student.getPhoneList().forEach(p -> assertNotEquals(phoneId, p.getId()));
	}

	@Test
	@DisplayName("test add new student")
	public void testCreateStudent() throws IOException {
		int courseId = 1, studentId=0, expectedId = 2011;
		String url = "http://localhost:" + port + "/students";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		Student student = Student.newBuilder()
				.setId(studentId)
				.setFirstName("Tom")
				.setLastName("Silverman")
				.setEmail("toms@aol.com")
				.addAllPhone(List.of(PhoneNumber.newBuilder().setId(999).setType(PhoneType.MOBILE).setNumber("123456").build()))
				.addAllCourseRefs(List.of(courseId))
				.build();
		byte[] data = Base64.getEncoder().encode(student.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpResponse = httpClient.execute(post);
		assertEquals(HttpStatus.CREATED.value(), httpResponse.getStatusLine().getStatusCode());
		InputStream responseStream = httpResponse.getEntity().getContent();
		student = Student.parseFrom(responseStream);
		assertEquals(expectedId, student.getId());
	}

	@Test
	@DisplayName("test edit student")
	public void testEditStudent() throws IOException {
		// @formatter:off
		int courseId = 1, expectedId = 1000;
		String url = "http://localhost:" + port + "/courses/" + courseId + "/students";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Students students = Students.parseFrom(responseStream);
		Student student = Student.newBuilder(students.getStudents(0))
				.setFirstName("Tom")
				.setLastName("Silverman")
				.setEmail("toms@aol.com")
				.addAllPhone(students.getStudents(0).getPhoneList())
				.addPhone(PhoneNumber.newBuilder().setId(888).setNumber("123456").setType(PhoneType.MOBILE))
				.build();
		url = "http://localhost:" + port + "/students";
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(student.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		httpResponse = httpClient.execute(post);
		assertEquals(HttpStatus.CREATED.value(), httpResponse.getStatusLine().getStatusCode());
		responseStream = httpResponse.getEntity().getContent();
		student = Student.parseFrom(responseStream);
		assertEquals(expectedId, student.getId());
		assertEquals("Tom", student.getFirstName());
		assertEquals("Silverman", student.getLastName());
		assertEquals("toms@aol.com", student.getEmail());
		assertEquals("123456", student.getPhone(0).getNumber());
		// @formatter:on
	}

	@Test
	@DisplayName("test create course")
	public void testCreateCourse() throws IOException {
		String url = "http://localhost:" + port + "/courses";
		Course course = Course.newBuilder()
				.setCourseName("New Course")
				.addAllStudent(List.of(Student.newBuilder().setId(1).setFirstName("Test").setLastName("Tester").setEmail("tester@testers.com").build())).build();
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(course.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(post);
		assertEquals(HttpStatus.CREATED.value(), httpPostResponse.getStatusLine().getStatusCode());
		InputStream postResponseStream = httpPostResponse.getEntity().getContent();
		course = Course.parseFrom(postResponseStream);
		assertEquals("New Course", course.getCourseName());
	}

	@Test
	@DisplayName("test get course students")
	public void testGetCourseStudents() throws IOException {
		String url = "http://localhost:" + port + "/courses/2/students";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		InputStream responseStream = httpResponse.getEntity().getContent();
		Students students = Students.parseFrom(responseStream);
		assertThat(students.getCourseId(), equalTo(2));
		students.getStudentsList().forEach(student -> assertThat(student.getEmail(), containsString("simpson")));
	}

	@Test
	@DisplayName("test add phone number")
	public void testAddPhoneNumber() throws IOException {
		int studentRef=1101, newId=251;
		String number = "654321";
		PhoneType type = PhoneType.LANDLINE;
		PhoneNumber phoneNumber = PhoneNumber.newBuilder().setId(0).setNumber(number).setType(type).setStudentRef(studentRef).build();
		String url = "http://localhost:" + port + "/phones";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(phoneNumber.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(post);
		assertEquals(HttpStatus.CREATED.value(), httpPostResponse.getStatusLine().getStatusCode());
		InputStream postResponseStream = httpPostResponse.getEntity().getContent();
		phoneNumber = PhoneNumber.parseFrom(postResponseStream);
		assertEquals(newId, phoneNumber.getId());
		assertEquals(number, phoneNumber.getNumber());
		assertEquals(studentRef, phoneNumber.getStudentRef());
		assertEquals(type, phoneNumber.getType());
	}

	@Test
	@DisplayName("test edit phone number")
	public void testEditPhoneNumber() throws IOException {
		int studentRef=1101, phoneNumberId=110;
		String number = "654321";
		PhoneType type = PhoneType.LANDLINE;
		PhoneNumber phoneNumber = PhoneNumber.newBuilder()
												.setId(phoneNumberId)
												.setNumber(number)
												.setType(type)
												.setStudentRef(studentRef).build();
		String url = "http://localhost:" + port + "/phones";
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/x-protobuf");
		byte[] data = Base64.getEncoder().encode(phoneNumber.toByteArray());
		post.setEntity(new ByteArrayEntity(data, ContentType.getByMimeType("application/x-protobuf")));
		HttpResponse httpPostResponse = httpClient.execute(post);
		assertEquals(HttpStatus.CREATED.value(), httpPostResponse.getStatusLine().getStatusCode());
		InputStream postResponseStream = httpPostResponse.getEntity().getContent();
		phoneNumber = PhoneNumber.parseFrom(postResponseStream);
		assertEquals(phoneNumberId, phoneNumber.getId());
		assertEquals(number, phoneNumber.getNumber());
		assertEquals(studentRef, phoneNumber.getStudentRef());
		assertEquals(type, phoneNumber.getType());
	}



	@ParameterizedTest
	@DisplayName("test camel case")
	@CsvSource({"protobuf.Course.course_name,courseName",
				"course_name,courseName",
				".protobuf.Course.my_Dumb_string,myDumbString",
				"my_Dumb_string,myDumbString",
				"my_Dumb string,myDumbString",
				".protobuf.Course.my_Dumb string,myDumbString"})
	public void testCamelCase(String actual,String expected) throws Exception {
		String field = actual;
		if (field.lastIndexOf(".") >= 0)
			field = field.substring(field.lastIndexOf(".") + 1);
		actual = CaseUtils.toCamelCase(field, false, '_', ' ');
		assertEquals(expected,actual);
	}

	private void assertResponse(String json) {
		assertThat(json, containsString("id"));
		assertThat(json, containsString("courseName"));
		assertThat(json, containsString("Protobuf with SpringBoot"));
		assertThat(json, containsString("student"));
		assertThat(json, containsString("firstName"));
		assertThat(json, containsString("lastName"));
		assertThat(json, containsString("email"));
		assertThat(json, containsString("homer.simpson@gmail.com"));
		assertThat(json, containsString("marge.simpson@gmail.com"));
		assertThat(json, containsString("lisa.simpson@gmail.com"));
		assertThat(json, containsString("bart.simpson@gmail.com"));
		assertThat(json, containsString("maggie.simpson@gmail.com"));
		assertThat(json, containsString("phone"));
		assertThat(json, containsString("number"));
		assertThat(json, containsString("type"));
	}

	@javax.annotation.concurrent.NotThreadSafe
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
	    public static final String METHOD_NAME = "DELETE";

	    public String getMethod() {
	        return METHOD_NAME;
	    }

	    public HttpDeleteWithBody(final String uri) {
	        super();
	        setURI(URI.create(uri));
	    }

	    public HttpDeleteWithBody(final URI uri) {
	        super();
	        setURI(uri);
	    }

	    public HttpDeleteWithBody() {
	        super();
	    }
	}
}
