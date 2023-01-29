package com.tnsilver.controller;

import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.tnsilver.protobuf.TrainingProtos.Course;
import com.tnsilver.protobuf.TrainingProtos.Courses;
import com.tnsilver.protobuf.TrainingProtos.Error;
import com.tnsilver.protobuf.TrainingProtos.Error.FieldError;
import com.tnsilver.protobuf.TrainingProtos.PhoneNumbers;
import com.tnsilver.protobuf.TrainingProtos.Student;
import com.tnsilver.protobuf.TrainingProtos.Student.PhoneNumber;
import com.tnsilver.protobuf.TrainingProtos.Students;
import com.tnsilver.repository.CourseRepository;

import io.envoyproxy.pgv.ReflectiveValidatorIndex;
import io.envoyproxy.pgv.ValidationException;
import io.envoyproxy.pgv.ValidatorIndex;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CourseController {

	// @formatter:off
	@Autowired ApplicationContext applicationContext;
	@Autowired CourseRepository courseRepo;
	@Autowired HttpServletRequest request;
	// @formatter:on

	@GetMapping(value = "/courses/{id}", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> findById(@PathVariable Integer id) {
		log.debug("{} getting course id {}", request.getRemoteHost(), id);
		Course course = courseRepo.findCourseById(id);
		return null != course ? new ResponseEntity<>(course, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping(value = "/courses/{id}")
	ResponseEntity<Void> deleteById(@PathVariable Integer id) {
		log.debug("{} deleting course id {}", request.getRemoteHost(), id);
		boolean result = courseRepo.deleteCourseById(id);
		return result ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping(value = "/students/{id}", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> findStudentById(@PathVariable Integer id) {
		log.debug("{} getting student id {}", request.getRemoteHost(), id);
		Student student = courseRepo.findStudentById(id);
		return null != student ? new ResponseEntity<>(student, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping(value = "/students/{id}")
	ResponseEntity<Void> deleteStudentById(@PathVariable Integer id) {
		log.debug("{} deleting student id {}", request.getRemoteHost(), id);
		boolean result = courseRepo.deleteStudentById(id);
		return result ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping(value = "/phones/{id}")
	ResponseEntity<Void> deletePhoneById(@PathVariable Integer id) {
		log.debug("{} deleting phone id {}", request.getRemoteHost(), id);
		boolean result = courseRepo.deletePhoneById(id);
		return result ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@GetMapping(value = "/courses", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> findAll() {
		log.debug("{} getting all courses", request.getRemoteHost());
		Courses courses = courseRepo.findAll();
		return null != courses && courses.getCoursesCount() > 0 ? new ResponseEntity<>(courses, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/courses", produces = "application/x-protobuf", consumes = "application/x-protobuf", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<? extends Message> saveCourse(@RequestBody byte[] payload) {
		Course course = null;
		// @formatter:off
		try {
			byte[] data = Base64.getDecoder().decode(payload);
			course = Course.parseFrom(data);
			ValidatorIndex index = new ReflectiveValidatorIndex();
			index.validatorFor(course.getClass()).assertValid(course);
			course = courseRepo.save(course);
			course = courseRepo.findCourseById(course.getId());
			log.debug("{} saved course: {}", request.getRemoteHost(), course);
			return null != course ? new ResponseEntity<>(course, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ValidationException vex) {
			log.warn("{} {} on '{}'", request.getRemoteHost(), vex.getReason(), camelCase(vex.getField()));
			Error error = Error.newBuilder().addData(Any.pack(course))
				.setError(camelCase(vex.getField()) + ": " + vex.getReason())
				.addFieldErrors(FieldError.newBuilder()
									.setName(camelCase(vex.getField()))
									.setStatus(vex.getReason())).build();
			return new ResponseEntity<>(error,HttpStatus.ACCEPTED);
		} catch (Exception ex) {
			log.error("{} {}", request.getRemoteHost(), ex);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		// @formatter:on
	}

	@RequestMapping(value = "/students", produces = "application/x-protobuf", consumes = "application/x-protobuf", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<? extends Message> saveStudent(@RequestBody byte[] payload) {
		Student student = null;
		// @formatter:off
		try {
			byte[] data = Base64.getDecoder().decode(payload);
			student = Student.parseFrom(data);
			ValidatorIndex index = new ReflectiveValidatorIndex();
			index.validatorFor(student.getClass()).assertValid(student);
			student = courseRepo.save(student);
			//student = courseRepo.findStudentById(student.getId());
			log.debug("{} saved student: {}", request.getRemoteHost(), student);
			return null != student ? new ResponseEntity<>(student, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ValidationException vex) {
			log.warn("{} {} on '{}'", request.getRemoteHost(), vex.getReason(), camelCase(vex.getField()));
			Error error = Error.newBuilder().addData(Any.pack(student))
				.setError(camelCase(vex.getField()) + ": " + vex.getReason())
				.addFieldErrors(FieldError.newBuilder()
									.setName(camelCase(vex.getField()))
									.setStatus(vex.getReason())).build();
			return new ResponseEntity<>(error,HttpStatus.ACCEPTED);
		} catch (Exception ex) {
			log.error("{} {}", request.getRemoteHost(), ex);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		// @formatter:on
	}

	@RequestMapping(value = "/phones", produces = "application/x-protobuf", consumes = "application/x-protobuf", method = { RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH })
	public ResponseEntity<? extends Message> savePhoneNumber(@RequestBody byte[] payload) {
		PhoneNumber phoneNumber = null;
		// @formatter:off
		try {
			byte[] data = Base64.getDecoder().decode(payload);
			phoneNumber = PhoneNumber.parseFrom(data);
			ValidatorIndex index = new ReflectiveValidatorIndex();
			index.validatorFor(phoneNumber.getClass()).assertValid(phoneNumber);
			phoneNumber = courseRepo.save(phoneNumber);
			log.debug("{} saved phone number: {}", request.getRemoteHost(), phoneNumber);
			return null != phoneNumber ? new ResponseEntity<>(phoneNumber, HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (ValidationException vex) {
			log.warn("{} {} on '{}'", request.getRemoteHost(), vex.getReason(), camelCase(vex.getField()));
			Error error = Error.newBuilder().addData(Any.pack(phoneNumber))
				.setError(camelCase(vex.getField()) + ": " + vex.getReason())
				.addFieldErrors(FieldError.newBuilder()
									.setName(camelCase(vex.getField()))
									.setStatus(vex.getReason())).build();
			return new ResponseEntity<>(error,HttpStatus.ACCEPTED);
		} catch (Exception ex) {
			log.error("{} {}", request.getRemoteHost(), ex);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		// @formatter:on
	}

	@GetMapping(value = "/courses/{id}/students", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> findCourseStudents(@PathVariable Integer id) {
		log.debug("{} getting students of course id {}", request.getRemoteHost(), id);
		Students students = courseRepo.findCourseStudents(id);
		return null != students ? new ResponseEntity<>(students, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(value = "/students/{id}/phones", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> findStudentPhoneNumbers(@PathVariable Integer id) {
		log.debug("{} getting phone numbers of student id {}", request.getRemoteHost(), id);
		PhoneNumbers phoneNumbers = courseRepo.findStudentPhoneNumbers(id);
		return null != phoneNumbers ? new ResponseEntity<>(phoneNumbers, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(value = "/reset", produces = "application/x-protobuf")
	ResponseEntity<? extends Message> reset() {
		courseRepo.init();
		Courses courses = courseRepo.findAll();
		log.debug("{} reset data", request.getRemoteHost());
		return null != courses && courses.getCoursesCount() > 0 ? new ResponseEntity<>(courses, HttpStatus.OK)
				: new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	private String camelCase(String fieldName) {
		//.protobuf.Course.course_name
		String field = fieldName;
		if (field.lastIndexOf(".") >= 0)
			field = field.substring(field.lastIndexOf(".") + 1);
		return CaseUtils.toCamelCase(field, false, '_', ' ');
	}

}